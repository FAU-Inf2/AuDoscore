#!/bin/bash -e

export rootDir=$(dirname $(dirname $(readlink -f $0)))
export fails=$(mktemp)
export failFile=$(mktemp)

run_single() {
	i=$1
	pushd $i > /dev/null 2> /dev/null
	echo -n "?"
	failFileTmp=$(mktemp)
	echo "* $i:" >> $failFileTmp
	$rootDir/tools/verify_single.sh &>> $failFileTmp
	if [ $? -eq 0 ]; then
		echo -n -e "\b."
	else
		echo -n -e "\bE"
		echo -n "." >> $fails
		cat $failFileTmp >> $failFile
		echo >> $failFile
	fi
	rm $failFileTmp
	popd > /dev/null 2> /dev/null
}

count=0
for i in tests/*; do
	count=$((count + 1))
done

start=`date +%s%N`
export -f run_single
parallel --gnu run_single ::: tests/*
end=`date +%s%N`

echo -e "\n"
cat $failFile
rm $failFile

failsCnt=$(cat $fails | wc -c)
rm $fails

elapsed=`echo "scale=3; ($end - $start) / 1000000000" | bc`
echo -e "\n$failsCnt/$count test(s) fails | Time: $elapsed s"

if [ $failsCnt -gt 0 ]; then
	exit 1
fi
exit 0
