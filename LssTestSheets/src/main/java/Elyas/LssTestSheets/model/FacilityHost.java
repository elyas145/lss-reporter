package Elyas.LssTestSheets.model;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

public class FacilityHost {
	private String name = "";
	private String address = "";
	private String city = "";
	private String province = "";
	private String postalCode = "";
	private String areaCode = "";
	private String phone = "";
	private String extension = "";
	private boolean fees = false;

	public FacilityHost(JSONObject obj) {
		setExamFees(obj.optBoolean("fees"));
		setName(obj.getString("name"));
		setAddress(obj.getString("address"));
		setCity(obj.getString("city"));
		setProvince(obj.getString("province"));
		setPostalCode(obj.getString("postal-code"));
		setAreaCode(obj.getString("area-code"));
		setPhone(obj.getString("phone"));
		setExtension(obj.getString("extension"));
	}

	public FacilityHost() {
	}

	public String getName() {
		return StringUtils.capitalize(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return StringUtils.capitalize(address);
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return StringUtils.capitalize(city);
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return StringUtils.capitalize(province);
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getPostalCode() {
		return postalCode.toUpperCase();
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

	public void validate(Warning warning) {
		if (name == null || name.trim().equals("")) {
			warning.add("Host name not specified.");
		}
		if (areaCode == null || areaCode.trim().equals("")) {
			warning.add("Host area Code not specified.");
		}
		if (phone == null || phone.trim().equals("")) {
			warning.add("Host phone not specified.");
		}
		if (extension == null || extension.trim().equals("")) {
			warning.add("Host extension not specified.");
		}
		if (postalCode == null || postalCode.trim().equals("")) {
			warning.add("Host postal code not specified.");
		}
		if (address == null || address.trim().equals("")) {
			warning.add("Host address not specified.");
		}
		if (city == null || city.trim().equals("")) {
			warning.add("Host city not specified.");
		}
		if (province == null || province.trim().equals("")) {
			warning.add("Host province not specified.");
		}
	}

	public String getFinalPhone() {
		String finalPhone = new String(phone);
		if (extension != null && !extension.equals("")) {
			finalPhone += " ext." + extension;
		}
		return finalPhone;
	}

	public String getFinalPhoneWithAreaCode() {
		String finalPhone = new String("(" + areaCode + ") " + phone);
		if (extension != null && !extension.equals("")) {
			finalPhone += " ext." + extension;
		}
		return finalPhone;
	}

}
