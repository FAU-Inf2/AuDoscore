package tools.sep;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.*;


public class SingleExecutionPreparer {

	private static void usage(){
		System.err.println("Usage: java SingleExecutionPreparer <ExampleTest>");
		System.exit(0);
	}

	private static void writeOutSingleTestExecution(String className){
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter("single_execution.sh")));
			writer.println("#!/bin/bash");
			writer.println("");
			writer.println("echo \"[\" 1>&2 ");
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Class tc = cl.loadClass(className);
			for(Method method : tc.getMethods()) {
				if(method.isAnnotationPresent(Test.class)){
					String methodName = method.getName();
					writer.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junit/junitpoints.jar:. -Dmethod=" + methodName + " -Djson=yes org.junit.runner.JUnitCore $(TEST) || echo");
					writer.println("echo \",\" 1>&2");
				}

			}	
		} catch(FileNotFoundException fne) {
			throw new Error("WARNING - Something bad happened while creating the single execution script: " + fne.getMessage());
		} catch(ClassNotFoundException cnfe) {
			throw new Error("WARNING - test class not found: " + className);
		} catch(UnsupportedEncodingException uee ) {
			throw new Error("WARNING - Something bad happened while creating the single execution script" + uee.getMessage());
		} catch (IOException ioe) {
			throw new Error("WARNING - Something bad happened while creating the single execution script" + ioe.getMessage());

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
