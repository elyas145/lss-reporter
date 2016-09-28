package Elyas.LssTestSheets.viewController;

import javafx.scene.control.CheckBox;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.factory.PersonFactory;
import Elyas.LssTestSheets.model.Employee;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Qualification;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class CourseImportController extends Controller implements Initializable {

	@FXML
	CheckBox chkImportCourse;
	@FXML
	CheckBox chkImportEmployees;
	@FXML
	CheckBox chkUpdateEmployees;
	@FXML
	CheckBox chkReplaceEmployees;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		chkImportCourse.setSelected(true);

	}

	@FXML
	protected void okAction(ActionEvent event) {
		if (chkImportCourse.isSelected()) {
			CourseFactory.importCourse();
		}else{
			Model.getInstance().setExternalCourse(true);
		}
		List<Employee> known = PersonFactory.getKnownEmployees();
		List<Employee> employees = new ArrayList<>();

		for (Qualification qualification : Model.getInstance().getQualifications()) {
			employees.addAll(qualification.getExaminers());
			employees.addAll(qualification.getInstructors());
		}
		if (chkImportEmployees.isSelected()) {

			boolean found;
			for (Employee employee : employees) {
				found = false;
				for (Employee employee2 : known) {
					if (employee.getName().toLowerCase().trim().equals(employee2.getName().toLowerCase().trim())) {
						found = true;
						break;
					}
				}
				if (!found) {
					PersonFactory.addNewEmployee(employee);
				}
			}
		}
		// update employees
		if (chkUpdateEmployees.isSelected()) {
			for (Employee employee : employees) {
				for (Employee employee2 : known) {
					if (employee.getName().toLowerCase().trim().equals(employee2.getName().toLowerCase().trim())) {
						employee.setKey(employee2.getKey());
						PersonFactory.updateEmployee(employee);
						break;
					}
				}

			}
		}
		// replace inputed employee data with data from the known employees.
		if (chkReplaceEmployees.isSelected()) {
			for (Employee employee : employees) {
				for (Employee employee2 : known) {
					if (employee.getName().toLowerCase().trim().equals(employee2.getName().toLowerCase().trim())) {
						employee.update(employee2);
						break;
					}
				}

			}
		}

		finishHandler.onFinish(ViewState.COURSE);
	}

}
