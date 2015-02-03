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
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Class tc = cl.loadClass(className);
			int counter = 0;
			for(Method method : tc.getMethods()) {
				if(method.isAnnotationPresent(Test.class)){
					if(counter > 0){
						writer.println("echo \",\" 1>&2");
					}
					String methodName = method.getName();
					writer.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp lib/json-simple-1.1.1.jar:lib/junit.jar:lib/junitpoints.jar:. -Dmethod=" + methodName + " -Djson=yes org.junit.runner.JUnitCore " + className + " || echo");
					counter++;
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
		writer.close();

	}

	public static void main(String args[]){
		String className = null;
		if(args == null || args.length != 1) {
			usage();	
		}
		
		writeOutSingleTestExecution(args[0]);

	}


}
