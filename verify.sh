#!/bin/bash

count=0
start=`date +%s%N`
export failed=$(mktemp)
export failfile=$(mktemp)

run_single() {
	i=$1
	pushd $i > /dev/null 2> /dev/null
	echo -n "?"
	tmpfailfile=$(mktemp)
	echo "* $i:" >> $tmpfailfile
	../../verify_single.sh &>> $tmpfailfile
	if [ $? -eq 0 ]; then
		echo -n -e "\b."
	else
		echo -n -e "\bE"
		echo -n "." >> $failed
		cat $tmpfailfile >> $failfile
		echo >> $failfile
	fi
	rm $tmpfailfile
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

failedCnt=$(cat $failed | wc -c)
rm $failed

elapsed=`echo "scale=3; ($end - $start) / 1000000000" | bc`
echo -e "\n$failedCnt/$count test(s) failed | Time: $elapsed s"

if [ $failedCnt -gt 0 ]; then
	exit 1
fi
exit 0
