#!/bin/sh

# FIXME: extract functions to common lib
err() {
	/bin/echo -e "\033[1;31m$1\033[0m" 1>&2
}

die() {
	if test "x" != "x$1" ; then
		err "$1"
	fi
	exit 100
}

checkTestfiles() {

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
	if test  "x${secret1}" = "x"  -a   "x${secret2}" = "x" ; then
		die "WARNING - Found no SECRETCLASS annotation in both testfiles ($file1, $file2)"
	fi
	## check if @SecretClass is present in both files
	if test  "x${secret1}" != "x"   -a  "x${secret2}" != "x" ; then
		die "WARNING - Found SECRETCLASS annotation in both testfiles: ($file1, $file2)"
	elif test  "x${ex1}" != "x"  -a  "x${ex2}" != "x" ; then
		if test "x${secret1}" != "x" ; then
			# file1 is secrettest ignoring @Exercises Annotation in Secrettest
			secretclass=$(basename $file1 ".java")
			testclass=$(basename $file2 ".java")
			err "WARNING - @Exercises specified in $file1 (Secrettest) -- ignoring"
		
		elif test "x${secret2}" != "x" ; then
			# file2 is secret
			secretclass=$(basename $file2 ".java")
			testclass=$(basename $file1 ".java")
			err "WARNING - @Exercises specified in $file2 (Secrettest) -- ignoring"
		fi
	else
		if test "x${secret1}" != "x" ; then
			secretclass=$(basename $file1 ".java")
			testclass=$(basename $file2 ".java")
			/bin/echo -e "TEST=$(basename $file2 .java)\n"
			/bin/echo -e "SECRETTEST=$(basename $file1 .java)\n"
		elif test "x${secret2}" != "x" ; then
			# file2 is secret
			secretclass=$(basename $file2 ".java")
			testclass=$(basename $file1 ".java")
			/bin/echo -e "TEST=$(basename $file1 .java)\n"
			/bin/echo -e "SECRETTEST=$(basename $file2 .java)\n"
		fi
	fi
}

scanTestFiles() {
	# look for junit folder
	junit_folder="junit"
	touch varsec.mk
	if test -d $junit_folder ; then
		## get the files
		files=""
		for entry in "$junit_folder"/*.java
		do	files="$files$entry "
		done
		
		file_count=$(ls -1 $junit_folder/*.java | wc -l)
		
		if test "${file_count}" = "1" ; then
			file1="$(echo "$files" | cut -d ' ' -f 1)"
			sec=$(grep '@*SecretClass' ${file1})
			if [ "x" != "x$sec" ]; then
				echo "SECRETTEST=$(basename ${file1} .java)" > varsec.mk
				echo "TEST=" >> varsec.mk
			else
				echo "TEST=$(basename ${file1} .java)" > varsec.mk
				echo "SECRETTEST=" >> varsec.mk
			fi
			exit
		elif test "${file_count}" = "2" ; then
			file1="$(echo $files | cut -d\  -f 1)"
			file2="$(echo $files | cut -d\  -f 2)"
			checkTestfiles ${file1} ${file2} > varsec.mk
		else
			err " WARNING - Maximum number of testfiles are 2 (Secrettest and Publictest) => abort"
			die
		fi
	else
		/bin/echo -e "WARNING - No junit folder found => abort\n"
		stage=$(cat var.mk | grep STAGE | cut -d= -f2)
		if test "x$stage" != "x0" ; then
			# FIXME: dirty hack in place:
			mkdir junit
			for i in `cat var.mk | grep TEST | cut -d= -f2`; do
				ln -s ../${i}.java junit/${i}.java
			done
			# do it once more
			scanTestFiles
		fi
	fi

}

scanTestFiles
