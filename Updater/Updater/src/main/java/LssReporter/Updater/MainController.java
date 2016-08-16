package LssReporter.Updater;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import javafx.scene.web.WebView;
import javafx.scene.control.Alert.AlertType;

public class MainController implements Initializable {

	@FXML
	Label lblStatus;
	@FXML
	ProgressBar prgsProgress;
	@FXML
	Button btnStart;
	private Integer cachedVersion = -1;
	private LocalDate lastUpdate;

	private int dateOffset = 8;

	public void initialize(URL location, ResourceBundle resources) {
		prgsProgress.setProgress(-1);

		FXWorker<Void, ProgressUpdate, String> thread = new FXWorker<Void, ProgressUpdate, String>(null) {

			private String description;

			@Override
			public String doInBackground(Void param) {
				try {
					System.out.println("do in background called.");
					Properties props = new Properties();
					props.put("mail.imap.host", "imap-mail.outlook.com");
					props.put("mail.imap.auth", "true");
					props.put("mail.imap.port", "993");
					props.put("mail.imap.starttls.enable", "true");
					props.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

					SMTPAuthenticator auth = new SMTPAuthenticator();
					auth.getPasswordAuthentication();

					// only update every 8 days.
					LocalDate nextUpdate = lastUpdate.plusDays(dateOffset);
					LocalDate today = LocalDate.now();
					if (!nextUpdate.isBefore(today)) {
						return "Your application is up to date!";
					}

					Session session = Session.getDefaultInstance(props);
					Store store = session.getStore("imap");
					PasswordAuthentication authentication = auth.getPasswordAuthentication();
					store.connect(authentication.getUserName(), authentication.getPassword());

					Folder folderInbox = store.getFolder("INBOX");
					folderInbox.open(Folder.READ_ONLY);

					SearchTerm searchCondition = new SearchTerm() {
						private static final long serialVersionUID = -8159023234781467178L;

						@Override
						public boolean match(Message message) {
							try {
								if (message.getSubject().contains("update")
										&& message.getFrom()[0].toString().contains("elyas.syoufi@gmail.com")) {
									return true;
								}
							} catch (MessagingException ex) {
								addException("Error reading update.", ex);
							}
							return false;
						}
					};

					Message[] foundMessages = folderInbox.search(searchCondition);
					List<Update> updates = new ArrayList<Update>();
					onProgressUpdate(new ProgressUpdate("Found " + foundMessages.length + " potential update(s).", -1));

					for (int i = 0; i < foundMessages.length; i++) {
						Message message = foundMessages[i];
						String subject = message.getSubject();
						System.out.println("Found message #" + i + ": " + subject);
						updates.add(new Update(message));
					}

					// sort the updates list.
					updates.sort(new Comparator<Update>() {

						public int compare(Update o1, Update o2) {
							if (o1.getUpdateNumber() < o2.getUpdateNumber()) {
								return -1;
							}
							if (o1.getUpdateNumber() > o2.getUpdateNumber()) {
								return 1;
							}
							return 0;
						}
					});

					int currentVersion = getCurrentUpdateVersion();

					boolean updatesFound = false;
					for (int i = 0; i < updates.size(); i++) {
						Update cUpdate = updates.get(i);
						if (currentVersion < cUpdate.getUpdateNumber()) {
							updatesFound = true;
							onProgressUpdate(
									new ProgressUpdate("Initializing update " + (i + 1) + " of " + updates.size(),
											i + 1 / updates.size()));
							try {
								cUpdate.initUpdate();
							} catch (Exception e) {
								addException("Error upplying update " + cUpdate.getUpdateNumber(), e);
							}
						}
					}

					for (int i = 0; i < updates.size(); i++) {
						Update cUpdate = updates.get(i);
						if (currentVersion < cUpdate.getUpdateNumber()) {
							onProgressUpdate(new ProgressUpdate("Applying update " + (i + 1) + " of " + updates.size(),
									i + 1 / updates.size()));
							try {
								cUpdate.applyUpdate();
								description += "<br/>" + cUpdate.getDescription();
								currentVersion = cUpdate.getUpdateNumber();
							} catch (Exception e) {
								addException("Error upplying update " + cUpdate.getUpdateNumber(), e);
							}
						}
					}

					// disconnect
					try {
						folderInbox.close(false);
						store.close();
					} catch (Exception exception) {

					}
					// update the version and date.
					if (currentVersion != -1) {
						InputStream is = MainController.class.getResourceAsStream("/secret.json");
						String jsonTxt = IOUtils.toString(is);
						JSONObject obj = new JSONObject(jsonTxt);
						obj.put("version", "" + currentVersion);
						obj.put("last_update", today.toString());

						try (PrintWriter out = new PrintWriter(
								MainController.class.getResource("/secret.json").toURI().getSchemeSpecificPart())) {
							out.println(obj.toString(4));
						}
					}
					if (!updatesFound) {
						return "Your application is up to date!";
					}
					return "Successfully installed updates.";
				} catch (Exception exception) {
					addException("an unknown error has occured.", exception);
				}
				return "an unknown error has occured.";
			}

			@Override
			public void updateProgress(ProgressUpdate param) {
				lblStatus.setText(param.getMessage());
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
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						btnStart.setDisable(false);
						if (description != null && !description.equals("")) {
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Update Description");
							alert.setHeaderText("Update Description");
							WebView webView = new WebView();
							webView.getEngine().loadContent("<html>" + "update test" + "</html>");
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

	@FXML
	protected void onStartAction(ActionEvent event) {
		try {
			if (System.getProperty("os.name").toLowerCase().contains("windows")) {
				Runtime.getRuntime().exec(MainController.class.getResource("/").toURI().getSchemeSpecificPart()
						+ "/jre/bin/java.exe -jar app.jar");
				System.exit(0);
			}
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}
		return cachedVersion;
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			InputStream is = MainController.class.getResourceAsStream("/secret.json");
			if (is == null) {
				return null;
			}
			String jsonTxt;
			try {
				jsonTxt = IOUtils.toString(is);
				JSONObject obj = new JSONObject(jsonTxt);
				cachedVersion = Integer.valueOf(obj.optString("version"));
				lastUpdate = LocalDate.parse(obj.getString("last_update"));
				return new PasswordAuthentication(obj.optString("user"), obj.optString("password"));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
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
