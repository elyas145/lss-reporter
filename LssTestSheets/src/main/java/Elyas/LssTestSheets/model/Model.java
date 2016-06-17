package Elyas.LssTestSheets.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Model {
	private static Model instance;

	public static Model getInstance() {
		if (instance == null) {
			instance = new Model();
		}
		return instance;
	}

	private Model() {

	}

	private Course course;
	private boolean isChanged;

	public void setCourse(Course c) {
		this.course = c;
		c.setOnChange(() -> {
			isChanged = true;
		});
	}

	public Course getCourse() {
		return this.course;
	}

	/**
	 * saves the course into a JSON file named "json/courses/courseName" in the
	 * resources directory
	 */
	public boolean save() {
		String course_path = course.getFilePath();

		File file;
		if (course_path == null) {
			URL url = this.getClass().getResource("/json/");
			File f;

			try {
				f = new File(url.toURI());
			} catch (URISyntaxException e) {
				f = new File(url.getPath());
			}
			String simple_path = f.getAbsolutePath();

			simple_path += "courses/";
			file = new File(simple_path);
			file.mkdirs();

			simple_path += course.getName();

			file = new File(simple_path);

			int i = 0;
			while (file.exists()) {
				String path = new String(simple_path);

				path += "-";

				path += i;
				file = new File(path);
				i++;
			}
			try {
				if (file.createNewFile()) {
					course.setFilePath(file.getAbsolutePath()
							.substring(file.getAbsolutePath().lastIndexOf(System.getProperty("file.separator")) + 1));
				} else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			URL url = this.getClass().getResource("/jsoncourses/");
			File f;

			try {
				f = new File(url.toURI());
			} catch (URISyntaxException e) {
				f = new File(url.getPath());
			}
			String path = f.getAbsolutePath() + System.getProperty("file.separator") + course_path;
			file = new File(path);
		}

		try {

			FileWriter writer = new FileWriter(file, false);
			writer.write(course.toJSON().toString(4));
			writer.flush();
			writer.close();
			isChanged = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean isChanged() {
		return isChanged;
	}

	public interface ChangeHandler {
		public void onChange();
	}

	public void addClient(Client c) {
		course.addClient(c);
	}

	public List<Client> getClients() {
		return course.getClients();
	}

	public void removeClient(Client c) {
		course.removeClient(c);
	}

	public void updateClient(Client c) {
		course.updateClient(c);
	}

	public void setFacility(Facility f) {
		course.setFacility(f);
	}

	public void setBarcodeOne(String text) {
		course.setBarcodeOne(text);
	}

	public void setBarcodeTwo(String text) {
		course.setBarcodeTwo(text);

	}

	public void setExam(Exam exam) {
		for (Qualification qualification : course.getQualifications()) {
			qualification.setExam(exam);
		}

	}

	public void setExam(Qualification qualification, Exam exam) {
		for (Qualification q : course.getQualifications()) {
			if (q.equals(qualification)) {
				q.setExam(exam);
			}
		}
	}

	public String getCourseName() {
		return course.getName();
	}

	public void setQualifications(Collection<Qualification> c) {
		if (course == null) {
			course = new Course(c);
			course.setOnChange(()->{
				isChanged = true;
			});
		} else
			course.setQualifications(c);

	}

	public Collection<String> getQualificationNames() {
		List<String> names = new ArrayList<>();
		for (Qualification qualification : course.getQualifications()) {
			names.add(qualification.getName());
		}
		return names;
	}

	public Report getReport() {
		return course.getReport();
	}

	public List<Qualification> getQualifications() {
		return course.getQualifications();
	}

	public Facility getFacility() {
		return course.getFacility();
	}

	public String getBarcodeOne() {
		return course.getBarcode1();
	}

	public String getBarcodeTwo() {
		return course.getBarcode2();
	}

	public List<Employee> getInstructors(String qual) {
		return course.getInstructors(qual);
	}
}
