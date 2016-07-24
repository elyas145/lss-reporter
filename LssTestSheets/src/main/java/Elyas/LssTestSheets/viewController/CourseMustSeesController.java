package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckListView;

import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.MustSee;
import Elyas.LssTestSheets.model.Prerequisite;
import Elyas.LssTestSheets.model.Prerequisite.Type;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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
	@FXML
	VBox vbPreReqs;

	private ObservableList<Client> obsClients;
	private ObservableList<MustSee> obsCurrentMustSees;
	private List<PrerequisiteController> currentPrerequisites;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		currentPrerequisites = new ArrayList<>();
		
	}

	/**
	 * the real initialize method.
	 * 
	 * @param name
	 *            the qualification's name that this tab represents
	 */
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
		if (currentClient == null)
			return;

		for (PrerequisiteController prerequisiteController : currentPrerequisites) {
			prerequisiteController.getPrerequisite();
		}
		currentPrerequisites.clear();
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
		vbPreReqs.getChildren().clear();
		for (Prerequisite prerequisite : currentClient.getPrerequisites(course)) {
			FXMLLoader loader;
			if (prerequisite.getType().equals(Type.CHECK)) {
				loader = new FXMLLoader(CourseMustSeesController.class.getResource("/fxml/prerequisite-check.fxml"));
			} else {
				loader = new FXMLLoader(CourseMustSeesController.class.getResource("/fxml/prerequisite-date.fxml"));
			}
			try {
				Node node = loader.load();
				PrerequisiteController controller = loader.getController();
				controller.setPrerequisite(prerequisite);
				if (!currentPrerequisites.isEmpty() && !currentPrerequisites.get(currentPrerequisites.size() - 1)
						.getKey().equals(prerequisite.getKey())) {
					Label label = new Label("OR");
					vbPreReqs.getChildren().add(label);
				}
				currentPrerequisites.add(controller);

				vbPreReqs.getChildren().add(node);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		chkLstMustSees.getCheckModel().getCheckedItems().addListener(new ListChangeListener<MustSee>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends MustSee> c) {
				while (c.next()) {
					c.getAddedSubList().get(0).setCompleted(c.wasAdded());
					Model.getInstance().setChanged();
				}
			}
		});
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
		// sets the check list.
		if (currentClient != null) {
			setCurrentClient(currentClient);
		}
		Model.getInstance().setChanged();
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
		// sets the check list.
		if (currentClient != null) {
			setCurrentClient(currentClient);
		}
		Model.getInstance().setChanged();
	}

	@FXML
	protected void passInstructorAction(ActionEvent event) {
		if(currentClient == null){
			return;
		}
		for (MustSee see : currentClient.getMustSees(course)) {
			if (see.isInstructorEvaluated()) {
				see.isCompleted = chkInst.isSelected();
			}
		}
		setCurrentClient(currentClient);
		Model.getInstance().setChanged();
	}

	@FXML
	protected void passExaminerAction(ActionEvent event) {
		if(currentClient == null){
			return;
		}
		for (MustSee see : currentClient.getMustSees(course)) {
			if (see.isExaminerEvaluated()) {
				see.isCompleted = chkExam.isSelected();
			}
		}
		setCurrentClient(currentClient);
		Model.getInstance().setChanged();
	}

	@Override
	public void finalize() {
		for (PrerequisiteController prerequisiteController : currentPrerequisites) {
			prerequisiteController.getPrerequisite();
		}

	}
}
