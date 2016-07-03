package Elyas.LssTestSheets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReportDay {
	private Map<String, Boolean> attendance;
	private String generalNotes;
	private List<ReportQualification> qualifications;
	private int dayNumber;
	
	public ReportDay(int day) {
		this.dayNumber = day;
		generalNotes = "";
		this.attendance = new HashMap<>();
		for (Client client : Model.getInstance().getClients()) {
			attendance.put(client.getID(), false);
		}
		this.qualifications = new ArrayList<>();
		for(Qualification qualification : Model.getInstance().getCourse().getQualifications()){
			qualifications.add(new ReportQualification(qualification.getName()));
		}
	}

	public ReportDay(JSONObject obj) {
		setGeneralNote(obj.getString("general"));
		JSONObject jsonAttendance = obj.getJSONObject("attendance");
		attendance = new HashMap<>();
		for(String id : jsonAttendance.keySet()){
			attendance.put(id, jsonAttendance.getBoolean(id));
		}
		dayNumber = obj.getInt("day");
		JSONArray jsonQuals = obj.getJSONArray("quals");
		qualifications = new ArrayList<>();
		for(int i = 0; i < jsonQuals.length(); i++){
			JSONObject jsonQual = jsonQuals.getJSONObject(i);
			qualifications.add(new ReportQualification(jsonQual));
		}
		
	}

	public void setNumber(int dayNumber) {
		this.dayNumber = dayNumber;		
	}

	public Map<String, Boolean> getAttendace() {
		return attendance;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		JSONObject att = new JSONObject();
		for(String key : attendance.keySet()){
			att.put(key, attendance.get(key));
		}
		object.put("attendance", att);
		object.put("general", generalNotes);
		object.put("day", String.valueOf(dayNumber));
		JSONArray array = new JSONArray();
		for(ReportQualification qualification : qualifications){
			array.put(qualification.toJSON());
		}
		object.put("quals", array);
		return object;
	}

	public void setGeneralNote(String text) {
		this.generalNotes = text;
		
	}

	public void setAttendance(Client client, boolean isHere) {
		attendance.put(client.getID(), isHere);		
	}

	public List<ReportQualification> getQualifications() {
		return this.qualifications;
	}

	public void setQualNote(ReportQualification qualification, Client client, String note) {
		qualifications.get(qualifications.indexOf(qualification)).setNote(client, note);
		
	}

	public String getQualNote(ReportQualification qualification, Client client) {
		
		return qualifications.get(qualifications.indexOf(qualification)).getNote(client);
	}

	public int getNumber() {
		return dayNumber;
	}

	public String getGeneralNotes() {
		return generalNotes;
	}

	public boolean isPresent(String id) {
		return attendance.get(id);
	}
}
