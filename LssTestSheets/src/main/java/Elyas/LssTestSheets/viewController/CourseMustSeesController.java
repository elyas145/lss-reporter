package Elyas.LssTestSheets.viewController;

import java.awt.Desktop.Action;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckListView;

import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.MustSee;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class CourseMustSeesController extends Controller implements Initializable {

	private String course;
	private Client currentClient;
	@FXML
	CheckBox chkAllInst;
	@FXML
	CheckBox chkAllExam;
	@FXML
	ListView<Client> lstClients;
	@FXML
	CheckBox chkInst;
	@FXML
	CheckBox chkExam;
	@FXML
	CheckListView<MustSee> chkLstMustSees;

	private ObservableList<Client> obsClients;
	private ObservableList<MustSee> obsCurrentMustSees;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setCourse(String name) {
		this.course = name;
		obsClients = FXCollections.observableList(Model.getInstance().getClients());
		lstClients.setItems(obsClients);
		boolean passAllInstructor = true;
		boolean passAllExaminer = true;
		for (Client client : obsClients) {
			for (MustSee see : client.getMustSees(course)) {
				if (see.isInstructorEvaluated() && !see.isCompleted()) {
					passAllInstructor = false;
				}
				if (see.isExaminerEvaluated() && !see.isCompleted()) {
					passAllExaminer = false;
				}
				if (!passAllExaminer && !passAllInstructor) {
					break;
				}
			}
		}
		chkAllInst.setSelected(passAllInstructor);
		chkAllExam.setSelected(passAllExaminer);
	}

	public void setCurrentClient(Client currentClient) {
		this.currentClient = currentClient;
		this.obsCurrentMustSees = FXCollections.observableList(currentClient.getMustSees(course));
		ObservableList<MustSee> nullList = FXCollections.observableList(new ArrayList<>());
		chkLstMustSees.setItems(nullList);
		chkLstMustSees.setItems(obsCurrentMustSees);
		
		boolean passInstructor = true;
		boolean passExaminer = true;
		for (MustSee see : obsCurrentMustSees) {
			if (see.isCompleted())
				chkLstMustSees.getCheckModel().check(see);
			if (see.isInstructorEvaluated() && !see.isCompleted()) {
				passInstructor = false;
			}
			if (see.isExaminerEvaluated() && !see.isCompleted()) {
				passExaminer = false;
			}
			if (!passExaminer && !passInstructor) {
				break;
			}
		}
		chkInst.setSelected(passInstructor);
		chkExam.setSelected(passExaminer);
	}

	@FXML
	protected void clientSelectAction(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {

		} else {
			setCurrentClient(lstClients.getSelectionModel().getSelectedItem());
		}
	}

	@FXML
	protected void chkAllInstructorAction(ActionEvent event) {
		for (Client client : obsClients) {
			for (MustSee see : client.getMustSees(course)) {
				if (see.isInstructorEvaluated()) {
					see.isCompleted = chkAllInst.isSelected();
				}
			}
		}
		//sets the check list.
		if (currentClient != null) {
			setCurrentClient(currentClient);
		}
	}

	@FXML
	protected void chkAllExaminerAction(ActionEvent event) {
		for (Client client : obsClients) {
			for (MustSee see : client.getMustSees(course)) {
				if (see.isExaminerEvaluated()) {
					see.isCompleted = chkAllExam.isSelected();
				}
			}
		}
		//sets the check list.
		if (currentClient != null) {
			setCurrentClient(currentClient);
		}
	}

	@FXML
	protected void passInstructorAction(ActionEvent event) {
		for (MustSee see : currentClient.getMustSees(course)) {
			if (see.isInstructorEvaluated()) {
				see.isCompleted = chkInst.isSelected();
			}
		}
		setCurrentClient(currentClient);
	}

	@FXML
	protected void passExaminerAction(ActionEvent event) {
		for (MustSee see : currentClient.getMustSees(course)) {
			if (see.isExaminerEvaluated()) {
				see.isCompleted = chkExam.isSelected();
			}
		}
		setCurrentClient(currentClient);
	}
}
