package Elyas.LssTestSheets.viewController;

import java.awt.Button;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.sound.midi.VoiceStatus;

import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.factory.FacilityFactory;
import Elyas.LssTestSheets.model.Facility;
import Elyas.LssTestSheets.model.FacilityHost;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Qualification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class CourseController extends Controller implements Initializable {
	private List<Facility> knownFacilities;

	@Override
	public ViewState getViewState() {
		return ViewState.COURSE;
	}

	@FXML
	CustomTextField txtBarcode1;
	@FXML
	CustomTextField txtBarcode2;

	@FXML
	ComboBox<Facility> cmbPreset;
	@FXML
	CheckBox chkDefault;
	@FXML
	CheckBox chkExamFees;
	@FXML
	CheckBox chkNewPreset;

	@FXML
	CustomTextField txtHostName;
	@FXML
	CustomTextField txtHostAddress;
	@FXML
	CustomTextField txtHostCity;
	@FXML
	CustomTextField txtHostProvince;
	@FXML
	CustomTextField txtHostPostalCode;
	@FXML
	CustomTextField txtHostAreaCode;
	@FXML
	CustomTextField txtHostPhone;
	@FXML
	CustomTextField txtHostExtension;

	@FXML
	CustomTextField txtFacilityName;
	@FXML
	CustomTextField txtFacilityAreaCode;
	@FXML
	CustomTextField txtFacilityPhone;
	@FXML
	CustomTextField txtFacilityExtension;
	@FXML
	TabPane tabExam;

	private List<ExamController> exams;
	private ObservableList<Facility> obsFacilities;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		knownFacilities = FacilityFactory.getKnownFacilities();
		exams = new ArrayList<>();
		obsFacilities = FXCollections.observableList(knownFacilities);
		cmbPreset.setItems(obsFacilities);
		Facility facil = Model.getInstance().getFacility();
		if (facil == null) {
			for (Facility facility : obsFacilities) {
				if (facility.getDefault()) {
					populate(facility);
					cmbPreset.getSelectionModel().select(facility);
					break;
				}
			}
		} else {
			populate(facil);
		}
		if (Model.getInstance().getBarcodeOne() != null) {
			txtBarcode1.setText(Model.getInstance().getBarcodeOne());
		}
		if (Model.getInstance().getBarcodeTwo() != null) {
			txtBarcode2.setText(Model.getInstance().getBarcodeTwo());
		}
		for (Qualification qual : Model.getInstance().getQualifications()) {
			FXMLLoader loader = new FXMLLoader(CourseController.class.getResource("/fxml/exam.fxml"));
			try {
				Tab tab = (Tab) loader.load();
				tab.setText(qual.getName());
				ExamController controller = (ExamController) loader.getController();
				exams.add(controller);
				controller.setQual(qual);
				tabExam.getTabs().add(tab);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@FXML
	protected void presetChanged(ActionEvent event) {
		Facility facility = cmbPreset.getValue();
		if (facility != null) {
			populate(facility);
		}
	}

	private void populate(Facility facility) {
		this.chkDefault.setSelected(facility.getDefault());
		FacilityHost host = facility.getHost();
		if (host != null) {
			this.chkExamFees.setSelected(host.getExamFees());
			this.txtHostName.setText(host.getName());
			this.txtHostAddress.setText(host.getAddress());
			this.txtHostCity.setText(host.getCity());
			this.txtHostProvince.setText(host.getProvince());
			this.txtHostPostalCode.setText(host.getPostalCode());
			this.txtHostAreaCode.setText(host.getAreaCode());
			this.txtHostPhone.setText(host.getPhone());
			this.txtHostExtension.setText(host.getExtension());
		}
		this.txtFacilityName.setText(facility.getName());
		this.txtFacilityAreaCode.setText(facility.getAreaCode());
		this.txtFacilityPhone.setText(facility.getPhone());
		this.txtFacilityExtension.setText(facility.getExtension());
		
		for(ExamController controller : this.exams){
			if(controller.getExamLocation() == "" || controller.getExamLocation().trim().equals("")){
				controller.setExamLocation(facility.getName());
			}
		}
	}

	@Override
	public boolean finalizeView() {
		Facility f = new Facility();
		f.setAreaCode(this.txtFacilityAreaCode.getText());
		f.setName(this.txtFacilityName.getText());
		f.setPhone(this.txtFacilityPhone.getText());
		f.setExtension(this.txtFacilityExtension.getText());
		f.setDefault(chkDefault.isSelected());
		FacilityHost host = new FacilityHost();

		host.setAddress(this.txtHostAddress.getText());
		host.setAreaCode(this.txtHostAreaCode.getText());
		host.setCity(this.txtHostCity.getText());
		host.setExamFees(this.chkExamFees.isSelected());
		host.setExtension(this.txtHostExtension.getText());
		host.setName(this.txtHostName.getText());
		host.setPhone(this.txtHostPhone.getText());
		host.setPostalCode(this.txtHostPostalCode.getText());
		host.setProvince(this.txtHostProvince.getText());

		f.setHost(host);
		Model.getInstance().setFacility(f);
		Model.getInstance().setBarcodeOne(this.txtBarcode1.getText());
		Model.getInstance().setBarcodeTwo(this.txtBarcode2.getText());

		if (chkNewPreset.isSelected()) {
			for (Facility facility : obsFacilities) {
				if (facility.getName().equals(f.getName())) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText(
							"Unable to set new preset. a preset with the same facility name already exists.");
					alert.setHeaderText(null);
					alert.showAndWait();
					break;
				} else {
					FacilityFactory.addNewFacility(f);
				}
			}
		}
		boolean updated = true;
		for (Facility facility : obsFacilities) {
			if (facility.equals(f)) {
				updated = false;
			}
		}
		if (updated && !chkNewPreset.isSelected() && cmbPreset.getSelectionModel().getSelectedItem() != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("Would you like to update the preset?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get().equals(ButtonType.OK)) {
				FacilityFactory.updateFacility(cmbPreset.getSelectionModel().getSelectedItem().getName(), f);
			} else {

			}

		}

		for (ExamController examController : exams) {
			examController.finalizeView();
		}
		return true;
	}

}
