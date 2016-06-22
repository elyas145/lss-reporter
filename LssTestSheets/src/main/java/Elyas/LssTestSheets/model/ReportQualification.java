package Elyas.LssTestSheets.model;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class ReportQualification {
	private Map<String, String> notes;
	private String name;

	public ReportQualification() {
		notes = new HashMap<>();
	}

	public ReportQualification(String name) {
		this.name = name;
		this.notes = new HashMap<>();
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("name", name);
		JSONObject note = new JSONObject();
		for (String key : notes.keySet()) {
			note.put(key, notes.get(key));
		}
		object.put("notes", note);
		return object;
	}

	public String getName() {
		return name;
	}

	public void setNote(Client client, String note) {
		notes.put(client.getID(), note);

	}

	public String getNote(Client client) {
		if (notes.get(client.getID()) != null)
			return notes.get(client.getID());
		return "";
	}
}
