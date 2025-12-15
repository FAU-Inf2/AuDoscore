MAKEFLAGS += --no-print-directory

LIBJUNITPOINTS=lib/junitpoints.jar
LIBJUNIT=lib/junit.jar
LIBJSONSIMPLE=lib/json-simple.jar
LIBALL=$(LIBJUNITPOINTS):$(LIBJUNIT):$(LIBJSONSIMPLE)


all: help

build: clean
	mkdir -p build
	make $(LIBJUNITPOINTS)

verify: $(LIBJUNITPOINTS)
	./tools/verify.sh

clean: miniclean
	rm -rf build
	rm -f $(LIBJUNITPOINTS)

maxiclean: clean
	rm -rf lib


miniclean:
	rm -rf *.class

SRCJUNITPOINTSJARJAVA := \
	tester/annotations/CompareInterface.java tester/annotations/Ex.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/InitializeOnce.java tester/annotations/NotForbidden.java tester/annotations/Points.java tester/annotations/Replace.java tester/annotations/SecretClass.java \
	tester/tools/CheckAnnotation.java tester/tools/ForbiddenUseSearcher.java tester/tools/InitializeOnceHandler.java tester/tools/InterfaceComparator.java tester/tools/JUnitWithPoints.java tester/tools/PointsMerger.java tester/tools/ReplaceManager.java tester/tools/ReplaceMixer.java tester/tools/SingleExecutionPreparer.java \
	tools/DiffJSON.java

$(LIBJUNIT):
	mkdir -p lib/
	wget -O $(LIBJUNIT) https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/6.0.1/junit-platform-console-standalone-6.0.1.jar

$(LIBJSONSIMPLE):
	mkdir -p lib/
	wget -O $(LIBJSONSIMPLE) https://repo1.maven.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/json-simple-1.1.1.jar

$(LIBJUNITPOINTS): $(LIBJUNIT) $(LIBJSONSIMPLE) $(SRCJUNITPOINTSJARJAVA)
	javac -source 25 -target 25 -encoding UTF-8 -d build -cp $(LIBJUNIT):$(LIBJSONSIMPLE):. $(SRCJUNITPOINTSJARJAVA)
	jar cvf $(LIBJUNITPOINTS) -C build .


-include var.mk
MAKEDIR=$(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PUBLICTEST=$(basename $(PUBLICTESTSOURCE))# remove suffix (e.g. ".java"/".scala")
SECRETTEST=$(basename $(SECRETTESTSOURCE))# remove suffix (e.g. ".java"/".scala")

SHELL=/bin/sh
ifneq ("$(wildcard /bin/dash)","")
	SHELL=/bin/dash
endif

compile-stage0:
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName) $(sutDirName)/*.java

compile-stage1: miniclean compile-stage0
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(junitDirName):$(interfacesDirName):$(sutDirName) -cp $(LIBALL) $(junitDirName)/$(PUBLICTESTSOURCE)
	java -cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) tester.tools.CheckAnnotation $(PUBLICTEST)
	java -cp $(LIBALL) tester.tools.ForbiddenUseSearcher $(PUBLICTEST) > forbidden.out
	if [ -s forbidden.out ]; then \
		cat forbidden.out 1>&2 ; \
		exit 1 ; \
	fi
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName) $(cleanroomDirName)/*.java
	make run-comparer

compile-stage2: miniclean compile-stage1
	if [ "x$(SECRETTEST)" != "x" ]; then \
		make compile-stage2-secret ; \
	fi

compile-stage2-secret:
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(junitDirName):$(interfacesDirName):$(sutDirName) -cp $(LIBALL) $(junitDirName)/$(SECRETTESTSOURCE)
	java -cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) -Dpub=$(PUBLICTEST) tester.tools.CheckAnnotation $(SECRETTEST)
	java -cp $(LIBALL) tester.tools.SingleExecutionPreparer "$(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName)" "-Djson=yes -Dpub=$(PUBLICTEST)" $(SECRETTEST) >> single_execution.sh
	java -cp $(LIBALL) tester.tools.ReplaceManager $(SECRETTEST)
	java -cp $(LIBALL) tester.tools.ReplaceManager --loop $(PUBLICTEST) $(SECRETTEST) >> loop.sh

compile: compile-stage$(STAGE)


run-comparer:
	java -cp $(LIBALL) tester.tools.InterfaceComparator $(PUBLICTEST)


run-stage0:
	echo "alles gut"

run-stage1:
	@java -XX:-OmitStackTraceInFastThrow -Xmx1024m -Djson=yes \
		-cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) \
		org.junit.platform.console.ConsoleLauncher execute \
		--disable-banner --details=none --fail-if-no-tests --reports-dir=reports \
		-c $(PUBLICTEST) \
		1>/dev/null 2>/dev/null; echo $$? > run1.exit

run-stage2:
	echo "{ \"vanilla\" : " > run2.err
	echo "[" >> run2.err
	cat run1.exit >> run2.exit
	cat run1.err >> run2.err
	echo "" >> run2.err
	if [ -s single_execution.sh ]; then \
		$(SHELL) ./single_execution.sh; echo $$? >> run2.exit; \
	fi
	echo "]" >> run2.err
	echo ", \"replaced\" : " >> run2.err
	echo "[" >> run2.err
	if [ -s loop.sh ]; then \
		$(SHELL) ./loop.sh; echo $$? >> run2.exit; \
	fi
	echo "]" >> run2.err
	echo "}" >> run2.err

run: run-stage$(STAGE)


help:
	@echo '=========================================================================='
	@echo 'Welcome to AuDoscore/ScExFuSS - The Grading System for Java/Scala Homework'
	@echo '--------------------------------------------------------------------------'
	@echo 'This Makefile serves different purposes, but it is NOT used directly for the grading itself.'
	@echo 'Running test cases for grading is done through the test.sh shell script (see README for details).'
	@echo '--------------------------------------------------------------------------'
	@echo 'Developers/Users use this Makefile from the project root folder to make:'
	@echo '- build:     freshly build the main library'
	@echo '- verify:    run all provided tests from the tests folder'
	@echo '- clean:     remove all generated artifacts (including the main grading library)'
	@echo '- maxiclean: remove all generated and downloaded artifacts (including all libraries)'
	@echo '--------------------------------------------------------------------------'
	@echo 'Testers use this Makefile from one of the provided test folders to make:'
	@echo '- test_run:     run the test in the current folder (keeping temporary execution folder)'
	@echo '- test_verify:  run and compare results of current test against its historical execution'
	@echo '- test_REBUILD: run and update historical execution of current test to its new results'
	@echo '- test_clean:   remove temporary execution folder (i.e. generated artifacts)'
	@echo '--------------------------------------------------------------------------'
	@echo 'All other make targets are used by the test.sh shell script and should not be called directly.'
	@echo 'Enjoy!'
	@echo '=========================================================================='


test_run:
	$(MAKEDIR)/test.sh -k --replace-error

test_verify:
	$(MAKEDIR)/tools/verify_single.sh

test_REBUILD:
	export REBUILD="X"; $(MAKEDIR)/tools/verify_single.sh

test_clean:
ifneq ("$(wildcard test.latest)","")
	rm -rf $(shell readlink -f test.latest)
	rm test.latest
else ifneq ("$(wildcard test.*)","")
	@echo "Found orphan test folder not symlinked by test.latest! You may want to remove it manually..."
	@exit 1
endif
