package Elyas.LssTestSheets;

import javax.swing.ImageIcon;

import Elyas.LssTestSheets.viewController.MainController;
import javafx.application.*;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Hello world!
 *
 */
public class App extends Application {
	private static Stage mainStage;

	@Override
	public void start(Stage stage) {
		mainStage = stage;
		stage.getIcons().add(new Image(App.class.getResourceAsStream("/images/LifeSaver.png")));
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			try {
				com.apple.eawt.Application.getApplication().setDockIconImage(new ImageIcon(App.class.getResource("/images/LifeSaver.png")).getImage());
			} catch (Exception e) {

			}
		}
		try {
			FXMLLoader ldr = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
			Parent root = (Parent) ldr.load();
			final MainController appCtrl = (MainController) ldr.getController();

			Scene scene = new Scene(root, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

			stage.setScene(scene);
			stage.show();

			scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent ev) {
					if (!appCtrl.shutdown()) {
						ev.consume();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Stage getMainStage() {
		return mainStage;
	}
}
