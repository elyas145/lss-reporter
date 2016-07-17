package Elyas.LssTestSheets.viewController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.controlsfx.control.textfield.CustomTextField;
import org.json.JSONObject;

import Elyas.LssTestSheets.App;
import Elyas.LssTestSheets.Config;
import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Warning;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class ExportController extends Controller implements Initializable {
	@FXML
	Hyperlink lnkWarnings;
	@FXML
	TextField txtEmail;
	@FXML
	TextField txtUserName;
	@FXML
	CheckBox chkSendCourseFile;
	@FXML
	CheckBox chkSendTestSheets;
	@FXML
	Label lblSend;
	@FXML
	ProgressIndicator prgsSend;
	@FXML
	HBox hbSend;
	@FXML
	CustomTextField txtDirectory;
	@FXML
	Label lblError;
	@FXML
	CheckBox chkExportCourse;
	@FXML
	CheckBox chkExportTestSheets;
	@FXML
	ProgressIndicator prgsExport;
	@FXML
	Label lblExport;
	@FXML
	HBox hbExport;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Warning warnings = CourseFactory.getCourseWarnings(Model.getInstance().getCourse());
		if (!warnings.containsWarnings()) {
			lnkWarnings.setVisible(false);
		}

	}

	@FXML
	protected void onSendAction(ActionEvent event) {
		String email = txtEmail.getText();
		String name = txtUserName.getText();
		boolean sendCourse = chkSendCourseFile.isSelected();
		boolean sendTestSheet = chkSendTestSheets.isSelected();
		String error = "";
		if (email.trim().equals("")) {
			error += "Please Enter the email address you would like to send the files to.";
		}
		if (name.trim().equals("")) {
			error += "\n\nPlease enter your name.";
		}
		if (!sendCourse && !sendTestSheet) {
			error += "\n\nPlease select at least one method of sending the data (course file or test sheets).";
		}

		if (!error.equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(error);
			alert.setHeaderText(null);
			alert.showAndWait();
			return;
		}
		try {
			hbSend.setVisible(true);
			lblSend.setText("Sending ...");
			((Button) event.getSource()).setDisable(true);
			prgsSend.setProgress(-1);
			Model.getInstance().sendInfo(sendCourse, sendTestSheet, name, email, (thread) -> {
				Platform.runLater(() -> {
					prgsSend.setProgress(1);
					lblSend.setText("Successfully sent the message!");
					((Button) event.getSource()).setDisable(false);
				});

			});
		} catch (AddressException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(error);
			alert.setHeaderText(null);
			alert.setContentText("the address provided is not a proper email address.");
			alert.showAndWait();
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(error);
			alert.setHeaderText(null);
			alert.setContentText("an error occured. (Unsupported Encoding Exception)");
			alert.showAndWait();
			e.printStackTrace();
		} catch (MessagingException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(error);
			alert.setHeaderText(null);
			alert.setContentText("an error occured. (Messaging Exception)");
			alert.showAndWait();
			e.printStackTrace();
		}
	}

	@FXML
	protected void onWarningsAction(ActionEvent event) {
		Warning warnings = CourseFactory.getCourseWarnings(Model.getInstance().getCourse());
		VBox vBox = new VBox(5);
		vBox.alignmentProperty().set(Pos.TOP_CENTER);
		ScrollPane scrollPane = new ScrollPane(vBox);
		scrollPane.fitToWidthProperty().set(true);
		TitledPane generalPane = new TitledPane();
		generalPane.setCollapsible(false);
		generalPane.setText("General");
		VBox generalBox = new VBox(5);

		for (String string : warnings.getWarnings()) {
			Label label = new Label(string);
			generalBox.getChildren().add(label);
		}
		generalPane.setContent(generalBox);
		vBox.getChildren().add(generalPane);
		for (String category : warnings.getCategories()) {
			if (warnings.getWarnings(category).isEmpty())
				continue;

			TitledPane pane = new TitledPane();
			pane.setText(category);
			pane.setCollapsible(false);
			VBox box = new VBox(5);

			for (String warning : warnings.getWarnings(category)) {
				Label label = new Label(warning);
				box.getChildren().add(label);
			}
			pane.setContent(box);
			vBox.getChildren().add(pane);
		}
		Button button = new Button("OK");
		vBox.getChildren().add(button);
		Scene scene = new Scene(scrollPane, 300, 150);
		Stage stage = new Stage();

		stage.setScene(scene);
		stage.show();

		button.setOnAction((e) -> stage.close());
	}

	@FXML
	protected void browseAction(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose Destination Folder.");
		File file = directoryChooser.showDialog(App.getMainStage());

		if (file != null) {
			txtDirectory.setText(file.getAbsolutePath());
		}
	}

	@FXML
	protected void exportAction(ActionEvent event) {
		if (txtDirectory.getText().trim().equals("")) {
			lblError.setVisible(true);
			return;
		}

		String directoryPath = txtDirectory.getText().trim();
		if (chkExportCourse.isSelected()) {
			File file = new File(directoryPath + System.getProperty("file.separator")
					+ Model.getInstance().getCourseName() + ".json");
			if (file.exists()) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirm Replacing a File.");
				alert.setContentText("A file with the name \"" + Model.getInstance().getCourseName()
						+ "\" already exists. Would you like to replace the existing file?");
				Optional<ButtonType> btn = alert.showAndWait();
				if (!btn.get().equals(ButtonType.OK)) {
					return;
				}
			}
		}
		hbExport.setVisible(true);
		prgsExport.setProgress(-1);
		CourseFactory.exportInfo(chkExportCourse.isSelected(), chkExportTestSheets.isSelected(), directoryPath,
				(Thread) -> {
					Platform.runLater(() -> {
						prgsExport.setProgress(1);
						lblExport.setText("Successfully exported the file(s)");
						((Button) event.getSource()).setDisable(false);
					});
				});

	}
}
