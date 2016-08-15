package LssReporter.Updater;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class MainController implements Initializable {

	@FXML
	Label lblStatus;
	@FXML
	ProgressBar prgsProgress;
	@FXML
	Button btnStart;
	public Integer cachedVersion = -1;

	public void initialize(URL location, ResourceBundle resources) {
		prgsProgress.setProgress(-1);

		FXWorker<Void, ProgressUpdate, String> thread = new FXWorker<Void, ProgressUpdate, String>(null) {

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
					for (int i = 0; i < updates.size(); i++) {
						Update cUpdate = updates.get(i);
						if (currentVersion < cUpdate.getUpdateNumber()) {
							onProgressUpdate(
									new ProgressUpdate("Initializing update " + (i + 1) + " of " + updates.size(),
											i + 1 / updates.size()));
							try {
								cUpdate.initUpdate();
								onProgressUpdate(
										new ProgressUpdate("Applying update " + (i + 1) + " of " + updates.size(),
												i + 1 / updates.size()));
								cUpdate.applyUpdate();
								currentVersion = cUpdate.getUpdateNumber();
							} catch (Exception e) {
								addException("Error upplying update " + cUpdate.getUpdateNumber(), e);
							}
						}
					}
					// disconnect
					folderInbox.close(false);
					store.close();

					// update the version.
					if (currentVersion != -1) {
						InputStream is = MainController.class.getResourceAsStream("/secret.json");
						String jsonTxt = IOUtils.toString(is);
						JSONObject obj = new JSONObject(jsonTxt);
						obj.put("version", "" + currentVersion);
						try (PrintWriter out = new PrintWriter(
								MainController.class.getResource("/secret.json").getPath())) {
							out.println(obj.toString(4));
						}
					}
					return "Successfully installed updates.";
				} catch (Exception exception) {
					addException("an unknown error has occured.", exception);
				}
				return null;
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

			}

		};

		thread.start();

	}

	@FXML
	protected void onStartAction(ActionEvent event) {

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
