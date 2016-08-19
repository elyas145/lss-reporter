package Elyas.LssTestSheets.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Report {
	private Map<String, String> generalNotes;
	private ArrayList<ReportDay> days;

	public void removeDay(ReportDay day) {
		for (ReportDay reportDay : days) {
			if (reportDay.equals(day)) {
				days.remove(reportDay);
				break;
			}
		}

	}

	public Report() {
		generalNotes = new HashMap<>();
		days = new ArrayList<>();
	}

	public Report(JSONObject obj) {
		if (obj != null) {
			JSONObject jsonGeneral = obj.getJSONObject("general");
			generalNotes = new HashMap<>();
			for (String id : jsonGeneral.keySet()) {
				generalNotes.put(id, jsonGeneral.getString(id));
			}

			JSONArray jsonDays = obj.getJSONArray("days");
			days = new ArrayList<>();
			for (int i = 0; i < jsonDays.length(); i++) {
				JSONObject jsonDay = jsonDays.getJSONObject(i);
				days.add(new ReportDay(jsonDay));
			}
		}
	}

	public String getGeneralNote(Client client) {
		return generalNotes.get(client.getID());

	}

	public void setGeneralNote(Client client, String note) {
		generalNotes.put(client.getID(), note);

	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		JSONObject general = new JSONObject();
		for (String key : generalNotes.keySet()) {
			general.put(key, generalNotes.get(key));
		}
		object.put("general", general);
		JSONArray array = new JSONArray();
		for (ReportDay day : days) {
			array.put(day.toJSON());
		}
		object.put("days", array);
		return object;
	}

	public void setAttendance(ReportDay day, Client client, boolean isHere) {
		days.get(days.indexOf(day)).setAttendance(client, isHere);

	}

	public void addDay(ReportDay day) {
		days.add(day);

	}

	public List<ReportDay> getDays() {
		return days;
	}

	public Integer getAbsenceCount(String id) {
		int i = 0;
		for (ReportDay reportDay : days) {
			if (!reportDay.isPresent(id)) {
				i++;
			}
		}
		return i;
	}

}
