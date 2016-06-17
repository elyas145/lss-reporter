package Elyas.LssTestSheets.factory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import Elyas.LssTestSheets.model.Facility;
import Elyas.LssTestSheets.model.FacilityHost;

public class FacilityFactory {

	public static List<Facility> getKnownFacilities() {
		ArrayList<Facility> facilities = new ArrayList<>();
		InputStream is = null;
		try {
			is = CourseFactory.class.getResourceAsStream("/json/facilities.json");
			String jsonTxt = IOUtils.toString(is);

			JSONObject obj = new JSONObject(jsonTxt);
			JSONArray jsonFacilities = obj.getJSONArray("facilities");
			Iterator<Object> iterator = jsonFacilities.iterator();
			while (iterator.hasNext()) {
				JSONObject jsonFacility = (JSONObject) iterator.next();
				Facility p = new Facility();
				p.setName(jsonFacility.getString("name"));
				p.setAreaCode(jsonFacility.getString("area-code"));
				p.setPhone(jsonFacility.getString("phone"));
				p.setExtension(jsonFacility.getString("extension"));
				p.setDefault(jsonFacility.getBoolean("default"));
				FacilityHost host = new FacilityHost();
				JSONObject objHost = jsonFacility.getJSONObject("host");
				host.setAddress(objHost.getString("address"));
				host.setName(objHost.getString("name"));
				host.setCity(objHost.getString("city"));
				host.setProvince(objHost.getString("province"));
				host.setPostalCode(objHost.getString("postal-code"));
				host.setAreaCode(objHost.getString("area-code"));
				host.setPhone(objHost.getString("phone"));
				host.setExtension(objHost.getString("extension"));
				host.setExamFees(objHost.getBoolean("fees"));

				p.setHost(host);
				facilities.add(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return facilities;
	}

	public static void addNewFacility(Facility facility) {

		try {
			InputStream is = CourseFactory.class.getResourceAsStream("/json/facilities.json");
			String jsonTxt = IOUtils.toString(is);
			JSONObject object = new JSONObject(jsonTxt);
			JSONObject facil = facility.toJSON();
			object.append("facilities", facil);

			is.close();
			File f;
			URL url = PersonFactory.class.getResource("/json/facilities.json");
			try {
				f = new File(url.toURI());
			} catch (URISyntaxException e) {
				f = new File(url.getPath());
			}

			FileWriter fw = new FileWriter(f, false);
			fw.write(object.toString(4));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void updateFacility(String name, Facility facility) {
		try {
			List<Facility> facilities = getKnownFacilities();
			for (Facility facility2 : facilities) {
				if (facility2.getName().equals(name)) {
					facilities.remove(facility2);
					facilities.add(facility);
					break;
				}
			}
			JSONObject object = new JSONObject();
			for (Facility facility2 : facilities) {
				object.append("facilities", facility2.toJSON());
			}
			File f;
			URL url = PersonFactory.class.getResource("/json/facilities.json");
			try {
				f = new File(url.toURI());
			} catch (URISyntaxException e) {
				f = new File(url.getPath());
			}
			FileWriter fw = new FileWriter(f, false);
			fw.write(object.toString(4));
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
