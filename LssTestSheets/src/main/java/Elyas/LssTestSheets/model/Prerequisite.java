package Elyas.LssTestSheets.model;

import org.json.JSONObject;

public class Prerequisite {

	public enum Type {
		DATE, CHECK
	};

	/**
	 * should only be used to get the meta data about a test sheet from a json
	 * file.
	 * 
	 * @param obj
	 */
	public Prerequisite(JSONObject obj) {
		key = obj.optString("key");
		name = obj.getString("name");
		dateEarned = obj.optString("date-earned");
		location = obj.optString("location");
		met = obj.optBoolean("met");
		if (dateEarned.trim().equals("") && location.trim().equals("")) {
			type = Type.CHECK;
		} else {
			type = Type.DATE;
		}

	}

	public Prerequisite(Prerequisite prerequisite) {
		this.key = new String(prerequisite.key);
		this.name = new String(prerequisite.name);
		this.type = prerequisite.type;
		this.met = prerequisite.met;
	}

	public Prerequisite() {
		// TODO Auto-generated constructor stub
	}

	public boolean isMet() {
		return met;
	}

	public Type getType() {
		return type;
	}

	public String getDate() {
		return dateEarned;
	}

	public String getLocation() {
		return location;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDateEarned() {
		return dateEarned;
	}

	public void setDateEarned(String dateEarned) {
		if (dateEarned != null)
			this.dateEarned = dateEarned;
	}

	public void setLocation(String location) {
		if (location != null)
			this.location = location;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setMet(boolean met) {
		this.met = met;
	}

	public void setDefaultMet() {
		met = dateEarned != null && !dateEarned.trim().equals("") && location != null && !location.trim().equals("");
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("name", name);
		object.put("dateEarned", dateEarned);
		object.put("location", location);
		object.put("met", met);
		return object;
	}

	String key;
	String name;
	String dateEarned;
	String location;
	Type type;
	boolean met;
}
