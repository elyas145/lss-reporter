package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.model.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EmployeeController extends Controller implements Initializable{
	
	@FXML
	CustomTextField txtName;
	@FXML
	CustomTextField txtID;
	@FXML
	CustomTextField txtEmail;
	@FXML
	CustomTextField txtAreaCode;
	@FXML
	CustomTextField txtPhone;
	@FXML
	CustomTextField txtExtension;
	@FXML
	Label lblTitle;
	private Stage stage;
	private Employee currentEmployee;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}

	public void setEmployee(Employee e){
		currentEmployee = e;
		lblTitle.setText(e.getName());
		txtName.setText(e.getName());
		txtID.setText(e.getId());
		txtEmail.setText(e.getEmail());
		txtAreaCode.setText(e.getAreaCode());
		txtPhone.setText(e.getPhone());
		txtExtension.setText(e.getExtension());
	}

	public Employee getEmployee() {
		
		Employee e;
		if(currentEmployee == null){
			e = new Employee();
		}else{
			e = currentEmployee;
		}
		e.setAreaCode(txtAreaCode.getText());
		e.setEmail(txtEmail.getText());
		e.setExtension(txtExtension.getText());
		e.setId(txtID.getText());
		e.setName(txtName.getText());
		e.setPhone(txtPhone.getText());
		return e;
	}

	public void setStage(Stage stage) {
		this.stage= stage;
		
	}
	
	@FXML
	protected void okActionHandler(ActionEvent event){
		finishHandler.onFinish(null);
	}
	
	@FXML
	protected void cancelActionHandler(ActionEvent event){
		stage.close();
	}
}
