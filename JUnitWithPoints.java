import java.util.*;
import java.io.*;
import java.lang.annotation.*;

import org.junit.*;
import org.junit.internal.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.lang.reflect.*;
import tester.*;
import tester.annotations.*;


// ******************** RULES HELPER for pretty code **************************************** //
final class PointsLogger extends JUnitWithPoints.PointsLogger {
}

final class PointsSummary extends JUnitWithPoints.PointsSummary {
}

public abstract class JUnitWithPoints {
	// ******************** RULES **************************************** //
	@Rule
	public final PointsLogger pointsLogger = new PointsLogger();
	@ClassRule
	public final static PointsSummary pointsSummary = new PointsSummary();

	public final static String REPLACE_IGNORE_MSG = "this test case is not executed at all; ignoring it would make the point proportions incorrect -> FAIL!";

	// ******************** BACKEND FUNCTIONALITY **************************************** //
	private static final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
	private static final HashMap<String, List<ReportEntry>> reportHashMap = new HashMap<>();
	private static long timeoutSum = 0;

	static {
		Locale.setDefault(Locale.US);
	}

	private static String getShortDisplayName(Description d) {
		String orig = d.getDisplayName();
		int ix = orig.indexOf('(');
		if (ix == -1) return orig;
		return orig.substring(0, ix);
	}

	// -------------------------------------------------------------------------------- //
	private static final class ReportEntry {
		Description description;
		Throwable throwable;
		Points points;

		private ReportEntry(Description description, Points points, Throwable throwable) {
			this.description = description;
			this.throwable = throwable;
			this.points = points;
		}

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

		protected String getComment(String comment, Description description) {
			if (comment.equals("<n.a.>")) {
				return getShortDisplayName(description);
			} else {
				return comment;
			}
		}

		final JSONObject format(double bonusDeclaredPerExercise, double pointsDeclaredPerExercise) {
			if (throwable != null && throwable.getLocalizedMessage() != null && throwable.getLocalizedMessage().equals(JUnitWithPoints.REPLACE_IGNORE_MSG)) {
				return null;
			}

			if(PointsLogger.isSkippedCase(description)) {
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

	private static final class ReportEntryComparator implements Comparator<ReportEntry> {
		public int compare(ReportEntry r1, ReportEntry r2) {
			if (r1 == null && r2 == null) return 0;
			if (r1 == null) return -1;
			if (r2 == null) return 1;
			boolean t1 = r1.throwable == null;
			boolean t2 = r2.throwable == null;
			if (true || t1 == t2) return r1.description.getDisplayName().compareTo(r2.description.getDisplayName());
			if (t1) return +1;
			return -1;
		}
	}

	// -------------------------------------------------------------------------------- //
	protected static class PointsLogger extends TestWatcher {

		private PrintStream saveOut;
		private PrintStream saveErr;

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

		public static boolean isSkippedCase(Description description) {
			String methodToBeExecuted = System.getProperty("method");
			if((methodToBeExecuted != null && !methodToBeExecuted.equals(""))){
				String method = getShortDisplayName(description);
				if(!method.equals(methodToBeExecuted)){
					return true;
				}
			}

			return false;
		}

		public boolean isSingleExec() {
			return (System.getProperty("single") != null && System.getProperty("single").equals("yes"));
		}

		@Override
		public final Statement apply(Statement base, Description description) {
			if (isIgnoredCase(description) || isSkippedCase(description)) {
				base = new MyStatement();
			}
			return super.apply(base, description);
		}


		Set<Long> threadIdsBefore = new HashSet<>();

		@Override
		protected void starting(Description description) {
			try {
				// check if @SecretCase annotation is present
				SecretCase sc = (SecretCase) description.getAnnotation(SecretCase.class);
				if(sc != null){
					throw new AnnotationFormatError("WARNING - found test case with SECRETCASE annotation: [" + description.getDisplayName() + "]");

				}
				
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

				threadIdsBefore.clear();
				Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
				for(Thread t : threadSet) {
					threadIdsBefore.add(t.getId());
				}
				
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

				if(!isIgnoredCase(description) && !isSingleExec()) {
					System.gc();
					Thread.sleep(50);
					System.gc();
					Thread.sleep(50);
					System.gc();
				}

			} catch (Exception e) {
				throw new AnnotationFormatError(e.getMessage());
			}
		}

		@Override
		protected final void failed(Throwable throwable, Description description) {
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
			for (Thread t : threadSet) {
				if (t.isAlive() && t.isInterrupted() && !threadIdsBefore.contains(t.getId()) && t.getName().matches("Thread-\\d+")) {
					/* JUnit interrupts but does not stop threads, this leaves room for side effects between cases
					 * e.g. students might be writing infinite loops which still allocate ressources
					 * we try to find these hanging threads here and kill them 
					 * see: https://groups.yahoo.com/neo/groups/junit/conversations/messages/24565
					 */
					t.stop(); // XXX: yes, stop is deprecated, but: we don't use this to test parallel code
					try {
						// wait a bit until the thread has been finally destroyed
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}
			
			System.setOut(saveOut);
			System.setErr(saveErr);

			Bonus bonusAnnotation = description.getAnnotation(Bonus.class);
			Malus malusAnnotation = description.getAnnotation(Malus.class);
			Points pointsAnnotation = description.getAnnotation(Points.class);
			if (bonusAnnotation != null || malusAnnotation != null || pointsAnnotation == null ) {
					throw new AnnotationFormatError("WARNING - found testcase with BONUS/MALUS annotation or no POINTS annoation [" + description.getDisplayName() + "]");
				
			} else if (pointsAnnotation != null && pointsAnnotation.exID().trim().length() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with empty exercise id in POINTS annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation != null && !exerciseHashMap.containsKey(pointsAnnotation.exID())) {
				throw new AnnotationFormatError("WARNING - found test case with non-declared exercise id in POINTS annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation != null && (pointsAnnotation.malus() == 0 || pointsAnnotation.bonus() == 0)) {
				throw new AnnotationFormatError("WARNING - found test case with illegal malus value or illegal bonus value in POINTS annotation: [" + description.getDisplayName() + "]");
			} else if (pointsAnnotation != null && (pointsAnnotation.malus() == -1 && pointsAnnotation.bonus() == -1)) {
				throw new AnnotationFormatError("WARNING - found test case with no malus value and no bonus value in POINTS annotation: [" + description.getDisplayName() + "]");
			} else {
				String exID = null;
				
				if(pointsAnnotation != null){
					exID = pointsAnnotation.exID();
				}
				if (!reportHashMap.containsKey(exID)) {
					reportHashMap.put(exID, new ArrayList<ReportEntry>());
				}
				reportHashMap.get(exID).add(new ReportEntry(description, pointsAnnotation, throwable));
			}
		}

		@Override
		protected final void succeeded(Description description) {
			failed(null, description);
		}
	}

	// -------------------------------------------------------------------------------- //
	protected static class PointsSummary extends ExternalResource {
		@Override
		public final Statement apply(Statement base, Description description) {
			reportHashMap.clear();
			exerciseHashMap.clear();
			Exercises exercisesAnnotation;
			String pubclassName = System.getProperty("pub");
			Class pub;
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			if(pubclassName != null){
				try{
					pub = cl.loadClass(pubclassName);
					exercisesAnnotation = (Exercises) pub.getAnnotation(Exercises.class);
				}catch (ClassNotFoundException cnfe){
					throw new AnnotationFormatError("WARNING - pub class not found [" + description.getDisplayName() + "]");
				}
			}else{
				exercisesAnnotation = description.getAnnotation(Exercises.class);

			}
			if (exercisesAnnotation == null || exercisesAnnotation.value().length == 0) {
				throw new AnnotationFormatError("WARNING - found test set without exercise points declaration: [" + description.getDisplayName() + "]");
			}
			for (Ex exercise : exercisesAnnotation.value()) {
				if (exercise.exID().trim().length() == 0) {
					throw new AnnotationFormatError("WARNING - found exercise points declaration with empty exercise name and following points: [" + exercise.points() + "]");
				} else if (exercise.points() == 0) {
					throw new AnnotationFormatError("WARNING - found exercise points declaration with illegal points value: [" + exercise.exID() + "]");
				} else if (exerciseHashMap.containsKey(exercise.exID())) {
					throw new AnnotationFormatError("WARNING - found exercise points declaration with duplicate exercise: [" + exercise.exID() + "]");
				}
				exerciseHashMap.put(exercise.exID(), exercise);
			}
			return super.apply(base, description);
		}

		@Override
		protected final void after() {
			if (timeoutSum > 60_000) {
				throw new AnnotationFormatError("WARNING - total timeout sum is too high, please reduce to max. 60000ms\": [" + timeoutSum + "ms]");
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
			double pointsAchievedTotal = 0;
			for (String exerciseId : exerciseIds) {
				double bonusDeclaredPerExercise, bonusAchievedPerExercise, pointsDeclaredPerExercise, pointsAchievedPerExercise;
				JSONObject jsonexercise = new JSONObject();
				Ex exercise = exerciseHashMap.get(exerciseId);
				List<ReportEntry> reportPerExercise = reportHashMap.get(exerciseId);
				Collections.sort(reportPerExercise, new ReportEntryComparator());
				bonusDeclaredPerExercise = 0;
				for (ReportEntry reportEntry : reportPerExercise) {
					if(reportEntry.points != null){
						if(reportEntry.points.bonus() != -1){
							bonusDeclaredPerExercise += Math.abs(reportEntry.points.bonus());
						}
					}
				}
				pointsDeclaredPerExercise = exercise.points();
				bonusAchievedPerExercise = 0;
				JSONArray jsontests = new JSONArray();
				for (ReportEntry reportEntry : reportPerExercise) {
					Points points = reportEntry.points;
					Throwable throwable = reportEntry.throwable;
					JSONObject json = reportEntry.format(bonusDeclaredPerExercise, pointsDeclaredPerExercise);
					if (json != null) {
						jsontests.add(json);
					}
					if(points != null){
						if(points.bonus() != -1 && throwable == null){
							bonusAchievedPerExercise += Math.abs(points.bonus());
						}
						if(points.malus() != -1 && throwable != null){
							bonusAchievedPerExercise -= Math.abs(points.malus());
						}
					}
				}
				if (bonusDeclaredPerExercise <= 0) {
					throw new AnnotationFormatError("Declare at least one Bonus case per exercise.");
				}
				jsonexercise.put("tests", jsontests);
				bonusAchievedPerExercise = Math.min(bonusDeclaredPerExercise, Math.max(0, bonusAchievedPerExercise));
				pointsAchievedPerExercise = Math.ceil(pointsDeclaredPerExercise * 2 * bonusAchievedPerExercise / bonusDeclaredPerExercise) / 2;
				pointsAchievedTotal += pointsAchievedPerExercise;
				jsonexercise.put("name", exercise.exID());
				jsonexercises.add(jsonexercise);
			}
			jsonsummary.put("exercises", jsonexercises);
			if (System.getProperty("json") != null && System.getProperty("json").equals("yes")) {
				System.err.println(jsonsummary);
			} else {
				try {
					try (FileWriter fileWriter = new FileWriter(new File(System.getProperty("user.dir"), "autocomment.txt"))) {
						fileWriter.write(jsonsummary.toString());
					}
				} catch (Throwable t) {
					System.err.println(jsonsummary);
				}
			}
		}
	}
}

class MyStatement extends Statement {
	public void evaluate() {
		Assert.fail(JUnitWithPoints.REPLACE_IGNORE_MSG);
	}
}
