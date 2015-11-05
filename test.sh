#!/bin/bash

# FIXME: breaks json
#if [ "x$1" == "x-v" ]; then
#	set -x
#	shift
#fi
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

	file1=$1; shift
	file2=$1;
	
	# get the annotations
	secret1=$(grep '@*SecretClass' $file1)
	secret2=$(grep '@*SecretClass' $file2)

	ex1=$(grep '@*Exercises' $file1)
	ex2=$(grep '@*Exercises' $file2)

	secretclass=
	testclass=

	## one file should have the @SecretClass annotation
	if [ "x${secret1}" == "x" ] && [ "x${secret2}" == "x" ]; then
		err "WARNING - Found no SECRETCLASS annotation in both testfiles"
		cleanexit
	fi
	## check if @SecretClass is present in both files
	if [ "x${secret1}" != "x" ] && [ "x${secret2}" != "x" ]; then
		err "WARNING - Found SECRETCLASS  annotation in both testfiles: $file1 $file2"
		cleanexit
	elif [ "x${ex1}" != "x" ] && [ "x${ex2}" != "x" ]; then
		if [ "x${secret1}" != "x" ]; then
			# file1 is secrettest ignoring @Exercises Annotation in Secrettest
			secretclass=$(basename $file1 ".java")
			testclass=$(basename $file2 ".java")
			err "WARNING - @Exercises specified in $file1 (Secrettest) -- ignoring"
		
		elif [ "x${secret2}" != "x" ]; then
			# file2 is secret
			secretclass=$(basename $file2 ".java")
			testclass=$(basename $file1 ".java")
			err "WARNING - @Exercises specified in $file2 (Secrettest) -- ignoring"
		fi
	else
		if [ "x${secret1}" != "x" ]; then
			secretclass=$(basename $file1 ".java")
			testclass=$(basename $file2 ".java")
		elif [ "x${secret2}" != "x" ]; then
			# file2 is secret
			secretclass=$(basename $file2 ".java")
			testclass=$(basename $file1 ".java")
		fi
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

function scanTestFiles {
	# look for junit folder
	junit_folder="junit"
	if [ -d $junit_folder ]; then
		## get the files
		files=()
		for entry in "$junit_folder"/*
		do	files+=("$entry")
		done
		
		file_count=$(ls -1 $junit_folder| grep -v ^1 | wc -l)
		
		if [ "${file_count}" == "1" ]; then
			testclass=$(basename ${files[0]} ".java")
		elif [ "${file_count}" == "2" ]; then
			checkTestfiles ${files[0]} ${files[1]}
		else
			err " WARNING - Maximum number of testfiles are 2 (Secrettest and Publictest) => abort"
			die
		fi
	else
		err "WARNING - No junit folder found => abort\n"
		die

	fi
}

function scanInterfaces {
	## look for interfaces folder
	first=1
	interfaces_folder="interfaces"
	if [ -d $interfaces_folder ]; then
		for entry in "$interfaces_folder"/*; do
			arg=$(basename $entry)
			if [ ${first} -eq 1 ];then
				interfaces=$arg
				first=0
			else
		 		interfaces="${interfaces} $arg"
			fi
		done
	else
		err "WARNNING - No interfaces folder found"
	fi
}

function scanCleanroom {
	first=1
	cleanroom_folder="cleanroom"
	if [ -d $cleanroom_folder ]; then
		for entry in "$cleanroom_folder"/*; do
			arg=$(basename $entry)
			if [ ${first} -eq 1 ]; then
				cleanroom=$arg
				first=0
			else
				cleanroom="${cleanroom} $arg"
			fi
		done
	else 
		err "WARNING - No cleanroom folder found"
		cleanexit
	fi


}

function scanStudentSources {
	for entry in ./*; do
		if [ -d $entry ]; then
			base_entry=$(basename $entry)
			if [ "$base_entry" != "junit" ] && [ "$base_entry" != "cleanroom" ] && [ "$base_entry" != "skeleton" ] && [ "$base_entry" != "interfaces" ] && [ "$base_entry" != "expected" ] && [[ "$base_entry" != "test."* ]]; then
				((undertestdircnt++))
				if [ ${undertestdircnt} -eq 1 ]; then
					undertestdirs=$base_entry
				else
					undertestdirs="${undertestdirs} $base_entry"
				fi	
			fi	
		fi
	done

}

function testIt {
	first_source=1
	undertestdir=$1
	for file in "$undertestdir"/*; do
		arg=$(basename $file)
		if [ ${first_source} -eq 1 ]; then
				studentsource=$arg
				first_source=0
		else
			studentsource="${studentsource} $arg"
		fi
	done

	if [ "${cleanroom}" != "${studentsource}" ]; then
		err "WARNING - cleanroom sources do not match sources under $1"
		cleanexit
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

	info "\nstage0 (student+interfaces only)"
	info "- compiling"
	( make compile-stage0 ) > comp0 2>&1
	checkexit $? "\nstudent result: ☠\n" comp0

	info "\nstage1 (with public test case)"
	info "- compiling"
	( make compile-stage1 ) > comp1.out 2> comp1.err
	checkexit $? "\nstudent result: ✘\n" comp1.err

	info "- comparing interfaces of student and cleanroom"
	( make run-comparer ) > inteface.out 2> interface.err
	checkexit $? "\nerror: ✘\n" interface.err
	
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
		( java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar:. $replace_error -Dpub=$testclass -Dsecret=$secretclass JUnitPointsMerger run2.err merged ) > merge 2>&1
	else
		( java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar:. $replace_error -Dpub=$testclass JUnitPointsMerger run2.err merged ) > merge 2>&1

	fi

	checkexit $? "\ninternal error\n" merge
	cat merged

	cleanexit

}

keep=0
if [ "x$1" == "x-k" ]; then
	keep=1
	shift
fi

replace_error=""
if [ "x$1" == "x--replace-error" ]; then
	replace_error="-DreplaceError=true"
	echo "fooo"
	shift
fi


# look for testfiles
info "scanning for testfiles"
secretclass=""
testclass=""
scanTestFiles

info "scanning for skeletons"
## TODO

## get the interfaces
info "scanning for interfaces"
interfaces=""
scanInterfaces

info "scanning for cleanroom"
cleanroom=""
scanCleanroom


if [ "$#" -eq 0 ]; then
	info "scanning for student sources"
	undertestdircnt=0
	undertestdirs=""
	scanStudentSources
else
	info "use args from commandline as student source dirs"
	undertestdircnt=$#
	undertestdirs=$@
fi

undertestdir="undertest"
if [ ${undertestdircnt} -eq 1 ]; then
	undertestdir=${undertestdirs}
	testIt ${undertestdir}
elif [ ${undertestdircnt} -gt 1 ]; then
	for i in ${undertestdirs}; do
		info "[recur test with $i]"
		keep=0
		(testIt $i)
		if [ $? != 0 ]; then
			die "testing $i failed"
		fi
		info "\n----------------------------------------\n"
	done
	exit 0
fi

