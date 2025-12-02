package tester.tools;

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

public final class SingleExecutionPreparer {
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
		final String cwd = System.getProperty("user.dir");
		try (URLClassLoader unitLoader = new URLClassLoader(new URL[]{new File(cwd, "junit").toURI().toURL()})) {
			Class<?> testClass = unitLoader.loadClass(testClassName);
			for (Method method : JUnitWithPoints.getTestMethodsSorted(testClass)) {
				if (method.isAnnotationPresent(tester.annotations.Points.class)) {
					System.out.println("echo \",\" >> run2.err");
					System.out.println("java -XX:-OmitStackTraceInFastThrow -Xmx1024m " + dParameters //
							+ " -cp " + classpath //
							+ " org.junit.platform.console.ConsoleLauncher execute" //
							+ " --disable-banner --details=none --fail-if-no-tests --reports-dir=reports" //
							+ " -m " + testClassName + "#" + method.getName() //
							+ " 1>/dev/null 2>/dev/null; echo $? >> run2.exit;" //
					);
					System.out.println("for gradeFile in `find ./reports/ -name run.grade -type f`; do cat $gradeFile >> run2.err; rm $gradeFile; done");
				}
			}
		} catch (IOException | ClassNotFoundException classNotFoundException) {
			throw new Error("WARNING - test class not found: " + testClassName);
		}
	}
}
