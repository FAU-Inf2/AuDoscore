include var.mk

TESTCLASS = $(TEST:=.class)
TESTSOURCE = $(TEST:=.java)
STUDENTCLASS = $(STUDENTSOURCE:%.java=%)

all:
	make prepare
	./test.sh $(TEST) $(STUDENTSOURCE) -- $(INTERFACES) -- cleanroom

clean:
	rm -rf build
	rm -rf replaced
	rm -rf mixed
	rm -f *.class
	rm -f lib/junitpoints.jar

miniclean:
	rm -f *.class */*.class

build:
	rm -rf build
	mkdir -p build

prepare: lib/junitpoints.jar

SRCJUNITPOINTSJAR := JUnitWithPoints.java tester/Replace.java JUnitPointsMerger.java tester/ReadReplace.java ReadForbidden.java ReplaceMixer.java

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
	./obfuscate
	cp $(TEST).java $(TEST).java.orig
	( /bin/echo -e "import org.junit.*;\n import tester.*;\n" ; cat $(TEST).java.orig ) > $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mkdir -p mixed || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace $(TEST) > compile2.sh || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	sh ./compile2.sh || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mv $(TEST).java.orig $(TEST).java
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace --loop $(TEST) > loop.sh	

compile: compile-stage$(STAGE)

run-stage0:
	echo "alles gut"

run-stage1:
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo

run-stage2:
	echo "{ \"vanilla\" : " 1>&2
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
	echo ", \"replaced\" : " 1>&2
	sh ./loop.sh
	echo "}" 1>&2

run: run-stage$(STAGE)


$(TESTCLASS): $(TESTSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE) $(INTERFACES)
