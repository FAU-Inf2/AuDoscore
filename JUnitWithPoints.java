import java.lang.reflect.Method;
import java.util.*;
import java.io.*;
import java.lang.annotation.*;

import org.junit.*;
import org.junit.internal.*;
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
	public final static PointsSummary pointsSummary = new PointsSummary();

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
		if (ix == -1) return orig;
		return orig.substring(0, ix);
	}

	// helper class for reports
	private static final class ReportEntry {
		Description description;
		Throwable throwable;
		Points points;
		boolean skipped;
		boolean fromSecretTest;

		private ReportEntry(Description description, Points points, Throwable throwable) {
			this.description = description;
			this.throwable = throwable;
			this.points = points;
			this.skipped = false;
		}

		// we did skip this test method
		public ReportEntry(Description description) {
			this.skipped = true;
		}

		// get sensible part/line of stack trace
		private String getStackTrace() {
			if (throwable == null || throwable instanceof AssertionError) return "";
			StackTraceElement st[] = throwable.getStackTrace();
			if (st.length == 0) return "";
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
			if (!success) {
				jsonTest.put("error", throwable.getClass().getSimpleName() + "(" + ((throwable.getLocalizedMessage() != null) ? throwable.getLocalizedMessage() : "") + ")" + getStackTrace());
			}
			return jsonTest;
		}
	}

	// helper class for logging purposes
	protected static class PointsLogger extends TestWatcher {

		private static PrintStream saveOut, saveErr;

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
			}
			return super.apply(base, description);
		}

		@Override
		protected void starting(Description description) {
            // disable stdout/stderr to avoid timeouts due to large debugging outputs
            if (saveOut == null) {
                saveOut = System.out;
                saveErr = System.err;

                System.setOut(new PrintStream(new OutputStream() {
                    public void write(int i) { }
                }));
                System.setErr(System.out);
            }
		}

		@Override
		protected final void failed(Throwable throwable, Description description) {
			Points pointsAnnotation = description.getAnnotation(Points.class);
			String exID = pointsAnnotation.exID();
			if (isIgnoredCase(description) || isSkippedCase(description)) {
				reportHashMap.get(exID).add(new ReportEntry(description));
			} else {
				reportHashMap.get(exID).add(new ReportEntry(description, pointsAnnotation, throwable));
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
	}

	// helper class for summaries
	protected static class PointsSummary extends ExternalResource {
		public static final int MAX_TIMEOUT_MS = 60_000;
		private static boolean isSecretClass = false;

		@Override
		public final Statement apply(Statement base, Description description) {
			// reset states
			reportHashMap.clear();
			exerciseHashMap.clear();

			Exercises exercisesAnnotation = getExercisesAnnotation(description);
			// checkAnnotations also fills exerciseHashMap
			checkAnnotations(description, exercisesAnnotation);

			// fill data structures
			for (Ex exercise : exercisesAnnotation.value()) {
				reportHashMap.put(exercise.exID(), new ArrayList<ReportEntry>());
			}

			// start the real JUnit test
			return super.apply(base, description);
		}

		// returns @Exercises annotation of public test class (if specified) or current class (otherwise)
		private Exercises getExercisesAnnotation(Description description) {
			Class<?> publicTestClass = getPublicTestClass();
			if (publicTestClass == null) {
				return description.getAnnotation(Exercises.class);
			} else {
				return publicTestClass.getAnnotation(Exercises.class);
			}
		}

		// returns public test class (if specified)
		private Class<?> getPublicTestClass() {
			String pubClassName = System.getProperty("pub");
			if (pubClassName == null) {
				return null;
			}
			try {
				return ClassLoader.getSystemClassLoader().loadClass(pubClassName);
			} catch (ClassNotFoundException e) {
				throw new AnnotationFormatError("WARNING - pub class specified, but not found [" + pubClassName + "]");
			}
		}

		// checks all annotation conditions
		// fills exerciseHashMap as side effect
		private void checkAnnotations(Description description, Exercises exercisesAnnotation) {
			// check annotations on class level
			if (exercisesAnnotation == null || exercisesAnnotation.value().length == 0) {
				throw new AnnotationFormatError("WARNING - did not find valid @Exercises declaration: [" + description.getDisplayName() + "]");
			}
			for (Ex exercise : exercisesAnnotation.value()) {
				if (exercise.exID().trim().length() == 0) {
					throw new AnnotationFormatError("WARNING - found @Exercises annotation with empty exercise name and following points: [" + exercise.points() + "]");
				} else if (exercise.points() <= 0) {
					throw new AnnotationFormatError("WARNING - found @Exercises annotation with illegal points value: [" + exercise.exID() + "]");
				} else if (exerciseHashMap.containsKey(exercise.exID())) {
					throw new AnnotationFormatError("WARNING - found @Exercises annotation with duplicate exercise: [" + exercise.exID() + "]");
				} else {
					exerciseHashMap.put(exercise.exID(), exercise);
				}
			}

			// check annotations on method level
			long timeoutSum = 0;
			HashSet<String> usedExercises = new HashSet<>(), bonusExercises = new HashSet<>();
			Class<?> clazz = description.getTestClass();
			SecretClass secretClassAnnotation = clazz.getAnnotation(SecretClass.class);
			isSecretClass = secretClassAnnotation != null;
			for (Method m : clazz.getMethods()) {
				Test test = m.getAnnotation(Test.class);
				if (test == null) {
					continue;
				}
				if (test.timeout() == 0) {
					throw new AnnotationFormatError("WARNING - found test case without 'timeout' in @Test annotation: [" + description.getDisplayName() + "]");
				}
				timeoutSum += test.timeout();

				Bonus bonusAnnotation = m.getAnnotation(Bonus.class);
				Malus malusAnnotation = m.getAnnotation(Malus.class);
				Points pointsAnnotation = m.getAnnotation(Points.class);
				Replace replaceAnnotation = m.getAnnotation(Replace.class);
				SecretCase secretCaseAnnotation = m.getAnnotation(SecretCase.class);

				if(secretCaseAnnotation != null && !isSecretClass){
					throw new AnnotationFormatError("WARNING - found test case with deprecated @SecretCase annotation in public test [" + description.getDisplayName() + "]");
				}

				if (bonusAnnotation != null || malusAnnotation != null) {
					throw new AnnotationFormatError("WARNING - found test case with deprecated @Bonus/@Malus annotation [" + description.getDisplayName() + "]");
				} else if (pointsAnnotation == null) {
					throw new AnnotationFormatError("WARNING - found test case without @Points annotation [" + description.getDisplayName() + "]");
				} else if (!isSecretClass && replaceAnnotation != null) {
					throw new AnnotationFormatError("WARNING - found test case with @Replace in a public test class: [" + description.getDisplayName() + "]");
				} else if (pointsAnnotation.exID().trim().length() == 0) {
					throw new AnnotationFormatError("WARNING - found test case with empty exercise id in @Points annotation: [" + description.getDisplayName() + "]");
				} else if (!exerciseHashMap.containsKey(pointsAnnotation.exID())) {
					throw new AnnotationFormatError("WARNING - found test case with non-declared exercise id in @Points annotation: [" + description.getDisplayName() + "]");
				} else if (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0) {
					throw new AnnotationFormatError("WARNING - found test case with illegal bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
				} else if (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1) {
					throw new AnnotationFormatError("WARNING - found test case without bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
				} else if (pointsAnnotation.bonus() != -1) {
					bonusExercises.add(pointsAnnotation.exID());
				}
				usedExercises.add(pointsAnnotation.exID());
			}
			if (usedExercises.size() != exerciseHashMap.size()) {
				throw new AnnotationFormatError("WARNING - found @Ex declaration without corresponding test method: [" + description.getDisplayName() + "]");
			}
			if (bonusExercises.size() != exerciseHashMap.size()) {
				throw new AnnotationFormatError("WARNING - found @Ex declaration without test method with bonus values: [" + description.getDisplayName() + "]");
			}
			if (timeoutSum > MAX_TIMEOUT_MS) {
				throw new AnnotationFormatError("WARNING - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
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
							reportJSON.put("fromSecret",isSecretClass);
							jsonTests.add(reportJSON);
						}
					}

					// collect result
					JSONObject jsonExercise = new JSONObject();
					jsonExercise.put("name", exerciseResults.getKey());
					jsonExercise.put("tests", jsonTests);
					jsonExercises.add(jsonExercise);
				}

				// add results to root node and write to stderr
				JSONObject jsonSummary = new JSONObject();
				jsonSummary.put("exercises", jsonExercises);
				PointsLogger.saveErr.println(jsonSummary);
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
