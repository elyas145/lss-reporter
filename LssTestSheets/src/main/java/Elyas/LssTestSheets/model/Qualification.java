package Elyas.LssTestSheets.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Model.ChangeHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

public class Qualification {
	private SimpleStringProperty name;
	private ArrayList<Employee> instructors;
	private ArrayList<Employee> examiners;
	private Exam exam;
	private String testSheetPath;
	private TestSheet testSheet;
	private String pdfPath;
	private ChangeHandler changeHandler;

	public Qualification() {
		name = new SimpleStringProperty();
		instructors = new ArrayList<>();
		examiners = new ArrayList<>();
		changeHandler = new ChangeHandler() {
			@Override
			public void onChange() {

			}
		};
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
			object.put("instructors", array);
		}
		if (exam != null) {
			object.put("exam", exam.toJSON());
		}
		return object;
	}

	public void setInstructors(Collection<Employee> i) {
		this.instructors = new ArrayList<>(i);
		changeHandler.onChange();
	}

	public void setExaminers(ObservableList<Employee> i) {
		this.examiners = new ArrayList<>(i);
		changeHandler.onChange();
	}

	public void setExam(Exam exam) {
		this.exam = exam;
		changeHandler.onChange();
	}

	public void addExaminer(Employee e) {
		examiners.add(e);
		changeHandler.onChange();

	}

	public void removeExaminer(Employee e) {
		examiners.remove(e);
		changeHandler.onChange();
	}

	public void removeInstructor(Employee e) {
		instructors.remove(e);
		changeHandler.onChange();
	}

	public void addInstructor(Employee e) {
		instructors.add(e);
		changeHandler.onChange();
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
		return this.exam;
	}

	public void setonChangeListener(ChangeHandler changeHandler) {
		this.changeHandler = changeHandler;
	}

	public List<Employee> getInstructors() {
		return instructors;
	}
}
