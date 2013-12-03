include var.mk

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
	rm -f mergedcomment.txt
	rm -f result.json


miniclean:
	rm -f *.class */*.class

build:
	rm -rf build
	mkdir -p build

prepare: lib/junitpoints.jar

SRCJUNITPOINTSJAR := JUnitWithPoints.java tester/Replace.java JUnitPointsMerger.java tester/ReadReplace.java ReadForbidden.java CheckMustUse.java tester/MustUse.java tester/MustNotUse.java tester/UsageRestriction.java ReplaceMixer.java

lib/junitpoints.jar: build $(SRCJUNITPOINTSJAR)
	javac -d build -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/tools.jar:. $(SRCJUNITPOINTSJAR)
	jar cvf lib/junitpoints.jar -C build .

compile-stage0:
	javac $(STUDENTSOURCE)	

compile-stage1: miniclean
	cp $(TEST).java $(TEST).java.orig
	( /bin/echo -e "import org.junit.*;\n import tester.*;\n" ; cat $(TEST).java.orig ) > $(TEST).java
	sed -i -e 's/@SecretCase/@Ignore/' $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mv $(TEST).java.orig $(TEST).java
	java -cp lib/junitpoints.jar:. ReadForbidden $(TEST) > forbidden
	chmod +x forbidden
	! ( javap -p -c $(STUDENTCLASS) | ./forbidden )
	rm forbidden

compile-stage2: miniclean
	cp $(TEST).java $(TEST).java.orig
	( /bin/echo -e "import org.junit.*;\n import tester.*;\n" ; cat $(TEST).java.orig ) > $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	./createTest2.sh $(TEST) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mv $(TEST).java.orig $(TEST).java
	java -cp lib/json-simple-1.1.1.jar:lib/junitpoints.jar:. CheckMustUse $(TEST) > checkMustUse.report

compile: compile-stage$(STAGE)

run-stage0:
	echo "alles gut"

run-stage1:
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo

run-stage2:
	echo "{ \"vanilla\" : " 1>&2
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -DMustUseDeductionJSON=yes -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
	echo ", \"replaced\" : " 1>&2
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace --loop $(TEST) > loop.sh	
	bash ./loop.sh
	echo "}" 1>&2

run: run-stage$(STAGE)


$(TESTCLASS): $(TESTSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE)
