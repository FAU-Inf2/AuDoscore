#! /usr/bin/env python

import subprocess, sys, re

try:
    classfile = sys.argv[1]
    functionRE = re.compile(sys.argv[2])
    toSearch = re.compile(sys.argv[3])
except (re.error, IndexError) as e:
    print("""usage: %s classfile functionregex searchregex
  Will disassemble classfile, then search for searchregex in every 
  function matching functionregex.

  If searchregex is found, returns 0.
  If searchregex is not found, but a function matchung functionregex, returns 1
  Else returns 2
"""
          % sys.argv[0])
    sys.exit(3)

#display out put line by line
proc = subprocess.Popen(['javap', '-p', '-c', sys.argv[1]],stdout=subprocess.PIPE)

insideWantedFunction = False
foundWantedFunction = False
foundWantedStringInWantedFunction = False
for line in iter(proc.stdout.readline,''):
    if not insideWantedFunction:
        if len(line) >= 3 and line[0:2] == '  ' and line[2] != ' ' \
                and functionRE.search(line):
            insideWantedFunction = True
            foundWantedFunction = True
    else:
        if line.strip() == "":
            insideWantedFunction = False
            continue
        if toSearch.search(line):
            foundWantedStringInWantedFunction = True

if foundWantedStringInWantedFunction:
    sys.exit(0)
elif foundWantedFunction:
    sys.exit(1)
else:
    sys.exit(2)

