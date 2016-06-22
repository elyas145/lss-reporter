package Elyas.LssTestSheets.model;

import java.util.Collection;

import org.json.JSONObject;

public class MustSee {
	String field;
	String name;
	String item;
	String type;
	boolean appEvaluated;
	boolean instructorEvaluated;
	boolean examinerEvaluated;
	public boolean isCompleted;

	public MustSee(JSONObject obj) {
		field = obj.optString("field");
		name = obj.optString("name");
		item = obj.optString("item");
		type = obj.optString("type");
		instructorEvaluated = obj.has("instructor-evaluated");
		appEvaluated = obj.has("app-evaluated");
		examinerEvaluated = obj.has("examiner-evaluated");
		isCompleted = obj.has("completed");
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAppEvaluated() {
		return appEvaluated;
	}

	public void setAppEvaluated(boolean appEvaluated) {
		this.appEvaluated = appEvaluated;
	}

	public boolean isInstructorEvaluated() {
		return instructorEvaluated;
	}

	public void setInstructorEvaluated(boolean instructorEvaluated) {
		this.instructorEvaluated = instructorEvaluated;
	}

	public boolean isExaminerEvaluated() {
		return examinerEvaluated;
	}

	public void setExaminerEvaluated(boolean examinerEvaluated) {
		this.examinerEvaluated = examinerEvaluated;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String toString() {
		if (instructorEvaluated)
			return "* " + this.name;
		return this.name;
	}

	public MustSee() {
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("completed", isCompleted);
		object.put("item", item);
		return object;
	}
}
