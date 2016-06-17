package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javax.jws.WebParam.Mode;

import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.factory.ViewFactoryResult;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.TestSheet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainInstructorController extends Controller implements Initializable{

	@FXML
	TabPane tbpMain;
	private List<InstructorController> controllers;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Collection<String> courses = Model.getInstance().getQualificationNames();
		controllers = new ArrayList<>();
		for (String course : courses) {
			FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource("/fxml/instructor.fxml"));
			try {
				Tab parent = (Tab)loader.load();
				InstructorController controller = loader.<InstructorController> getController();
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
