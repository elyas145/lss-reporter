package Elyas.LssTestSheets.model;


import org.json.JSONObject;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

@Deprecated
public class ReportQualification {
	private ObservableMap<String, String> notes;
	private String name;

	public ReportQualification() {
		init(null);
	}

	public ReportQualification(String name, MapChangeListener<String, String> changeListener) {
		this.name = name;
		init(changeListener);
		
	}

	public ReportQualification(JSONObject obj, MapChangeListener<String, String> changeListener) {
		name = obj.getString("name");
		JSONObject jsonNotes = obj.getJSONObject("notes");
		init(changeListener);
		for (String id : jsonNotes.keySet()) {
			notes.put(id, jsonNotes.getString(id));
		}
	}
	
	private void init(MapChangeListener<String, String> changeListener){
		this.notes = FXCollections.observableHashMap();
		if(changeListener != null){
			notes.addListener(changeListener);
		}
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
	
	public void addChangeListener(MapChangeListener<String, String> changeListener){
		notes.addListener(changeListener);
	}
}
