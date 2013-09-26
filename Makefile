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

lib/parser.jar:
	ant -f Parser/build.xml
	cp Parser/parser.jar lib/

compile: lib/junitpoints.jar lib/parser.jar $(TESTCLASS) $(TESTCLASSASPECT)
	./createTest2.sh $(TEST)

run:
	echo "{ \"vanilla\" : " 1>&2
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
	echo ", \"replaced\" : " 1>&2
	java -cp lib/json-simple-1.1.1.jar:lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectreplacer.jar:replaced -Dreplace=yes -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
	echo "}" 1>&2

build/JUnitWithPoints.class: build JUnitWithPoints.java Replace.java JUnitPointsMerger.java ReadReplace.java
	javac -d build -cp lib/json-simple-1.1.1.jar:lib/junit.jar:. JUnitWithPoints.java Replace.java JUnitPointsMerger.java ReadReplace.java

lib/junitpoints.jar: build/JUnitWithPoints.class
	jar cvf lib/junitpoints.jar -C build .

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
