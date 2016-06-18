package Elyas.LssTestSheets.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import Elyas.LssTestSheets.model.Model.ChangeHandler;
import javafx.beans.property.SimpleStringProperty;

public class Course {

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

	public Course() {
		name = new SimpleStringProperty();
		clients = new ArrayList<>();
		qualifications = new ArrayList<>();
		clientCount = 0L;
		changeHandler = new ChangeHandler() {
			@Override
			public void onChange() {

			}
		};
	}

	public Course(Collection<Qualification> c) {
		qualifications = new ArrayList<>(c);
		clients = new ArrayList<>();
		clientCount = 0L;
		changeHandler = new ChangeHandler() {
			@Override
			public void onChange() {

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
		}
		name = new SimpleStringProperty(n);
	}

	public Integer getClientsCount() {
		if (clients.isEmpty()) {
			return clientCount.intValue();
		}

		return this.clients.size();
	}

	public void setClientCount(Long l) {
		if (l != null)
			this.clientCount = l;
	}

	public void setName(String name) {
		this.name.set(name);
		changeHandler.onChange();
	}

	public String toString() {
		return name.get();

	}

	public void setFacility(Facility f) {
		this.facility = f;
		changeHandler.onChange();

	}

	public void setBarcodeOne(String text) {
		this.barcode1 = text;
		changeHandler.onChange();
	}

	public void setBarcodeTwo(String text) {
		this.barcode2 = text;
		changeHandler.onChange();
	}

	public ArrayList<Client> getClients() {
		return this.clients;
	}

	public String getName() {
		return this.name.get();
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
		object.toString(4);
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
		c.setID("" + clients.size());
		for (Qualification qualification : qualifications) {
			c.setMustSees(qualification.getName(), qualification.getMustSees());
		}

		changeHandler.onChange();
	}

	public void setOnChange(ChangeHandler handler) {
		this.changeHandler = handler;

	}

	public void removeClient(Client c) {
		if (clients == null || c == null)
			return;
		clients.remove(c);
		changeHandler.onChange();
	}

	public void updateClient(Client c) {
		if (clients == null)
			return;
		for (Client client : clients) {
 			if (client.getID() == c.getID()) {
				client.update(c);
				changeHandler.onChange();
			}
		}

	}

	public void addQualification(Qualification qualification) {
		if (qualification != null)
			this.qualifications.add(qualification);
		qualification.setonChangeListener(() -> {
			changeHandler.onChange();
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
			qualification.setonChangeListener(() -> {
				changeHandler.onChange();
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
			if(qualification.getName().equals(qual)){
				return qualification.getInstructors();
			}
		}
		return null;
	}

	public List<Employee> getExaminers(String qual) {
		for (Qualification qualification : qualifications) {
			if(qualification.getName().equals(qual)){
				return qualification.getExaminers();
			}
		}
		return null;
	}
}
