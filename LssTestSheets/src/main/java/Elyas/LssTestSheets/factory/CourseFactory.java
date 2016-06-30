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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import Elyas.LssTestSheets.Test;
import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Exam;
import Elyas.LssTestSheets.model.MustSee;
import Elyas.LssTestSheets.model.Prerequisite;
import Elyas.LssTestSheets.model.Prerequisite.Type;
import Elyas.LssTestSheets.model.Qualification;
import Elyas.LssTestSheets.model.TestSheet;
import Elyas.LssTestSheets.model.TestSheet.Student;
import Elyas.LssTestSheets.model.Warning;

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

	public static Warning getCourseWarnings(Course course) {
		Warning warning = new Warning();
		course.validate(warning);

		return warning;
	}

	/**
	 * generates pdf files for the test sheets for the course.
	 * 
	 * @param course
	 *            the course to generate test sheets for. the testsheet should
	 *            be set inside the course.
	 * @throws IOException
	 */
	public static List<PDDocument> generateTestSheets(Course course) throws IOException {

		final int instructor_pref_size = 12;
		final int pre_pref_size = 8;

		List<PDDocument> documents = new ArrayList<>();
		Queue<Client> clients = new LinkedList<>(course.getClients());

		for (Qualification qualification : course.getQualifications()) {
			TestSheet testSheet = qualification.getTestSheet();
			int number_of_testsheets = 1;
			// figure out how many copies of the pdf we need.

			while (course.getClientsCount() > (testSheet.getStudentCapacity() * number_of_testsheets)) {
				number_of_testsheets++;
			}

			URL url = CourseFactory.class.getResource(qualification.getPdfPath());
			File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				file = new File(url.getPath());
			}

			for (int i = 0; i < number_of_testsheets; i++) {
				PDDocument pdf = PDDocument.load(file);
				PDDocumentCatalog docCatalog = pdf.getDocumentCatalog();
				PDAcroForm acroForm = docCatalog.getAcroForm();
				PDResources res = acroForm.getDefaultResources();
				// barcodes
				acroForm.getField(testSheet.barcodeOneField()).setValue(course.getBarcode1());
				acroForm.getField(testSheet.barcodeTwoField()).setValue(course.getBarcode2());

				// clients info.
				Iterator<Student> iterator = testSheet.getStudentIterator();
				while (iterator.hasNext() && !clients.isEmpty()) {
					Student student = iterator.next();
					Client client = clients.poll();
					setPDFText(acroForm.getField(student.getName()), client.getName(), instructor_pref_size);
					setPDFText(acroForm.getField(student.getAddress()), client.getAddress(), instructor_pref_size);
					setPDFText(acroForm.getField(student.getCity()), client.getCity(), instructor_pref_size);
					setPDFText(acroForm.getField(student.getPostalCode()), client.getPostalCode(),
							instructor_pref_size);
					setPDFText(acroForm.getField(student.getEmail()), client.getEmail(), instructor_pref_size);
					setPDFText(acroForm.getField(student.getPhone()), client.getPhone(), instructor_pref_size);

					acroForm.getField(student.getYear()).setValue(client.getYear());
					acroForm.getField(student.getMonth()).setValue(client.getMonth());
					acroForm.getField(student.getDay()).setValue(client.getDay());

					Iterator<MustSee> mustSees = student.getMustSees().iterator();
					while (mustSees.hasNext()) {
						MustSee template = mustSees.next();
						MustSee see = client.getMustSee(qualification.getName(), template.getItem());
						if (see.isAppEvaluated()) {
							see.evaluate(client, qualification.getName());
						}

						if (see.isCompleted()) {
							acroForm.getField(template.getField()).setValue(testSheet.getPassValue(template));
						} else {
							acroForm.getField(template.getField()).setValue(testSheet.getFailValue(template));
						}
					}
					Iterator<Prerequisite> prerequisites = student.getPrerequisites().iterator();
					while (prerequisites.hasNext()) {
						Prerequisite template = prerequisites.next();
						Prerequisite prerequisite = client.getPrerequisite(qualification.getName(), template.getName());
						if (prerequisite.getType().equals(Type.DATE)) {
							setPDFText(acroForm.getField(template.getDateEarned()), prerequisite.getDateEarned(),
									pre_pref_size);
							setPDFText(acroForm.getField(template.getLocation()), prerequisite.getLocation(),
									pre_pref_size);
						}
					}
				}
				// instructor info
				setPDFText(acroForm.getField(testSheet.getInstructor().getName()),
						course.getInstructorsNames(qualification), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getInstructor().getID()),
						course.getInstructorsIDs(qualification), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getInstructor().getEmail()),
						course.getInstructorsEmails(qualification), instructor_pref_size);
				acroForm.getField(testSheet.getInstructor().getAreaCode())
						.setValue(course.getInstructorsAreaCode(qualification));
				setPDFText(acroForm.getField(testSheet.getInstructor().getPhone()),
						course.getInstructorsPhone(qualification), instructor_pref_size);

				// exam info
				System.out.println("facility name: ");
				setPDFText(acroForm.getField(testSheet.getExam().getFacilityName()), course.getFacility().getName(),
						instructor_pref_size);
				acroForm.getField(testSheet.getExam().getFacilityAreaCode())
						.setValue(course.getFacility().getAreaCode());
				System.out.println("facility phone: ");
				setPDFText(acroForm.getField(testSheet.getExam().getFacilityPhone()),
						course.getFacility().getFinalPhone(), instructor_pref_size);

				if (qualification.getExam() != null) {
					if (course.getQualification(qualification.getName()).getExam().isOriginal()) {
						acroForm.getField(testSheet.getExam().getOriginal())
								.setValue(testSheet.getExam().getOriginalValue());
					} else {
						acroForm.getField(testSheet.getExam().getOriginal())
								.setValue(testSheet.getExam().getRecertValue());
					}
					acroForm.getField(testSheet.getExam().getYear())
							.setValue(course.getQualification(qualification.getName()).getExamDate().getYear() + "");

					int day = course.getQualification(qualification.getName()).getExamDate().getDayOfMonth();
					String finalDay = "";
					if (day < 10) {
						finalDay = "0" + day;
					} else {
						finalDay = "" + day;
					}

					acroForm.getField(testSheet.getExam().getDay()).setValue(finalDay);

					int month = course.getQualification(qualification.getName()).getExamDate().getMonthValue();
					String finalMonth = "";
					if (month < 10) {
						finalMonth = "0" + day;
					} else {
						finalMonth = "" + day;
					}
					acroForm.getField(testSheet.getExam().getMonth()).setValue(finalMonth);
				} // end exam if statement

				// award info
				acroForm.getField(testSheet.getAward().getField()).setValue(testSheet.getAward().getAwardIssued());

				// host info
				if (course.getFacility().getHost().getExamFees()) {
					acroForm.getField(testSheet.getHost().getExamFeesField())
							.setValue(testSheet.getHost().getExamFeesAttached());
				} else {
					acroForm.getField(testSheet.getHost().getExamFeesField())
							.setValue(testSheet.getHost().getExamFeesNotAttached());
				}
				setPDFText(acroForm.getField(testSheet.getHost().getName()), course.getFacility().getHost().getName(),
						instructor_pref_size);

				acroForm.getField(testSheet.getHost().getAreaCode())
						.setValue(course.getFacility().getHost().getAreaCode());
				setPDFText(acroForm.getField(testSheet.getHost().getPhone()),
						course.getFacility().getHost().getFinalPhone(), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getHost().getStreet()),
						course.getFacility().getHost().getAddress(), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getHost().getCity()), course.getFacility().getHost().getCity(),
						instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getHost().getProvince()),
						course.getFacility().getHost().getProvince(), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getHost().getPostalCode()),
						course.getFacility().getHost().getPostalCode(), instructor_pref_size);

				// examiner info.
				setPDFText(acroForm.getField(testSheet.getExaminer().getName()),
						course.getQualification(qualification.getName()).getExaminersNames(), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getExaminer().getId()),
						course.getQualification(qualification.getName()).getExaminersIDs(), instructor_pref_size);
				setPDFText(acroForm.getField(testSheet.getExaminer().getEmail()),
						course.getQualification(qualification.getName()).getExaminersEmails(), instructor_pref_size);
				acroForm.getField(testSheet.getExaminer().getAreaCode())
						.setValue(course.getQualification(qualification.getName()).getExaminersAreaCode());
				setPDFText(acroForm.getField(testSheet.getExaminer().getPhone()),
						course.getQualification(qualification.getName()).getExaminersPhones(), instructor_pref_size);
				documents.add(pdf);
			}

		}
		return documents;
	}

	private static PDRectangle getFieldArea(PDField field) {

		PDRectangle result = field.getWidgets().get(0).getRectangle();
		if (result == null) {
			result = new PDRectangle();
		}
		return result;
	}

	private static void setPDFText(PDField field, String value, int prefSize) {

		PDTextField pdField = (PDTextField) field;

		pdField.setDefaultAppearance("/Helv " + getFontSize(pdField, value, prefSize) + " Tf 0 g");
		try {
			if (value == null) {
				pdField.setValue("");

			} else {
				pdField.setValue(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String getFontSize(PDField field, String value, int prefSize) {

		if(value == null || value.isEmpty())
			return ""+prefSize;
		
		PDRectangle rectangle = getFieldArea(field);
		float width = rectangle.getWidth();

		int numCharacters = value.length();
		System.out.println("width / length: " + width / numCharacters);
		if (width / numCharacters < 5) {
			return "6";
		} else if (width / numCharacters >= 5 && width / numCharacters < 6) {
			return "10";
		} else if (width / numCharacters >= 6 && width / numCharacters < 8) {
			return "12";
		} else {
			return "" + prefSize;
		}

	}
}
