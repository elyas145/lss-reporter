package LssReporter.Updater;

import javafx.application.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
/**
 * Hello world!
 *
 */
public class App extends Application
{
	private static Stage mainStage;
	@Override
	public void start(Stage stage) {
		mainStage = stage;
		try {
			FXMLLoader ldr = new FXMLLoader(getClass()
	                .getResource("/main.fxml"));
	    Parent root = (Parent) ldr.load();
	    MainController appCtrl = (MainController) ldr.getController();

	    Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
	    
	    stage.initStyle(StageStyle.UNDECORATED);

	    stage.setScene(scene);
	    stage.show();
	    
	   

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	public static Stage getMainStage(){
		return mainStage;
	}
}
