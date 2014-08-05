#!/bin/bash

( ./run_test.sh ) > /dev/null 2> /dev/null

error=0
for i in `find expected/ -type f`; do
	testfile=${i/expected/test.latest}
	sed -i -e 's/Exception(test timed out after \([^ ]*\) milliseconds): [^"]*/TimeoutException after \1 ms/' $i
	diff -u -I '^make' -I '^Makefile:' $i $testfile
	ec=$?
	error=$((error|ec))
done;

rm -rf $(readlink -f test.latest)
rm test.latest

exit $error
