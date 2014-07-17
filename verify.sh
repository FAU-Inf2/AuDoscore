#!/bin/bash

count=0
failed=0
start=`date +%s%N`
failmsg=""
 
for i in tests/*; do
	count=$((count + 1))
	pushd $i > /dev/null 2> /dev/null
	echo -n "?"
	msg=$(../../verify_single.sh)
	if [ $? -eq 0 ]; then
		echo -n -e "\b."
	else
		echo -n -e "\bE"
		failed=$((failed+1))
		failmsg="$failmsg $i:\n$msg"
	fi
	popd > /dev/null 2> /dev/null
done

end=`date +%s%N`
echo -e "\n"

echo -e $failmsg

elapsed=`echo "scale=3; ($end - $start) / 1000000000" | bc`
echo -e "\n$failed/$count test(s) failed | Time: $elapsed s"

if [ $failed -gt 0 ]; then
	exit 1
fi
exit 0
