#!/bin/bash

export AUDOSCORE_SECURITY_TOKEN="2c976d0b02898e9eb05155806bb65973";
( ./run_test.sh ) > /dev/null 2> /dev/null

error=0
for i in `find expected/ -type f`; do
	testfile=${i/expected/test.latest}
	if [[ -r "$testfile" ]]; then
		sed -i -e 's/Exception(test timed out after \([^ ]*\) milliseconds): [^"]*/TimeoutException after \1 ms/g' $testfile
		sed -i -e 's/StackOverflowError(): [^"]*/StackOverflowError()/g' $testfile
		if [[ "$i" == expected/run*.err ]] && [[ -s "$testfile" ]]; then
			# pretty print as json before diffing (if size > 0)
			cat $testfile | python -m json.tool > ${testfile}.new
			if [[ $? -ne 0 ]]; then
				echo -e "Above JSON is broken" >> $testfile
			else
				mv ${testfile}.new $testfile
			fi
		fi
		diff -b -u -I '^make' -I '^Makefile:' $i $testfile
		ec=$?
		if [[ $ec -ne 0 ]] && [[ "$i" == expected/run*.err ]]; then
			# in case of JSON, try to parse and compare as JSON
			java -cp ../../lib/junitpoints.jar:../../lib/json-simple-1.1.1.jar tools.jsondiff.JSONDiff $i $testfile
			ec=$?
		fi
	else
		echo "$testfile does not exist..."
		ec=-1
	fi
	error=$((error|ec))
	if [[ $ec -ne 0 ]] && [[ -n "$REBUILD" ]]; then
		cp $testfile $i
	fi
done;

rm -rf $(readlink -f test.latest)
rm test.latest

exit $error
