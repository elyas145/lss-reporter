package Elyas.LssTestSheets.model;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.ArrayList;
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
		award = new Award((JSONObject) obj.get("award"));
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

		public Host(JSONObject obj) {
			payment = new Payment((JSONObject) obj.get("payment"));
			name = (String) obj.get("name");
			areaCode = (String) obj.get("host-area-code");
			phone = (String) obj.get("host-phone");
			address = (String) obj.get("host-address");
			province = (String) obj.get("host-province");
			postalCode = (String) obj.get("host-postal-code");
		}

	}

	public class Instructor {
		String name;
		String id;
		String email;
		String areaCode;
		String phone;

		public Instructor(JSONObject obj) {
			name = (String) obj.get("name");
			id = (String) obj.get("id");
			email = (String) obj.get("email");
			areaCode = (String) obj.get("area-code");
			phone = (String) obj.get("phone");
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

}
