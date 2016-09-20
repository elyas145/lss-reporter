package Elyas.LssTestSheets.model;

import java.time.LocalDate;

import org.json.JSONObject;

public class Exam {

	private LocalDate date;
	private boolean isOriginal = true;

	public Exam() {
		date = LocalDate.MAX;
	}

	public Exam(JSONObject obj) {
		isOriginal = obj.optBoolean("isOriginal");
		String jsonDate = obj.optString("date");
		if (jsonDate != null) {
			date = LocalDate.parse(jsonDate);
		}

	}

	public void setDate(LocalDate value) {

		if (value != null && !this.date.equals(value)) {
			this.date = value;
			Model.getInstance().setChanged("Exam Date Changed.");
		}

	}

	public void setOriginal(boolean b) {
		if (this.isOriginal != b) {
			this.isOriginal = b;
			Model.getInstance().setChanged("Exam Originality Changed.");
		}

	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("date", date.toString());
		object.put("isOriginal", isOriginal);
		return object;
	}

	public LocalDate getDate() {
		return date;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Exam) {
			if (((Exam) obj).toJSON().toString().equals(this.toJSON().toString())) {
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

}
