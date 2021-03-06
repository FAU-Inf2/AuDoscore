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

	public static Method[] getMethodsSorted(final Class cls) {
		final Method[] methods = cls.getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(final Method method1, final Method method2) {
				return method1.getName().compareTo(method2.getName());
			}
		});
		return methods;
	}

	private static void writeOutSingleTestExecution(String cp, String dparam, String className){
		try{
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			Class tc = cl.loadClass(className);
			int counter = 0;
			for(Method method : getMethodsSorted(tc)) {
				if(method.isAnnotationPresent(Test.class)){
					if(counter > 0){
						System.out.println("echo \",\" 1>&2");
					}
					String methodName = method.getName();
					System.out.println("java -XX:-OmitStackTraceInFastThrow -Xmx1024m -cp "+cp + dparam + " tools.SingleMethodRunner " + className + " " + methodName);
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
