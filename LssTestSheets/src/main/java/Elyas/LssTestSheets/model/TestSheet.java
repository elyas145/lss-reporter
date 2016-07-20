package Elyas.LssTestSheets.model;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes.Name;

import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;

import Elyas.LssTestSheets.model.TestSheet.Examiner;
import Elyas.LssTestSheets.model.TestSheet.Host;
import Elyas.LssTestSheets.model.TestSheet.Instructor;
import Elyas.LssTestSheets.model.TestSheet.Student;
import Elyas.LssTestSheets.model.TestSheet.Exam.Type;
import javafx.fxml.Initializable;

public class TestSheet {

	private List<String> pageNumbers;
	private String pageTotal;
	private List<String> doubleSided;
	private String totalPass;
	private String totalFail;
	private String barcode1;
	private String barcode2;
	private String passValue;
	private String failValue;
	private Instructor instructor;
	private Exam exam;
	private Award award;
	private Host host;
	private Examiner examiner;
	private ArrayList<Student> students;
	private ArrayList<MustSee> mustSees;
	private String name;

	public TestSheet(JSONObject obj) {
		init();
		int i = 1;
		while (obj.has("page-number-" + i)) {
			pageNumbers.add((String) obj.get("page-number-" + i));
			i++;
		}
		pageTotal = (String) obj.get("page-total");

		i = 1;
		while (obj.has("double-sided-" + i)) {
			doubleSided.add((String) obj.get("double-sided-" + i));
			i++;
		}
		totalPass = (String) obj.get("total-pass");
		totalFail = (String) obj.get("total-fail");
		barcode1 = (String) obj.get("barcode1");
		barcode2 = (String) obj.get("barcode2");
		passValue = (String) obj.get("pass-value");
		failValue = (String) obj.get("fail-value");
		name = (String) obj.get("course-name");
		instructor = new Instructor((JSONObject) obj.get("instructor"));
		exam = new Exam((JSONObject) obj.get("exam"));
		JSONObject jsonAward = obj.optJSONObject("award");
		if(jsonAward != null){
			award = new Award(jsonAward);
		}
		host = new Host((JSONObject) obj.get("host"));
		examiner = new Examiner((JSONObject) obj.get("examiner"));
		JSONArray jsonStudents = obj.getJSONArray("students");
		for (Object object : jsonStudents) {
			JSONObject jsonStudent = (JSONObject) object;
			Student student = new Student(jsonStudent);
			students.add(student);
		}
		JSONArray jsonSees = (JSONArray) obj.get("must-sees");
		mustSees = new ArrayList<>();
		for (Object object : jsonSees) {
			JSONObject jsonObject = (JSONObject) object;
			MustSee see = new MustSee(jsonObject);
			mustSees.add(see);
		}
	}

	private void init() {
		pageNumbers = new ArrayList<>();
		doubleSided = new ArrayList<>();
		students = new ArrayList<>();
	}

	public class Student {
		String name;
		String address;
		String city;
		String postalCode;
		String email;
		String phone;
		String year;
		String month;
		String day;
		String apartment;
		
		private ArrayList<Prerequisite> prerequisites;
		private ArrayList<MustSee> mustSees;

		public Student(JSONObject obj) {
			name = (String) obj.get("name");
			address = (String) obj.get("address");
			city = (String) obj.get("city");
			postalCode = (String) obj.get("postal-code");
			email = (String) obj.get("email");
			phone = (String) obj.get("phone");
			year = (String) obj.get("year");
			month = (String) obj.get("month");
			day = (String) obj.get("day");
			apartment = obj.optString("apartment");
			JSONArray pres = obj.getJSONArray("prerequisites");
			prerequisites = new ArrayList<>();
			for (Object object : pres) {
				JSONObject jsonObject = (JSONObject) object;
				Prerequisite prerequisite = new Prerequisite(jsonObject);
				prerequisites.add(prerequisite);
			}
			mustSees = new ArrayList<>();
			JSONArray jsonSees = (JSONArray) obj.get("must-sees");
			for (Object object : jsonSees) {
				JSONObject jsonObject = (JSONObject) object;
				MustSee see = new MustSee(jsonObject);
				mustSees.add(see);
			}
		}

		public String getName() {
			return name;
		}

		public String getAddress() {
			return address;
		}

		public String getCity() {
			return city;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public String getEmail() {
			return email;
		}

		public String getPhone() {
			return phone;
		}

		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public String getDay() {
			return day;
		}

		public ArrayList<Prerequisite> getPrerequisites() {
			return prerequisites;
		}

		public ArrayList<MustSee> getMustSees() {
			return mustSees;
		}

		public String getApartment() {
			return apartment;
		}

	}

	public class Examiner {

		String name;
		String id;
		String email;
		String areaCode;
		String phone;

		public Examiner(JSONObject obj) {
			name = (String) obj.get("name");
			id = (String) obj.get("id");
			email = (String) obj.get("email");
			areaCode = (String) obj.get("area-code");
			phone = (String) obj.get("phone");
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		public String getEmail() {
			return email;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public String getPhone() {
			return phone;
		}

	}

	public class Host {

		public class Payment {
			String field;
			String attached;
			String notAttached;

			public Payment(JSONObject obj) {

				field = (String) obj.get("field");
				attached = (String) obj.get("attached-value");
				notAttached = (String) obj.get("not-attached-value");
			}
		}

		Payment payment;
		String name;
		String areaCode;
		String phone;
		String address;
		String province;
		String postalCode;
		String city;

		public Host(JSONObject obj) {
			payment = new Payment((JSONObject) obj.get("payment"));
			name = (String) obj.get("host-name");
			areaCode = (String) obj.get("host-area-code");
			phone = (String) obj.get("host-phone");
			address = (String) obj.get("host-address");
			province = (String) obj.get("host-province");
			postalCode = (String) obj.get("host-postal-code");
			city = (String) obj.get("host-city");
		}

		public String getExamFeesField() {
			return payment.field;
		}

		public String getExamFeesAttached() {
			return payment.attached;
		}

		public String getExamFeesNotAttached() {
			return payment.notAttached;
		}

		public String getName() {
			return name;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public String getPhone() {
			return phone;
		}

		public String getStreet() {
			return address;
		}

		public String getCity() {
			return city;
		}

		public String getProvince() {
			return province;
		}

		public String getPostalCode() {
			return postalCode;
		}

	}

	public class Instructor {
		private String name;
		String id;
		String email;
		String areaCode;
		String phone;

		public Instructor(JSONObject obj) {
			setName((String) obj.get("name"));
			id = (String) obj.get("id");
			email = (String) obj.get("email");
			areaCode = (String) obj.get("area-code");
			phone = (String) obj.get("phone");
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getID() {
			return id;
		}

		public String getEmail() {
			return email;
		}

		public String getAreaCode() {
			return areaCode;
		}

		public String getPhone() {
			return phone;
		}

	}

	public class Award {
		String field;
		String awardIssued;
		String awardNotIssued;

		public Award(JSONObject obj) {
			field = (String) obj.get("field");
			awardIssued = (String) obj.get("award-issued");
			awardNotIssued = (String) obj.get("award-not-issued");
		}

		public String getField() {
			return field;
		}

		public String getAwardIssued() {
			return awardIssued;
		}

		public String getAwardNotIssued() {
			return awardNotIssued;
		}

	}

	/**
	 * 
	 * @return a list of prerequisites that do not belong to any student.
	 */
	public List<Prerequisite> getGenericPrerequisites() {
		List<Prerequisite> prerequisites = students.get(0).prerequisites;
		List<Prerequisite> pres = new ArrayList<>();
		for (Prerequisite prerequisite : prerequisites) {
			pres.add(new Prerequisite(prerequisite));
		}
		Collections.sort(pres, new Comparator<Prerequisite>() {
			public int compare(Prerequisite o1, Prerequisite o2) {
				if (o1.key.equals(o2.key))
					return 0;
				return Integer.valueOf(o1.key) < Integer.valueOf(o2.key) ? -1 : 1;
			}
		});
		return pres;
	}

	public class Exam {

		public class Type {
			String field;
			String originalValue;
			String recertValue;

			public Type(JSONObject obj) {
				field = (String) obj.get("field");
				originalValue = (String) obj.get("original");
				recertValue = (String) obj.get("recert");
			}
		}

		String year;
		String day;
		String month;
		Type type;
		String facilityName;
		String facilityAreaCode;
		String facilityPhone;

		public Exam(JSONObject obj) {
			year = (String) obj.get("year");
			month = (String) obj.get("month");
			day = (String) obj.get("day");
			type = new Type((JSONObject) obj.get("type"));
			facilityName = (String) obj.get("facility-name");
			facilityAreaCode = (String) obj.get("facility-area-code");
			facilityPhone = (String) obj.get("facility-phone");
		}

		public String getFacilityName() {
			return facilityName;
		}

		public String getFacilityAreaCode() {
			return facilityAreaCode;
		}

		public String getFacilityPhone() {
			return facilityPhone;
		}

		public String getOriginal() {
			return type.field;
		}

		public String getOriginalValue() {

			return type.originalValue;
		}

		public String getRecertValue() {

			return type.recertValue;
		}

		public String getYear() {
			return year;
		}

		public String getMonth() {
			return month;
		}

		public String getDay() {
			return day;
		}
	}

	public List<MustSee> getClientMustSees() {
		List<MustSee> sees = new ArrayList<>();
		for (MustSee mustSee : mustSees) {
			MustSee see = new MustSee();
			see.name = new String(mustSee.name);
			see.instructorEvaluated = mustSee.instructorEvaluated;
			see.appEvaluated = mustSee.appEvaluated;
			see.examinerEvaluated = mustSee.examinerEvaluated;
			see.item = new String(mustSee.item);
			sees.add(see);
		}
		return sees;
	}

	public String getName() {
		return this.name;
	}

	public int getStudentCapacity() {
		return students.size();
	}

	public String barcodeOneField() {
		return barcode1;
	}

	public String barcodeTwoField() {
		return barcode2;
	}

	public Instructor getInstructor() {
		return instructor;
	}

	public Exam getExam() {

		return exam;
	}

	public Host getHost() {
		return host;
	}

	public Award getAward() {
		return award;
	}

	public Examiner getExaminer() {
		return examiner;
	}

	public Iterator<Student> getStudentIterator() {
		return students.iterator();
	}

	public String getPassValue(MustSee template) {
		for (MustSee mustSee : mustSees) {
			if (mustSee.getItem().equals(template.getItem())) {
				switch (mustSee.getType()) {
				case "2chk":
				case "1chk":
					return passValue;
				case "txt":
					return "Pass";
				default:
					return "";
				}
			}
		}
		return "";
	}

	public String getFailValue(MustSee template) {
		for (MustSee mustSee : mustSees) {
			if (mustSee.getItem().equals(template.getItem())) {
				switch (mustSee.getType()) {
				case "2chk":
					return failValue;
				case "1chk":
					return "Off";
				case "txt":
					return "Fail";
				default:
					return "";
				}
			}
		}
		return "";
	}

	public boolean hasApartment() {
		
		return students.get(0).apartment != null;
	}

	public List<String> getPageNumbers() {
		return this.pageNumbers;
	}

	public String getPageTotal() {
		return this.pageTotal;
	}

	public int getNumberOfPages() {
		return pageNumbers.size();
	}

	public List<String> getDoubleSided() {
		return this.doubleSided;
	}

	public String getTotalFail() {
		return this.totalFail;
	}

	public String getTotalPass() {
		return this.totalPass;
	}

}
