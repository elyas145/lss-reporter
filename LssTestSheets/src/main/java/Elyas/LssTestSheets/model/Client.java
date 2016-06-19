package Elyas.LssTestSheets.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.sun.media.jfxmedia.events.NewFrameEvent;

import javafx.beans.property.BooleanProperty;
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
	Map<String, Boolean> absence;
	Map<String, List<Prerequisite>> prerequisites;
	Map<String, List<MustSee>> mustSees;
	Map<String, Map<String, SimpleBooleanProperty>> properties;
	private String id;

	public Client() {
		absence = new HashMap<>();
		prerequisites = new HashMap<>();
		mustSees = new HashMap<>();
		properties = new HashMap<>();
	}

	public List<MustSee> getMustSees(String course) {
		return mustSees.get(course);
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
		return object;
	}

	public String getName() {
		return name;
	}

	public Integer getAbsenceCount() {
		int i = 0;
		for (Boolean b : absence.values()) {
			if (!b)
				i++;
		}
		return i;
	}

	public String getPrerequisitesMet(String course) {
		for (Prerequisite p : prerequisites.get(course)) {
			if (!p.isMet()) {
				return "No";
			}
		}
		return "Yes";
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

	public Map<String, Boolean> getAbsence() {
		return absence;
	}

	public void setAbsence(Map<String, Boolean> absence) {
		this.absence = absence;
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
		this.absence = c.absence;
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
		properties.put(course, new HashMap<>());
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
		properties.get(course).put("instructor", new SimpleBooleanProperty());
		properties.get(course).put("examiner", new SimpleBooleanProperty());
	}

	public ObservableValue<Boolean> instructorItemsProperty(String course) {
		
		return properties.get(course).get("instructor");
	}
	
	public void setInstructorItems(String course, boolean value){
		List<MustSee> sees = mustSees.get(course);
		for(MustSee see : sees){
			if(see.instructorEvaluated){
				see.isCompleted = value;
			}
		}
	}
	public void setExaminerItems(String course, boolean value){
		List<MustSee> sees = mustSees.get(course);
		for(MustSee see : sees){
			if(see.examinerEvaluated){
				see.isCompleted = value;
			}
		}
	}
	public ObservableValue<Boolean> examinerItemsProperty(String course) {
		return properties.get(course).get("examiner");
	}
	
	public String toString(){
		return this.getName();
	}
}
