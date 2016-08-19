package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckListView;

import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.ReportDay;
import Elyas.LssTestSheets.model.ReportQualification;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

public class DayReportController extends Controller implements Initializable {

	private ReportDay day;
	@FXML
	CheckListView<Client> chkLstAttendance;
	@FXML
	TextArea txtGeneralNotes;
	private ObservableList<Client> obsClients;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		obsClients = FXCollections.observableList(Model.getInstance().getClients());
		chkLstAttendance.setItems(obsClients);
		
		chkLstAttendance.getCheckModel().getCheckedItems().addListener(new ListChangeListener<Client>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Client> c) {
				c.next();
				if (c.wasAdded())
					Model.getInstance().getReport().setAttendance(day, c.getAddedSubList().get(0), c.wasAdded());
				else {
					Model.getInstance().getReport().setAttendance(day, c.getRemoved().get(0), c.wasAdded());
				}

			}
		});
	}

	@Override
	public void finalize() {
		day.setGeneralNote(txtGeneralNotes.getText());
	}

	@FXML
	protected void closeRequested(Event event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText(null);
		alert.setContentText(
				"Removing a day will remove all notes, and any information related to the day. Continue?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			finishHandler.onFinish(null);
			Model.getInstance().setChanged();
		} else {
			event.consume();
		}
	}

	public void setDay(ReportDay day) {
		if (day == null)
			return;

		this.day = day;
		Map<String, Boolean> att = day.getAttendace();
		if (att != null) {
			for (String id : att.keySet()) {
				if (att.get(id)) {
					for (Client client : obsClients) {
						if (client.getID().equals(id)) {
							chkLstAttendance.getCheckModel().check(client);
						}
					}
				}
			}
		}
		txtGeneralNotes.setText(day.getGeneralNotes());
	}

	public ReportDay getDay() {
		return this.day;
	}

}
