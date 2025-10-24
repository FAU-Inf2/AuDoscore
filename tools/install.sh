#!/bin/bash

callerDir=${PWD}
script=$(readlink -f $0)
scriptDir=$(dirname $script)
rootDir=$(dirname $scriptDir)

set -x
set -e

# first delete everything unwanted
find . -type f | grep -v -F -x -f ${scriptDir}/files_to_install | xargs rm -f

for f in `cat ${scriptDir}/files_to_install`; do
	# create dir
	base=$(dirname $f)
	mkdir -p ${base}

	# then copy
	cp ${rootDir}/$f ${callerDir}/${base}
done
