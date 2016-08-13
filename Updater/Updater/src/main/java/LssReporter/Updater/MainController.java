package LssReporter.Updater;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class MainController implements Initializable{
	
	@FXML
	Label lblStatus;
	@FXML
	ProgressBar prgsProgress;
	@FXML
	Button btnStart;
	
	public void initialize(URL location, ResourceBundle resources) {
		prgsProgress.setProgress(-1);
		
		NotifyingThread thread = new NotifyingThread() {			
			@Override
			public void doRun() {
				//check for update messages.
				
			}
		};
		
		thread.addListener(new ThreadCompleteListener() {			
			public void notifyOfThreadComplete(NotifyingThread thread) {
				//check for messages passed in the parameter.
				
			}
		});
		
		thread.start();
		
	}
	
	@FXML
	protected void onStartAction(ActionEvent event){
		
	}

}
