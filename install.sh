#!/bin/bash

callerdir=${PWD}
script=$(readlink -f $0)
scriptdir=$(dirname $script)

set -x
set -e

# first delete everything unwanted
find . -type f | grep -v -F -x -f ${scriptdir}/files_to_install | xargs rm -f

for f in `cat ${scriptdir}/files_to_install`; do
	# create dir
	base=$(dirname $f)
	mkdir -p ${base}

	# then copy
	cp ${scriptdir}/$f ${callerdir}/${base}
done

