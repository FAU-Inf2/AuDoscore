LIBJUNITPOINTS=lib/junitpoints.jar
LIBJUNIT=lib/junit.jar
LIBHAMCREST=lib/hamcrest-core.jar
LIBJSONSIMPLE=lib/json-simple-1.1.1.jar
LIBALL=$(LIBJUNITPOINTS):$(LIBJUNIT):$(LIBHAMCREST):$(LIBJSONSIMPLE)

-include var.mk
PUBLICTESTSOURCE = $(PUBLICTEST:=.java)
SECRETTESTSOURCE = $(SECRETTEST:=.java)

SHELL=/bin/sh
ifneq ("$(wildcard /bin/dash)","")
	SHELL=/bin/dash
endif


all: clean prepare

verify: clean prepare
	./verify.sh

miniclean:
	rm -rf *.class

clean: miniclean
	rm -rf build
	rm -f $(LIBJUNITPOINTS)

build:
	rm -rf build
	mkdir -p build

prepare: lib/junitpoints.jar

SRCJUNITPOINTSJAR := JUnitWithPoints.java PointsLogger.java PointsSummary.java \
	tester/annotations/CompareInterface.java tester/annotations/Ex.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/InitializeOnce.java tester/annotations/NotForbidden.java tester/annotations/Points.java tester/annotations/Replace.java tester/annotations/SecretClass.java \
	tester/tools/CheckAnnotation.java tester/tools/ForbiddenUseSearcher.java tester/tools/InterfaceComparator.java tester/tools/JUnitWithPointsImpl.java tester/tools/PointsMerger.java tester/tools/ReplaceManager.java tester/tools/ReplaceMixer.java tester/tools/SingleExecutionPreparer.java tester/tools/SingleMethodRunner.java \
	tools/DiffJSON.java

lib/junitpoints.jar: build $(SRCJUNITPOINTSJAR)
	javac -source 25 -target 25 -encoding UTF-8 -d build -cp $(LIBJUNIT):$(LIBHAMCREST):$(LIBJSONSIMPLE):. $(SRCJUNITPOINTSJAR)
	jar cvf $(LIBJUNITPOINTS) -C build .


compile-stage0:
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName) $(sutDirName)/*.java

compile-stage1: miniclean compile-stage0
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName) $(cleanroomDirName)/*.java
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName):$(junitDirName):$(sutDirName) -cp $(LIBALL) $(junitDirName)/$(PUBLICTESTSOURCE)
	java -cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) tester.tools.CheckAnnotation $(PUBLICTEST)
	java -cp $(LIBALL) tester.tools.ForbiddenUseSearcher $(PUBLICTEST) > forbidden.out
	if [ -s forbidden.out ]; then \
		cat forbidden.out 1>&2 ; \
		exit 1 ; \
	fi
	make run-comparer

compile-stage2: miniclean compile-stage1
	echo "echo \"[\" 1>&2" > loop.sh
	echo "make run-stage1" > single_execution.sh
	set -e ; \
	if [ "x$(SECRETTEST)" != "x" ]; then \
		make compile-stage2-secret ; \
		java -cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) -Dpub=$(PUBLICTEST) tester.tools.CheckAnnotation $(SECRETTEST) ; \
		echo "echo \",\" 1>&2" >> single_execution.sh;	\
		java -cp $(LIBALL):$(junitDirName) tester.tools.SingleExecutionPreparer "$(LIBALL):$(interfacesDirName):$(junitDirName):$(sutDirName)" "-Djson=yes -Dpub=$(PUBLICTEST)" $(SECRETTEST) >> single_execution.sh; \
	fi ; \
	echo "echo \"]\" 1>&2" >> loop.sh

compile-stage2-secret:
	./obfuscate
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName) $(cleanroomDirName)/*.java
	javac $(COMPILER_ARGS) -Xprefer:source -sourcepath $(interfacesDirName):$(junitDirName):$(sutDirName) -cp $(LIBALL) $(junitDirName)/$(SECRETTESTSOURCE)
	java -cp $(LIBALL) tester.tools.ReplaceManager $(SECRETTEST)
	java -cp $(LIBALL) tester.tools.ReplaceManager --loop $(PUBLICTEST) $(SECRETTEST) >> loop.sh

compile: compile-stage$(STAGE)


run-comparer:
	java -cp $(LIBALL) tester.tools.InterfaceComparator $(PUBLICTEST)

run-stage0:
	echo "alles gut"

run-stage1:
	java -XX:-OmitStackTraceInFastThrow -Xmx1024m \
		-cp $(LIBALL):$(interfacesDirName):$(junitDirName):$(sutDirName) \
		-Djson=yes org.junit.runner.JUnitCore $(PUBLICTEST) || echo

run-stage2:
	echo "{ \"vanilla\" : " 1>&2
	echo "[" 1>&2
	$(SHELL) ./single_execution.sh
	echo "]" 1>&2
	echo ", \"replaced\" : " 1>&2
	$(SHELL) ./loop.sh
	echo "}" 1>&2

run: run-stage$(STAGE)
