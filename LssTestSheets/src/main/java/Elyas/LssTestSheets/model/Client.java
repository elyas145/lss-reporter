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
	String name = "";
	String address = "";
	String city = "";
	String postalCode = "";
	String email = "";
	String phone = "";
	String year = "";
	String month = "";
	String day = "";
	Map<String, List<Prerequisite>> prerequisites;
	Map<String, List<MustSee>> mustSees;
	private String id = "";
	private String apartment = "";

	public Client() {
		prerequisites = new HashMap<>();
		mustSees = new HashMap<>();
	}

	public Client(JSONObject obj, List<Qualification> qualifications) {
		name = obj.getString("name");
		address = obj.getString("address");
		city = obj.getString("city");
		postalCode = obj.getString("postal-code");
		email = obj.getString("email");
		phone = obj.getString("phone");
		year = obj.getString("year");
		month = obj.getString("month");
		day = obj.getString("day");
		id = obj.getString("id");
		prerequisites = new HashMap<>();
		mustSees = new HashMap<>();
		for (Qualification qualification : qualifications) {
			JSONObject jsonQual = obj.getJSONObject(qualification.getName());
			JSONArray jsonPres = jsonQual.optJSONArray("prerequisites");

			prerequisites.put(qualification.getName(), qualification.getPrerequisites());
			if (jsonPres != null) {
				for (int i = 0; i < jsonPres.length(); i++) {
					JSONObject pre = jsonPres.getJSONObject(i);
					String name = pre.getString("name");
					for (Prerequisite prerequisite : prerequisites.get(qualification.getName())) {
						if (prerequisite.getName().equals(name)) {
							prerequisite.setDateEarned(pre.optString("dateEarned"));
							prerequisite.setMet(pre.optBoolean("met"));
							prerequisite.setLocation(pre.optString("location"));
							break;
						}
					}
				}
			}

			mustSees.put(qualification.getName(), qualification.getMustSees());
			JSONArray jsonSees = jsonQual.getJSONArray("must-sees");
			if (jsonSees != null) {
				for (int i = 0; i < jsonSees.length(); i++) {
					JSONObject see = jsonSees.getJSONObject(i);
					String item = see.getString("item");
					for (MustSee mustSee : mustSees.get(qualification.getName())) {
						if (mustSee.getItem().equals(item)) {
							mustSee.setCompleted(see.optBoolean("completed"));
						}
					}
				}
			}
		}

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

	public String getFinalAddress() {
		String finalAddress = address;
		if (apartment != null && !apartment.trim().equals("")) {
			finalAddress += " apt. " + apartment;
		}
		return finalAddress;
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

	public String getApartment() {
		return this.apartment;
	}

	public void update(Client c) {
		this.address = c.address;
		this.city = c.city;
		this.day = c.day;
		this.email = c.email;
		this.month = c.month;
		this.name = c.name;
		this.phone = c.phone;
		this.postalCode = c.postalCode;
		this.prerequisites = c.prerequisites;
		this.year = c.year;
		this.apartment = c.apartment;
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

	public MustSee getMustSee(String course, String item) {
		List<MustSee> sees = getMustSees(course);
		for (MustSee mustSee : sees) {
			if (mustSee.getItem().equals(item)) {
				return mustSee;
			}
		}
		return null;
	}

	public Prerequisite getPrerequisite(String course, String name) {
		List<Prerequisite> prerequisites = getPrerequisites(course);
		for (Prerequisite prerequisite : prerequisites) {
			if (prerequisite.getName().equals(name)) {
				return prerequisite;
			}
		}
		return null;
	}

	public void setApartment(String text) {
		this.apartment = text;

	}

	public boolean equals(Object other) {
		if (other instanceof Client) {
			Client client = (Client) other;
			return client.address.equals(address) && client.apartment.equals(apartment) && client.city.equals(city)
					&& client.day.equals(day) && client.email.equals(email) && client.id.equals(id)
					&& client.month.equals(month) && client.name.equals(name) && client.phone.equals(phone)
					&& client.postalCode.equals(postalCode) && client.year.equals(year);
		}
		return false;
	}
}
