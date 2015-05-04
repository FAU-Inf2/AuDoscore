package tools.sep;

import java.util.*;
import java.lang.*;
import java.lang.reflect.*;
import java.lang.annotation.*;
import org.junit.*;


public class SingleExecutionPreparer {
	private static void usage(){
		System.err.println("Usage: java SingleExecutionPreparer [-Dparam] <ExampleTest>");
		System.exit(0);
	}

	private static void writeOutSingleTestExecution(String cp, String dparam, String className){
		try{
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Class tc = cl.loadClass(className);
			int counter = 0;
			for(Method method : tc.getMethods()) {
				if(method.isAnnotationPresent(Test.class)){
					if(counter > 0){
						System.out.println("echo \",\" 1>&2");
					}
					String methodName = method.getName();
					System.out.println("java -XX:+UseConcMarkSweepGC -Xmx1024m -cp "+cp + dparam + " tools.SingleMethodRunner " + className + " " + methodName + " || echo");
					counter++;
				}
			
			}	
		} catch(ClassNotFoundException cnfe) {
			throw new Error("WARNING - test class not found: " + className);
		}
	}

	public static void main(String args[]){
		String className = null;
		String dparam = null;
		String cp = null;
		if(args == null || args.length < 2 || args.length > 3) {
			usage();	
		}
		
		if(args.length == 2) {
			cp = args[0];
			className = args[1];
			dparam = " ";
		} else if (args.length == 3){
			cp = args[0];
			dparam  = " " + args[1] + " ";
			className = args[2];
		}

		writeOutSingleTestExecution(cp, dparam, className);
	}


}
