all: lib/junitpoints.jar

clean:
	rm -rf build
	rm -rf replaced
	rm -f *.class
	rm -f lib/junitpoints.jar
	rm -f lib/aspectreplacer.jar

build:
	rm -rf build
	mkdir -p build

build/JUnitWithPoints.class: build JUnitWithPoints.java Replace.java
	javac -d build -cp lib/json-simple-1.1.1.jar:lib/junit.jar:. JUnitWithPoints.java Replace.java

lib/junitpoints.jar: build/JUnitWithPoints.class
	jar cvf lib/junitpoints.jar -C build .

ExampleTestcase.class: lib/junitpoints.jar ExampleTestcase.java Student.java
	javac -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. ExampleTestcase.java Student.java

test: ExampleTestcase.class
	java -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. org.junit.runner.JUnitCore ExampleTestcase

lib/aspectreplacer.jar: build asp/AllocFactoryAspect.java tester/Factory.java
	ajc -d build -1.7 -cp lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:. asp/AllocFactoryAspect.java tester/Factory.java	
	jar cvf lib/aspectreplacer.jar -C build .

ExampleTestcase.class.aspect: lib/junitpoints.jar lib/aspectreplacer.jar ExampleTestcase.java Student.java
	ajc -d replaced -1.7 -cp lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectreplacer.jar:. ExampleTestcase.java Student.java

test2: ExampleTestcase.class.aspect
	java -cp lib/json-simple-1.1.1.jar:lib/aspectjrt.jar:lib/junit.jar:lib/junitpoints.jar:lib/aspectreplacer.jar:replaced -Dreplace=yes org.junit.runner.JUnitCore ExampleTestcase
