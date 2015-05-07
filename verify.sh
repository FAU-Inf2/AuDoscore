#!/bin/bash

platform=
unamestr=`uname`
if [[ "$unamestr" == "Darwin" ]]; then
	platform="darwin"
fi
count=0
failed=0
start=`date +%s%N`
if [[ "$platform" == "darwin" ]]; then
	failfile=$(mktemp -t tmp)
else
	failfile=$(tempfile)
fi	

run_single() {
	i=$1
	pushd $i > /dev/null 2> /dev/null
	echo -n "?"
	unamestr=`uname`
	if [[ "$unamestr" == "Darwin" ]]; then
		tmpfailfile=$(mktemp -t tmp)
	else
		tmpfailfile=$(tempfile)

	fi
	echo "* $i:" >> $tmpfailfile
	../../verify_single.sh >> $tmpfailfile
	if [ $? -eq 0 ]; then
		echo -n -e "\b."
	else
		echo -n -e "\bE"
		failed=$((failed+1))
		cat $tmpfailfile >> $failfile
		echo >> $failfile
	fi
	popd > /dev/null 2> /dev/null
}

export -f run_single
 
for i in tests/*; do
	count=$((count + 1))
done

parallel --gnu run_single ::: tests/*

end=`date +%s%N`
echo -e "\n"

cat $failfile
rm $failfile

elapsed=`echo "scale=3; ($end - $start) / 1000000000" | bc`
echo -e "\n$failed/$count test(s) failed | Time: $elapsed s"

if [ $failed -gt 0 ]; then
	exit 1
fi
exit 0
