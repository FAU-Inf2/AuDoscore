all: lib/junitpoints.jar

clean:
	rm -rf build
	rm -f lib/junitpoints.jar

build:
	mkdir -p build

build/JUnitWithPoints.class: build JUnitWithPoints.java
	javac -d build -cp lib/junit.jar:. JUnitWithPoints.java

lib/junitpoints.jar: build/JUnitWithPoints.class
	jar cvf lib/junitpoints.jar -C build .

ExampleTestcase.class: lib/junitpoints.jar ExampleTestcase.java
	javac -cp lib/junit.jar:lib/junitpoints.jar:. ExampleTestcase.java

test: ExampleTestcase.class
	java -cp lib/junit.jar:lib/junitpoints.jar:. org.junit.runner.JUnitCore ExampleTestcase
