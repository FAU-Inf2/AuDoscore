package tester.tools;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import java.nio.charset.*;
import java.util.*;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import org.json.simple.*;
import tester.annotations.*;

public class JUnitWithPoints implements BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback, TestWatcher, AfterAllCallback {
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
		boolean skipped;
		long executionTime;

		private ReportEntry(ExtensionContext context, Points points, Throwable throwable, long executionTime) {
			this.context = context;
			this.throwable = throwable;
			this.points = points;
			this.executionTime = executionTime;
			this.skipped = false;
		}

		// we did skip this test method
		private ReportEntry(ExtensionContext context) {
			this.context = context;
			this.skipped = true;
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
				jsonTest.put("timeout", context.getElement().map(e -> e.getAnnotation(Timeout.class)).map(t -> t.value() + t.unit().toString()).orElse("unknown"));
			}
			if (!success) {
				jsonTest.put("error", throwable.getClass().getSimpleName() + "(" + ((throwable.getLocalizedMessage() != null) ? throwable.getLocalizedMessage() : "") + ")" + getStackTrace());
			}
			return jsonTest;
		}
	}

	private static PrintStream saveOut;
	private static PrintStream saveErr;
	private static boolean isSecretClass = false;
	private long startTime = 0;

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
		// fill data structures
		Exercises exercisesAnnotation = getExercisesAnnotation(context.getTestClass().orElse(null));
		for (Ex exercise : exercisesAnnotation.value()) {
			reportHashMap.put(exercise.exID(), new ArrayList<>());
		}
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) {
		// TODO: handle potential @InitializeOnce!
		startTime = System.currentTimeMillis();
	}

	@Override
	public void afterTestExecution(ExtensionContext context) {
	}

	public void testResult(ExtensionContext context, Throwable cause) {
		long executionTime = System.currentTimeMillis() - startTime;
		Points pointsAnnotation = context.getElement().map(m -> m.getAnnotation(Points.class)).orElse(null);
		String exID = pointsAnnotation.exID();
		reportHashMap.get(exID).add(new ReportEntry(context, pointsAnnotation, cause, executionTime));
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		testResult(context, null);
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		testResult(context, cause);
	}

	@Override
	public void testAborted(ExtensionContext context, Throwable cause) {
		// TODO: Timeout?
		testResult(context, cause);
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
					if (!reportEntry.skipped) {
						JSONObject reportJSON = reportEntry.toJSON();
						// mark test method regarding origin
						reportJSON.put("fromSecret", isSecretClass);
						jsonTests.add(new TreeMap<String, Object>(reportJSON));
					}
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
			saveErr = new PrintStream(saveErr, true, StandardCharsets.UTF_8);
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

	// returns public test class (if specified)
	static Class<?> getPublicTestClass() {
		String pubClassName = System.getProperty("pub");
		if (pubClassName == null) {
			return null;
		}
		try {
			return ClassLoader.getSystemClassLoader().loadClass(pubClassName);
		} catch (ClassNotFoundException e) {
			throw new AnnotationFormatError("ERROR - pub class specified, but not found [" + pubClassName + "]");
		}
	}

	// returns @Exercises annotation of public test class (if specified) or current class (otherwise)
	static Exercises getExercisesAnnotation(Class<?> testClass) {
		Class<?> publicTestClass = getPublicTestClass();
		if (publicTestClass == null) {
			return testClass.getAnnotation(Exercises.class);
		} else {
			isSecretClass = true;
			return publicTestClass.getAnnotation(Exercises.class);
		}
	}
}
