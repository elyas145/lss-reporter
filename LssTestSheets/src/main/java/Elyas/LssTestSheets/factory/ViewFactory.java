package Elyas.LssTestSheets.factory;

import java.io.IOException;

import Elyas.LssTestSheets.viewController.Controller;
import Elyas.LssTestSheets.viewController.ViewState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ViewFactory {

	public static ViewFactoryResult getNextView(ViewState state) {
		ViewFactoryResult result = null;
		Parent parent;
		FXMLLoader loader = null;
		if (state == null) {
			// start page
			loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/start.fxml"));
		} else {
			switch (state) {
			case EXAMINER:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/examiner-main.fxml"));
				break;
			case INSTRUCTOR:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/instructor-main.fxml"));
				break;
			case COURSE:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/course.fxml"));
				break;
			case START:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/start.fxml"));
				break;
			case COURSE_TYPE:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/courseType.fxml"));
				break;
			case STUDENT:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/client.fxml"));
				break;
			case STUDENTS:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/clients.fxml"));
				break;
			case MUST_SEES:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/must-sees-main.fxml"));
				break;
			case REPORT:
				loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/report-main.fxml"));
				break;
			default:
				break;

			}
		}
		if (loader != null) {
			try {
				parent = loader.load();
				Controller controller = loader.<Controller> getController();
				result = new ViewFactoryResult(parent, controller);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		return result;
	}

	public static ViewFactoryResult getView(String resourcePath) {
		FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource(resourcePath));
		ViewFactoryResult result = null;
		try {
			Parent parent = loader.load();
			Controller controller = loader.<Controller> getController();
			result = new ViewFactoryResult(parent, controller);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return result;
	}

	
}
