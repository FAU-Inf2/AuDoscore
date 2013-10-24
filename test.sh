#!/bin/bash

function info {
	echo -e "\033[1;34m$1\033[0m"
}

function err {
	echo -e "\033[1;31m$1\033[0m"
}

if [ $# -lt 2 ]; then
	err "no argument given, usage: $0 <TESTCLASS> <STUDENTSOURCE1> ... <STUDENTSOURCEn> ... <INTERFACE1> ... <INTERFACEn>";
	exit -1
fi

testclass=$1; shift

info "stage0 (student+interfaces only)"
info "- compiling"
( make compile-stage0 ) > /tmp/comp0.$$ 2>&1
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/comp0.$$
	err "\nstudent result: ☠\n";
	exit -1
fi

info "\nstage1 (with public test case)"
info "- compiling"
( make compile-stage1 ) > /tmp/comp1.$$ 2>&1
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/comp1.$$
	err "\nstudent result: ✘\n";
	exit -1
fi

info "- testing"
( make run-stage1 ) > /tmp/run1.$$ 2>&1
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/run1.$$
	err "\ninternal error\n";
	exit -1
fi
cat /tmp/run1.$$ | grep -v "^$$" | tail -2 | grep "OK ("
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/run1.$$
	err "\nstudent result: !\n";
else
	info "\nstudent result: ✔\n";
fi

info "\nstage2 (twice, with secret test cases and weaving)"
info "- compiling"
( make compile-stage2 ) > /tmp/comp2.$$ 2>&1
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/comp2.$$
	exit -1
fi

info "- testing"
( make run-stage2 ) > /tmp/run2.out.$$ 2> /tmp/run2.err.$$
if [ $? -ne 0 ]; then
	err "failed, stdout:"
	cat /tmp/run2.out.$$
	err "failed, stderr:"
	cat /tmp/run2.err$$
	err "\ninternal error\n";
	exit -1
fi

info "  json:"
cat /tmp/run2.err.$$

info "- merging"
( java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar JUnitPointsMerger /tmp/run2.err.$$ /tmp/merged.$$ ) > /tmp/merge.$$ 2>&1
if [ $? -ne 0 ]; then
	err "failed:"
	cat /tmp/merge.$$
	err "\ninternal error\n";
	exit -1
fi
cat /tmp/merged.$$
