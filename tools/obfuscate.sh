#!/bin/bash

# FIXME: this is a dirty workaround for NOrberts exercise
mkdir -p cleanroom

if [ -n "$AUDOSCORE_SECURITY_TOKEN" ]; then
	export TOKEN=${AUDOSCORE_SECURITY_TOKEN}
else
	export DATE=`date +%s`;
	export MD5=`md5sum lib/*jar cleanroom/*java | tail -1 | awk '{print $1;}'`;
	export TOKEN=${MD5}${DATE}
fi
sed -i -e "s|__clean|__clean${TOKEN}|g" cleanroom/*java
