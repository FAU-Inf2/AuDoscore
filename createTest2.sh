#!/bin/bash

set -e
set -x

mkdir -p mixed
list=`java -cp lib/junitpoints.jar:replaced:lib/aspectjrt.jar ReadReplace $1 | sort | uniq`
echo "$list" > list;
source list;
rm list;
echo "$list"
li=`echo "$list" | awk '{ print $NF}'`
if [ "x$li" != "x" ]; then
	javac -cp replaced -d replaced $li
fi