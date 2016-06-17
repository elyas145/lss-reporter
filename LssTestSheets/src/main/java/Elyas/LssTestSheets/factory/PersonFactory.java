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

import Elyas.LssTestSheets.model.Employee;

public class PersonFactory {

	public static List<Employee> getKnownEmployees() {
		ArrayList<Employee> persons = new ArrayList<>();
		InputStream is = null;
		try {
			is = CourseFactory.class.getResourceAsStream("/json/employees.json");
			String jsonTxt = IOUtils.toString(is);

			JSONObject obj = new JSONObject(jsonTxt);
			JSONArray jsonPersons = obj.getJSONArray("employees");
			for (Object object : jsonPersons) {
				JSONObject jsonPerson = (JSONObject) object;
				Employee p = new Employee();
				p.setName(jsonPerson.getString("name"));
				p.setAreaCode(jsonPerson.optString("area-code"));
				p.setPhone(jsonPerson.optString("phone"));
				p.setId(jsonPerson.optString("id"));
				p.setEmail(jsonPerson.optString("email"));
				p.setExtension(jsonPerson.optString("extension"));
				p.setKey(jsonPerson.getLong("e-key"));
				persons.add(p);
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

		return persons;
	}

	public static void addNewEmployee(Employee employee) {

		if (employee.getKey() == null) {
			List<Employee> knownEmployees = PersonFactory.getKnownEmployees();
			Long i = 0L;
			for (Employee e : knownEmployees) {
				if (e.getKey().equals(i)) {
					i++;
				}
			}
			employee.setKey(i);
		}
		InputStream is = null;
		try {
			is = CourseFactory.class.getResourceAsStream("/json/employees.json");
			String jsonTxt = IOUtils.toString(is);

			JSONObject obj = new JSONObject(jsonTxt);
			JSONArray jsonPersons = obj.getJSONArray("employees");
			JSONObject personObj = employee.toJSON();

			jsonPersons.put(personObj);
			is.close();
			File f;
			URL url = PersonFactory.class.getResource("/json/employees.json");
			try {
				f = new File(url.toURI());
			} catch (URISyntaxException e) {
				f = new File(url.getPath());
			}
			JSONObject object = new JSONObject();
			object.put("employees", jsonPersons);

			FileWriter fw = new FileWriter(f, false);
			fw.write(object.toString());
			fw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void updateEmployee(Employee employee) {
		List<Employee> emps = PersonFactory.getKnownEmployees();
		for (Employee e : emps) {
			if (e.getKey().equals(employee.getKey())) {
				e.update(employee);
				break;
			}
		}
		JSONArray array = new JSONArray();
		int i = 0;
		for (Employee e : emps) {
			array.put(employee.toJSON());
		}
		JSONObject object = new JSONObject();
		object.put("employees", array);
		File f;
		URL url = PersonFactory.class.getResource("/json/employees.json");
		try {
			f = new File(url.toURI());
		} catch (URISyntaxException e) {
			f = new File(url.getPath());
		}

		FileWriter fw;
		try {
			fw = new FileWriter(f, false);
			fw.write(object.toString(4));
			fw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
