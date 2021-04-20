-include var.mk

SHELL=/bin/sh
ifneq ("$(wildcard /bin/dash)","")
	SHELL=/bin/dash
endif

JUNIT5_JARS=$(shell find lib/junit5 -name "*.jar" | sort -d | tr '\n' ':' | sed 's/.$$//'):lib/apiguardian-api.jar:lib/opentest4j.jar

compiletest = \
	javac -cp $(JUNIT5_JARS):lib/junitpoints.jar -proc:only -processor tools.bomacon.BonusMalusConverter $(1).java > $(1).java.tmp ; \
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
	rm -f lib/junitpoints.jar

build:
	rm -rf build
	mkdir -p build

prepare: updategitrev lib/junitpoints.jar

updategitrev:
	git rev-parse HEAD > GITREV

SRCJUNITPOINTSJAR := JUnitWithPoints.java tester/annotations/Replace.java JUnitPointsMerger.java tester/ReadReplace.java ReadForbidden.java ReplaceMixer.java tester/annotations/SecretCase.java tester/annotations/Bonus.java tester/annotations/Ex.java tester/annotations/Points.java tester/annotations/Exercises.java tester/annotations/Forbidden.java tester/annotations/Malus.java tester/annotations/NotForbidden.java tester/annotations/SafeCallers.java tools/jsondiff/JSONDiff.java tools/bomacon/BonusMalusConverter.java tools/sep/SingleExecutionPreparer.java CheckAnnotation.java tools/JUnitRunner.java tools/SingleMethodRunner.java tools/ic/InterfaceComparer.java tools/ptc/PublicTestCleaner.java tester/TesterSecurityManager.java tester/AuDoscoreUtils.java tester/annotations/InitializeOnce.java

lib/junitpoints.jar: build $(SRCJUNITPOINTSJAR)
	javac -source 1.8 -target 1.8 -encoding UTF-8 -d build -cp lib/json-simple-1.1.1.jar:$(JUNIT5_JARS):lib/tools.jar:. $(SRCJUNITPOINTSJAR)
	jar cvf lib/junitpoints.jar -C build .


clean-pubtest: $(FILE)
	javac -cp $(JUNIT5_JARS):lib/junitpoints.jar -proc:only -processor tools.ptc.PublicTestCleaner $(FILE) > $(FILE).tmp
	mv $(FILE).tmp $(FILE)
	#javac -cp $(JUNIT5_JARS):lib/junitpoints.jar -proc:only -processor FullQualifier $(FILE) > $(FILE).tmp

compile-stage0:
	javac $(COMPILER_ARGS) $(STUDENTSOURCE)

compile-stage1: miniclean
	$(call compiletest,$(TEST),$(TESTCLASS))
	java -cp $(JUNIT5_JARS):lib/junitpoints.jar:. CheckAnnotation $(TEST)
	java -cp lib/junitpoints.jar:. ReadForbidden $(TEST) > forbidden
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
		java -cp $(JUNIT5_JARS):lib/junitpoints.jar:. -Dpub=$(TEST) CheckAnnotation $(SECRETTEST) ; \
		echo "make run-stage1" > single_execution.sh ;\
		echo "echo \",\" 1>&2" >> single_execution.sh;	\
		java -cp lib/junitpoints.jar:$(JUNIT5_JARS):. tools.sep.SingleExecutionPreparer "lib/json-simple-1.1.1.jar:$(JUNIT5_JARS):lib/hamcrest-core.jar:lib/junitpoints.jar:." "-Djson=yes -Dpub=$(TEST)" $(SECRETTEST) >> single_execution.sh; \
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
	COMPILER_ARGS="$(COMPILER_ARGS)" java -cp lib/junitpoints.jar:$(JUNIT5_JARS):. tester.ReadReplace $(SECRETTEST) > compile2.sh
	if [ "x$(INTERFACESOURCE)" != "x" ]; then \
		set -e ; \
		for i in $(INTERFACESOURCE); do \
			/bin/echo -e "package cleanroom;\n" > cleanroom/$$i; \
			cat $$i >> cleanroom/$$i; \
		done; \
	fi
	$(SHELL) -ex ./compile2.sh
	COMPILER_ARGS="$(COMPILER_ARGS)" java -cp lib/junitpoints.jar:$(JUNIT5_JARS):. tester.ReadReplace --loop -p $(TEST) $(SECRETTEST) >> loop.sh	
	echo "echo \"]\" 1>&2" >> loop.sh

compile: compile-stage$(STAGE)


run-comparer:
	java -cp lib/junitpoints.jar tools.ic.InterfaceComparer $(TEST)

run-stage0:
	echo "alles gut"

run-stage1:
	java -XX:-OmitStackTraceInFastThrow -Xmx1024m -cp lib/json-simple-1.1.1.jar:$(JUNIT5_JARS):lib/hamcrest-core.jar:lib/junitpoints.jar:. -Djson=yes tools.JUnitRunner $(TEST) || echo

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
	javac $(COMPILER_ARGS) -cp lib/json-simple-1.1.1.jar:$(JUNIT5_JARS):lib/junitpoints.jar:. $(TESTSOURCE) $(STUDENTSOURCE) $(INTERFACESOURCE)

$(SECRETCLASS): $(SECRETSOURCE) $(STUDENTSOURCE)
	javac $(COMPILER_ARGS) -cp lib/json-simple-1.1.1.jar:$(JUNIT5_JARS):lib/junitpoints.jar:. $(SECRETSOURCE) $(STUDENTSOURCE) $(INTERFACESOURCE)
