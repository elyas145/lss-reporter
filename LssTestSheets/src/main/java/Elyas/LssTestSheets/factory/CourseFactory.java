package Elyas.LssTestSheets.factory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
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
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.MustSee;
import Elyas.LssTestSheets.model.NotifyingThread;
import Elyas.LssTestSheets.model.Prerequisite;
import Elyas.LssTestSheets.model.Prerequisite.Type;
import Elyas.LssTestSheets.model.Qualification;
import Elyas.LssTestSheets.model.TestSheet;
import Elyas.LssTestSheets.model.ThreadCompleteListener;
import Elyas.LssTestSheets.model.TestSheet.Student;
import Elyas.LssTestSheets.model.TestSheetProperties;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
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
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return courses;
	}

	/**
	 * attempts to parse a course file. the file may exist in any directory in
	 * the system.
	 * 
	 * @param file
	 *            the object representing the course file
	 * @return the parsed course object, or null if unable to parse.
	 */
	public static Course getFullCourse(File file) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			String jsonTxt = new String(encoded, Charset.defaultCharset());
			JSONObject obj = new JSONObject(jsonTxt);
			Course course = new Course(obj);
			return course;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * simple course must exist internally, and should not be an external
	 * directory. i.e. it needs to exist in the resource directory.
	 * 
	 * @param simpleCourse
	 *            the course to retrieve
	 * @return the full course as stored in the file.
	 */
	public static Course getFullCourse(Course simpleCourse) {
		URL url = CourseFactory.class.getResource("/jsoncourses/");
		File f;

		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}
		String path = f.getAbsolutePath() + System.getProperty("file.separator") + simpleCourse.getFilePath();
		File file = new File(path);
		return getFullCourse(file);
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
	 * @param properties
	 * @throws IOException
	 */
	public static List<PDDocument> generateTestSheets(Course course, Properties properties) throws IOException {

		final int instructor_pref_size = 12;
		final int pre_pref_size = 6;

		List<PDDocument> documents = new ArrayList<>();
		Queue<Client> clients;
		for (Qualification qualification : course.getQualifications()) {
			clients = new LinkedList<>(course.getClients());
			TestSheet testSheet = qualification.getTestSheet();
			int number_of_testsheets = 1;

			// total pass and fail.
			int totalPass = 0;
			int totalFail = 0;
			if (properties.getProperty(TestSheetProperties.TOTAL_PASS_FAIL.name()).equals(true + "")) {
				totalPass = course.getTotalPass(qualification);
				totalFail = course.getTotalFail(qualification);

			}
			// figure out how many copies of the pdf we need.
			while (course.getClientsCount() > (testSheet.getStudentCapacity() * number_of_testsheets)) {
				number_of_testsheets++;
			}
			// students per sheet, assuming each sheet fits the same amount of
			// students.
			int studentsPerSheet = testSheet.getStudentCapacity() / testSheet.getNumberOfPages();
			int numberOfPages = (int) Math.ceil((double) clients.size() / studentsPerSheet);

			URL url = CourseFactory.class.getResource(qualification.getPdfPath());
			File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				file = new File(url.getPath());
			}
			int pageNumber = 0;
			for (int i = 0; i < number_of_testsheets; i++) {
				PDDocument pdf = PDDocument.load(file);
				PDDocumentCatalog docCatalog = pdf.getDocumentCatalog();
				PDAcroForm acroForm = docCatalog.getAcroForm();
				acroForm.getDefaultResources();

				// barcodes
				if (properties.getProperty(TestSheetProperties.INCLUDE_BARCODES.name()).equals(true + "")) {
					acroForm.getField(testSheet.barcodeOneField()).setValue(course.getBarcode1());
					acroForm.getField(testSheet.barcodeTwoField()).setValue(course.getBarcode2());
				}
				// page number
				if (properties.getProperty(TestSheetProperties.INCLUDE_PAGE_NUMBERS.name()).equals(true + "")) {
					for (String page : testSheet.getPageNumbers()) {
						if (pageNumber < numberOfPages) {
							pageNumber++;
						}
						acroForm.getField(page).setValue(pageNumber + "");

					}
					acroForm.getField(testSheet.getPageTotal()).setValue(numberOfPages + "");
				}
				// double sided
				if (properties.getProperty(TestSheetProperties.DOUBLE_SIDED.name()).equals(true + "")) {
					// if even number of pages, all double sided. if odd, last
					// one is not double sided.
					if (numberOfPages % 2 == 0) { // even
						for (String page : testSheet.getDoubleSided()) {
							acroForm.getField(page).setValue("Yes");
						}
					} else { // odd
						if (i == number_of_testsheets - 1) { // last document.
							for (int j = 0; j < testSheet.getNumberOfPages(); j++) {
								if (j < testSheet.getNumberOfPages() - 2) {
									acroForm.getField(testSheet.getDoubleSided().get(j)).setValue("Yes");
								}
							}
						} else {
							for (String page : testSheet.getDoubleSided()) {
								acroForm.getField(page).setValue("Yes");
							}
						}
					}
				}
				// total pass and fail.
				if (properties.getProperty(TestSheetProperties.TOTAL_PASS_FAIL.name()).equals(true + "")) {
					acroForm.getField(testSheet.getTotalFail()).setValue(totalFail + "");
					acroForm.getField(testSheet.getTotalPass()).setValue(totalPass + "");
				}

				// clients info.
				Iterator<Student> iterator = testSheet.getStudentIterator();
				while (iterator.hasNext() && !clients.isEmpty()) {
					// System.out.println("-----begin iteration-----\nclients
					// left: " + clients.size());

					Student student = iterator.next();
					Client client = clients.poll();
					try {
						if (student.getName().trim().equals("")) {
							// test sheet has first and last name.
							String first = client.getName().trim().substring(0, client.getName().lastIndexOf(' '));
							String last = client.getName().trim().substring(client.getName().lastIndexOf(' ') + 1,
									client.getName().length());
							setPDFText(acroForm.getField(student.getFirstName()), first, instructor_pref_size);
							setPDFText(acroForm.getField(student.getLastName()), last, instructor_pref_size);
						} else {
							setPDFText(acroForm.getField(student.getName()), client.getName(), instructor_pref_size);
						}
						if (testSheet.hasApartment()) {
							setPDFText(acroForm.getField(student.getAddress()), client.getAddress(),
									instructor_pref_size);
							setPDFText(acroForm.getField(student.getApartment()), client.getApartment(),
									instructor_pref_size);
						} else {
							setPDFText(acroForm.getField(student.getAddress()), client.getFinalAddress(),
									instructor_pref_size);
						}

						if (!student.getProvince().equals("")) {
							acroForm.getField(student.getProvince()).setValue(client.getProvince());
						}
						setPDFText(acroForm.getField(student.getCity()), client.getCity(), instructor_pref_size);
						setPDFText(acroForm.getField(student.getPostalCode()), client.getPostalCode(),
								instructor_pref_size);
						setPDFText(acroForm.getField(student.getEmail()), client.getEmail(), instructor_pref_size);
						setPDFText(acroForm.getField(student.getPhone()), client.getPhone(), instructor_pref_size);

						acroForm.getField(student.getYear()).setValue(client.getYear());
						acroForm.getField(student.getMonth()).setValue(client.getMonth());
						acroForm.getField(student.getDay()).setValue(client.getDay());

						if (!student.getGendre().equals("")) {
							String value;
							if (client.isMale()) {
								value = student.getMaleValue();
							} else {
								value = student.getFemaleValue();
							}
							acroForm.getField(student.getGendre()).setValue(value);
						}

					} catch (Exception e) {
						System.out.println("---------------- client info set error -----------------");
						System.out.println("name: " + student.getName());
						System.out.println("first name: " + student.getFirstName());
						System.out.println("last name: " + student.getLastName());

						e.printStackTrace();
					}
					Iterator<MustSee> mustSees = student.getMustSees().iterator();
					while (mustSees.hasNext()) {
						MustSee template = mustSees.next();
						MustSee see = client.getMustSee(qualification.getName(), template.getItem());
						try {

							if (see.isAppEvaluated()) {

								see.evaluate(client, qualification.getName());
								// System.out.println(client.getName() + " " +
								// see.getItem() + ": " + see.isCompleted());
							}

							if (see.isCompleted()) {
								if (see.getName().toLowerCase().equals("result") && properties
										.getProperty(TestSheetProperties.PASSED_RESULT.name()).equals(true + "")) {
									acroForm.getField(template.getField()).setValue(testSheet.getPassValue(template));
								}
								if (!see.getName().toLowerCase().equals("result")) {
									acroForm.getField(template.getField()).setValue(testSheet.getPassValue(template));
								}
							} else {
								// mark incomplete instructor items.
								if (see.isInstructorEvaluated()
										&& properties.getProperty(TestSheetProperties.INCOMPLETE_INST_ITEMS.name())
												.equals(true + "")) {
									acroForm.getField(template.getField()).setValue(testSheet.getFailValue(template));
								}
								// mark incomplete examiner items.
								if (see.isExaminerEvaluated()
										&& properties.getProperty(TestSheetProperties.INCOMPLETE_EXAM_ITEMS.name())
												.equals(true + "")) {
									acroForm.getField(template.getField()).setValue(testSheet.getFailValue(template));
								}
								// mark failed results.
								if (see.getName().toLowerCase().equals("result") && properties
										.getProperty(TestSheetProperties.FAILED_RESULT.name()).equals(true + "")) {
									acroForm.getField(template.getField()).setValue(testSheet.getFailValue(template));
								}
							}
						} catch (Exception e) {
							System.out.println("---------------- must see set error -----------------");
							System.out.println("name: " + template.getField());

							e.printStackTrace();
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
				if (testSheet.getInstructor().getAreaCode().equals("")) {
					setPDFText(acroForm.getField(testSheet.getInstructor().getPhone()),
							course.getInstructorsPhoneWithAreaCode(qualification), instructor_pref_size);
				} else {
					acroForm.getField(testSheet.getInstructor().getAreaCode())
							.setValue(course.getInstructorsAreaCode(qualification));
					setPDFText(acroForm.getField(testSheet.getInstructor().getPhone()),
							course.getInstructorsPhone(qualification), instructor_pref_size);
				}

				// exam info

				setPDFText(acroForm.getField(testSheet.getExam().getFacilityName()), course.getFacility().getName(),
						instructor_pref_size);

				if (testSheet.getExam().getFacilityAreaCode().equals("")) {
					setPDFText(acroForm.getField(testSheet.getExam().getFacilityPhone()),
							course.getFacility().getFinalPhoneWithAreaCode(), instructor_pref_size);
				} else {
					acroForm.getField(testSheet.getExam().getFacilityAreaCode())
							.setValue(course.getFacility().getAreaCode());
					setPDFText(acroForm.getField(testSheet.getExam().getFacilityPhone()),
							course.getFacility().getFinalPhone(), instructor_pref_size);
				}

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
						finalMonth = "0" + month;
					} else {
						finalMonth = "" + month;
					}
					acroForm.getField(testSheet.getExam().getMonth()).setValue(finalMonth);
				} // end exam if statement

				// award info
				if (testSheet.getAward() != null) {
					acroForm.getField(testSheet.getAward().getField()).setValue(testSheet.getAward().getAwardIssued());
				}
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

				if (testSheet.getHost().getAreaCode().equals("")) {
					setPDFText(acroForm.getField(testSheet.getHost().getPhone()),
							course.getFacility().getHost().getFinalPhoneWithAreaCode(), instructor_pref_size);
				} else {
					acroForm.getField(testSheet.getHost().getAreaCode())
							.setValue(course.getFacility().getHost().getAreaCode());
					setPDFText(acroForm.getField(testSheet.getHost().getPhone()),
							course.getFacility().getHost().getFinalPhone(), instructor_pref_size);
				}

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

				if (testSheet.getInstructor().getAreaCode().equals("")) {
					setPDFText(acroForm.getField(testSheet.getExaminer().getPhone()),
							course.getQualification(qualification.getName()).getExaminersPhonesWithAreaCode(),
							instructor_pref_size);
				} else {
					acroForm.getField(testSheet.getExaminer().getAreaCode())
							.setValue(course.getQualification(qualification.getName()).getExaminersAreaCode());
					setPDFText(acroForm.getField(testSheet.getExaminer().getPhone()),
							course.getQualification(qualification.getName()).getExaminersPhones(),
							instructor_pref_size);
				}

				if (properties.getProperty(TestSheetProperties.FLATTEN.name(), true + "").equals(true + "")
						|| properties.getProperty(TestSheetProperties.GENERATE_SINGLE_FILE.name(), true + "")
								.equals(true + "")) {
					acroForm.flatten();
				}
				// do we need to remove the last page.
				if (pageNumber == numberOfPages) { // last page of
													// testsheets
					int maxPages = testSheet.getNumberOfPages() * number_of_testsheets;
					if (numberOfPages < maxPages) { // we need to remove a page.
						pdf.removePage(pdf.getPages().get(pdf.getPages().getCount() - 1));
					}
				}

				documents.add(pdf);
			}

		}
		// merge documents into one document.
		if (properties.getProperty(TestSheetProperties.GENERATE_SINGLE_FILE.name(), true + "").equals(true + "")) {
			PDDocument destination = documents.get(0);
			PDFMergerUtility utility = new PDFMergerUtility();
			for (PDDocument pdDocument : documents) {
				if (pdDocument != destination) {
					utility.appendDocument(destination, pdDocument);
					pdDocument.close();
				}
			}
			return Arrays.asList(destination);
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

		if (value == null || value.isEmpty())
			return "" + prefSize;

		PDRectangle rectangle = getFieldArea(field);
		float width = rectangle.getWidth();

		int numCharacters = value.length();
		// System.out.println(field.getFullyQualifiedName() + " width / length:
		// " + width / numCharacters);
		if (prefSize <= 6) {
			return "" + prefSize;
		}
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

	public static TestSheet getTestSheetByName(Qualification qualification) {
		List<Qualification> qualifications = getSupportedQualifications();
		for (Qualification qual : qualifications) {
			if (qual.getName().equals(qualification.getName())) {
				qualification.setTestSheetPath(qual.getPdfPath(), qual.getTestSheetPath());
				return getTestSheet(qualification.getTestSheetPath());
			}
		}
		return null;
	}

	public static void importCourse() {
		Model.getInstance().getCourse().setFilePath(null);
		Model.getInstance().save();

	}

	public static void exportInfo(boolean exportCourse, boolean exportTestSheets, Properties properties,
			String directoryPath, ThreadCompleteListener onFinish, Course course) {

		NotifyingThread thread = new NotifyingThread() {

			@Override
			public void doRun() {
				if (exportCourse) {
					JSONObject cor = course.toJSON();
					File file = new File(
							directoryPath + System.getProperty("file.separator") + course.getName() + ".json");
					FileWriter writer;
					try {
						Course c = new Course(cor);
						c.setFilePath(file.getAbsolutePath());
						writer = new FileWriter(file, false);

						writer.write(c.toJSON().toString(4));
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				if (exportTestSheets) {
					try {
						List<PDDocument> docs = CourseFactory.generateTestSheets(Model.getInstance().getCourse(),
								properties);
						int i = 1;
						for (PDDocument doc : docs) {
							String dir = directoryPath + System.getProperty("file.separator")
									+ StringUtils.capitalize(Model.getInstance().getCourseName()) + " Testsheet "
									+ (i++) + ".pdf";
							File file = new File(dir);
							if (file.exists()) {
								final String name = Model.getInstance().getCourseName() + " Testsheet " + (i - 1);
								Platform.runLater(() -> {
									Alert alert = new Alert(AlertType.CONFIRMATION);
									alert.setTitle("Confirm Replacing a File.");
									alert.setContentText("A file with the name \"" + name
											+ "\" already exists. Would you like to replace the existing file?");
									Optional<ButtonType> btn = alert.showAndWait();
									if (btn.get().equals(ButtonType.OK)) {
										try {
											doc.save(file);
											doc.close();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								});

							} else {
								doc.save(file);
								doc.close();
							}

						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
		};
		thread.addListener(onFinish);
		thread.start();
	}
}
