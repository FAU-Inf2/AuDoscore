import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;

import org.json.simple.*;

import tester.*;
import tester.annotations.*;


// rules helpers to shorten code
final class PointsLogger extends JUnitWithPoints.PointsLogger {}
final class PointsSummary extends JUnitWithPoints.PointsSummary {}

public abstract class JUnitWithPoints {
	// these rules help to collect necessary information from test methods
	@RegisterExtension
	public final PointsLogger pointsLogger = new PointsLogger();
	@RegisterExtension
	public static final PointsSummary pointsSummary = new PointsSummary();

	// backend data structures
	private static final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
	private static final HashMap<String, List<ReportEntry>> reportHashMap = new HashMap<>();

	static {
		// set locale explicitly to avoid differences in reading/writing floats
		Locale.setDefault(Locale.US);
	}

	// shortens description if possible
	private static String getShortDisplayName(final ExtensionContext d) {
		String orig = d.getDisplayName();
		int ix = orig.indexOf('(');
		if (ix == -1) {
			return orig;
		}
		return orig.substring(0, ix);
	}

	// helper class for reports
	private static final class ReportEntry {
		ExtensionContext ctx;
		Throwable throwable;
		Points points;
		boolean skipped;
		long executionTime;

		private ReportEntry(ExtensionContext ctx, Points points, Throwable throwable, long executionTime) {
			this.ctx = ctx;
			this.throwable = throwable;
			this.points = points;
			this.executionTime = executionTime;
			this.skipped = false;
		}

		// we did skip this test method
		public ReportEntry(ExtensionContext ctx) {
			this.ctx = ctx;
			this.skipped = true;
		}

		// get sensible part/line of stack trace
		private String getStackTrace() {
			if (throwable == null
					|| throwable instanceof AssertionError
					|| throwable instanceof TimeoutException) {
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
		private String getComment(String comment, ExtensionContext ctx) {
			if (comment.equals("<n.a.>")) { // default value -> use short method name
				return getShortDisplayName(ctx);
			} else {
				return comment;
			}
		}

		// converts collected result to JSON
		private JSONObject toJSON() {
			boolean success = (throwable == null);
			JSONObject jsonTest = new JSONObject();
			jsonTest.put("id", getShortDisplayName(this.ctx));
			jsonTest.put("success", success);
			jsonTest.put("desc", getComment(points.comment(), this.ctx));
			if (System.getenv("AUDOSCORETIMINGS") != null) {
				jsonTest.put("executionTimeInMS", executionTime);
				if (this.ctx.getRequiredTestMethod().getAnnotation(Timeout.class) == null) {
					final Timeout timeout = this.ctx.getRequiredTestClass().getAnnotation(Timeout.class);
					jsonTest.put("timeout", timeout.unit().toMillis(timeout.value()));
				} else {
					final Timeout timeout = this.ctx.getRequiredTestMethod().getAnnotation(Timeout.class);
					jsonTest.put("timeout", timeout.unit().toMillis(timeout.value()));
				}
			}
			if (!success) {
				jsonTest.put("error", throwable.getClass().getSimpleName() + "("
						+ (throwable.getLocalizedMessage() != null
							? throwable.getLocalizedMessage()
							: "")
						+ ")" + getStackTrace());
			}
			return jsonTest;
		}
	}

	// helper class for logging purposes
	protected static class PointsLogger implements BeforeTestExecutionCallback,
			ExecutionCondition,
			InvocationInterceptor,
			TestWatcher {

		private long startTime = 0;
		private long endTime = 0;

		// test methods are ignored if their replace set is different to the specified one
		// FIXME: is that still necessary with single execution?
		protected boolean isIgnoredCase(final ExtensionContext ctx) {
			String doReplace = System.getProperty("replace");
			if ((doReplace != null && !doReplace.equals(""))) {
				String ncln = ReadReplace.getCanonicalReplacement(ctx);
				if (!doReplace.equals(ncln)) {
					return true;
				}
			}
			return false;
		}

		// test methods are skipped during single test method execution
		protected boolean isSkippedCase(final ExtensionContext ctx) {
			String methodToBeExecuted = System.getProperty("method");
			if (methodToBeExecuted != null && !methodToBeExecuted.equals("")) {
				String method = getShortDisplayName(ctx);
				if (!method.equals(methodToBeExecuted)) {
					return true;
				}
			}

			return false;
		}

		@Override
		public void beforeTestExecution(final ExtensionContext ctx) {
			performInitializeOnce(ctx);
			this.startTime = System.currentTimeMillis();
		}

		@Override
		public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext ctx) {
			if (isIgnoredCase(ctx) || isSkippedCase(ctx)) {
				return ConditionEvaluationResult.disabled("skipped");
			}
			return ConditionEvaluationResult.enabled("enabled");
		}

		@Override
		public void interceptTestMethod(InvocationInterceptor.Invocation<Void> invocation,
					ReflectiveInvocationContext<Method> invocationContext, ExtensionContext ctx)
				throws Throwable {

			long timeout;
			if (invocationContext.getExecutable().getAnnotation(Timeout.class) == null) {
				final Timeout annotation = invocationContext.getTargetClass().getAnnotation(Timeout.class);
				timeout = annotation.unit().toMillis(annotation.value());
			} else {
				final Timeout annotation = invocationContext.getExecutable().getAnnotation(Timeout.class);
				timeout = annotation.unit().toMillis(annotation.value());
			}

			final ExecutorService executorService = Executors.newFixedThreadPool(1);
			try {
				executorService.submit(() -> {
					try {
						invocation.proceed();
					} catch (Throwable e) {
						throw new RuntimeException(e);
					}
				}).get(timeout, TimeUnit.MILLISECONDS);
			} catch (final ExecutionException e) {
				throw e.getCause().getCause();
			} catch (final InterruptedException|TimeoutException e) {
				throw new TimeoutException(
						invocationContext.getExecutable().getName()
						+ "() timed out after "
						+ timeout
						+ " milliseconds");
			}
		}

		@Override
		public void testAborted(final ExtensionContext ctx, final Throwable cause) {
			testFailed(ctx, cause);
		}

		@Override
		public void testDisabled(final ExtensionContext ctx, final Optional<String> reason) {
			testFailed(ctx, null);
		}

		@Override
		public void testFailed(final ExtensionContext ctx, final Throwable cause) {
			this.endTime = System.currentTimeMillis();
			long executionTime  = this.endTime - this.startTime;
			Points pointsAnnotation = ctx.getRequiredTestMethod().getAnnotation(Points.class);
			String exID = pointsAnnotation.exID();
			if (isIgnoredCase(ctx) || isSkippedCase(ctx)) {
				reportHashMap.get(exID).add(new ReportEntry(ctx));
			} else {
				reportHashMap.get(exID).add(new ReportEntry(ctx, pointsAnnotation, cause, executionTime));
			}
		}

		@Override
		public void testSuccessful(final ExtensionContext ctx) {
			testFailed(ctx, null);
		}

		private void performInitializeOnce(final ExtensionContext ctx) {
			final Class<?> testClass = ctx.getRequiredTestClass();

			for (final Field f : testClass.getDeclaredFields()) {
				final InitializeOnce initOnce = f.getAnnotation(InitializeOnce.class);
				if (initOnce != null) {
					// @InitializeOnce field found. First, check if the result is
					// already computed
					final File initFile = new File(testClass.getCanonicalName()
							+ "-" + f.getName() + ".tmp");
					boolean recompute = !initFile.exists();
					if (!recompute) {
						// The result has been computed, just restore it
						try (ObjectInputStream in
								= new ObjectInputStream(new FileInputStream(initFile))) {
							f.set(null, in.readObject());
						} catch (final Exception e) {
							recompute = true;
						}
					}
					if (recompute) {
						// The result must be computed, stored in the field, and saved in
						// initFile
						try {
							final Object result = testClass
									.getDeclaredMethod(initOnce.value()).invoke(null);
							f.set(null, result);

							try (ObjectOutputStream out
									= new ObjectOutputStream(new FileOutputStream(initFile))) {
								out.writeObject(result);
							} catch (final Exception e) {
								initFile.delete(); // Clean up
							}
						} catch (final NoSuchMethodException e) {
							// Should be checked by CheckAnnotations
							throw new IllegalStateException(e);
						} catch (final InvocationTargetException e) {
							// This may be an exception in student code, so we make this
							// test fail
							Assertions.fail(String.valueOf(e.getCause()));
						} catch (final IllegalAccessException e) {
							// Internal error
							System.exit(1);
						}
					}
				}
			}
		}
	}

	// helper class for summaries
	protected static class PointsSummary implements BeforeAllCallback, AfterAllCallback {
		public static final int MAX_TIMEOUT_MS = 60_000;
		private static PrintStream saveOut;
		private static PrintStream saveErr;
		private static boolean isSecretClass = false;
		private List<String> safeCallerList;

		@Override
		public void afterAll(final ExtensionContext ctx) {

			// Reset security manager
			try {
				System.setSecurityManager(null);
			} catch (final SecurityException e) { /* Ignore */ }

			try {
				saveErr = new PrintStream(saveErr, true, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// silently ignore exception -> it's not that important after all
			}

			if (System.getProperty("json") != null
					&& System.getProperty("json").equals("yes")) {

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

				saveErr.println(jsonSummary);
			}
		}

		@Override
		public void beforeAll(final ExtensionContext ctx) {
			// reset states
			reportHashMap.clear();
			exerciseHashMap.clear();

			Exercises exercisesAnnotation = getExercisesAnnotation(ctx);

			// fill data structures
			for (Ex exercise : exercisesAnnotation.value()) {
				reportHashMap.put(exercise.exID(), new ArrayList<ReportEntry>());
				exerciseHashMap.put(exercise.exID(), exercise);
			}

			// Obtain a list of safe callers (i.e., callers that are known to
			// contain no malicious code)
			final SafeCallers safeCallerAnnotation
					= ctx.getRequiredTestClass().getAnnotation(SafeCallers.class);
			if (safeCallerAnnotation == null) {
				this.safeCallerList = Collections.emptyList();
			} else {
				this.safeCallerList = Arrays.asList(safeCallerAnnotation.value());
			}

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

		// returns @Exercises annotation of public test class (if specified) or current class (otherwise)
		public static Exercises getExercisesAnnotation(final ExtensionContext ctx) {
			return getExercisesAnnotation(ctx.getRequiredTestClass());
		}

		public static Exercises getExercisesAnnotation(final Class<?> clazz) {
			Class<?> publicTestClass = getPublicTestClass();
			if (publicTestClass == null) {
				return clazz.getAnnotation(Exercises.class);
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
	}
}
