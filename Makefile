LIBJUNIT=lib/junit.jar
LIBHAMCREST=lib/hamcrest-core.jar
LIBJSONSIMPLE=lib/json-simple-1.1.1.jar
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

LIBJUNITPOINTS=lib/junitpoints.jar

-include var.mk

SHELL=/bin/sh
ifneq ("$(wildcard /bin/dash)","")
	SHELL=/bin/dash
endif

compiletest = \
	javac $(JAVAMODULEEXPORTS) $(JAVAMODULEOPENS) -cp $(LIBJUNIT):$(LIBJUNITPOINTS) -proc:only -processor FullQualifier $(1).java > $(1).java.tmp ; \
	mv $(1).java.tmp $(1).java ; \
	make -B $(2); \
	javac $(COMPILER_ARGS) cleanroom/*.java;

ifndef SECRETCLASS
	-include varsec.mk
endif
TESTCLASS = $(TEST:=.class)
TESTSOURCE = $(TEST:=.java)
SECRETCLASS = $(SECRETTEST:=.class)
SECRETSOURCE = $(SECRETTEST:=.java)
INTERFACESOURCE ?= $(filter %.java,$(INTERFACES))

all: prepare

verify: prepare verify-ptc
	./verify.sh

verify-ptc: prepare
	./verify-ptc.sh

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

SRCJUNITPOINTSJAR := JUnitWithPoints.java tester/annotations/Replace.java JUnitPointsMerger.java tester/ReadReplace.java ReadForbidden.java ReplaceMixer.java tester/annotations/Ex.java tester/annotations/Points.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/NotForbidden.java tester/annotations/SafeCallers.java tools/jsondiff/JSONDiff.java FullQualifier.java tools/sep/SingleExecutionPreparer.java CheckAnnotation.java tools/SingleMethodRunner.java tools/ic/InterfaceComparator.java tools/ptc/PublicTestCleaner.java tester/TesterSecurityManager.java tester/annotations/InitializeOnce.java

lib/junitpoints.jar: build $(SRCJUNITPOINTSJAR)
	javac -source 17 -target 17 $(JAVAMODULEEXPORTS) -encoding UTF-8 -d build -cp $(LIBJUNIT):$(LIBJSONSIMPLE):. $(SRCJUNITPOINTSJAR)
	jar cvf $(LIBJUNITPOINTS) -C build .


clean-pubtest: $(FILE)
	javac $(JAVAMODULEEXPORTS) $(JAVAMODULEOPENS) -cp $(LIBJUNIT):$(LIBJUNITPOINTS) -proc:only -processor tools.ptc.PublicTestCleaner $(FILE) > $(FILE).tmp
	mv $(FILE).tmp $(FILE)
	javac $(JAVAMODULEEXPORTS) $(JAVAMODULEOPENS) -cp $(LIBJUNIT):$(LIBJUNITPOINTS) -proc:only -processor FullQualifier $(FILE) > $(FILE).tmp

compile-stage0:
	javac $(COMPILER_ARGS) $(STUDENTSOURCE)

compile-stage1: miniclean
	$(call compiletest,$(TEST),$(TESTCLASS))
	java -cp $(LIBJUNIT):$(LIBJUNITPOINTS):. CheckAnnotation $(TEST)
	java -cp $(LIBJUNITPOINTS):. ReadForbidden $(TEST) > forbidden
	chmod +x forbidden
	if [ "x$(SECRETTEST)" != "x" ]; then \
		STUDENTCLASSES=$$(find . -maxdepth 1 -iname "*.class" -a \! \( -name "$(TEST)*" -o -name "$(SECRETTEST)*" \) ); \
	else \
		STUDENTCLASSES=$$(find . -maxdepth 1 -iname "*.class" -a \! -name "$(TEST)*"); \
	fi; \
	if [ "x$(INTERFACESOURCE)" != "x" ]; then \
		STUDENTCLASSES=$$(echo "$$STUDENTCLASSES" | grep -vE "$$(echo "$(INTERFACESOURCE)" | sed 's/\.java/(\\\$$[^.]*)?\.class/g' | tr " " "|")"); \
	fi; \
	for i in $$STUDENTCLASSES; do \
		rm -f javap.out; \
		javap -p -c $$i > javap.out; \
		sed -i -e 's/(.*//' javap.out; \
		! ( cat javap.out | ./forbidden 1>&2 ) || exit 1; \
	done
	rm forbidden
	make run-comparer


compile-stage2: miniclean
	make compile-stage1
	echo "echo \"[\" 1>&2" > loop.sh
	if [ "x$(SECRETTEST)" != "x" ]; then \
		set -e ; \
		make compile-stage2-secret ; \
		java -cp $(LIBJUNIT):$(LIBJUNITPOINTS):. -Dpub=$(TEST) CheckAnnotation $(SECRETTEST) ; \
		echo "make run-stage1" > single_execution.sh ;\
		echo "echo \",\" 1>&2" >> single_execution.sh;	\
		java -cp $(LIBJUNITPOINTS):$(LIBJUNIT):. tools.sep.SingleExecutionPreparer "$(LIBJSONSIMPLE):$(LIBJUNIT):$(LIBHAMCREST):$(LIBJUNITPOINTS):." "-Djson=yes -Dpub=$(TEST)" $(SECRETTEST) >> single_execution.sh; \
	else \
		set -e ; \
		echo "echo \"]\" 1>&2" >> loop.sh ; \
		echo "make run-stage1" > single_execution.sh ; \
	fi

compile-stage2-secret:
	./obfuscate
	$(call compiletest,$(SECRETTEST),$(SECRETCLASS))
	for i in cleanroom/*.java; do \
		cp $$i $${i}.bak; \
		/bin/echo -e "package cleanroom;" > $$i ; \
		cat $${i}.bak >> $$i; \
	done
	COMPILER_ARGS="$(COMPILER_ARGS) $(JAVAMODULEOPENS)" java -cp $(LIBJUNITPOINTS):$(LIBJUNIT):. tester.ReadReplace $(SECRETTEST) > compile2.sh
	if [ "x$(INTERFACESOURCE)" != "x" ]; then \
		set -e ; \
		for i in $(INTERFACESOURCE); do \
			/bin/echo -e "package cleanroom;\n" > cleanroom/$$i; \
			cat $$i >> cleanroom/$$i; \
		done; \
	fi
	$(SHELL) -ex ./compile2.sh
	COMPILER_ARGS="$(COMPILER_ARGS)" java -cp $(LIBJUNITPOINTS):$(LIBJUNIT):. tester.ReadReplace --loop -p $(TEST) $(SECRETTEST) >> loop.sh
	echo "echo \"]\" 1>&2" >> loop.sh

compile: compile-stage$(STAGE)


run-comparer:
	java -cp $(LIBJUNITPOINTS) tools.ic.InterfaceComparator $(TEST)

run-stage0:
	echo "alles gut"

run-stage1:
	java -XX:-OmitStackTraceInFastThrow -Xmx1024m -cp $(LIBJSONSIMPLE):$(LIBJUNIT):$(LIBHAMCREST):$(LIBJUNITPOINTS):. -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo

run-stage2:
	echo "{ \"vanilla\" : " 1>&2
	echo "[" 1>&2
	$(SHELL) ./single_execution.sh
	echo "]" 1>&2
	echo ", \"replaced\" : " 1>&2
	$(SHELL) ./loop.sh
	echo "}" 1>&2

run: run-stage$(STAGE)


$(TESTCLASS): $(TESTSOURCE) $(STUDENTSOURCE)
	javac $(COMPILER_ARGS) -cp $(LIBJSONSIMPLE):$(LIBJUNIT):$(LIBJUNITPOINTS):. $(TESTSOURCE) $(STUDENTSOURCE) $(INTERFACESOURCE)

$(SECRETCLASS): $(SECRETSOURCE) $(STUDENTSOURCE)
	javac $(COMPILER_ARGS) -cp $(LIBJSONSIMPLE):$(LIBJUNIT):$(LIBJUNITPOINTS):. $(SECRETSOURCE) $(STUDENTSOURCE) $(INTERFACESOURCE)
