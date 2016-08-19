package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.controlsfx.control.textfield.CustomTextField;
import org.json.JSONObject;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.factory.PersonFactory;
import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.factory.ViewFactoryResult;
import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Employee;
import Elyas.LssTestSheets.model.FXWorker;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.NotifyingThread;
import Elyas.LssTestSheets.model.ProgressUpdate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientsController extends Controller implements Initializable {

	@FXML
	TableView<Client> table;
	@FXML
	TableColumn<Client, String> colName;
	@FXML
	TableColumn<Client, Integer> colAbsence;
	@FXML
	TableColumn<Client, String> colPrerequisites;
	@FXML
	CustomTextField txtSearch;

	private ObservableList<Client> obsClients;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		colName.setCellValueFactory(new PropertyValueFactory<Client, String>("name"));
		colAbsence.setCellValueFactory(new PropertyValueFactory<Client, Integer>("absenceCount"));
		colPrerequisites.setCellValueFactory(new PropertyValueFactory<Client, String>("prerequisitesMet"));

		List<Client> clients = Model.getInstance().getClients();
		obsClients = FXCollections.observableList(clients);
		FilteredList<Client> filteredData = new FilteredList<>(obsClients, p -> true);

		txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter
				// text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (person.toJSON().toString().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches.
				}
				return false; // Does not match.
			});
		});

		SortedList<Client> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(table.comparatorProperty());
		table.setItems(sortedData);
	}

	/**
	 * called when the user requests to add more clients.
	 * 
	 * @param event
	 */
	@FXML
	protected void addClientAction(ActionEvent event) {
		this.finishHandler.onFinish(ViewState.STUDENT);
	}

	@FXML
	protected void verifyAddressesAction(ActionEvent event) {

		ProgressBar progressBar = new ProgressBar(-1);
		Label label = new Label("Connecting to service.");
		Alert alert = new Alert(AlertType.WARNING);

		alert.getDialogPane().setContent(new VBox(progressBar, label));

		FXWorker<Void, ProgressUpdate, Map<String, String>> worker = new FXWorker<Void, ProgressUpdate, Map<String, String>>(
				null) {
			@Override
			public Map<String, String> doInBackground(Void param) {
				String key = getApiKey();
				Map<String, String> messages = new HashMap<>();
				if (key == null) {
					return null;
				}
				GeoApiContext context = new GeoApiContext().setApiKey(key);
				int i = 1;

				for (Client client : Model.getInstance().getClientsAsCopy()) {
					try {
						onProgressUpdate(
								new ProgressUpdate("Verifying " + i + "/" + Model.getInstance().getClients().size(),
										i / Model.getInstance().getClients().size()));
						GeocodingResult[] results = GeocodingApi.geocode(context, client.getFullAddress()).await();

						if (results.length > 1) {
							messages.put(client.getName(),
									"Multiple possible addresses found (" + results.length + ").\n");
							i++;
							continue;
						}

						if (results.length == 1) {
							boolean number = false;
							boolean street = false;
							boolean city = false;
							boolean postal = false;

							for (AddressComponent component : results[0].addressComponents) {
								String name = component.types[0].toString();
								switch (name.toLowerCase()) {
								case "street_number":
									number = true;
									break;
								case "route":
									street = true;
									break;
								case "locality":
									city = true;
									break;
								case "postal_code":
									postal = true;
									break;
								default:
									break;
								}
							}
							String msg = "";
							if (!number) {
								msg += "street number not found.";
							}
							if (!street) {
								msg += " street not found.";
							}
							if (!city) {
								msg += " city not found.";

							}
							if (!postal) {
								msg += " postal code not found.";
							}
							if (!msg.equals("")) {
								messages.put(client.getName(), msg);
							}
						}

					} catch (Exception e) {
						addException("Unknown error occured", e);
					}
					i++;
				}

				return messages;

			}

			@Override
			public void updateProgress(ProgressUpdate param) {
				label.setText(param.getMessage());
				progressBar.setProgress(param.getProgress());
			}

			@Override
			public void onFinish(Map<String, String> param, List<Exception> exceptions) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						alert.close();
						if (param == null || !exceptions.isEmpty()) {
							Alert alert2 = new Alert(AlertType.ERROR);
							alert2.setContentText("An error occured while checking the addresses.");
							alert2.showAndWait();
						}
						if (param != null) {
							if (param.isEmpty()) {
								Alert alert2 = new Alert(AlertType.INFORMATION);
								alert2.setContentText("All addresses have been verified. all looks good!.");
								alert2.showAndWait();
							} else {
								if (!exceptions.isEmpty()) {
									return;
								}
								String message = "";
								for (String name : param.keySet()) {
									message += name + ": " + param.get(name) + "\n";
								}
								Alert alert2 = new Alert(AlertType.INFORMATION);
								alert2.setContentText("we found some problems.\n\n" + message);
								alert2.showAndWait();
							}
						}

					}
				});
			}
		};	
		
		worker.start();
		alert.show();
	}

	private String getApiKey() {
		try {
			InputStream is = CourseFactory.class.getResourceAsStream("/json/mapsAPI.json");
			String jsonTxt;
			jsonTxt = IOUtils.toString(is);
			JSONObject object = new JSONObject(jsonTxt);
			return object.getString("key");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@FXML
	protected void removeClientAction(ActionEvent event) {
		Client c = table.getSelectionModel().getSelectedItem();
		if (c == null)
			return;
		obsClients.remove(c);
		Model.getInstance().removeClient(c);
	}

	/**
	 * called when the user selects a specific client.
	 * 
	 * @param event
	 */
	@FXML
	protected void onClientSelected(MouseEvent event) {
		Client c = table.getSelectionModel().getSelectedItem();
		if (c == null)
			return;

		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
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
						// work around to update the table.
						table.getColumns().get(0).setVisible(false);
						table.getColumns().get(0).setVisible(true);
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
		for (Client c : obsClients) {
			Model.getInstance().updateClient(c);
		}
	}

	@Override
	public ViewState getViewState() {
		return ViewState.STUDENTS;
	}

}
