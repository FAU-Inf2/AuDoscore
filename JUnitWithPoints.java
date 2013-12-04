import java.util.*;
import java.io.*;
import java.lang.annotation.*;

import org.junit.*;
import org.junit.rules.*;
import org.junit.runner.*;
import org.junit.runners.model.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.lang.reflect.*;
import tester.*;

// ******************** ANNOTATIONS **************************************** //
@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface Exercises {
	Ex[] value();
}

@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface Ex {
	String exID();

	double points();

	String comment() default "<n.a.>";
}

@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface Forbidden {
	String[] value();
}

@Inherited
@Target(java.lang.annotation.ElementType.TYPE)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface NotForbidden {
	String[] value();
}

// -------------------------------------------------------------------------------- //
@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface Bonus {
	String exID();

	double bonus();

	String comment() default "<n.a.>";
}

@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface Malus {
	String exID();

	double malus();

	String comment() default "<n.a.>";
}

@Inherited
@Target(java.lang.annotation.ElementType.METHOD)
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@interface SecretCase {
}

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
		Bonus bonus;
		Malus malus;
		Throwable throwable;

		private ReportEntry(Description description, Bonus bonus, Malus malus, Throwable throwable) {
			this.description = description;
			this.bonus = bonus;
			this.malus = malus;
			this.throwable = throwable;
		}

		private String getStackTrace() {
			if (throwable == null || throwable instanceof AssertionError) return "";
			StackTraceElement st[] = throwable.getStackTrace();
			if (st.length == 0) return "";
			StackTraceElement ste = st[0]; // TODO: maybe search for student code here
			return ": " + ste.getClassName() + "." + ste.getMethodName() + "(line " + ste.getLineNumber() + ")";
		}

		protected String getComment(String comment, Description description) {
			if (comment.equals("<n.a.>")) {
				return getShortDisplayName(description);
			} else {
				return comment;
			}
		}

		protected double getPoints(double pts, double pointsDeclaredPerExercise, double bonusDeclaredPerExercise) {
			return pointsDeclaredPerExercise * Math.abs(pts) / bonusDeclaredPerExercise;
		}

		final void format(double bonusDeclaredPerExercise, double pointsDeclaredPerExercise, JSONArray tests) {
			if (throwable != null && throwable.getLocalizedMessage() != null && throwable.getLocalizedMessage().equals(JUnitWithPoints.REPLACE_IGNORE_MSG)) {
				// FIXME maybe test for json
				return;
			}

			boolean success = (throwable == null);
			String desc = null;
			double score = 0;

			if (bonus != null) {
				desc = getComment(bonus.comment(), description);
			}
			if (bonus != null && throwable == null) {
				score = getPoints(bonus.bonus(), pointsDeclaredPerExercise, bonusDeclaredPerExercise);
			}
			if (malus != null && throwable == null) {
				if (bonus == null) { // only set comment if nothing was added before, points already default to zero
					desc = getComment(malus.comment(), description);
				}
			}
			if (malus != null && throwable != null) {
				// in case of failure: overwrite bonus
				score = -getPoints(malus.malus(), pointsDeclaredPerExercise, bonusDeclaredPerExercise);
				desc = getComment(malus.comment(), description);
			}

			JSONObject jsontest = new JSONObject();
			jsontest.put("id", getShortDisplayName(description));
			jsontest.put("success", (Boolean) (success));
			jsontest.put("desc", desc);
			jsontest.put("score", ((Double) score).toString());
			if (!success) {
				jsontest.put("error", throwable.getClass().getSimpleName() + "(" + ((throwable.getLocalizedMessage() != null) ? throwable.getLocalizedMessage() : "") + ")" + getStackTrace());
			}
			tests.add(jsontest);
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

		@Override
		public final Statement apply(Statement base, Description description) {
			if (isIgnoredCase(description)) {
				base = new MyStatement();
			}
			return super.apply(base, description);
		}

		@Override
		protected void starting(Description description) {
			try {
				Test testAnnotation = description.getAnnotation(Test.class);
				if (testAnnotation.timeout() == 0) {
					throw new AnnotationFormatError("WARNING - found test case without TIMEOUT in @Test annotation: [" + description.getDisplayName() + "]");
				}
				timeoutSum += testAnnotation.timeout();

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

				if (!isIgnoredCase(description)) {
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
			System.setOut(saveOut);
			System.setErr(saveErr);

			Bonus bonusAnnotation = description.getAnnotation(Bonus.class);
			Malus malusAnnotation = description.getAnnotation(Malus.class);
			if (bonusAnnotation == null && malusAnnotation == null) {
				throw new AnnotationFormatError("WARNING - found test case without BONUS or MALUS annotation: [" + description.getDisplayName() + "]");
			} else if (bonusAnnotation != null && bonusAnnotation.exID().trim().length() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with empty exercise id in BONUS annotation: [" + description.getDisplayName() + "]");
			} else if (bonusAnnotation != null && !exerciseHashMap.containsKey(bonusAnnotation.exID())) {
				throw new AnnotationFormatError("WARNING - found test case with non-declared exercise id in BONUS annotation: [" + description.getDisplayName() + "]");
			} else if (bonusAnnotation != null && bonusAnnotation.bonus() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with illegal bonus value in BONUS annotation: [" + description.getDisplayName() + "]");
			} else if (malusAnnotation != null && malusAnnotation.exID().trim().length() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with empty exercise id in MALUS annotation: [" + description.getDisplayName() + "]");
			} else if (malusAnnotation != null && !exerciseHashMap.containsKey(malusAnnotation.exID())) {
				throw new AnnotationFormatError("WARNING - found test case with non-declared exercise id in MALUS annotation: [" + description.getDisplayName() + "]");
			} else if (malusAnnotation != null && malusAnnotation.malus() == 0) {
				throw new AnnotationFormatError("WARNING - found test case with illegal malus value in MALUS annotation: [" + description.getDisplayName() + "]");
			} else {
				String exID = null;
				if (bonusAnnotation != null && malusAnnotation != null) {
					if (!bonusAnnotation.exID().equals(malusAnnotation.exID())){
						throw new AnnotationFormatError("WARNING - found test case with different exercise id in MALUS/BONUS annotations: [" + description.getDisplayName() + "]");
					}
				}
				if (bonusAnnotation != null) {
					exID = bonusAnnotation.exID();
				}
				if (malusAnnotation != null) {
					exID = malusAnnotation.exID();
				}
				if (!reportHashMap.containsKey(exID)) {
					reportHashMap.put(exID, new ArrayList<ReportEntry>());
				}
				reportHashMap.get(exID).add(new ReportEntry(description, bonusAnnotation, malusAnnotation, throwable));
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
			Exercises exercisesAnnotation = description.getAnnotation(Exercises.class);
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
				throw new AnnotationFormatError("WARNING - total timeout sum is too high for \"Tobis judge hosts\": [" + timeoutSum + "ms]");
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
					if (reportEntry.bonus != null) {
						bonusDeclaredPerExercise += Math.abs(reportEntry.bonus.bonus());
					}
				}
				pointsDeclaredPerExercise = exercise.points();
				bonusAchievedPerExercise = 0;
				JSONArray jsontests = new JSONArray();
				for (ReportEntry reportEntry : reportPerExercise) {
					Bonus bonus = reportEntry.bonus;
					Malus malus = reportEntry.malus;
					Throwable throwable = reportEntry.throwable;
					if (bonus != null && throwable == null) {
						bonusAchievedPerExercise += Math.abs(bonus.bonus());
					}
					if (malus != null && throwable != null) {
						bonusAchievedPerExercise -= Math.abs(malus.malus());
					}
				}
				jsonexercise.put("tests", jsontests);
				bonusAchievedPerExercise = Math.min(bonusDeclaredPerExercise, Math.max(0, bonusAchievedPerExercise));
				pointsAchievedPerExercise = Math.ceil(pointsDeclaredPerExercise * 2 * bonusAchievedPerExercise / bonusDeclaredPerExercise) / 2;
				pointsAchievedTotal += pointsAchievedPerExercise;
				jsonexercise.put("possiblePts", ((Double) exercise.points()).toString());
				jsonexercise.put("name", exercise.exID());
				jsonexercise.put("score", String.format("%1$.1f", pointsAchievedPerExercise));
				jsonexercises.add(jsonexercise);
			}
			jsonsummary.put("exercises", jsonexercises);
			jsonsummary.put("score", String.format("%1$.1f", pointsAchievedTotal));
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
