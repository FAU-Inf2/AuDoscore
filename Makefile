-include var.mk

STUDENTCLASS = $(STUDENTSOURCE:%.java=%)

ifndef SECRETCLASS
	tmp:=$(shell tools/detect_junit.sh)
	include varsec.mk
endif
TESTCLASS = $(TEST:=.class)
TESTSOURCE = $(TEST:=.java)
SECRETCLASS = $(SECRETTEST:=.class)
SECRETSOURCE = $(SECRETTEST:=.java)

all: prepare

verify: prepare
	./verify.sh

miniclean:
	rm -f *.class */*.class

clean:
	rm -rf build
	rm -rf replaced
	rm -rf mixed
	rm -f *.class
	rm -f lib/junitpoints.jar

build:
	rm -rf build
	mkdir -p build

prepare: updategitrev lib/junitpoints.jar

updategitrev:
	git rev-parse HEAD > GITREV

SRCJUNITPOINTSJAR := JUnitWithPoints.java tester/annotations/Replace.java JUnitPointsMerger.java tester/ReadReplace.java ReadForbidden.java ReplaceMixer.java tester/annotations/SecretCase.java tester/annotations/Bonus.java tester/annotations/Ex.java tester/annotations/Points.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/Malus.java tester/annotations/NotForbidden.java tools/jsondiff/JSONDiff.java FullQualifier.java tools/bomacon/BonusMalusConverter.java tools/sep/SingleExecutionPreparer.java CheckAnnotation.java

lib/junitpoints.jar: build $(SRCJUNITPOINTSJAR)
	javac -d build -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/tools.jar:. $(SRCJUNITPOINTSJAR)
	jar cvf lib/junitpoints.jar -C build .

compile-stage0:
	javac $(STUDENTSOURCE)	

compile-stage1: miniclean
	cp $(TEST).java $(TEST).java.orig
	javac -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -proc:only -processor tools.bomacon.BonusMalusConverter $(TEST).java > $(TEST).java.tmp
	mv $(TEST).java.tmp $(TEST).java
	javac -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -proc:only -processor FullQualifier $(TEST).java > $(TEST).java.tmp
	mv $(TEST).java.tmp $(TEST).java
#	sed -i -e 's/@tester.annotations.SecretCase/@org.junit.Ignore/' $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mv $(TEST).java.orig $(TEST).java
	java -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar:. CheckAnnotation $(TEST)
	java -cp lib/junitpoints.jar:. ReadForbidden $(TEST) > forbidden
	chmod +x forbidden
	! ( javap -p -c $(STUDENTCLASS) | ./forbidden 1>&2 )
	rm forbidden

compile-stage2: miniclean
	cp $(TEST).java $(TEST).java.orig
	javac -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -proc:only -processor tools.bomacon.BonusMalusConverter $(TEST).java > $(TEST).java.tmp
	mv $(TEST).java.tmp $(TEST).java
	javac -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -proc:only -processor FullQualifier $(TEST).java > $(TEST).java.tmp
	mv $(TEST).java.tmp $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mkdir -p mixed || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace $(TEST) > compile2.sh || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	if [ "x$(INTERFACES)" != "x" ]; then \
		for i in $(INTERFACES); do \
			/bin/echo -e "package cleanroom;\n" > cleanroom/$$i; \
			cat $$i >> cleanroom/$$i; \
		done; \
	fi
	sh -e ./compile2.sh || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mv $(TEST).java.orig $(TEST).java
	echo "echo \"[\" 1>&2" > loop.sh
	if [ "x$(SECRETTEST)" != "x" ]; then \
		java -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar:. -Dpub=$(TEST) CheckAnnotation $(SECRETTEST) ; \
		java -cp lib/junitpoints.jar:lib/junit.jar:. -DwithSecret=yes tester.ReadReplace --loop $(TEST) >> loop.sh ; \
		echo "echo \",\" 1>&2" >> loop.sh ; \
		make compile-stage2-secret ; \
		java -cp lib/tools.jar:lib/junitpoints.jar:lib/junit.jar:. tools.sep.SingleExecutionPreparer "lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:." -Djson=yes $(TEST) > single_execution.sh ;\
		echo "echo \",\" 1>&2" >> single_execution.sh;	\
		java -cp lib/tools.jar:lib/junitpoints.jar:lib/junit.jar:. tools.sep.SingleExecutionPreparer "lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:." "-Djson=yes -Dpub=$(TEST)" $(SECRETTEST) >> single_execution.sh; \
	else \
		java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace --loop $(TEST) >> loop.sh ; \
		echo "echo \"]\" 1>&2" >> loop.sh ; \
		java -cp lib/tools.jar:lib/junitpoints.jar:lib/junit.jar:. tools.sep.SingleExecutionPreparer "lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:." -Djson=yes $(TEST) > single_execution.sh ; \
	fi		

compile-stage2-secret:
	./obfuscate
	cp $(SECRETTEST).java $(SECRETTEST).java.orig
	javac -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -proc:only -processor tools.bomacon.BonusMalusConverter $(SECRETTEST).java > $(SECRETTEST).java.tmp
	mv $(SECRETTEST).java.tmp $(SECRETTEST).java
	javac -cp lib/tools.jar:lib/junit.jar:lib/junitpoints.jar -proc:only -processor FullQualifier $(SECRETTEST).java > $(SECRETTEST).java.tmp
	mv $(SECRETTEST).java.tmp $(SECRETTEST).java
	make -B $(SECRETCLASS) || ( mv $(SECRETTEST).java.orig $(SECRETTEST).java; /bin/false; )
	mkdir -p mixed || ( mv $(SECRETTEST).java.orig $(SECRETTEST).java; /bin/false; )
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace $(SECRETTEST) > compile2.sh || ( mv $(SECRETTEST).java.orig $(SECRETTEST).java; /bin/false; )
	if [ "x$(INTERFACES)" != "x" ]; then \
		for i in $(INTERFACES); do \
			/bin/echo -e "package cleanroom;\n" > cleanroom/$$i; \
			cat $$i >> cleanroom/$$i; \
		done; \
	fi
	sh -e ./compile2.sh || ( mv $(SECRETTEST).java.orig $(SECRETTEST).java; /bin/false; )
	make -B $(SECRETCLASS) || ( mv $(SECRETTEST).java.orig $(SECRETTEST).java; /bin/false; )
	mv $(SECRETTEST).java.orig $(SECRETTEST).java
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace --loop -p $(TEST) $(SECRETTEST) >> loop.sh	
	echo "echo \"]\" 1>&2" >> loop.sh

compile: compile-stage$(STAGE)

run-stage0:
	echo "alles gut"

run-stage1:
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo

run-stage2:
	echo "{ \"vanilla\" : " 1>&2
	echo "[" 1>&2
	sh ./single_execution.sh
	echo "]" 1>&2
	echo ", \"replaced\" : " 1>&2
	sh ./loop.sh
	echo "}" 1>&2

run: run-stage$(STAGE)


$(TESTCLASS): $(TESTSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE) $(INTERFACES)

$(SECRETCLASS): $(SECRETSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(SECRETSOURCE) $(STUDENTSOURCE) $(INTERFACES)
