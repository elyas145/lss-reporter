package Elyas.LssTestSheets.model;

import java.time.LocalDate;

import org.json.JSONObject;

public class Exam {

	private LocalDate date;
	private boolean isOriginal = true;
	private String location = "";

	public Exam() {
		date = LocalDate.MAX;
	}

	public Exam(JSONObject obj) {
		isOriginal = obj.optBoolean("isOriginal");
		location = obj.optString("location");
		String jsonDate = obj.optString("date");
		if (jsonDate != null) {
			date = LocalDate.parse(jsonDate);
		}

	}

	public void setDate(LocalDate value) {

		if (value != null && !this.date.equals(value)) {
			this.date = value;
			
		}

	}

	public void setOriginal(boolean b) {
		if (this.isOriginal != b) {
			this.isOriginal = b;
		}

	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("date", date.toString());
		object.put("isOriginal", isOriginal);
		object.put("location", location);
		return object;
	}

	public LocalDate getDate() {
		return date;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Exam) {
			Exam other = (Exam) obj;
			if (other.isOriginal == this.isOriginal && other.date.equals(this.date) && other.location.equals(this.location)) {
				return true;
			}
		}
		return false;
	}

	public boolean isOriginal() {
		return isOriginal;
	}

	public void validate(Warning warning) {
		if (date == null || date.equals(LocalDate.MAX)) {
			warning.add("Exam date not set.");
		}

	}

	public void setLocation(String loc){
		if(location == null || location.equals(loc)){
			return;
		}
		location = loc;
	}
	public String getLocation() {
		return location;
	}

}
