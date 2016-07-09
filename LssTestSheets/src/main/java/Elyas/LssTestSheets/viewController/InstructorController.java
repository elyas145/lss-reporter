package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.factory.PersonFactory;
import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.factory.ViewFactoryResult;
import Elyas.LssTestSheets.model.Employee;
import Elyas.LssTestSheets.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InstructorController extends Controller implements Initializable {

	@FXML
	private ListView<Employee> instructorsView;
	@FXML
	private ListView<Employee> peopleView;

	private ObservableList<Employee> obsInstructors;
	private ObservableList<Employee> obsPersons;
	private String course;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		List<Employee> registeredPersons = PersonFactory.getKnownEmployees();
		obsPersons = FXCollections.observableList(registeredPersons);
		peopleView.setItems(obsPersons);

		List<Employee> instructors = new ArrayList<>();
		obsInstructors = FXCollections.observableList(instructors);

		instructorsView.setItems(obsInstructors);
		instructorsView.setOnMouseClicked(event -> {
			if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
				employeeSelected(instructorsView.getSelectionModel().getSelectedItem());
			}
		});
		peopleView.setOnMouseClicked(event -> {
			if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
				employeeSelected(peopleView.getSelectionModel().getSelectedItem());
				
			}
		});

	}

	private void employeeSelected(Employee e) {
		if(e == null)
			return;
		
		ViewFactoryResult result = ViewFactory.getView("/fxml/employee.fxml");
		final EmployeeController controller = (EmployeeController) result.controller;
		Scene scene = new Scene(result.parent);
		final Stage stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		controller.setEmployee(e);
		stage.show();
		controller.setStage(stage);
		controller.setOnFinishHandler(new FinishHandler() {
			@Override
			public void onFinish(ViewState state) {
				Employee employee = controller.getEmployee();
				if (preCheck(employee)) {
					PersonFactory.updateEmployee(employee);
					List<Employee> persons = new ArrayList<>(obsPersons);
					List<Employee> instructors = new ArrayList<>(obsInstructors);
					obsPersons.removeAll(obsPersons);
					for (Employee e : persons) {
						obsPersons.add(e);
					}
					
					obsInstructors.removeAll(obsInstructors);
					for (Employee e : instructors) {
						obsInstructors.add(e);
					}
					
					stage.close();
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("Please give your employee a name!");
					alert.showAndWait();
				}
			}

			private boolean preCheck(Employee employee) {
				if (employee.getName() == null || employee.getName().trim().equals("")) {
					return false;
				}
				return true;
			}
		});
		
	}
	
	@FXML
	protected void addButtonAction(ActionEvent event) {
		Employee selectedPerson = peopleView.getSelectionModel().getSelectedItem();
		if (selectedPerson == null) {
			return;
		}
		obsInstructors.add(selectedPerson);
		obsPersons.remove(selectedPerson);
		//Model.getInstance().getCourse().getQualification(course).addInstructor(selectedPerson);
	}

	@FXML
	protected void removeButtonAction(ActionEvent event) {
		Employee selectedPerson = instructorsView.getSelectionModel().getSelectedItem();
		if (selectedPerson == null)
			return;

		obsPersons.add(selectedPerson);
		obsInstructors.remove(selectedPerson);

	}

	@FXML
	protected void newPersonAction(ActionEvent event) {
		ViewFactoryResult result = ViewFactory.getView("/fxml/employee.fxml");
		final EmployeeController controller = (EmployeeController) result.controller;
		Scene scene = new Scene(result.parent);
		final Stage stage = new Stage();
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
		controller.setStage(stage);
		controller.setOnFinishHandler(new FinishHandler() {
			@Override
			public void onFinish(ViewState state) {
				Employee employee = controller.getEmployee();
				if (preCheck(employee)) {
					PersonFactory.addNewEmployee(employee);
					obsPersons.add(employee);
					stage.close();
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("Please give your employee a name!");
					alert.showAndWait();
				}
			}

			private boolean preCheck(Employee employee) {
				if (employee.getName() == null || employee.getName().trim().equals("")) {
					return false;
				}
				return true;
			}
		});
	}

	@Override
	public ViewState getViewState() {

		return ViewState.INSTRUCTOR;
	}

	public void setCourse(String course) {
		this.course = course;
		List<Employee> employees = Model.getInstance().getInstructors(course);
		
			obsInstructors = FXCollections.observableList(employees);
			instructorsView.setItems(obsInstructors);
			for (Employee employee : obsInstructors) {
				for (Employee employee2 : obsPersons) {
					if(employee.equals(employee2)){
						obsPersons.remove(employee);
						break;
					}
				}
			}
		
	}
}
