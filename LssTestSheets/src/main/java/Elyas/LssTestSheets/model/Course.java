package Elyas.LssTestSheets.model;

import java.io.IOException;
import java.text.Collator;
import java.time.LocalDate;
import java.time.chrono.JapaneseChronology;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Elyas.LssTestSheets.model.Model.ChangeHandler;
import javafx.beans.property.SimpleStringProperty;

public class Course {

	public Course() {
		name = new SimpleStringProperty("");
		clients = new ArrayList<>();
		qualifications = new ArrayList<>();
		clientCount = 0L;
		report = new Report();
		changeHandler = new ChangeHandler() {
			@Override
			public void onChange(String change) {
				Model.getInstance().setChanged(change);
			}
		};
	}

	public Course(Collection<Qualification> c) {
		qualifications = new ArrayList<>(c);
		clients = new ArrayList<>();
		clientCount = 0L;
		report = new Report();
		changeHandler = new ChangeHandler() {
			@Override
			public void onChange(String change) {
				Model.getInstance().setChanged(change);
			}
		};
		String n = "";
		int i = 0;
		for (Qualification qualification : qualifications) {
			if (i == qualifications.size() - 1) {
				n += qualification.getName();
			} else {
				n += qualification.getName() + " and ";
			}
			i++;
		}
		name = new SimpleStringProperty(n);
	}

	public Course(JSONObject obj) throws JSONException {
		filePath = obj.optString("path");
		name = new SimpleStringProperty(obj.getString("name"));
		barcode1 = obj.optString("barcode1");
		barcode2 = obj.optString("barcode2");
		clientCount = obj.optLong("client-count");

		qualifications = new ArrayList<>();
		JSONArray jsonQuals = obj.getJSONArray("quals");
		for (int i = 0; i < jsonQuals.length(); i++) {
			JSONObject jsonQual = jsonQuals.getJSONObject(i);
			qualifications.add(new Qualification(jsonQual));
		}

		JSONArray jsonClients = obj.optJSONArray("clients");
		clients = new ArrayList<>();
		if (jsonClients != null) {

			for (int i = 0; i < jsonClients.length(); i++) {
				JSONObject jsonClient = jsonClients.getJSONObject(i);
				clients.add(new Client(jsonClient, qualifications));
			}
		}

		facility = new Facility(obj.optJSONObject("facility"));
		report = new Report(obj.optJSONObject("report"));
	}

	public Integer getClientsCount() {
		if (clients.isEmpty()) {
			return clientCount.intValue();
		}

		return this.clients.size();
	}

	public String getBarcodes() {
		String barcodes = "";
		if (barcode1 != null && !barcode1.equals("")) {
			barcodes += barcode1;
		}
		if (barcode2 != null && !barcode2.equals("") && barcode1 != null && !barcode1.equals("")) {
			barcodes += ", ";
		}
		if (barcode2 != null && !barcode2.equals("")) {
			barcodes += barcode2;
		}
		return barcodes;
	}

	public void setClientCount(Long l) {
		if (l != null)
			this.clientCount = l;
	}

	public void setName(String name) {
		if (!this.name.get().equals(name)) {
			this.name.set(name);
		}
	}

	public String toString() {
		return name.get();

	}

	public void setFacility(Facility f) {
		if (this.facility == null || !this.facility.equals(f)) {
			this.facility = f;
			changeHandler.onChange("Course Facility Changed.");
		}

	}

	public void setBarcodeOne(String text) {
		if (this.barcode1 == null || !this.barcode1.equals(text)) {
			this.barcode1 = text;
			changeHandler.onChange("Barcode One Changed");
		}
	}

	public void setBarcodeTwo(String text) {
		if (this.barcode2 == null || !this.barcode2.equals(text)) {
			this.barcode2 = text;
			changeHandler.onChange("Barcode Two Changed");
		}
	}

	public ArrayList<Client> getClients() {
		Collections.sort(clients, new Comparator<Client>() {
			@Override
			public int compare(Client o1, Client o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return this.clients;
	}

	public String getName() {
		return StringUtils.capitalize(this.name.get());
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("path", filePath);
		object.put("name", name.get());
		object.put("barcode1", (barcode1 == null ? "" : barcode1));
		object.put("barcode2", (barcode2 == null ? "" : barcode2));

		if (facility != null) {
			object.put("facility", facility.toJSON());
		}
		object.put("client-count", clients.size());

		if (clients != null && !clients.isEmpty()) {
			JSONArray array = new JSONArray();
			for (Client client : clients) {
				array.put(client.toJSON());
			}
			object.put("clients", array);
		}

		if (!qualifications.isEmpty()) {
			JSONArray array = new JSONArray();
			for (Qualification qual : qualifications) {
				array.put(qual.toJSON());
			}
			object.put("quals", array);
		}
		object.put("report", report.toJSON());
		return object;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String relativePath) {
		this.filePath = relativePath;
	}

	public void addClient(Client c) {
		if (clients == null) {
			clients = new ArrayList<>();
		}
		clients.add(c);
		if (c.getID() == null || c.getID().equals(""))
			c.setID("" + clientID++);

		for (Qualification qualification : qualifications) {
			c.setMustSees(qualification.getName(), qualification.getMustSees());
			c.setPrerequisites(qualification.getName(), qualification.getPrerequisites());
		}

		changeHandler.onChange("Client, "+c.getName()+", Added to Course.");
	}

	public void setOnChange(ChangeHandler handler) {
		this.changeHandler = handler;

	}

	public void removeClient(Client c) {
		if (clients == null || c == null)
			return;
		clients.remove(c);
		changeHandler.onChange("Client, "+c.getName()+", Removed From Course.");
	}

	public void updateClient(Client c) {
		if (clients == null)
			return;
		for (Client client : clients) {
			if (client.getID().equals(c.getID())) {
				if (!client.equals(c)) {
					client.update(c);
					changeHandler.onChange("Client, "+c.getName()+", Updated.");
				}
			}
		}

	}

	public void addQualification(Qualification qualification) {
		if (qualification != null)
			this.qualifications.add(qualification);
		qualification.setonChangeListener((String change) -> {
			changeHandler.onChange(change);
		});

	}

	public List<Qualification> getQualifications() {
		return qualifications;
	}

	public Qualification getQualification(String course) {
		for (Qualification qualification : qualifications) {
			if (qualification.getName().equals(course)) {
				return qualification;
			}
		}
		return null;
	}

	public String getExamDate() {
		List<LocalDate> dates = new ArrayList<>();
		for (Qualification qualification : qualifications) {
			boolean exists = false;
			for (LocalDate localDate : dates) {
				if (qualification.getExamDate().equals(localDate)) {
					exists = true;
				}
			}
			if (!exists) {
				dates.add(qualification.getExamDate());
			}
		}

		int i = 0;
		String string = "";
		for (LocalDate localDate : dates) {
			if (i == dates.size() - 1) {
				string += localDate.toString();
			} else {
				string += localDate.toString() + ", ";
			}
			i++;
		}
		return string;
	}

	public void setExamDate(String exam) {
		for (Qualification qualification : qualifications) {
			qualification.setExamDate(LocalDate.parse(exam));
		}
	}

	public Report getReport() {
		return this.report;
	}

	public void setQualifications(Collection<Qualification> c) {
		this.qualifications = new ArrayList<>(c);
		for (Qualification qualification : c) {
			qualification.setonChangeListener((String change) -> {
				changeHandler.onChange(change);
			});
		}

	}

	public Facility getFacility() {
		return facility;
	}

	public String getBarcode1() {
		return barcode1;
	}

	public String getBarcode2() {
		return barcode2;
	}

	public List<Employee> getInstructors(String qual) {
		for (Qualification qualification : qualifications) {
			if (qualification.getName().equals(qual)) {
				return qualification.getInstructors();
			}
		}
		return null;
	}

	public List<Employee> getExaminers(String qual) {
		for (Qualification qualification : qualifications) {
			if (qualification.getName().equals(qual)) {
				return qualification.getExaminers();
			}
		}
		return null;
	}

	/**
	 * checks for user errors in the course fields.
	 * 
	 * @param warning
	 *            the object to store the warnings in.
	 */
	public void validate(Warning warning) {
		facility.validate(warning.newCategory("Facility"));
		Warning clientWarnings = warning.newCategory("Clients");
		if (clients.isEmpty()) {
			warning.add("Clients not specified.");
		}
		for (Client client : clients) {
			client.validate(clientWarnings);
		}
		if (barcode1 == null || barcode1.trim().equals("")) {
			warning.add("Barcode one not specified.");
		}
		if (barcode2 == null || barcode2.trim().equals("")) {
			warning.add("Barcode two not specified.");
		}
		Warning qualWarnings = warning.newCategory("Qualifications");
		for (Qualification qualification : qualifications) {
			qualification.validate(qualWarnings);
		}
	}

	public String getInstructorsNames(Qualification qual) {
		String names = "";
		int i = 0;
		List<Employee> employees = qualifications.get(qualifications.indexOf(qual)).getInstructors();
		if (employees == null || employees.isEmpty())
			return "";
		for (Employee employee : employees) {
			if (i == employees.size() - 1) {
				names += employee.getName();
			} else {
				names += employee.getName() + ", ";
			}
			i++;
		}
		return names;
	}

	public String getExaminersNames(Qualification qual) {
		String names = "";
		int i = 0;
		List<Employee> employees = qualifications.get(qualifications.indexOf(qual)).getExaminers();
		if (employees == null || employees.isEmpty())
			return "";
		for (Employee employee : employees) {
			if (i == employees.size() - 1) {
				names += employee.getName();
			} else {
				names += employee.getName() + ", ";
			}
			i++;
		}
		return names;
	}

	public String getInstructorsIDs(Qualification qual) {

		String names = "";
		int i = 0;
		List<Employee> employees = qualifications.get(qualifications.indexOf(qual)).getInstructors();
		if (employees == null || employees.isEmpty())
			return "";
		for (Employee employee : employees) {
			if (i == employees.size() - 1) {
				names += employee.getId();
			} else {
				names += employee.getId() + ", ";
			}
			i++;
		}
		return names;
	}

	public String getInstructorsEmails(Qualification qual) {
		String names = "";
		int i = 0;
		List<Employee> employees = qualifications.get(qualifications.indexOf(qual)).getInstructors();
		if (employees == null || employees.isEmpty())
			return "";

		for (Employee employee : employees) {
			if (i == employees.size() - 1) {
				names += employee.getEmail();
			} else {
				names += employee.getEmail() + ", ";
			}
			i++;
		}
		return names;
	}

	public String getInstructorsAreaCode(Qualification qual) {
		List<Employee> employees = qualifications.get(qualifications.indexOf(qual)).getInstructors();
		if (employees == null || employees.isEmpty())
			return "";
		return employees.get(0).getAreaCode();
	}

	public String getInstructorsPhone(Qualification qual) {
		String names = "";
		int i = 0;
		List<Employee> employees = qualifications.get(qualifications.indexOf(qual)).getInstructors();
		if (employees == null || employees.isEmpty())
			return "";

		for (Employee employee : employees) {
			if (i == employees.size() - 1) {
				names += employee.getFinalPhone();
			} else {
				names += employee.getFinalPhone() + ", ";
			}
			i++;
		}
		return names;
	}

	public String getInstructorsPhoneWithAreaCode(Qualification qualification) {
		String names = "";
		int i = 0;
		List<Employee> employees = qualifications.get(qualifications.indexOf(qualification)).getInstructors();
		if (employees == null || employees.isEmpty())
			return "";

		for (Employee employee : employees) {
			if (i == employees.size() - 1) {
				names += "(" + employee.getAreaCode() + ") " + employee.getFinalPhone();
			} else {
				names += "(" + employee.getAreaCode() + ") " + employee.getFinalPhone() + ", ";
			}
			i++;
		}
		return names;
	}

	public int getTotalPass(Qualification qualification) {
		int i = 0;
		for (Client client : clients) {
			MustSee see = client.getMustSee(qualification.getName(), "result");
			see.evaluate(client, qualification.getName());
			if (see.isCompleted()) {
				i++;
			}
		}
		return i;
	}

	public int getTotalFail(Qualification qualification) {
		int i = 0;
		for (Client client : clients) {
			MustSee see = client.getMustSee(qualification.getName(), "result");
			see.evaluate(client, qualification.getName());
			if (!see.isCompleted()) {
				i++;
			}
		}
		return i;
	}

	private SimpleStringProperty name;

	private ArrayList<Client> clients;
	private Facility facility;
	private String barcode1;
	private String barcode2;

	private String filePath;
	private ChangeHandler changeHandler;
	private Long clientCount;
	private List<Qualification> qualifications;

	private Report report;

	private int clientID;

}
