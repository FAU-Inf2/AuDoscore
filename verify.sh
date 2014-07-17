#!/bin/bash

count=0
failed=0
start=`date +%s%N`
 
for i in tests/*; do
	count=$((count + 1))
	pushd $i > /dev/null 2> /dev/null
	echo -n "?"
	../../verify_single.sh
	if [ $? -eq 0 ]; then
		echo -n -e "\b."
	else
		echo -n -e "\bE"
		failed=$((failed+1))
	fi
	popd > /dev/null 2> /dev/null
done

end=`date +%s%N`
elapsed=`echo "scale=3; ($end - $start) / 1000000000" | bc`
echo " | $failed/$count test(s) failed | Time: $elapsed s"

if [ $failed -gt 0 ]; then
	exit 1
fi
exit 0
