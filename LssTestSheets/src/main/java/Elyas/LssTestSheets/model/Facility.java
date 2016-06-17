package Elyas.LssTestSheets.model;

import org.json.JSONObject;

public class Facility {
	private String name;
	private String areaCode;
	private String phone;
	private String extension;
	private FacilityHost host;
	private boolean isDefault;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAreaCode() {
		return areaCode;
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
		return this.name;
	}

	public boolean equals(Object other) {
		if (other instanceof Facility) {
			Facility facility = (Facility) other;
			return facility.isDefault == isDefault && facility.name.equals(this.name) && facility.areaCode.equals(areaCode) && facility.phone.equals(phone)
					&& facility.extension.equals(extension) && facility.host.equals(host);
		}
		return false;
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
}
