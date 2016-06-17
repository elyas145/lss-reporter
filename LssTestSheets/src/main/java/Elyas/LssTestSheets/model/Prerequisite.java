package Elyas.LssTestSheets.model;

import org.json.JSONObject;

public class Prerequisite {

	String key;
	String name;
	String dateEarned;
	String location;

	/**
	 * should only be used to get the meta data about a test sheet from a json
	 * file.
	 * 
	 * @param obj
	 */
	public Prerequisite(JSONObject obj) {
		key = obj.getString("key");
		name = obj.getString("name");
		dateEarned = obj.getString("date-earned");
		location = obj.getString("location");
	}

	public boolean isMet() {
		if (dateEarned != null && !dateEarned.equals("") && location != null && !location.equals("")) {
			return true;
		}
		return false;
	}

}
