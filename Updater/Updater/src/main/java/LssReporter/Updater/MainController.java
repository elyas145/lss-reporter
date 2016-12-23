package LssReporter.Updater;

import java.awt.Desktop.Action;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ToggleButton;
import javafx.scene.web.WebView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.transform.Translate;

public class MainController implements Initializable {

	@FXML
	Label lblStatus;
	@FXML
	ProgressBar prgsProgress;
	@FXML
	Button btnStart;
	@FXML
	ToggleButton tglEnglish;
	@FXML
	ToggleButton tglFrench;
	@FXML
	Label lblTitle;
	@FXML
	ProgressIndicator prgsLang;

	private Integer cachedVersion = -1;
	private LocalDate lastUpdate;

	private int dateOffset = 8;
	private String language;
	private String currentStatus;

	public void initialize(URL location, ResourceBundle resources) {
		// disable language switching until implemented
		tglEnglish.setDisable(true);
		tglFrench.setDisable(true);

		// only update every 8 days.
		LocalDate nextUpdate = lastUpdate.plusDays(dateOffset);
		LocalDate today = LocalDate.now();

		if (!nextUpdate.isBefore(today)) {
			lblStatus.setText("Your application is up to date!");
		} else {
			checkUpdates();
		}

		prgsProgress.setProgress(-1);

		FXWorker<Void, ProgressUpdate, String> thread = new FXWorker<Void, ProgressUpdate, String>(null) {

			private String description = "";

			@Override
			public String doInBackground(Void param) {
				try {
					
				} catch (Exception exception) {
					addException("an unknown error has occured.", exception);
				}
				return "an unknown error has occured.";
			}

			@Override
			public void updateProgress(ProgressUpdate param) {
				lblStatus.setText(param.getMessage());
				currentStatus = param.getMessage();
				prgsProgress.setProgress(param.getProgress());

			}

			@Override
			public void onFinish(String param, List<Exception> exceptions) {
				if (!exceptions.isEmpty()) {
					for (Exception exception : exceptions) {
						exception.printStackTrace();
					}
				}
				if (param != null) {
					onProgressUpdate(new ProgressUpdate(param, 1));
					currentStatus = param;
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						btnStart.setDisable(false);
						if (description != null && !description.equals("")) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle(Dictionary.get("alert.update-description"));
							alert.setHeaderText(Dictionary.get("alert.update-description"));
							WebView webView = new WebView();
							webView.getEngine().loadContent("<html>" + description + "</html>");
							webView.setPrefSize(200, 50);
							alert.getDialogPane().setContent(webView);
							alert.showAndWait();
						}

					}
				});
			}

		};

		thread.start();

	}

	private void checkUpdates() {

	}

	@FXML
	protected void onEnglishAction(ActionEvent event) {
		changeLanguage("english");
	}

	@FXML
	protected void onFrenchAction(ActionEvent event) {
		changeLanguage("french");
	}

	private void changeLanguage(String lang) {
		if (lang.equals("english")) {
			tglFrench.setSelected(false);
			tglEnglish.setSelected(true);
			this.language = "en";
		} else {
			tglFrench.setSelected(true);
			tglEnglish.setSelected(false);
			this.language = "fr";
		}
		FXWorker<String, String, String> languageChanger = new FXWorker<String, String, String>(null) {

			@Override
			public String doInBackground(String param) {
				onProgressUpdate(null);
				try {
					if (param.equals("en")) {
						Dictionary.setLanguage(Language.EN_CA);
					} else {
						Dictionary.setLanguage(Language.FR_CA);
					}
				} catch (Exception e) {
					addException("an error occured while changing the language.", e);
				}
				return "Successfully set the new language.";

			}

			@Override
			public void updateProgress(String param) {
				prgsLang.setVisible(true);
			}

			@Override
			public void onFinish(String param, final List<Exception> exceptions) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						prgsLang.setVisible(false);
						if (!exceptions.isEmpty()) {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setContentText("an error occured while changing the language.");
							alert.showAndWait();
						}
						translate();
					}

				});
			}
		};

	}

	public void translate() {
		lblStatus.setText(Dictionary.get(currentStatus));

	}

	@FXML
	protected void onStartAction(ActionEvent event) {
		try {
			String name = System.getProperty("os.name").toLowerCase();
			if (name.contains("windows")) {
				Runtime.getRuntime().exec(MainController.class.getResource("/").toURI().getSchemeSpecificPart()
						+ "/jre/bin/java.exe -jar app.jar");
				System.exit(0);
			} else if (name.contains("mac")) {
				String cmd[] = {
						MainController.class.getResource("/").toURI().getSchemeSpecificPart()
								+ "../../../../plugins/JRE/Contents/Home/jre/bin/java",
						"-jar", MainController.class.getResource("/app.jar").toURI().getSchemeSpecificPart() };
				ProcessBuilder builder = new ProcessBuilder();
				builder.command(cmd);
				Process process = builder.start();

				if (process == null || !process.isAlive()) {
					throw new Exception();
				}
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Unable to start the application. your system may not be supported.");
			alert.showAndWait();
		}
	}

	private int getCurrentUpdateVersion() {
		if (cachedVersion == -1) {

			InputStream is = MainController.class.getResourceAsStream("/secret.json");
			if (is == null) {
				return -1;
			}

			String jsonTxt;
			try {
				jsonTxt = IOUtils.toString(is);
				JSONObject obj = new JSONObject(jsonTxt);
				cachedVersion = Integer.valueOf(obj.optString("version"));
				lastUpdate = LocalDate.parse(obj.getString("last_update"));
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return cachedVersion;
	}

	public class ProgressUpdate {
		private String message;
		private float progress;

		public ProgressUpdate(String message, float progress) {
			this.message = message;
			this.progress = progress;
		}

		public String getMessage() {
			return message;
		}

		public float getProgress() {
			return progress;
		}
	}

}
