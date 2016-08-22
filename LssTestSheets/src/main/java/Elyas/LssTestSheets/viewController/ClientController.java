package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.Validate;
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
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class ClientController extends Controller implements Initializable {

	@FXML
	CustomTextField txtName;
	@FXML
	CustomTextField txtAddress;
	@FXML
	CustomTextField txtProvince;
	@FXML
	CustomTextField txtCity;
	@FXML
	CustomTextField txtPostalCode;
	@FXML
	CustomTextField txtEmail;
	@FXML
	CustomTextField txtPhone;
	@FXML
	CustomTextField txtApt;

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
	@FXML
	RadioButton rdbMale;
	@FXML
	RadioButton rdbFemale;
	
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
		if(stage != null){
			String error = validate();
			if(error != ""){
				showError(error);
				return;
			}
		}
		Client c = getClient();
		if (c.getName().equals("")) {
			showError("Please provide a name.");
			return;
		}
		if (currentClient == null) {
			Model.getInstance().addClient(c);
		}
		this.finishHandler.onFinish(ViewState.STUDENTS);
	}

	private String validate() {
		String error = "";
		String name = txtName.getText().trim();
		if (!name.contains(" ")) {
			error += "Name not valid.\n";
		}
		if (txtAddress.getText().trim().equals("")) {
			error += "Address not valid.\n";
		}
		if (txtCity.getText().trim().equals("")) {
			error += "City not valid.\n";
		}
		if(txtProvince.getText().equals("") || txtProvince.getText().length() != 2){
			error += "Province not valid.\n";
		}

		String postal = ""; 
		for(Character character : txtPostalCode.getText().trim().toCharArray()){
			if(character != ' '){
				postal += character;
			}
		}
		
		if (postal.length() != 6) {
			error += "Postal code not valid.\n";
		}else{
			boolean postalValid = true;
			for (int i = 0; i < postal.length(); i++) {
				if(i % 2 != 0){
					try{						
						Integer.valueOf(postal.charAt(i));						
					}catch(Exception e){
						error += "Postal code not valid.\n";
						postalValid = false;
						break;
					}
				}				
			}
			if(postalValid){
				String newPostal = "";
				for(int i = 0; i < postal.length(); i++){
					if (i == 3){
						newPostal += " ";
					}
					newPostal += (""+postal.charAt(i)).toUpperCase();
				}
				txtPostalCode.setText(newPostal);
			}
			
		}
		
		String phone = "";
		for(Character character : txtPhone.getText().trim().toCharArray()){
			try{
				phone += Integer.valueOf(""+character);
			}catch(Exception e){
				
			}
		}
		if(phone.length() != 10){
			error += "Phone number not valid.\n";
		}else{
			char[] characters = phone.toCharArray();
			String newPhone = "";
			for(int i = 0; i < characters.length; i++){
				if(i == 0){
					newPhone += "(";
				}else if(i == 3){
					newPhone += ") ";
				}else if(i == 6){
					newPhone += "-";
				}
				newPhone += characters[i];
			}
			txtPhone.setText(newPhone);
		}
		
		if(txtYear.getText().trim().length() != 2){
			error += "Year not valid.\n";
		}else{
			try{
				Integer.valueOf(txtYear.getText().trim());
			}catch(Exception e){
				error += "Year not valid.\n";
			}
		}
		
		if(txtMonth.getText().trim().length() != 2){
			error += "Month not valid.\n";
		}else{
			try{
				Integer.valueOf(txtMonth.getText().trim());
			}catch(Exception e){
				error += "Month not valid.\n";
			}
		}
		
		if(txtDay.getText().trim().length() != 2){
			error += "Day not valid.\n";
		}else{
			try{
				Integer.valueOf(txtDay.getText().trim());
			}catch(Exception e){
				error += "Day not valid.\n";
			}
		}
		return error;
	}

	@FXML
	protected void addAndNext(ActionEvent event) {
		Client c = getClient();
		if(c.getName().equals("")){
			showError("Please provide a name.");
			return;
		}
		Model.getInstance().addClient(c);
		lblAdded.setText("Successfully added " + c.getName() + " to the course.");
		lblAdded.setVisible(true);
		resetFields();
	}

	private void showError(String err) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText(err);

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
		this.txtApt.setText("");
		this.txtProvince.setText("ON");
	}

	Client getClient() {
		Client c;
		if (currentClient == null) {
			c = new Client();
		} else {
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
		c.setApartment(txtApt.getText());
		c.setMale(rdbMale.isSelected());
		c.setProvince(txtProvince.getText());
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
		this.rdbMale.setSelected(c.isMale());
		this.rdbFemale.setSelected(!c.isMale());
		this.txtProvince.setText(c.getProvince());

	}

	@Override
	public void finalize() {

	}
}
