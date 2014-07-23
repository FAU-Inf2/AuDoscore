#!/bin/bash

( ./run_test.sh ) > /dev/null 2> /dev/null

error=0
for i in `find expected -type f`; do
	testfile=${i/expected/test.latest}
	diff -u -I '^make' -I '^Makefile:' $i $testfile
	ec=$?
	error=$((error|ec))
done;

rm -rf $(readlink -f test.latest)
rm test.latest

exit $error
