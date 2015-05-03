#!/bin/bash

count=0
failed=0
start=`date +%s%N`
failfile=$(tempfile)
 
for i in tests/*; do
	count=$((count + 1))
	pushd $i > /dev/null 2> /dev/null
	echo -n "?"
	tmpfailfile=$(tempfile)
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
done

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
