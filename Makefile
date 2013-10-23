include var.mk

TESTCLASSASPECT = $(TEST:=.class.aspect)
TESTCLASS = $(TEST:=.class)
TESTSOURCE = $(TEST:=.java)
STUDENTCLASS = $(STUDENTSOURCE:%.java=%)

all:
	make prepare
	make compile-stage0
	echo -e "\n\n\033[1;31mcompiling student code without tests\033[0m\n\n"
	make run-stage0
	echo -e "\n\n\033[1;31mcompiling student code with main-tests\033[0m\n\n"
	make compile-stage1
	echo -e "\n\n\033[1;31mrunning student code with main-tests\033[0m\n\n"
	make run-stage1 | grep -v "^$$" | tail -2 | grep "OK ("
	echo -e "\n\n\033[1;31mcompiling student code with all tests (vanilla and replaced)\033[0m\n\n"
	make compile-stage2
	echo -e "\n\n\033[1;31mrunning student code with all tests (vanilla and replaced)\033[0m\n\n"
	make run-stage2 2> result.json
	echo -e "\n\n\033[1;31mmerging results of vanilla and replaced\033[0m\n\n"
	java -cp lib/junitpoints.jar:lib/json-simple-1.1.1.jar JUnitPointsMerger result.json mergedcomment.txt
	cat mergedcomment.txt
	rm mergedcomment.txt result.json

clean:
	rm -rf build
	rm -rf replaced
	rm -rf mixed
	rm -f *.class
	rm -f lib/junitpoints.jar
	rm -f lib/parser.jar
	rm -f mergedcomment.txt
	rm -f result.json
	ant -f Parser/build.xml clean

build:
	rm -rf build
	mkdir -p build

prepare: lib/junitpoints.jar lib/parser.jar

lib/junitpoints.jar: build JUnitWithPoints.java Replace.java JUnitPointsMerger.java ReadReplace.java ReadForbidden.java
	javac -d build -cp lib/json-simple-1.1.1.jar:lib/junit.jar:. JUnitWithPoints.java Replace.java JUnitPointsMerger.java ReadReplace.java ReadForbidden.java
	jar cvf lib/junitpoints.jar -C build .

lib/parser.jar:
	ant -f Parser/build.xml
	cp Parser/parser.jar lib/

compile-stage0:
	javac $(STUDENTSOURCE)	

compile-stage1:
	sed -iorig -e 's/@SecretCase/@Ignore/' $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).javaorig $(TEST).java; /bin/false; )
	mv $(TEST).javaorig $(TEST).java
	java -cp lib/junitpoints.jar:. ReadForbidden $(TEST) > forbidden
	chmod +x forbidden
	! ( javap -c $(STUDENTCLASS) | ./forbidden )
	rm forbidden

compile-stage2: $(TESTCLASS) $(TESTCLASSASPECT)
	./createTest2.sh $(TEST)
	make -B $(TESTCLASS)

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


$(TESTCLASS): $(TESTSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE)

$(TESTCLASSASPECT): $(TESTSOURCE) $(STUDENTSOURCE) 
	CLASSPATH="lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectjtools.jar:." java org.aspectj.tools.ajc.Main -Xreweavable -1.7 -d replaced $(TESTSOURCE) $(STUDENTSOURCE) $(INTERFACES) tester/Factory.java asp/AllocFactoryAspect.java

.PHONY: lib/parser.jar
