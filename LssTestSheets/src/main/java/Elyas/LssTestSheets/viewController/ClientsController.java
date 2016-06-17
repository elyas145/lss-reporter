package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.factory.PersonFactory;
import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.factory.ViewFactoryResult;
import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Employee;
import Elyas.LssTestSheets.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientsController extends Controller implements Initializable{

	@FXML
	TableView<Client> table;
	@FXML
	TableColumn<Client, String> colName;
	@FXML
	TableColumn<Client, Integer> colAbsence;
	@FXML
	TableColumn<Client, String> colPrerequisites;
	
	private ObservableList<Client> obsClients;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		colName.setCellValueFactory(new PropertyValueFactory<Client, String>("name"));
		colAbsence.setCellValueFactory(new PropertyValueFactory<Client, Integer>("absenceCount"));
		colPrerequisites.setCellValueFactory(new PropertyValueFactory<Client, String>("prerequisitesMet"));
		
		List<Client> clients = Model.getInstance().getClients();
		obsClients = FXCollections.observableList(clients);
		table.setItems(obsClients);
	}
	
	/**
	 * called when the user requests to add more clients.
	 * @param event
	 */
	@FXML
	protected void addClientAction(ActionEvent event){
		this.finishHandler.onFinish(ViewState.STUDENT);
	}
	
	@FXML
	protected void removeClientAction(ActionEvent event){
		Client c = table.getSelectionModel().getSelectedItem();
		if(c == null)
			return;
		obsClients.remove(c);
		Model.getInstance().removeClient(c);
	}
	
	/**
	 * called when the user selects a specific client.
	 * @param event
	 */
	@FXML
	protected void onClientSelected(MouseEvent event){
		Client c = table.getSelectionModel().getSelectedItem();
		if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2){
			ViewFactoryResult result = ViewFactory.getView("/fxml/client.fxml");
			final ClientController controller = (ClientController) result.controller;
			controller.setClient(c);
			Scene scene = new Scene(result.parent);
			final Stage stage = new Stage();
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);  
			stage.show();
			controller.setStage(stage);
			controller.setOnFinishHandler(new FinishHandler() {
				@Override
				public void onFinish(ViewState state) {
					Client client = controller.getClient();
					if (preCheck(client)) {
						Model.getInstance().updateClient(client);
						List<Client> clients = Model.getInstance().getClients();
						obsClients.removeAll(obsClients);
						for(Client client2 : clients){
							obsClients.add(client2);
						}
						stage.close();
					} else {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText(null);
						alert.setContentText("Please give your client a name!");
						alert.showAndWait();
					}
				}

				private boolean preCheck(Client client) {
					if (client.getName() == null || client.getName().trim().equals("")) {
						return false;
					}
					return true;
				}
			});
		}
	}
	
	@Override
	public void finalize() {
		for(Client c : obsClients){
			Model.getInstance().updateClient(c);
		}
	}

	@Override
	public ViewState getViewState(){
		return ViewState.STUDENTS;
	}

}
