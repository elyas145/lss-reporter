package Elyas.LssTestSheets.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MetaEventListener;

import org.apache.commons.lang3.StringUtils;
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
		instructorEvaluated = obj.optBoolean("instructor-evaluated");
		appEvaluated = obj.optBoolean("app-evaluated");
		examinerEvaluated = obj.optBoolean("examiner-evaluated");
		isCompleted = obj.optBoolean("completed");
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
			return "* " + StringUtils.capitalize(this.name);
		return StringUtils.capitalize(this.name);
	}

	public MustSee() {
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("completed", isCompleted);
		object.put("item", item);
		return object;
	}

	public void evaluate(Client client, String course) {
		if (item.equals("result")) {
			isCompleted = true;
			for (MustSee see : client.getMustSees(course)) {
				if (see.isExaminerEvaluated() || see.isInstructorEvaluated()) {
					if (!see.isCompleted()) {
						isCompleted = false;
					}
				}
			}
		} else if (item.equals("prereqs")) {
			String met = client.getPrerequisitesMet(course).toLowerCase();
			if (met.equals("checked") || met.equals("n/a")){
				isCompleted = true;
			}else{
				isCompleted = false;
			}
		}

	}
}
