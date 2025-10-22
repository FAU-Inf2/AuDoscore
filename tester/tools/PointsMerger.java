package tester.tools;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import tester.annotations.*;

public class PointsMerger {
	private static final class SingleReport {
		boolean success;
		String description;
		String message;
	}

	private static final class SingleReportComparator implements Comparator<SingleReport> {
		public int compare(SingleReport r1, SingleReport r2) {
			if (r1 == null && r2 == null) {
				return 0;
			}
			if (r1 == null) {
				return -1;
			}
			if (r2 == null) {
				return 1;
			}
			boolean t1 = r1.success;
			boolean t2 = r2.success;
			if (t1 == t2) {
				if (r1.description.compareTo(r2.description) == 0) {
					return r1.message.compareTo(r2.message);
				}
				return r1.description.compareTo(r2.description);
			}
			if (t1) {
				return +1;
			}
			return -1;
		}
	}

	static {
		Locale.setDefault(Locale.US);
	}

	private static String summary = "";
	private static double points = 0;
	private static Class<?> pub = null;
	private static Class<?> secret = null;
	private static final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
	private static final HashMap<String, Double> bonusPerExHashMap = new HashMap<>();

	private static double getPoints(double pts, double pointsDeclaredPerExercise, double bonusDeclaredPerExercise) {
		return pointsDeclaredPerExercise * Math.abs(pts) / bonusDeclaredPerExercise;
	}

	// extract points annotation from either secret or public class and calculate the point
	private static double getLocalPoint(Boolean success, String id, Boolean fromSecret) {
		Points points;
		if (fromSecret) {
			// test method originated from a secret test
			try {
				final Method method = secret.getMethod(id);
				points = method.getAnnotation(Points.class);
			} catch (NoSuchMethodException noSuchMethodException) {
				throw new Error("WARNING - Method " + id + " was not found in secret test class " + secret.getName());
			}
		} else {
			try {
				final Method method = pub.getMethod(id);
				points = method.getAnnotation(Points.class);
			} catch (NoSuchMethodException noSuchMethodException) {
				throw new Error("WARNING - Method " + id + " was not found in public test class " + pub.getName());
			}
		}
		double score = 0;
		if (points.bonus() != -1 && success) {
			score = getPoints(points.bonus(), exerciseHashMap.get(points.exID()).points(), bonusPerExHashMap.get(points.exID()));
		}
		if (points.malus() != -1 && !success) {
			score = -getPoints(points.malus(), exerciseHashMap.get(points.exID()).points(), bonusPerExHashMap.get(points.exID()));
		}
		return score;
	}

	private static String getFormattedErrorString(String error) {
		if (error.length() <= 1000 || !error.startsWith("ComparisonFailure")) {
			return error;
		}
		// we have a very long ComparisonFailure -> try to "compress" it
		final int expectedPos = error.indexOf("expected:<");
		if (expectedPos < 0) {
			return error; // unexpected error format
		}
		final int butWasPos = error.indexOf("> but was:<", expectedPos);
		if (butWasPos < 0) {
			return error; // unexpected error format
		}
		final int expectedDiffStart = error.indexOf('[', expectedPos);
		final int expectedDiffEnd = expectedDiffStart < 0 ? -1 : error.lastIndexOf(']', butWasPos);
		final int butWasDiffStart = error.indexOf('[', butWasPos);
		final int butWasDiffEnd = butWasDiffStart < 0 ? -1 : error.lastIndexOf(']');
		final StringBuilder resultBuilder = new StringBuilder();
		// expected:
		if (expectedDiffEnd > expectedPos) {
			if (expectedDiffEnd - expectedDiffStart > 50) {
				resultBuilder.append(error, 0, expectedDiffStart + 11).append(">...<").append(error, expectedDiffEnd - 10, butWasPos);
			} else {
				resultBuilder.append(error, 0, butWasPos);
			}
		} else {
			resultBuilder.append(error, 0, butWasPos);
		}
		// but was:
		if (butWasDiffEnd > butWasPos) {
			if (butWasDiffEnd - butWasDiffStart > 50) {
				resultBuilder.append(error, butWasPos, butWasDiffStart + 11).append(">...<").append(error.substring(butWasDiffEnd - 10));
			} else {
				resultBuilder.append(error.substring(butWasPos));
			}
		} else {
			resultBuilder.append(error.substring(butWasPos));
		}
		return resultBuilder.toString();
	}

	private static void merge(ArrayList<JSONObject> replacedExercises, JSONObject vanillaExercise) { // merges two exercises
		ArrayList<SingleReport> reports = new ArrayList<>();
		double localPoints = 0;
		JSONArray vanillaExerciseTests = (JSONArray) vanillaExercise.get("tests");
		/* FIXME: this is not longer true, try to create a better check soon ;-)
		   int cnt = 0;
		   for (JSONObject rex : replacedExercises) {
		   cnt++;
		   JSONArray rextests = (JSONArray) rex.get("tests");
		   if (rextests.size() != vanillaExerciseTests.size()) {
		   throw new RuntimeException("vanilla and #" + cnt + " of replaced do have different number of tests (" + vanillaExerciseTests.size() + " vs. " + rextests.size() + ") for exercise " + rex.get("name"));
		   }
		   }
		 */
		JSONObject rexCounterpart = null;
		for (Object exerciseTest : vanillaExerciseTests) {
			JSONObject vanillaExerciseTest = (JSONObject) exerciseTest;
			JSONObject usedResult = vanillaExerciseTest;
			for (JSONObject rexIt : replacedExercises) {
				boolean found = false;
				JSONArray replacedExerciseTests = (JSONArray) rexIt.get("tests");
				for (int j = 0; !found && j < replacedExerciseTests.size(); j++) {
					JSONObject replacedExerciseTest = (JSONObject) replacedExerciseTests.get(j);
					if (replacedExerciseTest.get("id").equals(vanillaExerciseTest.get("id"))) {
						rexCounterpart = replacedExerciseTest;
						if ((Boolean) replacedExerciseTest.get("success")) {
							usedResult = replacedExerciseTest;
						}
						found = true;
					}
				}
				/* FIXME: this is not longer true, see above
				   if (!found) {
				   throw new RuntimeException("could not find " + vanillaExerciseTest.get("id") + " in replaced tests");
				   }
				 */
			}
			String localSummary = "";
			if ((Boolean) vanillaExerciseTest.get("success")) {
				usedResult = vanillaExerciseTest;
			}
			double localScore = getLocalPoint((Boolean) usedResult.get("success"), (String) usedResult.get("id"), (Boolean) usedResult.get("fromSecret"));
			localPoints += localScore;
			localSummary += ((Boolean) usedResult.get("success")) ? "✓" : "✗";
			localSummary += String.format(" %1$6.2f", localScore) + " | ";
			localSummary += (String) usedResult.get("desc");
			Object error = usedResult.get("error");
			if (error != null) {
				localSummary += " | " + getFormattedErrorString((String) error);
			}
			String replaceErrorProperty = System.getProperty("replaceError");
			if (replaceErrorProperty != null && replaceErrorProperty.equals("true")) {
				if (rexCounterpart != null) {
					Object replaceError = rexCounterpart.get("error");
					if (replaceError != null) {
						localSummary += " | " + getFormattedErrorString((String) replaceError);
					}
				}

			}
			Long execTime = (Long) usedResult.get("executionTimeInMS");
			if (execTime != null) {
				Long timeout = (Long) usedResult.get("timeout");
				localSummary += " | " + execTime + "ms (of " + timeout + "ms)";
			}
			localSummary += "\n";
			SingleReport r = new SingleReport();
			r.success = ((Boolean) usedResult.get("success"));
			r.message = localSummary;
			r.description = (String) usedResult.get("id");
			reports.add(r);
		}
		localPoints += 0.00001; // XXX: add epsilon here
		localPoints = Math.max(0., localPoints);
		localPoints = Math.floor(2. * localPoints) / 2; // round down to half points
		localPoints = Math.min(localPoints, exerciseHashMap.get((String) vanillaExercise.get("name")).points());
		points += localPoints;
		summary += "\n" + vanillaExercise.get("name");
		summary += String.format(" (%1$.1f points):", localPoints) + "\n";
		reports.sort(new SingleReportComparator());
		StringBuilder reportMessages = new StringBuilder();
		for (SingleReport report : reports) {
			reportMessages.append(report.message);
		}
		summary += reportMessages;
	}

	private static boolean isJSONArray(Object obj) {
		return (obj instanceof JSONArray);
	}

	private static void preparePointsCalc() {
		exerciseHashMap.clear();
		Exercises exercisesAnnotation;
		// get the public class name via -D param
		if (System.getProperty("pub") != null) {
			// load public test
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			try {
				pub = cl.loadClass(System.getProperty("pub"));
				exercisesAnnotation = pub.getAnnotation(Exercises.class);
				for (Ex exercise : exercisesAnnotation.value()) {
					// save Exercises in HashMap
					exerciseHashMap.put(exercise.exID(), exercise);
					bonusPerExHashMap.put(exercise.exID(), 0.0);
				}
				// get sum of bonus
				for (Method method : pub.getMethods()) {
					if (method.isAnnotationPresent(Points.class)) {
						Points points = method.getAnnotation(Points.class);
						if (points.bonus() != -1) {
							double bonusPts = bonusPerExHashMap.get(points.exID());
							bonusPts += points.bonus();
							bonusPerExHashMap.put(points.exID(), bonusPts);
						}
					}
				}
			} catch (ClassNotFoundException classNotFoundException) {
				throw new Error("WARNING - public test class not found: " + System.getProperty("pub"));
			}
		}
		if (System.getProperty("secret") != null) {
			// load secret test
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			try {
				secret = cl.loadClass(System.getProperty("secret"));
				for (Method method : secret.getMethods()) {
					if (method.isAnnotationPresent(Points.class)) {
						Points points = method.getAnnotation(Points.class);
						if (points.bonus() != -1) {
							double bonusPts = bonusPerExHashMap.get(points.exID());
							bonusPts += points.bonus();
							bonusPerExHashMap.put(points.exID(), bonusPts);
						}
					}
				}
			} catch (ClassNotFoundException classNotFoundException) {
				throw new Error("WARNING - secret test class not found");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static JSONObject recursiveMergeJArray(JSONArray raw) {
		if (raw.isEmpty()) {
			return new JSONObject();
		}
		if (raw.size() == 1) {
			return (JSONObject) raw.getFirst();
		}
		if (raw.size() == 2) {
			return baseMergeJArray((JSONObject) raw.get(0), (JSONObject) raw.get(1));
		}
		int half = raw.size() / 2;
		JSONArray firstHalf = new JSONArray();
		JSONArray secondHalf = new JSONArray();
		for (int i = 0; i < half; i++) {
			firstHalf.add(raw.get(i));
		}
		for (int i = 0; i < raw.size() - half; i++) {
			secondHalf.add(raw.get(half + i));
		}
		JSONObject a = recursiveMergeJArray(firstHalf);
		JSONObject b = recursiveMergeJArray(secondHalf);
		return baseMergeJArray(a, b);
	}

	@SuppressWarnings("unchecked")
	private static JSONObject baseMergeJArray(JSONObject o1, JSONObject o2) {
		if (o1 == null || o1.isEmpty()) {
			return o2;
		}
		if (o2 == null || o2.isEmpty()) {
			return o1;
		}
		JSONArray vanillaExercises1 = (JSONArray) o1.get("exercises");
		JSONArray vanillaExercises2 = (JSONArray) o2.get("exercises");
		for (Object vanillaExercise1_Object : vanillaExercises1) {
			JSONObject vanillaExercise1 = (JSONObject) vanillaExercise1_Object;
			for (int j = 0; j < vanillaExercises2.size(); j++) {
				JSONObject vanillaExercise2 = (JSONObject) vanillaExercises2.get(j);
				if (vanillaExercise1.get("name").equals(vanillaExercise2.get("name"))) {
					// if the names of the exercises match
					JSONArray tests = (JSONArray) vanillaExercise1.get("tests");
					tests.addAll((JSONArray) vanillaExercise2.get("tests"));
					vanillaExercises2.remove(vanillaExercise2);
					break;
				}
			}
		}
		return o1;
	}

	private static JSONObject mergeVanilla(Object rawVanilla) {
		if (!isJSONArray(rawVanilla)) {
			return (JSONObject) rawVanilla;
		}
		return recursiveMergeJArray((JSONArray) rawVanilla);
	}

	@SuppressWarnings("unchecked")
	private static JSONArray mergeReplaced(JSONArray rawReplaced) {
		JSONArray replaced = new JSONArray();
		for (Object o : rawReplaced) {
			if (isJSONArray(o)) {
				JSONObject result = recursiveMergeJArray((JSONArray) o);
				replaced.add(result);
			} else {
				replaced.add(o);
			}
		}
		return replaced;
	}

	static void main(String[] args) throws Exception {
		String inputFile = (args.length == 2) ? args[0] : "result.json";
		String outputFile = (args.length == 2) ? args[1] : "mergedcomment.txt";
		JSONParser parser = new JSONParser();
		try (final Reader reader = Files.newBufferedReader(Paths.get(inputFile))) {
			JSONObject jsonObject = (JSONObject) parser.parse(reader);
			JSONArray vanillaResultsJSON = (JSONArray) mergeVanilla(jsonObject.get("vanilla")).get("exercises");
			preparePointsCalc();
			JSONArray replacedResultsJSON = mergeReplaced((JSONArray) jsonObject.get("replaced"));
			TreeMap<String, Integer> sortedEx = new TreeMap<>();
			for (int i = 0; i < vanillaResultsJSON.size(); i++) {
				JSONObject vex = (JSONObject) vanillaResultsJSON.get(i);
				String name = (String) vex.get("name");
				sortedEx.put(name + "__" + i, i);
			}
			for (Integer i : sortedEx.values()) {
				JSONObject vex = (JSONObject) vanillaResultsJSON.get(i);
				ArrayList<JSONObject> rexs = new ArrayList<>();
				for (int k = 0; k < replacedResultsJSON.size(); k++) {
					JSONObject replaced = (JSONObject) replacedResultsJSON.get(k);
					JSONArray replacedex = (JSONArray) replaced.get("exercises");
					if (vanillaResultsJSON.size() != replacedex.size()) {
						throw new RuntimeException("vanilla and replaced #" + k + " do have different number of exercises");
					}
					boolean found = false;
					for (int j = 0; !found && j < replacedex.size(); j++) {
						JSONObject rex = (JSONObject) replacedex.get(j);
						if (rex.get("name").equals(vex.get("name"))) {
							found = true;
							rexs.add(rex);
						}
					}
					if (!found) {
						throw new RuntimeException("could not find " + vex.get("name") + " in replaced exercises #" + k);
					}
				}
				merge(rexs, vex);
			}
			summary = "Score: " + String.format("%1$.1f\n", points) + summary;
			File file = new File(outputFile);
			if (!file.exists()) {
				if (!file.createNewFile()) {
					throw new IOException("Cannot create file '" + file + "'");
				}
			}
			try (final Writer bw = Files.newBufferedWriter(file.getAbsoluteFile().toPath(), StandardCharsets.UTF_8)) {
				bw.write(summary);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("invalid json");
			throw e;
		}
	}
}
