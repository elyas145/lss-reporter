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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		report = Model.getInstance().getReport();
		clients = FXCollections.observableList(Model.getInstance().getClients());
		dayControllers = new ArrayList<>();
		lstClients.setItems(clients);

	}

	@FXML
	protected void addNewDayAction(ActionEvent event) {
		FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/report-day.fxml"));
		try {
			Tab tab = (Tab) loader.load();
			DayReportController controller = (DayReportController) loader.getController();
			dayControllers.add(controller);
			tab.setText("Day " + dayControllers.size());
			ReportDay day = new ReportDay(dayControllers.size());
			controller.setDay(day);
			controller.setOnFinishHandler((state)->{
				report.removeDay(controller.getDay());
				dayControllers.remove(controller);
			});
			mainTab.getTabs().add(tab);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	protected void onClientSelection(MouseEvent event) {
		
	}

	@Override
	public void finalize() {

	}

}
