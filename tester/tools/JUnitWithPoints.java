package tester.tools;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import org.json.simple.*;
import tester.annotations.*;

public class JUnitWithPoints implements BeforeAllCallback, BeforeTestExecutionCallback, TestWatcher, AfterAllCallback {
	static {
		// set locale explicitly to avoid differences in reading/writing floats
		Locale.setDefault(Locale.US);
	}

	// backend data structures
	private static final HashMap<String, List<ReportEntry>> reportHashMap = new HashMap<>();

	// helper class for reports
	private static final class ReportEntry {
		ExtensionContext context;
		Throwable throwable;
		Points points;
		long executionTime;

		private ReportEntry(ExtensionContext context, Points points, Throwable throwable, long executionTime) {
			this.context = context;
			this.throwable = throwable;
			this.points = points;
			this.executionTime = executionTime;
		}

		// get sensible part/line of stack trace
		private String getStackTrace() {
			if (throwable == null || throwable instanceof AssertionError) {
				return "";
			}
			StackTraceElement[] st = throwable.getStackTrace();
			if (st.length == 0) {
				return "";
			}
			StackTraceElement ste = st[0]; // TODO: maybe search for student code here
			int i = 1;
			while (ste.getClassName().indexOf('.') >= 0 && i < st.length) {
				ste = st[i];
				i++;
			}
			return ": " + ste.getClassName() + "." + ste.getMethodName() + "(line " + ste.getLineNumber() + ")";
		}

		// determine comment for students
		private String getComment(String comment, ExtensionContext context) {
			if (comment.equals("<n.a.>")) { // default value -> use short method name
				return getShortDisplayName(context);
			} else {
				return comment;
			}
		}

		// converts collected result to JSON
		@SuppressWarnings("unchecked")
		private JSONObject toJSON() {
			boolean success = (throwable == null);
			JSONObject jsonTest = new JSONObject();
			jsonTest.put("id", getShortDisplayName(context));
			jsonTest.put("success", success);
			jsonTest.put("desc", getComment(points.comment(), context));
			if (System.getenv("AUDOSCORETIMINGS") != null) {
				jsonTest.put("executionTimeInMS", executionTime);
				jsonTest.put("timeout", context.getElement() //
						.map(e -> e.getAnnotation(Timeout.class)) //
						.or(() -> Optional.of(Points.class.getAnnotation(Timeout.class))) //
						.map(t -> java.util.concurrent.TimeUnit.MILLISECONDS.convert(t.value(), t.unit())).orElse(0L));
			}
			if (!success) {
				jsonTest.put("error", throwable.getClass().getSimpleName() + "(" + ((throwable.getLocalizedMessage() != null) ? throwable.getLocalizedMessage() : "") + ")" + getStackTrace());
			}
			return jsonTest;
		}
	}

	private static PrintStream saveOut, saveErr;
	private static boolean isSecretClass;
	private long startTime;

	@Override
	public void beforeAll(ExtensionContext context) {
		// disable stdout/stderr to avoid timeouts due to large debugging outputs
		if (saveOut == null) {
			saveOut = System.out;
			saveErr = System.err;
			System.setOut(new PrintStream(OutputStream.nullOutputStream()));
			System.setErr(System.out);
		}
		// reset states
		reportHashMap.clear();
		context.getTestClass().filter(tc -> tc.isAnnotationPresent(Exercises.class)).ifPresent(tc -> isSecretClass = false);
		context.getTestClass().filter(tc -> tc.isAnnotationPresent(SecretClass.class)).ifPresent(tc -> isSecretClass = true);
		// fill data structures
		getExercisesAnnotation(context.getTestClass().orElse(null)).map(Exercises::value).ifPresent(exercises -> {
			for (Ex exercise : exercises) reportHashMap.put(exercise.exID(), new ArrayList<>());
		});
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) {
		// TODO: handle potential @InitializeOnce!
		startTime = System.currentTimeMillis();
	}

	private void processTestResult(ExtensionContext context, Throwable cause) {
		long executionTime = System.currentTimeMillis() - startTime;
		context.getElement() //
				.filter(m -> m.isAnnotationPresent(Points.class)) //
				.map(m -> m.getAnnotation(Points.class)) //
				.ifPresent(pointsAnnotation -> //
						reportHashMap.get(pointsAnnotation.exID()).add(new ReportEntry(context, pointsAnnotation, cause, executionTime)));
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		processTestResult(context, null);
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		processTestResult(context, cause);
	}

	@Override
	public void testAborted(ExtensionContext context, Throwable cause) {
		processTestResult(context, cause);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterAll(ExtensionContext context) {
		if (System.getProperty("json") != null && System.getProperty("json").equals("yes")) {
			// loop over all reports and collect results
			JSONArray jsonExercises = new JSONArray();
			for (Map.Entry<String, List<ReportEntry>> exerciseResults : reportHashMap.entrySet()) {
				JSONArray jsonTests = new JSONArray();
				// loop over all results for that exercise
				for (ReportEntry reportEntry : exerciseResults.getValue()) {
					JSONObject reportJSON = reportEntry.toJSON();
					// mark test method regarding origin
					reportJSON.put("fromSecret", isSecretClass);
					jsonTests.add(new TreeMap<String, Object>(reportJSON));
				}
				// collect result
				JSONObject jsonExercise = new JSONObject();
				jsonExercise.put("name", exerciseResults.getKey());
				jsonExercise.put("tests", jsonTests);
				jsonExercises.add(new TreeMap<String, Object>(jsonExercise));
			}
			// add results to root node and write to stderr
			JSONObject jsonSummary = new JSONObject();
			jsonSummary.put("exercises", jsonExercises);
			saveErr = new PrintStream(saveErr, true, java.nio.charset.StandardCharsets.UTF_8);
			saveErr.println(jsonSummary);
		}
	}

	// shortens description if possible
	private static String getShortDisplayName(ExtensionContext context) {
		String orig = context.getDisplayName();
		int ix = orig.indexOf('(');
		if (ix == -1) {
			return orig;
		}
		return orig.substring(0, ix);
	}

	// returns public test class if specified as system property
	private static Class<?> getPublicTestClass() {
		String pubClassName = System.getProperty("pub");
		try {
			return (pubClassName == null) ? null : ClassLoader.getSystemClassLoader().loadClass(pubClassName);
		} catch (ClassNotFoundException e) {
			throw new java.lang.annotation.AnnotationFormatError("ERROR - pub class specified, but not found [" + pubClassName + "]");
		}
	}

	// returns @Exercises annotation of public test class (if specified) or current class - if annotation is present
	static Optional<Exercises> getExercisesAnnotation(Class<?> currentTestClass) {
		Class<?> publicTestClass = getPublicTestClass();
		if (publicTestClass != null && publicTestClass.isAnnotationPresent(Exercises.class)) {
			return Optional.of(publicTestClass.getAnnotation(Exercises.class));
		} else if (currentTestClass != null && currentTestClass.isAnnotationPresent(Exercises.class)) {
			return Optional.of(currentTestClass.getAnnotation(Exercises.class));
		} else {
			return Optional.empty();
		}
	}

	static List<Method> getTestMethodsSorted(Class<?> testClass) {
		List<Method> testMethods = new LinkedList<>();
		if (testClass == null) return testMethods;
		LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.discoveryRequest() //
				.selectors(DiscoverySelectors.selectClass(testClass)) //
				.build();
		TestPlan testPlan = LauncherFactory.create().discover(request);
		testPlan.accept(new TestPlan.Visitor() {
			@Override
			public void visit(TestIdentifier testIdentifier) {
				testIdentifier.getSource().ifPresent(ts -> {
					if (ts instanceof MethodSource ms && !ms.getJavaMethod().isAnnotationPresent(Disabled.class)) {
						testMethods.add(ms.getJavaMethod());
					}
				});
			}
		});
		testMethods.sort(Comparator.comparing(Method::getName));
		return testMethods;
	}
}
