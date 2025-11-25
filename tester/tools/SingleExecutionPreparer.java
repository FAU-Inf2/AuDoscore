package tester.tools;

import java.util.*;
import java.lang.reflect.*;

public class SingleExecutionPreparer {
	static void main(String[] args) {
		String classpath;
		String dParameters;
		String testClassName;
		if (args == null || args.length < 2 || args.length > 3) {
			System.err.println("Usage: java SingleExecutionPreparer <classpath> [-dParameters] <testClassName>");
			return;
		} else if (args.length == 2) {
			dParameters = "";
			testClassName = args[1];
		} else {
			dParameters = args[1];
			testClassName = args[2];
		}
		classpath = args[0];
		writeOutSingleTestExecution(classpath, dParameters, testClassName);
	}

	private static void writeOutSingleTestExecution(String classpath, String dParameters, String testClassName) {
		try {
			Class<?> tc = ClassLoader.getSystemClassLoader().loadClass(testClassName);
			int counter = 0;
			for (Method method : JUnitWithPoints.getTestMethodsSorted(tc)) {
				if (method.isAnnotationPresent(tester.annotations.Points.class)) {
					if (counter > 0) {
						System.out.println("echo \",\" 1>&2");
					}
					System.out.println("java -XX:-OmitStackTraceInFastThrow -Xmx1024m" //
							+ " -cp " + classpath //
							+ " " + dParameters //
							+ " org.junit.platform.console.ConsoleLauncher execute --disable-banner --fail-if-no-tests" //
							+ " -m " + testClassName + "#" + method.getName() //
							+ " ; echo $? >> run2.exit" //
					);
					counter++;
				}
			}
		} catch (ClassNotFoundException classNotFoundException) {
			throw new Error("WARNING - test class not found: " + testClassName);
		}
	}
}
