#!/bin/bash

# FIXME: extract functions to common lib
function err {
	echo -e "\033[1;31m$1\033[0m"
}

function die {
	err "$1"
	exit -1
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
		die "WARNING - Found no SECRETCLASS annotation in both testfiles ($file1, $file2)"
	fi
	## check if @SecretClass is present in both files
	if [ "x${secret1}" != "x" ] && [ "x${secret2}" != "x" ]; then
		die "WARNING - Found SECRETCLASS annotation in both testfiles: ($file1, $file2)"
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
			echo -e "TEST=$(basename $file2 .java)\n"
			echo -e "SECRETTEST=$(basename $file1 .java)\n"
		elif [ "x${secret2}" != "x" ]; then
			# file2 is secret
			secretclass=$(basename $file2 ".java")
			testclass=$(basename $file1 ".java")
			echo -e "TEST=$(basename $file1 .java)\n"
			echo -e "SECRETTEST=$(basename $file2 .java)\n"
		fi
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
			die "found only one class"
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

scanTestFiles > varsec.mk
