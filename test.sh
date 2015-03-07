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
cd ${callerdir}
if [ $keep -eq 0 ]; then
	info "\ncleaning up"
	rm -rf test.$$
else
	info "keeping directory"
	ln -sf test.$$ test.latest
fi
if [ $# -gt 0 ]; then
	exit $1
fi
exit 0
}


function checkTestfiles {
	result=$(grep '*@SecretClass' "${testclass}.java")
	result2=$(grep '@tester.annotations.SecretClass' "${testclass}.java")
	if [ "x${result}" != "x" ]  ||  [ "x${result2}" != "x" ]; then
		echo "WARNING - Found SECRETCLASS annotation in public testfile [${testclass}.java]" > pre.err
		cleanexit
	fi

	result=$(grep '@SecretClass' "${secretclass}.java")
	result2=$(grep '@tester.annotations.SecretClass' "${secretclass}.java")
	if [ "x${result}" == "x" ] && [ "x${result2}" == "x" ]; then
		echo "WARNING - Found no SECRETCLASS annotation in secret testfile [${secretclass}.java]" > pre.err
		cleanexit
	fi
	
	result=$(grep '@Exercises' "${secretclass}.java")
	result2=$(grep '@tester.annotations.Exercises' "${secretclass}.java")
	if [ "x${result}" != "x" ] || [ "x${result2}" != "x" ]; then
		echo "WARNING - Found EXERCISES annotation in secret testfile [${secretclass}.java], ignoring"> pre.err
	fi
	
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
	errfile=$(basename $file .out).err
	cat $file | grep -B1 "^java.lang.annotation.AnnotationFormatError" >> $errfile
	die "\ninternal error\n";
fi
}

if [ $# -lt 2 ]; then
	err "no argument given"
	info "usage: $0 [-k] [--single] [-s <SECRETCLASS>] <TESTCLASS> <STUDENTSOURCE1> ... <STUDENTSOURCEn> -- <INTERFACE1> ... <INTERFACEn> --";
	info " e.g.: $0 ExampleTest Student.java -- PublicInterface.java -- undertest*"
	info " -k: keep directory and create symlink test.latest"
	info " -s: specify a secret testclass"
	info " --single: single execution of methods"
	exit -1
fi

keep=0
if [ "x$1" == "x-k" ]; then
	keep=1
	shift
fi
secretclass=
if [ "x$1" == "x-s" ]; then
	shift
	secretclass=$1; shift
	secretclass=$(basename $secretclass ".java")
fi
testclass=$1; shift
testclass=$(basename $testclass ".java")
arg=$1; shift
arg=$(basename $arg)
studentsource=$arg

while [ $# -gt 0 ] && [ "x$1" != "x--" ]; do
	arg=$1; shift
	arg=$(basename $arg)
	studentsource="${studentsource} $arg"
done

shift || true # throw -- away if exists

interfaces=""
while [ $# -gt 0 ] && [ "x$1" != "x--" ]; do
	arg=$1; shift
	arg=$(basename $arg)
	interfaces="${interfaces} $arg"
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
${scriptdir}/install.sh > /dev/null 2> /dev/null || die "failed"

info "- write var.mk"
echo "STUDENTSOURCE = ${studentsource}" > var.mk
echo "TEST = ${testclass}" >> var.mk
echo "INTERFACES = ${interfaces}" >> var.mk
echo "SECRETTEST = ${secretclass}" >> var.mk

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
if [ "x$secretclass" != "x" ]; then
	cp ${secretclass}.java "${testdir}"/ || die "failed"
fi
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


if [ "x$secretclass" != "x" ]; then
	info "- check testfiles"
	checkTestfiles
fi

info "\nstage0 (student+interfaces only)"
info "- compiling"
( make compile-stage0 ) > comp0 2>&1
checkexit $? "\nstudent result: ☠\n" comp0

info "\nstage1 (with public test case)"
info "- compiling"
( make compile-stage1 ) > comp1.out 2> comp1.err
checkexit $? "\nstudent result: ✘\n" comp1.err

info "- testing"
( make run-stage1 ) > run1.out 2> run1.err
ec=$?
cat run1.out run1.err > run1
checkexit $ec "\ninternal error\n" run1
cat run1.out | grep -v "^$$" | grep -v "^make" | tail -2 | grep "OK ("
if [ $? -ne 0 ]; then
	err "failed:"
	cat run1
	err "\nstudent result: !\n";
else
	info "\nstudent result: ✔\n";
fi

checkAnnotationFormatError run1.out

info "\nstage2 (twice, with secret test cases and weaving)"
info "- compiling"
( make compile-stage2 ) > comp2 2>&1
checkexit $? "\ninternal error\n" comp2
if [ "x$secretclass" != "x" ]; then
	( make compile-stage2-secret ) > comp2 2>&1
	checkexit $? "\ninternal error\n" comp2
fi

info "- testing"
( make run-stage2 ) > run2.out 2> run2.err

if [ $? -ne 0 ]; then
	err "failed, stdout:"
	cat run2.out
	err "failed, stderr:"
	cat run2.err
	die "\ninternal error\n";
fi

checkAnnotationFormatError run2.out

info "  json:"
cat run2.err

info "- merging"
if [ "x$secretclass" != "x" ]; then
	( java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar:. -Dpub=$testclass -Dsecret=$secretclass JUnitPointsMerger run2.err merged ) > merge 2>&1
else
	( java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar:. -Dpub=$testclass JUnitPointsMerger run2.err merged ) > merge 2>&1

fi

checkexit $? "\ninternal error\n" merge
cat merged

cleanexit
