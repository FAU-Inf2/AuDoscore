import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

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
	private static void merge(JSONObject rex, JSONObject vex) { // merges two exercises
		ArrayList<SingleReport> reps = new ArrayList<>();
		double localpoints = 0;
		JSONArray rextests = (JSONArray) rex.get("tests");
		JSONArray vextests = (JSONArray) vex.get("tests");
		if (rextests.size() != vextests.size()) {
			throw new RuntimeException("vanilla and replaced do have different number of tests for exercise " + rex.get("name"));
		}
		for (int i = 0; i < vextests.size(); i++) {
			JSONObject vextest = (JSONObject) vextests.get(i);
			boolean found = false;
			for (int j = 0; !found && j < rextests.size(); j++) {
				JSONObject rextest = (JSONObject) rextests.get(i);
				if (rextest.get("id").equals(vextest.get("id"))) {
					String localSummary = "";
					found = true;
					JSONObject usedresult = null;
					if ((Boolean) vextest.get("success") || !((Boolean) rextest.get("success"))) {
						usedresult = vextest;
					} else {
						usedresult = rextest;
					}
					double localscore = Double.parseDouble((String) usedresult.get("score"));
					localpoints += localscore;
					localSummary += ((Boolean) usedresult.get("success")) ? "✓" : "✗";

					localSummary += String.format(" %1$6.2f", localscore) + " | ";
					localSummary += (String) usedresult.get("desc");
					Object error = usedresult.get("error");
					if (error != null) {
						localSummary += " | " + (String) error;
					}
					localSummary += "\n";

					SingleReport r = new SingleReport();
					r.success = ((Boolean) usedresult.get("success"));
					r.message = localSummary;
					r.description = (String)rextest.get("id");
					reps.add(r);
				}
			}
			if (!found) {
				throw new RuntimeException("could not find " + vextest.get("id") + " in replaced tests");
			}
		}
		localpoints = Math.max(0., localpoints);
		localpoints = Math.ceil(2. * localpoints) / 2; // round up to half points
		points += localpoints;
		summary += "\n" + (String) vex.get("name");
		summary += String.format(" (%1$.1f points):", localpoints) + "\n";
		Collections.sort(reps, new SingleReportComparator());
		for (SingleReport r : reps) {
			summary += r.message;
		}
	}

	public static void main(String[] args) throws Exception {
		String inputFile = (args.length == 2) ? args[0] : "result.json";
		String outputFile = (args.length == 2) ? args[1] : "mergedcomment.txt";
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj  = (JSONObject) parser.parse(new FileReader(inputFile));
			JSONObject vanilla = (JSONObject) obj.get("vanilla");
			JSONArray vanillaex = (JSONArray) vanilla.get("exercises");
			JSONObject replaced = (JSONObject) obj.get("replaced");
			JSONArray replacedex = (JSONArray) replaced.get("exercises");
			if (vanillaex.size() != replacedex.size()) {
				throw new RuntimeException("vanilla and replaced do have different number of exercises");
			}
			for (int i = 0; i < vanillaex.size(); i++) {
				JSONObject vex = (JSONObject) vanillaex.get(i);
				boolean found = false;
				for (int j = 0; !found && j < replacedex.size(); j++) {
					JSONObject rex = (JSONObject) replacedex.get(j);
					if (rex.get("name").equals(vex.get("name"))) {
						found = true;
						merge(rex,vex);
					}
				}
				if (!found) {
					throw new RuntimeException("could not find " + vex.get("name") + " in replaced exercises");
				}
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
