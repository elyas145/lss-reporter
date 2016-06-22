package Elyas.LssTestSheets.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Client {
	String name;
	String address;
	String city;
	String postalCode;
	String email;
	String phone;
	String year;
	String month;
	String day;
	Map<String, List<Prerequisite>> prerequisites;
	Map<String, List<MustSee>> mustSees;
	private String id;

	public Client() {
		prerequisites = new HashMap<>();
		mustSees = new HashMap<>();
	}

	public List<MustSee> getMustSees(String course) {
		return mustSees.get(course);
	}

	public String getName() {
		return name;
	}

	public Integer getAbsenceCount() {

		return Model.getInstance().getReport().getAbsenceCount(id);
	}

	public void setName(String text) {
		this.name = text;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public List<Prerequisite> getPrerequisites(String course) {
		return prerequisites.get(course);
	}

	public void setPrerequisites(String course, List<Prerequisite> list) {
		this.prerequisites.put(course, list);
	}

	public String getID() {
		return this.id;
	}

	public void update(Client c) {
		this.address = c.address;
		this.city = c.address;
		this.day = c.day;
		this.email = c.email;
		this.month = c.month;
		this.name = c.name;
		this.phone = c.phone;
		this.postalCode = c.postalCode;
		this.prerequisites = c.prerequisites;
		this.year = c.year;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setMustSees(String course, List<MustSee> clientMustSees) {
		this.mustSees.put(course, clientMustSees);
		SimpleBooleanProperty inst = new SimpleBooleanProperty();
		inst.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setInstructorItems(course, newValue);

			}
		});

		SimpleBooleanProperty examiner = new SimpleBooleanProperty();
		examiner.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				setExaminerItems(course, newValue);

			}
		});
	}

	public void setInstructorItems(String course, boolean value) {
		List<MustSee> sees = mustSees.get(course);
		for (MustSee see : sees) {
			if (see.instructorEvaluated) {
				see.isCompleted = value;
			}
		}
	}

	public void setExaminerItems(String course, boolean value) {
		List<MustSee> sees = mustSees.get(course);
		for (MustSee see : sees) {
			if (see.examinerEvaluated) {
				see.isCompleted = value;
			}
		}
	}

	public String getPrerequisitesMet() {
		Map<String, Boolean> keys = new HashMap<>();

		for (List<Prerequisite> pres : prerequisites.values()) {
			for (Prerequisite pre : pres) {
				if (keys.get(pre.key) == null) {
					keys.put(pre.key, pre.isMet());
				} else {
					if (keys.get(pre.key) && !pre.isMet()) {
						keys.put(pre.key, false);
					}
				}
			}
		}
		for (String key : keys.keySet()) {
			if (keys.get(key)) {
				return "Checked";
			}
		}
		return "Not checked";
	}

	public String toString() {
		return this.getName();
	}

	public void validate(Warning warning) {
		if (name == null || name.trim().equals("")) {
			warning.add("Client name not specified.");
		}
		if (address == null || address.trim().equals("")) {
			warning.add(name + " address name not specified.");
		}
		if (city == null || city.trim().equals("")) {
			warning.add(name + " city not specified.");
		}
		if (postalCode == null || postalCode.trim().equals("")) {
			warning.add(name + " postal code not specified.");
		}
		if (email == null || email.trim().equals("")) {
			warning.add(name + " email not specified.");
		}
		if (phone == null || phone.trim().equals("")) {
			warning.add(name + " phone not specified.");
		}
		if (year == null || year.trim().equals("")) {
			warning.add(name + " year of birth not specified.");
		}
		if (month == null || month.trim().equals("")) {
			warning.add(name + " month of birth not specified.");
		}
		if (day == null || day.trim().equals("")) {
			warning.add(name + " day of birth not specified.");
		}

		if (getPrerequisitesMet().equals("Not checked")) {
			warning.add(name + "prerequisites not checked / met");
		}
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("name", name);
		object.put("address", address);
		object.put("city", city);
		object.put("postal-code", postalCode);
		object.put("email", email);
		object.put("phone", phone);
		object.put("year", year);
		object.put("month", month);
		object.put("day", day);
		object.put("id", id);
		
		for (String qual : prerequisites.keySet()) {
			JSONArray pres = new JSONArray();
			for (Prerequisite prerequisite : prerequisites.get(qual)) {
				pres.put(prerequisite.toJSON());
			}
			JSONArray sees = new JSONArray();
			for (MustSee see : mustSees.get(qual)) {
				sees.put(see.toJSON());
			}
			JSONObject obj = new JSONObject();
			obj.put("prerequisites", pres);
			obj.put("must-sees", sees);
			object.put(qual, obj);
		}

		return object;
	}
}
