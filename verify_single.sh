#!/bin/bash

( ./run_test.sh ) > /dev/null 2> /dev/null

error=0
for i in expected/*; do
	diff -u -I '^make' -I '^Makefile:' $i test.latest/$(basename "$i")
	ec=$?
	error=$((error|ec))
done;

rm -rf $(readlink -f test.latest)
rm test.latest

exit $error
