package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.ReportQualification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class QualReportController extends Controller implements Initializable {

	@FXML
	ListView<Client> lstClients;
	@FXML
	TextArea txtNotes;
	private ObservableList<Client> obsClients;
	private ReportQualification qualification;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		obsClients = FXCollections.observableList(Model.getInstance().getClients());
		lstClients.setItems(obsClients);
	}

	public void setQual(ReportQualification qualification) {
		if (qualification != null)
			this.qualification = qualification;
	}

	@FXML
	protected void onClientSelect(MouseEvent event) {

	}
}
