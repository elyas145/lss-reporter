package Elyas.LssTestSheets.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PrimitiveIterator.OfDouble;

import org.json.JSONArray;
import org.json.JSONObject;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Model.ChangeHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class Qualification {

	public Qualification() {
		name = new SimpleStringProperty();
		instructors = new ArrayList<>();
		examiners = new ArrayList<>();
		changeHandler = new ChangeHandler() {
			@Override
			public void onChange(String change) {
				Model.getInstance().setChanged(change);
			}
		};
	}

	public Qualification(JSONObject obj) {
		JSONObject jsonExam = obj.optJSONObject("exam");
		if (jsonExam != null) {
			exam = new Exam(jsonExam);
		}
		name = new SimpleStringProperty(obj.getString("name"));

		JSONArray jsonInstructors = obj.optJSONArray("instructors");
		instructors = new ArrayList<>();
		if (jsonInstructors != null) {
			for (int i = 0; i < jsonInstructors.length(); i++) {
				JSONObject jsonInstructor = jsonInstructors.getJSONObject(i);
				instructors.add(new Employee(jsonInstructor));
			}
		}

		JSONArray jsonExaminers = obj.optJSONArray("examiners");
		examiners = new ArrayList<>();
		if (jsonExaminers != null) {
			for (int i = 0; i < jsonExaminers.length(); i++) {
				JSONObject jsonExaminer = jsonExaminers.getJSONObject(i);
				examiners.add(new Employee(jsonExaminer));
			}
		}

		testSheet = CourseFactory.getTestSheetByName(this);
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("name", name.get());

		if (instructors != null && !instructors.isEmpty()) {
			JSONArray array = new JSONArray();
			for (Employee employee : instructors) {
				array.put(employee.toJSON());
			}
			object.put("instructors", array);
		}

		if (examiners != null && !examiners.isEmpty()) {
			JSONArray array = new JSONArray();
			for (Employee employee : examiners) {
				array.put(employee.toJSON());
			}
			object.put("examiners", array);
		}
		if (exam != null) {
			object.put("exam", exam.toJSON());
		}
		return object;
	}

	public void setInstructors(Collection<Employee> i) {
		this.instructors = new ArrayList<>(i);
		changeHandler.onChange("Instructors Changed for "+ this.getName()+".");
	}

	public void setExaminers(ObservableList<Employee> i) {
		this.examiners = new ArrayList<>(i);
		changeHandler.onChange("Examiners Changed for "+ this.getName()+".");
	}

	public void setExam(Exam exam) {
		this.exam = exam;
		changeHandler.onChange("Exam Changed for "+ this.getName()+".");
	}

	public void addExaminer(Employee e) {
		examiners.add(e);
		changeHandler.onChange("Examiner added for "+ this.getName()+".");

	}

	public void removeExaminer(Employee e) {
		examiners.remove(e);
		changeHandler.onChange("Examiner Removed for "+ this.getName()+".");
	}

	public void removeInstructor(Employee e) {
		instructors.remove(e);
		changeHandler.onChange("Instructors Removed for "+ this.getName()+".");
	}

	public void addInstructor(Employee e) {
		instructors.add(e);
		changeHandler.onChange("Instructor Added for "+ this.getName()+".");
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);

	}

	public List<MustSee> getMustSees() {
		if (testSheet == null) {
			testSheet = CourseFactory.getTestSheet(testSheetPath);
		}
		return testSheet.getClientMustSees();
	}

	public String getTestSheetPath() {
		return testSheetPath;
	}

	public void setTestSheetPath(String pdfPath, String testSheetPath) {
		this.testSheetPath = testSheetPath;
		this.pdfPath = pdfPath;
	}

	public String getPdfPath() {
		return pdfPath;
	}

	public LocalDate getExamDate() {
		if (exam != null) {
			return exam.getDate();
		}
		return LocalDate.MAX;
	}

	public String toString() {
		return this.name.get();
	}

	public boolean equals(Object object) {
		if (object instanceof Qualification) {
			Qualification other = (Qualification) object;
			if (other.getName().equals(this.getName())) {
				return true;
			}
		}
		return false;
	}

	public void setExamDate(LocalDate date) {
		if (exam == null)
			exam = new Exam();
		exam.setDate(date);

	}

	public Exam getExam() {
		if (exam == null)
			exam = new Exam();
		return this.exam;
	}

	public void setonChangeListener(ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

	public List<Employee> getInstructors() {
		return instructors;
	}

	public List<Employee> getExaminers() {
		return examiners;
	}

	public List<Prerequisite> getPrerequisites() {
		if (testSheet == null) {
			testSheet = CourseFactory.getTestSheet(testSheetPath);
		}
		return testSheet.getGenericPrerequisites();
	}

	public void validate(Warning warning) {
		if (instructors.isEmpty()) {
			warning.add("Instructor(s) not added");
		}
		if (examiners.isEmpty()) {
			warning.add("Examiner(s) not added");
		}
		for (Employee employee : instructors) {
			employee.validate(warning);
		}
		for (Employee employee : examiners) {
			employee.validate(warning);
		}
		if (exam == null) {
			warning.add("Exam not set.");
		} else {
			exam.validate(warning);
		}

	}

	public TestSheet getTestSheet() {
		if (this.testSheet == null)
			testSheet = CourseFactory.getTestSheet(testSheetPath);
		return testSheet;
	}

	public String getExaminersNames() {
		String names = "";
		int i = 0;
		for (Employee employee : examiners) {
			if (i++ == examiners.size() - 1) {
				names += employee.getName();
			} else {
				names += employee.getName() + ", ";
			}
		}
		return names;
	}

	public String getExaminersIDs() {
		String ids = "";
		int i = 0;
		for (Employee employee : examiners) {
			if (i++ == examiners.size() - 1) {
				ids += employee.getId();
			} else {
				ids += employee.getId() + ", ";
			}
		}
		return ids;
	}

	public String getExaminersEmails() {
		String emails = "";
		int i = 0;
		for (Employee employee : examiners) {
			if (i++ == examiners.size() - 1) {
				emails += employee.getEmail();
			} else {
				emails += employee.getEmail() + ", ";
			}
		}
		return emails;
	}

	public String getExaminersPhones() {
		String phones = "";
		int i = 0;
		for (Employee employee : examiners) {
			if (i++ == examiners.size() - 1) {
				phones += employee.getFinalPhone();
			} else {
				phones += employee.getFinalPhone() + ", ";
			}
		}
		return phones;
	}

	public String getExaminersAreaCode() {
		if (examiners == null || examiners.isEmpty()) {
			return "";
		} else {
			return examiners.get(0).getAreaCode();
		}
	}

	public String getExaminersPhonesWithAreaCode() {
		String phones = "";
		int i = 0;
		for (Employee employee : examiners) {
			if (i++ == examiners.size() - 1) {
				phones += "(" + employee.getAreaCode() + ") " + employee.getFinalPhone();
			} else {
				phones += "(" + employee.getAreaCode() + ") " + employee.getFinalPhone() + ", ";
			}
		}
		return phones;
	}

	private SimpleStringProperty name;
	private ArrayList<Employee> instructors;
	private ArrayList<Employee> examiners;
	private Exam exam;
	private String testSheetPath;
	private TestSheet testSheet;
	private String pdfPath;
	private ChangeHandler changeHandler;

}
