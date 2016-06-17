package Elyas.LssTestSheets.model;

import org.json.JSONObject;

public class FacilityHost {
	private String name;
	private String address;
	private String city;
	private String province;
	private String postalCode;
	private String areaCode;
	private String phone;
	private String extension;
	private boolean fees;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public void setExamFees(boolean b) {
		this.fees = b;

	}

	public boolean getExamFees() {
		return fees;
	}

	public boolean equals(Object other) {
		if (other instanceof FacilityHost) {
			FacilityHost host = (FacilityHost) other;
			return host.fees == fees && host.name.equals(name) && host.areaCode.equals(areaCode)
					&& host.extension.equals(extension) && host.postalCode.equals(postalCode)
					&& host.address.equals(address) && host.city.equals(city) && host.province.equals(province);
		}
		return false;
	}

	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("fees", fees);
		object.put("name", name);
		object.put("area-code", areaCode);
		object.put("phone", phone);
		object.put("extension", extension);
		object.put("postal-code", postalCode);
		object.put("address", address);
		object.put("city", city);
		object.put("province", province);

		return object;
	}

}
