import java.util.*;
import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

import org.json.simple.*;

import tester.*;
import tester.annotations.*;


// rules helpers to shorten code
final class PointsLogger extends JUnitWithPoints.PointsLogger {}
final class PointsSummary extends JUnitWithPoints.PointsSummary {}

public abstract class JUnitWithPoints {
	// these rules help to collect necessary information from test methods
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public static final PointsSummary pointsSummary = new PointsSummary();

	// backend data structures
	private static final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
	private static final HashMap<String, List<ReportEntry>> reportHashMap = new HashMap<>();

	static {
		// set locale explicitly to avoid differences in reading/writing floats
		Locale.setDefault(Locale.US);
	}

	// shortens description if possible
	private static String getShortDisplayName(Description d) {
		String orig = d.getDisplayName();
		int ix = orig.indexOf('(');
		if (ix == -1) {
			return orig;
		}
		return orig.substring(0, ix);
	}

	// helper class for reports
	private static final class ReportEntry {
		Description description;
		Throwable throwable;
		Points points;
		boolean skipped;
		long executionTime;

		private ReportEntry(Description description, Points points, Throwable throwable, long executionTime) {
			this.description = description;
			this.throwable = throwable;
			this.points = points;
			this.executionTime = executionTime;
			this.skipped = false;
		}

		// we did skip this test method
		public ReportEntry(Description description) {
			this.skipped = true;
		}

		// get sensible part/line of stack trace
		private String getStackTrace() {
			if (throwable == null || throwable instanceof AssertionError) {
				return "";
			}

			StackTraceElement st[] = throwable.getStackTrace();
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
		protected String getComment(String comment, Description description) {
			if (comment.equals("<n.a.>")) { // default value -> use short method name
				return getShortDisplayName(description);
			} else {
				return comment;
			}
		}

		// converts collected result to JSON
		final JSONObject toJSON() {
			boolean success = (throwable == null);
			JSONObject jsonTest = new JSONObject();
			jsonTest.put("id", getShortDisplayName(description));
			jsonTest.put("success", success);
			jsonTest.put("desc", getComment(points.comment(), description));
			if (System.getenv("AUDOSCORETIMINGS") != null) {
				jsonTest.put("executionTimeInMS", executionTime);
				jsonTest.put("timeout", description.getAnnotation(Test.class).timeout());
			}
			if (!success) {
				jsonTest.put("error", throwable.getClass().getSimpleName() + "(" + ((throwable.getLocalizedMessage() != null) ? throwable.getLocalizedMessage() : "") + ")" + getStackTrace());
			}
			return jsonTest;
		}
	}

	// helper class for logging purposes
	protected static class PointsLogger extends TestWatcher {

		private long startTime = 0;
		private long endTime = 0;

		// test methods are ignored if their replace set is different to the specified one
		// FIXME: is that still necessary with single execution?
		protected boolean isIgnoredCase(Description description) {
			String doReplace = System.getProperty("replace");
			if ((doReplace != null && !doReplace.equals(""))) {
				String ncln = ReadReplace.getCanonicalReplacement(description);
				if (!doReplace.equals(ncln)) {
					return true;
				}
			}
			return false;
		}

		// test methods are skipped during single test method execution
		protected boolean isSkippedCase(Description description) {
			String methodToBeExecuted = System.getProperty("method");
			if((methodToBeExecuted != null && !methodToBeExecuted.equals(""))){
				String method = getShortDisplayName(description);
				if(!method.equals(methodToBeExecuted)){
					return true;
				}
			}

			return false;
		}

		@Override
		public final Statement apply(Statement base, Description description) {
			if (isIgnoredCase(description) || isSkippedCase(description)) {
				// don't execute these test methods
				base = new SkipStatement();
			} else {
				// Handle potential @InitializeOnce
				base = performInitializeOnce(base, description);
			}
			return super.apply(base, description);
		}

		@Override
		protected void starting(Description description) {
			startTime = System.currentTimeMillis();
		}

		@Override
		protected final void failed(Throwable throwable, Description description) {
			// Reset security manager
			try {
				System.setSecurityManager(null);
			} catch (final SecurityException e) { /* Ignore */ }
			
			endTime = System.currentTimeMillis();
			long executionTime  = endTime - startTime;
			Points pointsAnnotation = description.getAnnotation(Points.class);
			String exID = pointsAnnotation.exID();
			if (isIgnoredCase(description) || isSkippedCase(description)) {
				reportHashMap.get(exID).add(new ReportEntry(description));
			} else {
				reportHashMap.get(exID).add(new ReportEntry(description, pointsAnnotation, throwable,executionTime));
			}
		}

		@Override
		protected void skipped(AssumptionViolatedException e, Description description) {
			failed(null, description);
		}

		@Override
		protected final void succeeded(Description description) {
			failed(null, description);
		}

		private Statement performInitializeOnce(final Statement base, final Description description) {
			Statement result = base;
			for (final Field f : description.getTestClass().getDeclaredFields()) {
				final InitializeOnce initOnce = f.getAnnotation(InitializeOnce.class);
				if (initOnce != null) {
					final Statement oldStmt = result;

					// We need a named class here to allow it in the security manager

					class InitOnceStatement extends Statement {
						@Override
						public void evaluate() throws Throwable {
							// @InitializeOnce field found. First, check if the result is
							// already computed
							final File initFile = new File(description.getTestClass().getCanonicalName()
									+ "-" + f.getName() + ".tmp");
							boolean recompute = !initFile.exists();
							if (!recompute) {
								// The result has been computed, just restore it
								try (final ObjectInputStream in
										= new ObjectInputStream(new FileInputStream(initFile))) {
									f.set(null, in.readObject());
								} catch (final IOException e) {
									recompute = true;
								}
							}
							if (recompute) {
								// The result must be computed, stored in the field, and saved in
								// initFile
								try {
									final Object result = description.getTestClass()
											.getDeclaredMethod(initOnce.value()).invoke(null);
									f.set(null, result);

									try (final ObjectOutputStream out
											= new ObjectOutputStream(new FileOutputStream(initFile))) {
										out.writeObject(result);
									} catch (final IOException e) {
										initFile.delete(); // Clean up
									}
								} catch (final NoSuchMethodException e) {
									// Should be checked by CheckAnnotations
									throw new IllegalStateException(e);
								} catch (final InvocationTargetException e) {
									// This may be an exception in student code, so we make this
									// test fail
									Assert.fail(String.valueOf(e.getCause()));
								}
							}

							// Now proceed with the next statement
							oldStmt.evaluate();
						}
					}

					result = new InitOnceStatement();
				}
			}
			return result;
		}
	}

	// helper class for summaries
	protected static class PointsSummary extends ExternalResource {
		public static final int MAX_TIMEOUT_MS = 60_000;
		private static PrintStream saveOut, saveErr;
		private static boolean isSecretClass = false;
		private List<String> safeCallerList;

		@Override
		public final Statement apply(Statement base, Description description) {
			// reset states
			reportHashMap.clear();
			exerciseHashMap.clear();

			Exercises exercisesAnnotation = getExercisesAnnotation(description);

			// fill data structures
			for (Ex exercise : exercisesAnnotation.value()) {
				reportHashMap.put(exercise.exID(), new ArrayList<ReportEntry>());
				exerciseHashMap.put(exercise.exID(), exercise);
			}

			// Obtain a list of safe callers (i.e., callers that are known to
			// contain no malicious code)
			final SafeCallers safeCallerAnnotation = description.getAnnotation(SafeCallers.class);
			if (safeCallerAnnotation == null) {
				this.safeCallerList = Collections.emptyList();
			} else {
				this.safeCallerList = Arrays.asList(safeCallerAnnotation.value());
			}

			// start the real JUnit test
			return super.apply(base, description);
		}

		// returns @Exercises annotation of public test class (if specified) or current class (otherwise)
		public static Exercises getExercisesAnnotation(Description description) {
			Class<?> publicTestClass = getPublicTestClass();
			if (publicTestClass == null) {
				return description.getAnnotation(Exercises.class);
			} else {
				isSecretClass = true;
				return publicTestClass.getAnnotation(Exercises.class);
			}
		}

		// returns public test class (if specified)
		public static Class<?> getPublicTestClass() {
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

		@Override
		// create and print JSON summary to stderr (if requested)
		protected final void after() {
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
				try {
					saveErr = new PrintStream(saveErr, true, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// silently ignore exception -> it's not that important after all
				}
				saveErr.println(jsonSummary);
			}
		}

		@Override
		protected final void before() {
			// disable stdout/stderr to avoid timeouts due to large debugging outputs
			if (saveOut == null) {
				saveOut = System.out;
				saveErr = System.err;

				System.setOut(new PrintStream(new OutputStream() {
					public void write(int i) { }
				}));
				System.setErr(System.out);

				// Install security manager
				try {
					System.setSecurityManager(new TesterSecurityManager(this.safeCallerList));
				} catch (final SecurityException e) { /* Ignore */ }
			}
		}
	}
}

// helper class to skip test methods
class SkipStatement extends Statement {
	public void evaluate() {
		Assume.assumeTrue(false);
	}
}
