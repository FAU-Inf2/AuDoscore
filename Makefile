include var.mk

TESTCLASS = $(TEST:=.class)
TESTSOURCE = $(TEST:=.java)
STUDENTCLASS = $(STUDENTSOURCE:%.java=%)
COCO = false
all:
	make prepare
	./test.sh $(TEST) $(STUDENTSOURCE) -- $(INTERFACES) -- student
all-coco:
	make prepare
	export COCO true
	./test.sh $(TEST) $(STUDENTSOURCE) -- $(INTERFACES) -- student
	
verify:
	make prepare
	./verify.sh

verify-coco:
	make prepare
	export COCO true
	./verify.sh
	
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

prepare: updategitrev lib/junitpoints.jar

updategitrev:
	git rev-parse HEAD > GITREV

SRCJUNITPOINTSJAR := JUnitWithPoints.java tester/annotations/Replace.java JUnitPointsMerger.java tester/ReadReplace.java ReadForbidden.java ReplaceMixer.java tester/annotations/Bonus.java tester/annotations/Ex.java tester/annotations/Points.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/Malus.java tester/annotations/NotForbidden.java tester/annotations/SecretCase.java tools/jsondiff/JSONDiff.java FullQualifier.java tools/bomacon/BonusMalusConverter.java

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
	sed -i -e 's/@tester.annotations.SecretCase/@org.junit.Ignore/' $(TEST).java
	make -B $(TESTCLASS) || ( mv $(TEST).java.orig $(TEST).java; /bin/false; )
	mv $(TEST).java.orig $(TEST).java
	java -cp lib/junitpoints.jar:. ReadForbidden $(TEST) > forbidden
	chmod +x forbidden
	! ( javap -p -c $(STUDENTCLASS) | ./forbidden 1>&2 )
	rm forbidden

compile-stage2: miniclean
	./obfuscate
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
	java -cp lib/junitpoints.jar:lib/junit.jar:. tester.ReadReplace --loop $(TEST) > loop.sh	

compile: compile-stage$(STAGE)

run-stage0:
	echo "alles gut"

run-stage1:
ifeq ($(COCO),true)
	java -javaagent:lib/jacocoagent.jar=destfile=jacoco1.exec -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
else
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
endif
		
run-stage2:
	echo "{ \"vanilla\" : " 1>&2
ifeq ($(COCO),true)	
	java -javaagent:lib/jacocoagent=destfile=jacoco2.exec -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
else
	java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo
endif
	echo ", \"replaced\" : " 1>&2
	sh ./loop.sh
	echo "}" 1>&2

run: run-stage$(STAGE)


$(TESTCLASS): $(TESTSOURCE) $(STUDENTSOURCE)
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE) $(INTERFACES)
