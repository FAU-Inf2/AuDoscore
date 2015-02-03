package tools.sep;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.*;


public class SingleExecutionPreparer {
	private static boolean isPublic = false;
	private static boolean isSecret = false;
	private static void usage(){
		System.err.println("Usage: java SingleExecutionPreparer  [--public | --secret ] <ExampleTest>");
		System.exit(0);
	}

	private static void writeOutSingleTestExecution(String className){
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new BufferedWriter(new FileWriter("single_execution.sh")));
			writer.println("#!/bin/bash");
			writer.println("");
			if(isPublic) {
				writer.println("echo \"[\" 1>&2 ");
			}
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
		if((!isPublic && !isSecret) || isSecret) {
			writer.println("echo \"]\" 1>&2");
		}
		writer.close();

	}

	public static void main(String args[]){
		String className = null;
		if(args == null || args.length > 2) {
			usage();	
		}

		if(args.length == 1) {
			className = args[0];
		}
		if(args.length == 2) {
			if(args[0].equals("--public")) {
				isPublic = true;
			}
			if(args[0].equals("--secret")) {
				isSecret = true;
			}else{
				usage();
			}
			className = args[1];
		}

		writeOutSingleTestExecution(className);

	}


}
