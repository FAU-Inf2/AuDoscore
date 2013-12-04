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
	private static void merge(ArrayList<JSONObject> rexs, JSONObject vex) { // merges two exercises
		ArrayList<SingleReport> reps = new ArrayList<>();
		double localpoints = 0;
		JSONArray vextests = (JSONArray) vex.get("tests");
		/*
		FIXME: this is not longer true, try to create a better check soon ;-)
		int cnt = 0;
		for (JSONObject rex : rexs) {
			cnt++;
			JSONArray rextests = (JSONArray) rex.get("tests");
			if (rextests.size() != vextests.size()) {
				throw new RuntimeException("vanilla and #" + cnt + " of replaced do have different number of tests (" + vextests.size() + " vs. " + rextests.size() + ") for exercise " + rex.get("name"));
			}
		}
		*/
		for (int i = 0; i < vextests.size(); i++) {
			JSONObject vextest = (JSONObject) vextests.get(i);
			JSONObject usedresult = vextest;
			for (JSONObject rexIt : rexs) {
				boolean found = false;
				JSONArray rextests = (JSONArray) rexIt.get("tests");
				for (int j = 0; !found && j < rextests.size(); j++) {
					JSONObject rextest = (JSONObject) rextests.get(j);
					if (rextest.get("id").equals(vextest.get("id"))) {
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
			r.description = (String)usedresult.get("id");
			reps.add(r);
		}
		localpoints = Math.max(0., localpoints);
		localpoints = Math.ceil(2. * localpoints) / 2; // round up to half points
		localpoints = Math.min(localpoints, Double.parseDouble((String) vex.get("possiblePts")));
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
			JSONArray replaceds = (JSONArray) obj.get("replaced");

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
				merge(rexs,vex);
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
