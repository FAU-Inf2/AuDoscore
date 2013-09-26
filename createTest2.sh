#!/bin/bash

list=`java -cp lib/junitpoints.jar:. ReadReplace $1 | sort | uniq`
echo "$list" > list;
source list;
rm list;
li=`echo $list | awk '{ print $NF}'`
javac $li
