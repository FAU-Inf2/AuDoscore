package tools.sep;

import java.util.*;
import java.lang.reflect.*;
import org.junit.Test;

public class SingleExecutionPreparer {
	private static void usage() {
		System.err.println("Usage: java SingleExecutionPreparer [-Dparam] <ExampleTest>");
		System.exit(0);
	}

	public static Method[] getMethodsSorted(final Class<?> cls) {
		final Method[] methods = cls.getMethods();
		Arrays.sort(methods, Comparator.comparing(Method::getName));
		return methods;
	}

	private static void writeOutSingleTestExecution(String cp, String dParameter, String className) {
		try {
			Class<?> tc = ClassLoader.getSystemClassLoader().loadClass(className);
			int counter = 0;
			for (Method method : getMethodsSorted(tc)) {
				if (method.isAnnotationPresent(Test.class)) {
					if (counter > 0) {
						System.out.println("echo \",\" 1>&2");
					}
					String methodName = method.getName();
					System.out.println("java -XX:-OmitStackTraceInFastThrow -Xmx1024m -cp " + cp + dParameter + " tools.SingleMethodRunner " + className + " " + methodName);
					counter++;
				}
			}
		} catch (ClassNotFoundException classNotFoundException) {
			throw new Error("WARNING - test class not found: " + className);
		}
	}

	public static void main(String[] args) {
		String cp;
		String className;
		String dParameter;
		if (args == null || args.length < 2 || args.length > 3) {
			usage();
			return;
		}
		if (args.length == 2) {
			cp = args[0];
			className = args[1];
			dParameter = " ";
		} else {
			cp = args[0];
			dParameter = " " + args[1] + " ";
			className = args[2];
		}
		writeOutSingleTestExecution(cp, dParameter, className);
	}
}
