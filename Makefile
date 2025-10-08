LIBJUNITPOINTS=lib/junitpoints.jar
LIBJUNIT=lib/junit.jar
LIBHAMCREST=lib/hamcrest-core.jar
LIBJSONSIMPLE=lib/json-simple-1.1.1.jar
LIBALL=$(LIBJUNITPOINTS):$(LIBJUNIT):$(LIBHAMCREST):$(LIBJSONSIMPLE)

JAVAMODULEEXPORTS=\
    --add-exports=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED \
    --add-exports=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
    --add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
    --add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
    --add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED
JAVAMODULEOPENS=\
    -J--add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED \
    -J--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED \
    -J--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
    -J--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
    -J--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED

-include var.mk

SHELL=/bin/sh
ifneq ("$(wildcard /bin/dash)","")
	SHELL=/bin/dash
endif

PUBLICTESTSOURCE = $(PUBLICTEST:=.java)
PUBLICTESTCLASS = $(PUBLICTEST:=.class)
SECRETTESTSOURCE = $(SECRETTEST:=.java)
SECRETTESTCLASS = $(SECRETTEST:=.class)
INTERFACESOURCE ?= $(filter %.java,$(INTERFACES))

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
	rm -f $(LIBJUNITPOINTS)

build:
	rm -rf build
	mkdir -p build

prepare: updategitrev lib/junitpoints.jar

updategitrev:
	git rev-parse HEAD > GITREV

SRCJUNITPOINTSJAR := JUnitWithPoints.java JUnitPointsMerger.java CheckAnnotation.java tester/annotations/CompareInterface.java tester/annotations/Ex.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/InitializeOnce.java tester/annotations/NotForbidden.java tester/annotations/Points.java tester/annotations/Replace.java tester/annotations/SecretClass.java tools/ForbiddenUseSearcher.java tools/JavaSourcePrettyPrinter.java tools/ReadReplace.java tools/ReplaceMixer.java tools/SingleMethodRunner.java tools/ic/InterfaceComparator.java tools/jsondiff/JSONDiff.java tools/sep/SingleExecutionPreparer.java

lib/junitpoints.jar: build $(SRCJUNITPOINTSJAR)
	javac -source 25 -target 25 $(JAVAMODULEEXPORTS) -encoding UTF-8 -d build -cp $(LIBJUNIT):$(LIBHAMCREST):$(LIBJSONSIMPLE):. $(SRCJUNITPOINTSJAR)
	jar cvf $(LIBJUNITPOINTS) -C build .


compile-stage0:
	javac $(COMPILER_ARGS) -sourcepath $(interfacesDirName) $(sutDirName)/*.java

compile-stage1: miniclean compile-stage0
	javac -Xprefer:source $(COMPILER_ARGS) -sourcepath $(interfacesDirName):$(cleanroomDirName) -cp $(LIBALL) $(cleanroomDirName)/*.java
	javac -Xprefer:source $(COMPILER_ARGS) -sourcepath $(interfacesDirName):$(junitDirName):$(sutDirName) -cp $(LIBALL) $(junitDirName)/$(PUBLICTESTSOURCE)
	java -cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) CheckAnnotation $(PUBLICTEST)
	java -cp $(LIBALL) tools.ForbiddenUseSearcher $(PUBLICTEST) > forbidden.out
	if [ -s forbidden.out ]; then \
		cat forbidden.out 1>&2 ; \
		exit 1 ; \
	fi
	make run-comparer

compile-stage2: miniclean
	make compile-stage1
	echo "echo \"[\" 1>&2" > loop.sh
	echo "make run-stage1" > single_execution.sh
	if [ "x$(SECRETTEST)" != "x" ]; then \
		set -e ; \
		make compile-stage2-secret ; \
		java -cp $(LIBALL):$(junitDirName):$(interfacesDirName):$(sutDirName) -Dpub=$(PUBLICTEST) CheckAnnotation $(SECRETTEST) ; \
		echo "echo \",\" 1>&2" >> single_execution.sh;	\
		java -cp $(LIBALL):$(junitDirName) tools.sep.SingleExecutionPreparer "$(LIBALL):$(interfacesDirName):$(sutDirName):$(junitDirName)" "-Djson=yes -Dpub=$(PUBLICTEST)" $(SECRETTEST) >> single_execution.sh; \
	else \
		set -e ; \
		echo "echo \"]\" 1>&2" >> loop.sh ; \
	fi

compile-stage2-secret:
	./obfuscate
	javac -Xprefer:source $(COMPILER_ARGS) -sourcepath $(interfacesDirName):$(cleanroomDirName) -cp $(LIBALL) $(cleanroomDirName)/*.java
	javac -Xprefer:source $(COMPILER_ARGS) -sourcepath $(interfacesDirName):$(junitDirName):$(sutDirName) -cp $(LIBALL) $(junitDirName)/$(SECRETTESTSOURCE)
	for i in $(cleanroomDirName)/*.java; do \
		cp $$i $${i}.bak; \
		/bin/echo -e "package cleanroom;\n" > $$i ; \
		cat $${i}.bak >> $$i; \
	done
	COMPILER_ARGS="$(COMPILER_ARGS) $(JAVAMODULEOPENS)" \
	java -cp $(LIBALL) tools.ReadReplace $(SECRETTEST) > compile2.sh
	if [ "x$(INTERFACESOURCE)" != "x" ]; then \
		set -e ; \
		for i in $(INTERFACESOURCE); do \
			/bin/echo -e "package cleanroom;\n" > $(cleanroomDirName)/$$i; \
			cat $(interfacesDirName)/$$i >> $(cleanroomDirName)/$$i; \
		done; \
	fi
	$(SHELL) -ex ./compile2.sh
	COMPILER_ARGS="$(COMPILER_ARGS) $(JAVAMODULEOPENS)" \
	java -cp $(LIBALL) tools.ReadReplace --loop -p $(PUBLICTEST) $(SECRETTEST) >> loop.sh
	echo "echo \"]\" 1>&2" >> loop.sh

compile: compile-stage$(STAGE)


run-comparer:
	java -cp $(LIBJUNITPOINTS) tools.ic.InterfaceComparator $(PUBLICTEST)

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
