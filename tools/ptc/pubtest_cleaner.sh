#!/bin/bash

declare -a annotations=("@*Bonus(*","@*Exercises({*","@*Forbidden(*","@*Malus(*","@*NotForbidden(*","@*Points(*","@*SecretClass(*")

function removeAnnotations {
file=$1
	for annotation in "${annotations[@]}"; do
		sed -i "/${i}/d" $file	
	done
}

recursive=0
arg=$1

if [ "x$arg" == "x-r" ]; then
	recursive=1
	shift
	arg=$1
fi

## check input file? or directory?
if [ -d $arg ]; then
	if [ $recursive -eq 1 ]; then
		for i in "${annotations[@]}"; do
			find $arg -type f -name "*.java" | xargs sed -i "/${i}/d"
		done
	else
		for annotation in "${annotations[@]}"; do
			find $arg -maxdepth 0 -type f -name "*.java" | xargs sed -i "/${i}/d"
		done
	fi
elif [ -f $arg ]; then
	if [[ $arg == *.java ]];then
		removeAnnotations $arg
	fi
else
	echo "$arg is not valid"
	exit 1
fi
