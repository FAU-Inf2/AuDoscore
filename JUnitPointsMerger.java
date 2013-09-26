import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class JUnitPointsMerger {
	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj  = (JSONObject) parser.parse(new FileReader("result.json")); // FIXME: make this configurable
			JSONObject vanilla = (JSONObject) obj.get("vanilla");
			JSONArray vanillaex = (JSONArray) vanilla.get("exercises");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
