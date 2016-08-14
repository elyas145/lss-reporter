package LssReporter.Updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
						@Override
						public boolean match(Message message) {
							try {
								if (message.getSubject().contains("update")) {
									return true;
								}
							} catch (MessagingException ex) {
								addException("Error reading update.", ex);
							}
							return false;
						}
					};

					Message[] foundMessages = folderInbox.search(searchCondition);

					for (int i = 0; i < foundMessages.length; i++) {
						Message message = foundMessages[i];
						String subject = message.getSubject();
						System.out.println("Found message #" + i + ": " + subject);
					}

					// disconnect
					folderInbox.close(false);
					store.close();
					return "Successfully installed updates.";
				} catch (Exception exception) {
					addException("an unknown error has occured.", exception);
				}
				return null;
			}

			@Override
			public void updateProgress(ProgressUpdate param) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFinish(String param, List<Exception> exceptions) {
				if(!exceptions.isEmpty()){
					for(Exception exception : exceptions){
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
