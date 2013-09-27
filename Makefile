include var.mk

TESTCLASSASPECT = $(TEST:=.class.aspect)
TESTCLASS = $(TEST:=.class)
TESTSOURCE = $(TEST:=.java)
all: lib/junitpoints.jar lib/parser.jar

clean:
	rm -rf build
	rm -rf replaced
	rm -f *.class
	rm -f lib/junitpoints.jar
	rm -f lib/parser.jar
	rm -f mergedcomment.txt
	rm -f result.json
	ant -f Parser/build.xml clean

build:
	rm -rf build
	mkdir -p build

compile-stage0:
	javac $(STUDENTSOURCE)	

compile-stage1: lib/junitpoints.jar lib/parser.jar $(TESTCLASS)

compile-stage2: lib/junitpoints.jar lib/parser.jar $(TESTCLASS) $(TESTCLASSASPECT)
	./createTest2.sh $(TEST)

compile: compile-stage$(STAGE)

run-stage0:
	echo "alles gut"

run-stage1:
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo

run-stage2:
	echo "{ \"vanilla\" : " 1>&2
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
	echo ", \"replaced\" : " 1>&2
	java -cp lib/json-simple-1.1.1.jar:lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectreplacer.jar:replaced -Dreplace=yes -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
	echo "}" 1>&2

run: run-stage$(STAGE)


$(TESTCLASS): lib/junitpoints.jar $(TESTSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE)

test: $(TESTCLASS)
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. org.junit.runner.JUnitCore $(TEST)

$(TESTCLASSASPECT): lib/junitpoints.jar $(TESTSOURCE) $(STUDENTSOURCE) 
	ajc -Xreweavable -d replaced -1.7 -cp lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE) tester/Factory.java asp/AllocFactoryAspect.java

test2: $(TESTCLASSASPECT) 
	./createTest2.sh $(TEST)
	java -cp lib/json-simple-1.1.1.jar:lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectreplacer.jar:replaced -Dreplace=yes org.junit.runner.JUnitCore $(TEST)

result.json: $(TESTCLASS) $(TESTCLASSASPECT)
	./createTest2.sh $(TEST)
	echo "{ \"vanilla\" : " > result.json
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) 2>> result.json || /bin/true
	echo ", \"replaced\" : " >> result.json
	java -cp lib/json-simple-1.1.1.jar:lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectreplacer.jar:replaced -Dreplace=yes -Djson=yes org.junit.runner.JUnitCore $(TEST) 2>> result.json || /bin/true
	echo "}" >> result.json

mergedcomment.txt: result.json lib/junitpoints.jar
	java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar JUnitPointsMerger result.json mergedcomment.txt

.PHONY: lib/parser.jar
