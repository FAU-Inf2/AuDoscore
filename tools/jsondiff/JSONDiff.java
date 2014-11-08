package tools.jsondiff;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Cuong Bui <cuong.bui@fau.de>
 * @version
 * @since 2014-10-31
 */
public class JSONDiff {

	/**
	 * Prints the usage message
	 * 
	 * */
	private static void usage() {
		System.out
				.println("Usage: java JSONDiff pathToFirstJsonFile pathToSecondJsonFile");
		System.exit(-1);
	}

	/**
	 * Checks if the Object is of type JSONArray
	 * 
	 * @param obj
	 *            object to be checked
	 * @return true if the object is a JSONArray otherwise false
	 * 
	 * */
	private static boolean isJSONArray(Object obj) {
		return (obj instanceof JSONArray);
	}

	/**
	 * Checks if the Object is of type JSONObject
	 * 
	 * @param obj
	 *            object to be checked
	 * @return true if the object is a JSONObject otherwise false
	 * */
	private static boolean isJSONObject(Object obj) {
		return (obj instanceof JSONObject);
	}

	/**
	 * Checks if the Object is of type Boolean, String or Number
	 * 
	 * @param obj
	 *            object to be checked
	 * @return true if the object matches with one of the types described
	 * 
	 * */
	private static boolean isJSONElement(Object obj) {
		if ((obj instanceof String) || (obj instanceof Number)
				|| (obj instanceof Boolean)) {
			return true;
		}
		return false;
	}

	/**
	 * Compares JSONArrays or JSONObjects recursively ignoring the order of the
	 * elements within the object.
	 * 
	 * @param jo1
	 *            first object
	 * @param jo2
	 *            second object
	 * @return 0 if equal otherwise 1
	 * 
	 * */
	private static int compare(Object jo1, Object jo2) {

		if (isJSONElement(jo1) && isJSONElement(jo2)) {
			if (!jo1.equals(jo2)) {
				return 1;
			}
			return 0;
		}

		if (isJSONArray(jo1) && isJSONArray(jo2)) {
			JSONArray a1 = (JSONArray) jo1;
			JSONArray a2 = (JSONArray) jo2;

			if (a1.size() != a2.size()) {
				return 1;
			}
			int equal = 1;

			/*
			 * compare each element of a1 with each element of a2, because the
			 * order is maybe not the same
			 */
			for (int i = 0; i < a1.size(); i++) {
				for (int j = 0; j < a2.size(); j++) {
					equal = compare(a1.get(i), a2.get(j));
					if (equal == 0) {
						break;
					}
				}

				if (equal == 1) {
					return 1;
				}
			}
		} else if (isJSONObject(jo1) && isJSONObject(jo2)) {
			JSONObject j1 = (JSONObject) jo1;
			JSONObject j2 = (JSONObject) jo2;
			if (j1.size() != j2.size()) {
				return 1;
			}

			@SuppressWarnings("unchecked")
			Set<String> keys = new HashSet<String>(j1.keySet());
			for (String key : keys) {
			
				Object value1 = j1.get(key);
				Object value2 = j2.get(key);
				if (compare(value1, value2) == 1) {
					return 1;
				}
			}

		} else {
			return 1;
		}
		return 0;
	}

	/**
	 * The main method is where the files which contains the JSONString are
	 * parsed and passed to the compare() method.
	 * 
	 * @param args
	 *            contains the paths to the files which contains the JSONStrings
	 * 
	 */
	public static void main(String[] args) {
		if (args == null || args.length != 2) {
			usage();
		}

		String pathToJson1 = args[0];
		String pathToJson2 = args[1];

		try {
			Object obj1 = new JSONParser().parse(new FileReader(pathToJson1));
			Object obj2 = new JSONParser().parse(new FileReader(pathToJson2));

			int equal = compare(obj1, obj2);
			System.exit(equal);

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}
}
