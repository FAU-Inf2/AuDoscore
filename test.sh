#!/bin/bash

LIBJUNITPOINTS=lib/junitpoints.jar
LIBJUNIT=lib/junit.jar
LIBHAMCREST=lib/hamcrest-core.jar
LIBJSONSIMPLE=lib/json-simple-1.1.1.jar
LIBALL=$LIBJUNITPOINTS:$LIBJUNIT:$LIBHAMCREST:$LIBJSONSIMPLE

callerDir=${PWD}
scriptFile=$(readlink -f $0)
scriptDir=$(dirname $scriptFile)

COMPILER_ARGS="$COMPILER_ARGS -g -parameters"

interfacesDirName="interfaces"
cleanroomDirName="cleanroom"
junitDirName="junit"
sutDirName="student"

function info {
	echo -e "\033[1;34m$1\033[0m"
}

function err {
	echo -e "\033[1;31m$1\033[0m"
}

function cleanExit {
	cd ${callerDir}
	if [ $keep -eq 0 ]; then
		info "\ncleaning up"
		rm -rf test.$$
	else
		info "keeping directory"
		rm -f test.latest
		ln -s test.$$ test.latest
	fi
	if [ $# -gt 0 ]; then
		exit $1
	fi
	exit 0
}

function die {
	err "$1"
	cleanExit -1
}

function checkExit {
	exitcode=$1; shift
	msg=$1; shift
	file=$1; shift
	if [ ${exitcode} -ne 0 ]; then
		err "failed:"
		cat ${file}
		die "${msg}"
	fi
}

function checkTestFiles {
	file1=$1; shift
	file2=$1;
	# locate the annotations
	secret1=$(grep '@*SecretClass' $file1)
	secret2=$(grep '@*SecretClass' $file2)
	ex1=$(grep '@*Exercises' $file1)
	ex2=$(grep '@*Exercises' $file2)
	# identify test files
	secTestFile=
	pubTestFile=
	## one file should have the @SecretClass annotation
	if [ "x${secret1}" == "x" ] && [ "x${secret2}" == "x" ]; then
		err "WARNING - Found no SecretClass annotation in both test files"
		cleanExit
	fi
	## check if @SecretClass is present in both files
	if [ "x${secret1}" != "x" ] && [ "x${secret2}" != "x" ]; then
		err "WARNING - Found SecretClass annotation in both test files: $file1 $file2"
		cleanExit
	elif [ "x${ex1}" != "x" ] && [ "x${ex2}" != "x" ]; then
		if [ "x${secret1}" != "x" ]; then
			# file1 is secretTest ignoring @Exercises annotation in secretTest
			secTestFile=$(basename $file1 ".java")
			pubTestFile=$(basename $file2 ".java")
			err "WARNING - @Exercises specified in $file1 (SecretTest) -- ignoring"
		elif [ "x${secret2}" != "x" ]; then
			# file2 is secretTest ignoring @Exercises annotation in secretTest
			secTestFile=$(basename $file2 ".java")
			pubTestFile=$(basename $file1 ".java")
			err "WARNING - @Exercises specified in $file2 (SecretTest) -- ignoring"
		fi
	else
		if [ "x${secret1}" != "x" ]; then
			# file1 is secretTest
			secTestFile=$(basename $file1 ".java")
			pubTestFile=$(basename $file2 ".java")
		elif [ "x${secret2}" != "x" ]; then
			# file2 is secretTest
			secTestFile=$(basename $file2 ".java")
			pubTestFile=$(basename $file1 ".java")
		fi
	fi
}

function checkAnnotationFormatError {
	file=$1; shift
	egrep -q "^(Exception in thread \"main\" )?java.lang.annotation.AnnotationFormatError" $file
	if [ $? -eq 0 ]; then
		err "test case format wrong:"
		cat $file
		err "\nSummary:\n";
		cat $file | egrep -B1 "^(Exception in thread \"main\" )?java.lang.annotation.AnnotationFormatError" | sed -e 's/.*ERROR - /ERROR - /'
		errorFile=$(basename $file .out).err
		cat $file | egrep -B1 "^(Exception in thread \"main\" )?java.lang.annotation.AnnotationFormatError" >> $errorFile
		die "\ninternal error\n";
	fi

}

function scanCleanroom {
	first=1
	if [ -d $cleanroomDirName ]; then
		for entry in "$cleanroomDirName"/*; do
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
		cleanExit
	fi
}

function scanInterfaces {
	first=1
	if [ -d $interfacesDirName ]; then
		for entry in "$interfacesDirName"/*; do
			arg=$(basename $entry)
			if [ ${first} -eq 1 ];then
				interfaces=$arg
				first=0
			else
		 		interfaces="${interfaces} $arg"
			fi
		done
	else
		err "WARNING - No interfaces folder found"
	fi
}

function scanJunit {
	# look for junit folder
	if [ -d $junitDirName ]; then
		## get the files
		files=()
		for entry in "$junitDirName"/*; do
			files+=("$entry")
		done
		file_count=$(ls -1 $junitDirName| grep -v ^1 | wc -l)
		if [ "${file_count}" == "1" ]; then
			pubTestFile=$(basename ${files[0]} ".java")
		elif [ "${file_count}" == "2" ]; then
			checkTestFiles ${files[0]} ${files[1]}
		else
			err " WARNING - Maximum number of test files are 2 (SecretTest and PublicTest) => abort"
			die
		fi
	else
		err "WARNING - No junit folder found => abort\n"
		die
	fi
}

function scanSystemUnderTest {
	for entry in ./*; do
		if [ -d $entry ]; then
			base_entry=$(basename $entry)
			if [ "$base_entry" != "cleanroom" ] && [ "$base_entry" != "interfaces" ] && [ "$base_entry" != "junit" ] \
			&& [ "$base_entry" != "skeleton" ] && [ "$base_entry" != "expected" ] && [[ "$base_entry" != "test."* ]]; then
				((systemUnderTestDirCnt++))
				if [ ${systemUnderTestDirCnt} -eq 1 ]; then
					systemUnderTestDirs=$base_entry
				else
					systemUnderTestDirs="${systemUnderTestDirs} $base_entry"
				fi	
			fi	
		fi
	done
}

function testIt {
	systemUnderTestDir=$1
	first_source=1
	for file in "$systemUnderTestDir"/*; do
		arg=$(basename $file)
		if [ ${first_source} -eq 1 ]; then
			studentSource=$arg
			first_source=0
		else
			studentSource="${studentSource} $arg"
		fi
	done

	if [ "${cleanroom}" != "${studentSource}" ]; then
		err "WARNING - cleanroom sources do not match sources under $1"
		cleanExit
	fi

	info "preparing test setup"
	info "- create test dir"
	testDir="${callerDir}/test.$$"
	mkdir "$testDir" || die "failed to create test dir $testDir"
	cd "$testDir" > /dev/null 2> /dev/null

	info "- copy/install test infrastructure"
	${scriptDir}/tools/install.sh > /dev/null 2> /dev/null || die "failed"

	info "- write var.mk"
	echo "interfacesDirName = ${interfacesDirName}" > var.mk
	echo "cleanroomDirName = ${cleanroomDirName}" >> var.mk
	echo "junitDirName = ${junitDirName}" >> var.mk
	echo "sutDirName = ${sutDirName}" >> var.mk
	echo "STUDENTSOURCE = ${studentSource}" >> var.mk
	echo "INTERFACES = ${interfaces}" >> var.mk
	echo "PUBLICTEST = ${pubTestFile}" >> var.mk
	echo "SECRETTEST = ${secTestFile}" >> var.mk

	if [ "x${interfaces}" != "x" ]; then
		info "copy interfaces"
		mkdir "$testDir"/"$interfacesDirName" || die "failed to create test sub-dir $testDir/$interfacesDirName"
		if [ -r ../"$interfacesDirName" ]; then
			pushd ../"$interfacesDirName" > /dev/null || die "failed"
			cp ${interfaces} "${testDir}"/"$interfacesDirName"/ || die "failed"
			popd > /dev/null
		fi
	fi

	info "copy system under test (SUT)"
	mkdir "$testDir"/$sutDirName || die "failed to create test sub-dir $testDir/$sutDirName"
	pushd ../${systemUnderTestDir} > /dev/null || die "failed"
	cp ${studentSource} "${testDir}"/$sutDirName/ || die "failed"
	popd > /dev/null

	info "\nstage0 (compile interfaces and SUT only)"
	info "- compiling"
	( make compile-stage0 ) > comp0 2>&1
	checkExit $? "\nstudent result: ☠\n" comp0

	info "\ncopy cleanroom"
	mkdir "$testDir"/"$cleanroomDirName" || die "failed to create test sub-dir $testDir/$cleanroomDirName"
	pushd ../"$cleanroomDirName" > /dev/null || die "failed"
	cp ${studentSource} "${testDir}"/"$cleanroomDirName" || die "failed"
	popd > /dev/null

	info "copy junit tests"
	mkdir "$testDir"/"$junitDirName" || die "failed to create test sub-dir $testDir/$junitDirName"
	pushd ../"$junitDirName" > /dev/null || die "failed"
	cp ${pubTestFile}.java "${testDir}"/"$junitDirName"/ || die "failed"
	if [ "x$secTestFile" != "x" ]; then
		cp ${secTestFile}.java "${testDir}"/"$junitDirName"/ || die "failed"
	fi
	popd > /dev/null

	info "\nstage1 (run with public test)"
	info "- compiling"
	( make compile-stage1 ) > comp1.out 2> comp1.err
	ec=$?
	checkAnnotationFormatError comp1.err
	checkExit $ec "\nstudent result: ✘\n" comp1.err

	info "- testing"	
	( make run-stage1 ) > run1.out 2> run1.err
	ec=$?
	cat run1.out run1.err > run1
	checkExit $ec "\ninternal error\n" run1
	cat run1.out | grep -v "^$$" | grep -v "^make" | tail -2 | grep "OK ("
	if [ $? -ne 0 ]; then
		err "failed:"
		cat run1
		err "\nstudent result: !\n";
	else
		info "\nstudent result: ✔\n";
	fi

	info "\nstage2 (run twice: with secret test and weaving)"
	info "- compiling"
	( make compile-stage2 ) > comp2 2>&1
	ec=$?
	checkAnnotationFormatError comp2
	checkExit $ec "\ninternal error\n" comp2

	info "- testing"
	( make run-stage2 ) > run2.out 2> run2.err
	ec=$?
	if grep -q "java.lang.NoSuchFieldError:" "run2.out"; then
		checkExit 1 "\ninternal error\n" run2.out
	fi
	if [ $? -ne 0 ]; then
		err "failed, stdout:"
		cat run2.out
		err "failed, stderr:"
		cat run2.err
		die "\ninternal error\n";
	fi

	info "  json:"
	cat run2.err

	info "- merging"
	if [ "x$secTestFile" != "x" ]; then
		( java -cp $LIBALL:$interfacesDirName:$junitDirName:$sutDirName $replace_error -Dpub=$pubTestFile -Dsecret=$secTestFile tester.tools.PointsMerger run2.err merged ) > merge 2>&1
	else
		( java -cp $LIBALL:$interfacesDirName:$junitDirName:$sutDirName $replace_error -Dpub=$pubTestFile tester.tools.PointsMerger run2.err merged ) > merge 2>&1
	fi
	checkExit $? "\ninternal error\n" merge
	cat merged

	cleanExit
}

keep=0
if [ "x$1" == "x-k" ]; then
	keep=1
	shift
fi

replace_error=""
if [ "x$1" == "x--replace-error" ]; then
	replace_error="-DreplaceError=true"
	echo -e "\033[1;33mINFO: option --replace-error is set.\033[0m"
	shift
fi


info "scanning cleanroom"
cleanroom=""
scanCleanroom

info "scanning interfaces"
interfaces=""
scanInterfaces

info "scanning junit"
secTestFile=""
pubTestFile=""
scanJunit

# info "scanning skeleton"
# nothing TODO

if [ "$#" -eq 0 ]; then
	info "scanning system under test (SUT) - collecting automatically"
	systemUnderTestDirCnt=0
	systemUnderTestDirs=""
	scanSystemUnderTest
else
	info "scanning system under test (SUT) - using args from commandline"
	systemUnderTestDirCnt=$#
	systemUnderTestDirs=$@
fi

systemUnderTestDir="undertest"
if [ ${systemUnderTestDirCnt} -eq 1 ]; then
	systemUnderTestDir=${systemUnderTestDirs}
	testIt ${systemUnderTestDir}
elif [ ${systemUnderTestDirCnt} -gt 1 ]; then
	for i in ${systemUnderTestDirs}; do
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
