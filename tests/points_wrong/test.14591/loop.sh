echo "[" 1>&2
java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:--THIS-WILL-NEVER-HAPPEN:.  -Dreplace=--THIS-WILL-NEVER-HAPPEN -Djson=yes org.junit.runner.JUnitCore UnitTest || echo
echo "]" 1>&2
