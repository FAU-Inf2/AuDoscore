public class SingleExecutionPreparer {

	private static usage(){
		System.err.println("Usage: java SingleExectionPreparer <ExampleTest.java>");
		System.exit(0);
	}

	private static void writeOutSingleTestExecution(String className){
		PrintWriter writer = new PrintWriter("single_execution.sh", "UTF-8");
		writer.println("#!/bin/bash");
		writer.println("");
		writer.println("echo \"[\" 1>&2 ");
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		try{
			Class tc = cl.loadClass(className);
			for(Method method : tc.getMethods()) {
				if(method.isAnnotationPresent(Test.class)){
					String methodName = method.getName();
					writer.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junit/junitpoints.jar:. -Dmethod=" + methodName + " -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo");
					writer.println("echo , 1>&2");
				}

			}
			
		}catch(Exception e) {
			throw Error("Something bad happened while preparing single execution of testmethods");
		}

		writer.println("echo \"]\" 1>&2");
		writer.close();

	}

	public static void main(String args[]){
		if(args.length != 1) {
			usage();
		}
		
		writeOutSingleTestExecution(args[0]);

	}


}
