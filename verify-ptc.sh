#!/bin/bash

start=`date +%s%N`

testdir=$(mktemp -d)
mkdir -p "$testdir"

failoutput=$(mktemp)
touch "$failoutput"

failures=0
successes=0
for t in tests/*; do
	if [ -d "$t/junit" ]; then
		# Check if the test name matches "compile_error" -> if yes, use cleanroom
		echo "$t" | grep -q "compile_error"
		if [ $? -eq 0 ]; then
			use_cleanroom=1
		else
			use_cleanroom=0
		fi
		# Copy all interfaces and the cleanroom to $testdir
		if [ "$use_cleanroom" -eq 0 -a -d "$t/student" ]; then
			cp "$t/student"/* "$testdir"
		else
			cp "$t/cleanroom"/* "$testdir"
		fi
		if [ -d "$t/interfaces" ]; then
			cp "$t/interfaces"/* "$testdir"
		fi

		for testpath in $(find "$t/junit" -iname "*.java"); do
			if grep -qF "IGNORE_FOR_PTC" "$testpath"; then
				: # Skip
			else
				testname=$(basename "$testpath")
				destpath="$testdir/$testname"
				procerr=$(mktemp)
				javac -cp lib/junitpoints.jar -proc:only -processor tools.ptc.PublicTestCleaner "$testpath" \
						> "$destpath" 2>"$procerr"
				if [ $? -eq 0 ]; then
					currentdir=$(pwd)
					comperr=$(mktemp)
					cd "$testdir"
					javac -cp ".:$currentdir/lib/junit.jar" "$testname" >"$comperr" 2>&1
					if [ $? -eq 0 ]; then
						echo -n -e "."
						successes=$(($successes+1))
					else
						echo -n -e "E"
						failures=$(($failures+1))
						echo "* $testpath" >> "$failoutput"
						cat "$comperr" >> "$failoutput"
					fi
					cd "$currentdir"
					rm -f "$comperr"
				else
					echo -n -e "E"
					failures=$(($failures+1))
					echo "* $testpath" >> "$failoutput"
					cat "$procerr" >> "$failoutput"
				fi

				# Cleanup test files and class files
				rm -f "$procerr"
				rm -f "$destpath"
				rm -f "$testdir"/*.class
			fi
		done

		# Cleanup cleanroom and interfaces
		rm -f "$testdir"/*.java
	fi
done

end=`date +%s%N`
echo -e "\n"

cat "$failoutput"

# Clean temporary files
rm -Rf "$testdir"
rm -f "$failoutput"

count=$(($failures+$successes))
elapsed=`echo "scale=3; ($end - $start) / 1000000000" | bc`
echo -e "\n$failures/$count test(s) failed | Time: $elapsed s"

if [ $failures -gt 0 ]; then
	exit 1
fi
exit 0
