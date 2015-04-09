#!/bin/bash

declare -a annotations=("@*Bonus(*" "@*Exercises({*" "@*Forbidden(*" "@*Malus(*" "@*NotForbidden(*" "@*Points(*" "@*SecretClass(*" "@Rule" "@ClassRule" "import tester.*;" "public final PointsLogger pointLogger = new PointLogger();" "public final static PointsSummary = new PointsSummary()" "extends JUnitWithPoints" "import org.junit.rules.*;" "import java.lang.reflect.*")

function clean_file {
	file=$1
	if [[ $file == *.java ]];then
		for i in "${annotations[@]}"; do
			sed -i "/${i}/d" $file
		done
	fi
}

function recursive_clean {
	start_dir=$1
	for file in "$start_dir"/*; do
		if [ ! -d "$file" ]; then
			clean_file $file
		else
			recursive_clean $file
		fi
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
		recursive_clean $arg
	else
		for file in "$arg"; do
			if [ -f $file ]; then
				clean_file $arg
			fi
		done
	fi
elif [ -f $arg ]; then
	clean_file $arg
else
	echo "$arg is not valid"
	exit 1
fi
