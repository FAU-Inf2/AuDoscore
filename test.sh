#!/bin/bash

callerdir=${PWD}
script=$(readlink -f $0)
scriptdir=$(dirname $script)

function info {
	echo -e "\033[1;34m$1\033[0m"
}

function err {
	echo -e "\033[1;31m$1\033[0m"
}

function cleanexit {
	# info "\ncleaning up"
	# cd ${callerdir}
	# rm -rf test.$$
	if [ $# -gt 0 ]; then
		exit $1
	fi
	exit 0
}

function die {
	err "$1"
	cleanexit -1
}

function checkexit {
	exitcode=$1; shift
	msg=$1; shift
	file=$1; shift
	if [ ${exitcode} -ne 0 ]; then
		err "failed:"
		cat ${file}
		die "${msg}"
	fi
}

function checkAnnotationFormatError {
	file=$1; shift
	grep -q "^java.lang.annotation.AnnotationFormatError" $file
	if [ $? -eq 0 ]; then
		err "testcase format wrong:"
		cat $file
		err "\nSummary:\n";
		cat $file | grep -B1 "^java.lang.annotation.AnnotationFormatError"
		die "\ninternal error\n";
	fi
}

if [ $# -lt 2 ]; then
	err "no argument given"
	info "usage: $0 <TESTCLASS> <STUDENTSOURCE1> ... <STUDENTSOURCEn> -- <INTERFACE1> ... <INTERFACEn> --";
	info " e.g.: $0 ExampleTest Student.java -- PublicInterface.java -- undertest*"
	exit -1
fi

testclass=$1; shift
studentsource=$1; shift

while [ $# -gt 0 ] && [ "x$1" != "x--" ]; do
	studentsource="${studentsource} $1"
	shift
done

shift || true # throw -- away if exists

interfaces=""
while [ $# -gt 0 ] && [ "x$1" != "x--" ]; do
	interfaces="${interfaces} $1"
	shift
done

shift || true # throw -- away if exists

undertestdirs=""
undertestdircnt=0
while [ $# -gt 0 ] && [ "x$1" != "x--" ]; do
	((undertestdircnt++))
	if [ ${undertestdircnt} -eq 1 ]; then
		undertestdirs=$1
	else
		undertestdirs="${undertestdirs} $1"
	fi
	shift
done

undertestdir="undertest"
if [ ${undertestdircnt} -eq 1 ]; then
	undertestdir=${undertestdirs}
elif [ ${undertestdircnt} -gt 1 ]; then
	for i in ${undertestdirs}; do
		info "[recur test with $i]"
		$0 ${testclass} ${studentsource} -- ${interfaces} -- $i
		if [ $? != 0 ]; then
			die "testing $i failed"
		fi
		info "\n----------------------------------------\n"
	done
	exit 0
fi


info "preparing test setup"
info "- create testdir"
testdir="${callerdir}/test.$$"
mkdir "$testdir" || die "failed to create test dir test.$$"
cd "$testdir" > /dev/null 2> /dev/null

# must be first step
info "- copy/install test infrastructure"
${scriptdir}/install.sh || die "failed"

info "- write var.mk"
echo "STUDENTSOURCE = ${studentsource}" > var.mk
echo "TEST = ${testclass}" >> var.mk
echo "INTERFACES = ${interfaces}" >> var.mk

info "- copy student sources"
pushd ../${undertestdir} > /dev/null || die "failed"
cp ${studentsource} "${testdir}"/ || die "failed"
popd > /dev/null

info "- copy cleanroom sources"
mkdir cleanroom || die "failed"
pushd ../cleanroom > /dev/null || die "failed"
cp ${studentsource} "${testdir}"/cleanroom || die "failed"
popd > /dev/null

info "- copy test sources"
pushd ../junit > /dev/null || die "failed"
cp ${testclass}.java "${testdir}"/ || die "failed"
popd > /dev/null

if [ "x${interfaces}" != "x" ]; then
	info "- copy interfaces"
	if [ -r ../interfaces ]; then
		pushd ../interfaces > /dev/null || die "failed"
	else
		pushd ../skeleton > /dev/null || die "failed"
	fi
	cp ${interfaces} "${testdir}"/ || die "failed"
	popd > /dev/null
fi

info "\nstage0 (student+interfaces only)"
info "- compiling"
( make compile-stage0 ) > /tmp/comp0.$$ 2>&1
checkexit $? "\nstudent result: ☠\n" /tmp/comp0.$$

info "\nstage1 (with public test case)"
info "- compiling"
( make compile-stage1 ) > /tmp/comp1.$$ 2>&1
checkexit $? "\nstudent result: ✘\n" /tmp/comp1.$$

info "- testing"
( make run-stage1 ) > /tmp/run1.$$ 2>&1
checkexit $? "\ninternal error\n" /tmp/run1.$$
cat /tmp/run1.$$ | grep -v "^$$" | tail -2 | grep "OK ("
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/run1.$$
	err "\nstudent result: !\n";
else
	info "\nstudent result: ✔\n";
fi

checkAnnotationFormatError /tmp/run1.$$

info "\nstage2 (twice, with secret test cases and weaving)"
info "- compiling"
( make compile-stage2 ) > /tmp/comp2.$$ 2>&1
checkexit $? "\ninternal error\n" /tmp/comp2.$$

info "- testing"
( make run-stage2 ) > /tmp/run2.out.$$ 2> /tmp/run2.err.$$
if [ $? -ne 0 ]; then
	err "failed, stdout:"
	cat /tmp/run2.out.$$
	err "failed, stderr:"
	cat /tmp/run2.err$$
	die "\ninternal error\n";
fi

checkAnnotationFormatError /tmp/run2.out.$$

info "  json:"
cat /tmp/run2.err.$$

info "- merging"
( java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar JUnitPointsMerger /tmp/run2.err.$$ /tmp/merged.$$ ) > /tmp/merge.$$ 2>&1
checkexit $? "\ninternal error\n" /tmp/merge.$$
cat /tmp/merged.$$

cleanexit
