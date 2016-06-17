package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.model.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainMustSeesController extends Controller implements Initializable {

	@FXML
	TabPane tbpMain;
	List<CourseMustSeesController> controllers;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Collection<String> courses = Model.getInstance().getQualificationNames();
		controllers = new ArrayList<>();
		for (String course : courses) {
			FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/must-sees-course.fxml"));
			try {
				Tab parent = (Tab) loader.load();
				CourseMustSeesController controller = loader.<CourseMustSeesController> getController();
				controller.setCourse(course);
				parent.setText(course);
				tbpMain.getTabs().add(parent);
				controllers.add(controller);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
