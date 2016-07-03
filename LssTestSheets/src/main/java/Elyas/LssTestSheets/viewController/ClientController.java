package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ClientController extends Controller implements Initializable {

	@FXML
	CustomTextField txtName;
	@FXML
	CustomTextField txtAddress;
	@FXML
	CustomTextField txtCity;
	@FXML
	CustomTextField txtPostalCode;
	@FXML
	CustomTextField txtEmail;
	@FXML
	CustomTextField txtPhone;
	@FXML
	CustomTextField txtYear;
	@FXML
	CustomTextField txtMonth;
	@FXML
	CustomTextField txtDay;
	@FXML
	Button btnAddDone;
	@FXML
	Button btnAddNext;
	@FXML
	Button btnCancel;
	@FXML
	Label lblAdded;
	private Stage stage;
	private Client currentClient;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		txtCity.setText("Ottawa");

	}

	@FXML
	protected void cancel(ActionEvent event) {
		this.finishHandler.onFinish(ViewState.STUDENTS);
	}

	@FXML
	protected void addAndDone(ActionEvent event) {
		Client c = getClient();
		if(c.getName().trim().equals("")){
			showError();
			return;
		}
		if(currentClient == null){
			Model.getInstance().addClient(c);	
		}
		this.finishHandler.onFinish(ViewState.STUDENTS);
	}

	@FXML
	protected void addAndNext(ActionEvent event) {
		Client c = getClient();
		if(c.getName().trim().equals("")){
			showError();
			return;
		}
		Model.getInstance().addClient(c);
		lblAdded.setText("Successfully added " + c.getName() + " to the course.");
		lblAdded.setVisible(true);
		resetFields();
	}

	private void showError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText("Please provide a name.");

		alert.showAndWait();
		
	}

	private void resetFields() {
		this.txtAddress.setText("");
		this.txtCity.setText("Ottawa");
		this.txtDay.setText("");
		this.txtEmail.setText("");
		this.txtMonth.setText("");
		this.txtName.setText("");
		this.txtPhone.setText("");
		this.txtPostalCode.setText("");
		this.txtYear.setText("");
	}

	Client getClient() {
		Client c;
		if(currentClient == null){
			c = new Client();
		}else{
			c = currentClient;
		}
		
		c.setName(txtName.getText());
		c.setAddress(txtAddress.getText());
		c.setCity(txtCity.getText());
		c.setDay(txtDay.getText());
		c.setEmail(txtEmail.getText());
		c.setMonth(txtMonth.getText());
		c.setPhone(txtPhone.getText());
		c.setPostalCode(txtPostalCode.getText());
		c.setYear(txtYear.getText());
		return c;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		this.btnAddDone.setText("Update");
		this.btnAddNext.setVisible(false);
		
	}

	public void setClient(Client c) {
		currentClient = c;
		this.txtAddress.setText(c.getAddress());
		this.txtCity.setText(c.getCity());
		this.txtDay.setText(c.getDay());
		this.txtEmail.setText(c.getEmail());
		this.txtMonth.setText(c.getMonth());
		this.txtName.setText(c.getName());
		this.txtPhone.setText(c.getPhone());
		this.txtPostalCode.setText(c.getPostalCode());
		this.txtYear.setText(c.getYear());
		
	}
	
	@Override
	public void finalize(){
		
	}
}
