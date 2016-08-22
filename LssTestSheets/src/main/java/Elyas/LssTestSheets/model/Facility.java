package Elyas.LssTestSheets.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class Facility {
	private String name;
	private String areaCode;
	private String phone;
	private String extension;
	private FacilityHost host;
	private boolean isDefault;

	public Facility(JSONObject obj) {
		if (obj != null) {
			setName(obj.getString("name"));
			setAreaCode(obj.getString("area-code"));
			setPhone(obj.getString("phone"));
			setExtension(obj.getString("extension"));
			setHost(new FacilityHost(obj.getJSONObject("host")));
			setDefault(obj.optBoolean("default"));
		}

	}

	public Facility() {
	}

	public String getName() {
		return StringUtils.capitalize(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaCode() {
		return areaCode.toUpperCase();
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public FacilityHost getHost() {
		return host;
	}

	public void setHost(FacilityHost host) {
		this.host = host;
	}

	public void setDefault(boolean b) {
		this.isDefault = b;
	}

	public boolean getDefault() {
		return this.isDefault;
	}

	public String toString() {
		return StringUtils.capitalize(this.name);
	}

	public boolean equals(Object other) {
		if (other instanceof Facility) {
			Facility facility = (Facility) other;
			return facility.isDefault == isDefault && facility.name.equals(this.name)
					&& facility.areaCode.equals(areaCode) && facility.phone.equals(phone)
					&& facility.extension.equals(extension) && facility.host.equals(host);
		}
		return false;
	}

	public String getFinalPhone() {
		if (this.extension == null || this.extension.equals("")) {
			return this.phone;
		}
		return phone + " ext." + extension;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("default", isDefault);
		object.put("name", name);
		object.put("area-code", areaCode);
		object.put("phone", phone);
		object.put("extension", extension);
		object.put("host", host.toJSON());
		return object;
	}

	public void validate(Warning warning) {
		if (name == null || name.trim().equals("")) {
			warning.add("Name not specified.");
		}
		if (areaCode == null || areaCode.trim().equals("")) {
			warning.add("Area code not specified.");
		}
		if (phone == null || phone.trim().equals("")) {
			warning.add("Phone not specified.");
		}
		if (extension == null || extension.trim().equals("")) {
			warning.add("Extension not specified.");
		}
		host.validate(warning);

	}

	public String getFinalPhoneWithAreaCode() {

		return "(" + areaCode + ") " + getFinalPhone();
	}

}
