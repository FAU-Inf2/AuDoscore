all: lib/junitpoints.jar

clean:
	rm -rf build
	rm -f lib/junitpoints.jar

build:
	mkdir -p build

build/JUnitWithPoints.class: build JUnitWithPoints.java
	javac -d build -cp lib/json-simple-1.1.1.jar:lib/junit.jar:. JUnitWithPoints.java

lib/junitpoints.jar: build/JUnitWithPoints.class
	jar cvf lib/junitpoints.jar -C build .

ExampleTestcase.class: lib/junitpoints.jar ExampleTestcase.java
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. ExampleTestcase.java

test: ExampleTestcase.class
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. org.junit.runner.JUnitCore ExampleTestcase

ExampleTestcase.class.aspect: lib/junitpoints.jar ExampleTestcase.java
	ajc -1.7 -cp lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:. ExampleTestcase.java asp/AllocFactoryAspect.java tester/Factory.java

test2: ExampleTestcase.class.aspect
	java -cp lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:. -Dreplace=yes org.junit.runner.JUnitCore ExampleTestcase
