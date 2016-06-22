package Elyas.LssTestSheets.model;

import org.json.JSONObject;

public class Employee {

	private String name = "";
	private String areaCode = "";
	private String phone = "";
	private String id = "";
	private String email = "";
	private String extension = "";
	private Long key = null;

	public String getName() {
		return name;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public String getPhone() {
		return phone;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setName(String name) {
		if (name != null)
			this.name = name;

	}

	public void setAreaCode(String areaCode) {
		if (areaCode != null)
			this.areaCode = areaCode;

	}

	public void setPhone(String phone) {
		if (phone != null)
			this.phone = phone;

	}

	public void setId(String id) {
		if (id != null)
			this.id = id;

	}

	public void setEmail(String email) {
		if (email != null)
			this.email = email;

	}

	public void setExtension(String extension) {
		if (extension != null)
			this.extension = extension;
	}

	public String toString() {
		return name;
	}

	public String getExtension() {
		return extension;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("e-key", key.intValue());
		object.put("name", name);
		object.put("id", id);
		object.put("email", email);
		object.put("area-code", areaCode);
		object.put("phone", phone);
		object.put("extension", extension);
		return object;
	}

	public void setKey(Long l) {
		if (l != null)
			this.key = l;

	}

	public boolean equals(Object other) {
		if (other instanceof Employee) {
			Employee e = (Employee) other;
			return e.key.equals(key);
		}
		return false;
	}

	public Long getKey() {
		return key;
	}

	public void update(Employee e) {
		this.areaCode = e.areaCode;
		this.email = e.email;
		this.extension = e.extension;
		this.id = e.id;
		this.name = e.name;
		this.phone = e.phone;

	}

	public void validate(Warning warning) {
		if (areaCode == null || areaCode.trim().equals("")) {
			warning.add(name + " area code not specified.");
		}
		if (email == null || email.trim().equals("")) {
			warning.add(name + " email not specified.");
		}
		if (id == null || id.trim().equals("")) {
			warning.add(name + " id not specified.");
		}
		if (name == null || name.trim().equals("")) {
			warning.add(name + " name not specified.");
		}
		if (phone == null || phone.trim().equals("")) {
			warning.add(name + " phone not specified.");
		}
		
	}

}
