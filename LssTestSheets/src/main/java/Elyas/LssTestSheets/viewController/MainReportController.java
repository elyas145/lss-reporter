package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.factory.ViewFactoryResult;
import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Report;
import Elyas.LssTestSheets.model.ReportDay;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class MainReportController extends Controller implements Initializable {

	@FXML
	TabPane mainTab;
	@FXML
	ListView<Client> lstClients;
	@FXML
	TextArea txtGeneralNotes;
	private ObservableList<Client> clients;
	private Report report;
	private List<DayReportController> dayControllers;
	private Client currentClient;
	private int dayNumber = 1;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		report = Model.getInstance().getReport();
		clients = FXCollections.observableList(Model.getInstance().getClients());
		dayControllers = new ArrayList<>();
		lstClients.setItems(clients);
		for (ReportDay day : Model.getInstance().getReport().getDays()) {
			addDay(day);
		}
		mainTab.getSelectionModel().selectedItemProperty().addListener((ov) -> finalize());

	}

	private void addDay(ReportDay day) {
		FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/report-day.fxml"));
		try {
			Tab tab = (Tab) loader.load();
			DayReportController controller = (DayReportController) loader.getController();
			dayControllers.add(controller);
			tab.setText("Day " + day.getNumber());

			controller.setDay(day);

			controller.setOnFinishHandler((state) -> {
				report.removeDay(controller.getDay());
				dayControllers.remove(controller);
			});
			mainTab.getTabs().add(tab);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	protected void addNewDayAction(ActionEvent event) {

		ReportDay day = new ReportDay(dayNumber++);
		Model.getInstance().getReport().addDay(day);
		addDay(day);
	}

	@FXML
	protected void onClientSelection(MouseEvent event) {

		Client client = lstClients.getSelectionModel().getSelectedItem();

		if (client == null) {
			return;
		}
		if (currentClient != null)
			Model.getInstance().getReport().setGeneralNote(currentClient, txtGeneralNotes.getText());
		currentClient = client;
		String notes = Model.getInstance().getReport().getGeneralNote(client);
		if (notes != null) {
			txtGeneralNotes.setText(notes);
		} else {
			txtGeneralNotes.setText("");
		}
	}

	@Override
	public void finalize() {
		for (DayReportController day : dayControllers) {
			day.finalize();
		}
		if (currentClient != null) {
			Model.getInstance().getReport().setGeneralNote(currentClient, txtGeneralNotes.getText());
		}
	}

}
