import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import java.lang.annotation.*;
import java.lang.reflect.*;

import tester.annotations.*;

public class JUnitPointsMerger {
    private static final class SingleReport {
        boolean success;
        String description;
        String message;
    }


    private static final class SingleReportComparator implements Comparator<SingleReport> {
        public int compare(SingleReport r1, SingleReport r2) {
            if (r1 == null && r2 == null) return 0;
            if (r1 == null) return -1;
            if (r2 == null) return 1;
            boolean t1 = r1.success;
            boolean t2 = r2.success;
            if (t1 == t2) {
                if (r1.description.compareTo(r2.description) == 0) {
                    return r1.message.compareTo(r2.message);
                }
                return r1.description.compareTo(r2.description);
            }
            if (t1) return +1;
            return -1;
        }
    }

    static {
        Locale.setDefault(Locale.US);
    }

    private static String summary = "";
    private static double points = 0;
    private static Class pub = null;
    private static Class secret = null;
    private static final HashMap<String, Ex> exerciseHashMap = new HashMap<>();
    private static final HashMap<String, Double> bonusPerExHashMap = new HashMap<>();


    private static double getPoints(double pts, double pointsDeclaredPerExercise, double bonusDeclaredPerExercise) {
        return pointsDeclaredPerExercise * Math.abs(pts) / bonusDeclaredPerExercise;
    }

    // extract points annotation from either secret or public class and calculate the point
    private static double getLocalPoint(Boolean success, String id, Boolean fromSecret) {

        Method method = null;
        Points points = null;

        if (fromSecret) {
            // test method originated from a secret test
            try {
                method = secret.getMethod(id);
                points = (Points) method.getAnnotation(Points.class);
            } catch (NoSuchMethodException nsme) {
                throw new Error("WARNING - Method " + method.getName() + " was not found in secret test class " + pub.getName());
            }
        } else {
            try {
                method = pub.getMethod(id);
                points = (Points) method.getAnnotation(Points.class);
            } catch (NoSuchMethodException nsme) {
                throw new Error("WARNING - Method " + method.getName() + " was not found in public test class " + pub.getName());
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


    private static void merge(ArrayList<JSONObject> rexs, JSONObject vex) { // merges two exercises
        ArrayList<SingleReport> reps = new ArrayList<>();
        double localpoints = 0;
        JSONArray vextests = (JSONArray) vex.get("tests");
        /* FIXME: this is not longer true, try to create a better check soon ;-)
        int cnt = 0;
		for (JSONObject rex : rexs) {
			cnt++;
			JSONArray rextests = (JSONArray) rex.get("tests");
			if (rextests.size() != vextests.size()) {
				throw new RuntimeException("vanilla and #" + cnt + " of replaced do have different number of tests (" + vextests.size() + " vs. " + rextests.size() + ") for exercise " + rex.get("name"));
			}
		}
		*/

        JSONObject rexCounterpart = null;
        for (int i = 0; i < vextests.size(); i++) {
            JSONObject vextest = (JSONObject) vextests.get(i);
            JSONObject usedresult = vextest;
            for (JSONObject rexIt : rexs) {
                boolean found = false;
                JSONArray rextests = (JSONArray) rexIt.get("tests");
                for (int j = 0; !found && j < rextests.size(); j++) {
                    JSONObject rextest = (JSONObject) rextests.get(j);
                    if (rextest.get("id").equals(vextest.get("id"))) {
                        rexCounterpart = rextest;
                        if ((Boolean) rextest.get("success")) {
                            usedresult = rextest;
                        }
                        found = true;
                    }
                }
                /* FIXME: this is not longer true, see above
				   if (!found) {
				   throw new RuntimeException("could not find " + vextest.get("id") + " in replaced tests");
				   }
				   */
            }
            String localSummary = "";
            if ((Boolean) vextest.get("success")) {
                usedresult = vextest;
            }

            double localscore = getLocalPoint((Boolean) usedresult.get("success"), (String) usedresult.get("id"), (Boolean) usedresult.get("fromSecret"));
            localpoints += localscore;
            localSummary += ((Boolean) usedresult.get("success")) ? "✓" : "✗";

            localSummary += String.format(" %1$6.2f", localscore) + " | ";
            localSummary += (String) usedresult.get("desc");
            Object error = usedresult.get("error");
            if (error != null) {
                localSummary += " | " + (String) error;
            }

            String replaceErrorProperty = System.getProperty("replaceError");
            if (replaceErrorProperty != null && replaceErrorProperty.equals("true")) {
                if (rexCounterpart != null) {
                    Object replaceError = rexCounterpart.get("error");
                    if (replaceError != null) {
                        localSummary += " | " + (String) replaceError;
                    }
                }

            }

            localSummary += "\n";

            SingleReport r = new SingleReport();
            r.success = ((Boolean) usedresult.get("success"));
            r.message = localSummary;
            r.description = (String) usedresult.get("id");
            reps.add(r);
        }
        localpoints += 0.00001; // XXX: add epsilon here
        localpoints = Math.max(0., localpoints);
        localpoints = Math.floor(2. * localpoints) / 2; // round down to half points
        localpoints = Math.min(localpoints, exerciseHashMap.get(vex.get("name")).points());
        points += localpoints;
        summary += "\n" + (String) vex.get("name");
        summary += String.format(" (%1$.1f points):", localpoints) + "\n";
        Collections.sort(reps, new SingleReportComparator());
        for (SingleReport r : reps) {
            summary += r.message;
        }
    }

    private static boolean isJSONArray(Object obj) {
        return (obj instanceof JSONArray);
    }

    private static void preparePointsCalc(JSONArray vanillaex) {
        exerciseHashMap.clear();
        Exercises exercisesAnnotation;

        // get the public class name via -D param
        if (System.getProperty("pub") != null) {
            // load public test
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            try {
                pub = cl.loadClass(System.getProperty("pub"));
                exercisesAnnotation = (Exercises) pub.getAnnotation(Exercises.class);
                for (Ex exercise : exercisesAnnotation.value()) {
                    // save Exercises in HashMap
                    exerciseHashMap.put(exercise.exID(), exercise);
                    bonusPerExHashMap.put(exercise.exID(), 0.0);
                }
                // get sum of bonus
                for (Method method : pub.getMethods()) {
                    if (method.isAnnotationPresent(Points.class)) {
                        Points points = (Points) method.getAnnotation(Points.class);
                        if (points.bonus() != -1) {
                            double bonusPts = bonusPerExHashMap.get(points.exID());
                            bonusPts += points.bonus();
                            bonusPerExHashMap.put(points.exID(), bonusPts);
                        }
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                throw new Error("WARNING - public test class not found: " + System.getProperty("pub"));
            }
        }

        if (System.getProperty("secret") != null) {
            //load secret test
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            try {
                secret = cl.loadClass(System.getProperty("secret"));
                for (Method method : secret.getMethods()) {
                    if (method.isAnnotationPresent(Points.class)) {
                        Points points = (Points) method.getAnnotation(Points.class);
                        if (points.bonus() != -1) {
                            double bonusPts = bonusPerExHashMap.get(points.exID());
                            bonusPts += points.bonus();
                            bonusPerExHashMap.put(points.exID(), bonusPts);
                        }
                    }
                }
            } catch (ClassNotFoundException cnfe) {
                throw new Error("WARNING - secret test class not found");
            }

        }
    }


    private static JSONObject recursiveMergeJArray(JSONArray raw) {
        if (raw.size() < 1) {
            return new JSONObject();
        }

        if (raw.size() == 1) {
            return (JSONObject) raw.get(0);
        }

        if (raw.size() == 2) {
            return baseMergeJArray((JSONObject) raw.get(0), (JSONObject) raw.get(1));
        }

        int half = raw.size() / 2;
        JSONArray firsthalf = new JSONArray();
        JSONArray secondhalf = new JSONArray();

        for (int i = 0; i < half; i++) {
            firsthalf.add(raw.get(i));
        }

        for (int i = 0; i < raw.size() - half; i++) {
            secondhalf.add(raw.get(half + i));
        }

        JSONObject a = recursiveMergeJArray(firsthalf);
        JSONObject b = recursiveMergeJArray(secondhalf);

        return baseMergeJArray(a, b);

    }

    private static JSONObject baseMergeJArray(JSONObject o1, JSONObject o2) {

        if (o1 == null || o1.isEmpty()) {
            return o2;
        }

        if (o2 == null || o2.isEmpty()) {
            return o1;
        }
        JSONArray vanillaex1 = (JSONArray) o1.get("exercises");
        JSONArray vanillaex2 = (JSONArray) o2.get("exercises");
        for (int i = 0; i < vanillaex1.size(); i++) {
            JSONObject vex1 = (JSONObject) vanillaex1.get(i);
            for (int j = 0; j < vanillaex2.size(); j++) {
                JSONObject vex2 = (JSONObject) vanillaex2.get(j);
                if (vex1.get("name").equals(vex2.get("name"))) {
                    // if the name of the exercises matches
                    JSONArray tests = (JSONArray) vex1.get("tests");
                    tests.addAll((JSONArray) vex2.get("tests"));
                    vanillaex2.remove(vex2);
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
        JSONObject result = recursiveMergeJArray((JSONArray) rawVanilla);
        return result;
    }

    private static JSONArray mergeReplaced(JSONArray rawReplaced) {
        JSONArray replaced = new JSONArray();
        for (int i = 0; i < rawReplaced.size(); i++) {
            if (isJSONArray(rawReplaced.get(i))) {
                JSONObject result = recursiveMergeJArray((JSONArray) rawReplaced.get(i));
                replaced.add(result);
            } else {
                replaced.add(rawReplaced.get(i));
            }
        }

        return replaced;
    }

    public static void main(String[] args) throws Exception {
        String inputFile = (args.length == 2) ? args[0] : "result.json";
        String outputFile = (args.length == 2) ? args[1] : "mergedcomment.txt";
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader(inputFile));
            Object rawVanilla = obj.get("vanilla");

            JSONObject vanilla = mergeVanilla(rawVanilla);
            JSONArray vanillaex = (JSONArray) vanilla.get("exercises");
            preparePointsCalc(vanillaex);

            JSONArray replaceds = mergeReplaced((JSONArray) obj.get("replaced"));

            for (int i = 0; i < vanillaex.size(); i++) {
                JSONObject vex = (JSONObject) vanillaex.get(i);
                ArrayList<JSONObject> rexs = new ArrayList<>();
                for (int k = 0; k < replaceds.size(); k++) {
                    JSONObject replaced = (JSONObject) replaceds.get(k);
                    JSONArray replacedex = (JSONArray) replaced.get("exercises");
                    if (vanillaex.size() != replacedex.size()) {
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
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(summary);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("invalid json");
            throw e;
        }
    }
}
