package Elyas.LssTestSheets.factory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Exam;
import Elyas.LssTestSheets.model.Qualification;
import Elyas.LssTestSheets.model.TestSheet;

public class CourseFactory {

	public static List<Qualification> getSupportedQualifications() {
		ArrayList<Qualification> quals = new ArrayList<>();
		try {
			InputStream is = CourseFactory.class.getResourceAsStream("/json/courses.json");
			String jsonTxt;

			jsonTxt = IOUtils.toString(is);

			JSONObject obj = new JSONObject(jsonTxt);
			JSONArray jsonCourses = obj.getJSONArray("courses");

			for (int i = 0; i < jsonCourses.length(); i++) {
				JSONObject jsonCourse = jsonCourses.getJSONObject(i);
				Qualification c = new Qualification();
				c.setName(jsonCourse.getString("name"));
				String pdf = jsonCourse.getString("pdf");
				String template = jsonCourse.getString("template");
				c.setTestSheetPath(pdf, template);
				quals.add(c);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return quals;
	}

	public static List<Course> getSimpleSavedCourses() {
		URL url = CourseFactory.class.getResource("/jsoncourses");
		File f;
		if (url == null)
			return new ArrayList<>();

		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
		if (!f.exists())
			return new ArrayList<>();

		ArrayList<Course> courses = new ArrayList<>();

		File[] files = f.listFiles();

		for (File file : files) {
			byte[] encoded;
			try {
				encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
				String jsonTxt = new String(encoded, Charset.defaultCharset());
				Course course = new Course();
				if (jsonTxt.isEmpty())
					continue;

				JSONObject obj = new JSONObject(jsonTxt);

				course.setName(obj.getString("name"));
				course.setFilePath(obj.getString("path"));
				course.setBarcodeOne(obj.getString("barcode1"));
				course.setBarcodeTwo(obj.getString("barcode2"));
				course.setClientCount(obj.getLong("client-count"));

				JSONArray array = obj.optJSONArray("quals");
				for (Object object : array) {
					Qualification q = new Qualification();
					JSONObject qual = (JSONObject) object;
					JSONObject jsonExam = qual.optJSONObject("exam");
					q.setName(qual.getString("name"));
					if (jsonExam != null) {
						Exam exam = new Exam();
						exam.setDate(LocalDate.parse((String) jsonExam.get("date")));
						exam.setOriginal((Boolean) jsonExam.get("isOriginal"));
						q.setExam(exam);
					}
					course.addQualification(q);
				}

				courses.add(course);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return courses;
	}

	public static Course getFullCourse(Course c) {
		return null;
	}

	public static TestSheet getTestSheet(String path) {
		byte[] encoded;

		URL url = CourseFactory.class.getResource(path);
		File file;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		}

		try {
			encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			String jsonTxt = new String(encoded, Charset.defaultCharset());
			JSONObject obj = new JSONObject(jsonTxt);
			TestSheet testSheet = new TestSheet(obj);
			return testSheet;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
