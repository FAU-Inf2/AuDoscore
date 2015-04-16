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

	public final static String SKIPPED_MSG = "This testcase is skipped intentionally.";

	// backend data structures
	private static final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
	private static final HashMap<String, List<ReportEntry>> reportHashMap = new HashMap<>();
	private static long timeoutSum = 0;

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

		private ReportEntry(Description description, Points points, Throwable throwable) {
			this.description = description;
			this.throwable = throwable;
			this.points = points;
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

		final JSONObject format(double bonusDeclaredPerExercise, double pointsDeclaredPerExercise) {
			if (throwable != null && throwable.getLocalizedMessage() != null && throwable.getLocalizedMessage().equals(JUnitWithPoints.SKIPPED_MSG)) {
				return null;
			}

			boolean success = (throwable == null);
			String desc = null;
			if(points != null){
				if(points.bonus() != -1){
					desc = getComment(points.comment(), description);
				}
			
				if(points.malus() != -1 && success){
					if(points.bonus() == -1){
						desc = getComment(points.comment(), description);
					}
				}
				if(points.malus() != -1 && !success){
					// in case of failure: overwrite bonus
					desc = getComment(points.comment(), description);
				}
			}
			JSONObject jsontest = new JSONObject();
			jsontest.put("id", getShortDisplayName(description));
			jsontest.put("success", (Boolean) (success));
			jsontest.put("desc", desc);
			if (!success) {
				jsontest.put("error", throwable.getClass().getSimpleName() + "(" + ((throwable.getLocalizedMessage() != null) ? throwable.getLocalizedMessage() : "") + ")" + getStackTrace());
			}
			return jsontest;
		}
	}

	// helper class for logging purposes
	protected static class PointsLogger extends TestWatcher {

		private static PrintStream saveOut, saveErr;

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
				base = new SkipStatement();
			}
			return super.apply(base, description);
		}

		@Override
		protected void starting(Description description) {
			try {
				Class tc = description.getTestClass();
				SecretClass st = (SecretClass) tc.getAnnotation(SecretClass.class);
				Replace r = description.getAnnotation(Replace.class);
				if(st == null && r != null){
					// @Replace in a public test
					throw new AnnotationFormatError("WARNING - found test case with REPLACE in a public test file: [" + description.getDisplayName() + "]");
				}

				Test testAnnotation = description.getAnnotation(Test.class);
				if (testAnnotation.timeout() == 0) {
					throw new AnnotationFormatError("WARNING - found test case without TIMEOUT in @Test annotation: [" + description.getDisplayName() + "]");
				}
				timeoutSum += testAnnotation.timeout();

				// disable stdout/stderr to avoid timeouts due to large debugging outputs
				if (saveOut == null) {
					saveOut = System.out;
					saveErr = System.err;

					System.setOut(new PrintStream(new OutputStream() {
						public void write(int i) {
						}
					}));

					System.setErr(new PrintStream(new OutputStream() {
						public void write(int i) {
						}
					}));
				}
			} catch (Exception e) {
				throw new AnnotationFormatError(e.getMessage());
			}
		}

		@Override
		protected final void failed(Throwable throwable, Description description) {
			Bonus bonusAnnotation = description.getAnnotation(Bonus.class);
			Malus malusAnnotation = description.getAnnotation(Malus.class);
			Points pointsAnnotation = description.getAnnotation(Points.class);
			if (bonusAnnotation != null || malusAnnotation != null) {
				throw new AnnotationFormatError("WARNING - found testcase with deprecated @Bonus/@Malus annotation [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation == null) {
				throw new AnnotationFormatError("WARNING - found testcase without @Points annoation [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.exID().trim().length() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with empty exercise id in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (!exerciseHashMap.containsKey(pointsAnnotation.exID())) {
				throw new AnnotationFormatError("WARNING - found test case with non-declared exercise id in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with illegal bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1) {
				throw new AnnotationFormatError("WARNING - found test case without bonus/malus value in @Points annotation: [" + description.getDisplayName() + "]");
			} else {
				String exID = pointsAnnotation.exID();
				if (!reportHashMap.containsKey(exID)) {
					reportHashMap.put(exID, new ArrayList<ReportEntry>());
				}
				reportHashMap.get(exID).add(new ReportEntry(description, pointsAnnotation, throwable));
			}
		}

		@Override
		protected void skipped(AssumptionViolatedException e, Description description) {
			Throwable t = new Throwable(JUnitWithPoints.SKIPPED_MSG);
			failed(t, description);
		}

		@Override
		protected final void succeeded(Description description) {
			failed(null, description);
		}
	}

	// helper class for summaries
	protected static class PointsSummary extends ExternalResource {
		public static final int MAX_TIMEOUT_MS = 60_000;

		@Override
		public final Statement apply(Statement base, Description description) {
			reportHashMap.clear();
			exerciseHashMap.clear();
			Exercises exercisesAnnotation;
			String pubClassName = System.getProperty("pub");
			if (pubClassName != null) {
				try {
					Class pub = ClassLoader.getSystemClassLoader().loadClass(pubClassName);
					exercisesAnnotation = (Exercises) pub.getAnnotation(Exercises.class);
				} catch (ClassNotFoundException e) {
					throw new AnnotationFormatError("WARNING - pub class not found [" + pubClassName + "]");
				}
			} else {
				exercisesAnnotation = description.getAnnotation(Exercises.class);

			}
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
				}
				exerciseHashMap.put(exercise.exID(), exercise);
			}
			return super.apply(base, description);
		}

		@Override
		protected final void after() {
			if (timeoutSum > MAX_TIMEOUT_MS) {
				throw new AnnotationFormatError("WARNING - total timeout sum is too high, please reduce to max. " + MAX_TIMEOUT_MS + "ms: [" + timeoutSum + "ms]");
			}
			for (String exerciseId : exerciseHashMap.keySet()) {
				if (!reportHashMap.containsKey(exerciseId)) {
					String doReplace = System.getProperty("replace");
					if (doReplace == null || doReplace.equals("")) {
						throw new AnnotationFormatError("WARNING - found exercise points declaration for exercise without test case: [" + exerciseId + "]");
					}
				}
			}
			String[] exerciseIds = reportHashMap.keySet().toArray(new String[0]);
			Arrays.sort(exerciseIds);
			JSONObject jsonsummary = new JSONObject();
			JSONArray jsonexercises = new JSONArray();
			for (String exerciseId : exerciseIds) {
				double bonusDeclaredPerExercise, pointsDeclaredPerExercise;
				JSONObject jsonexercise = new JSONObject();
				Ex exercise = exerciseHashMap.get(exerciseId);
				List<ReportEntry> reportPerExercise = reportHashMap.get(exerciseId);
				bonusDeclaredPerExercise = 0;
				for (ReportEntry reportEntry : reportPerExercise) {
					if(reportEntry.points != null){
						if(reportEntry.points.bonus() != -1){
							bonusDeclaredPerExercise += Math.abs(reportEntry.points.bonus());
						}
					}
				}
				pointsDeclaredPerExercise = exercise.points();
				JSONArray jsontests = new JSONArray();
				for (ReportEntry reportEntry : reportPerExercise) {
					JSONObject json = reportEntry.format(bonusDeclaredPerExercise, pointsDeclaredPerExercise);
					if (json != null) {
						jsontests.add(json);
					}
				}
				if (bonusDeclaredPerExercise <= 0) {
					throw new AnnotationFormatError("Declare at least one Bonus case per exercise.");
				}
				jsonexercise.put("tests", jsontests);
				jsonexercise.put("name", exercise.exID());
				jsonexercises.add(jsonexercise);
			}
			jsonsummary.put("exercises", jsonexercises);
			if (System.getProperty("json") != null && System.getProperty("json").equals("yes")) {
				PointsLogger.saveErr.println(jsonsummary);
			} else {
				try {
					try (FileWriter fileWriter = new FileWriter(new File(System.getProperty("user.dir"), "autocomment.txt"))) {
						fileWriter.write(jsonsummary.toString());
					}
				} catch (Throwable t) {
					PointsLogger.saveErr.println(jsonsummary);
				}
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
