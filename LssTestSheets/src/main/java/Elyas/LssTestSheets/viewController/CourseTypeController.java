package Elyas.LssTestSheets.viewController;

import java.util.Collection;
import java.util.List;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.CheckListView;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Client;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Model;
import Elyas.LssTestSheets.model.Qualification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class CourseTypeController extends Controller implements Initializable {

	@FXML
	CheckListView<Qualification> lstCourses;

	@FXML
	Label lblError;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		List<Qualification> courses = CourseFactory.getSupportedQualifications();
		lstCourses.getItems().addAll(courses);

	}

	@FXML
	protected void onCancelClicked(ActionEvent event) {
		finishHandler.onFinish(ViewState.START);
	}

	@FXML
	protected void onNextClicked(ActionEvent event) {
		Collection<Qualification> c = lstCourses.getCheckModel().getCheckedItems();

		if (c == null || c.isEmpty()) {
			lblError.setVisible(true);
		} else {
			Model.getInstance().setQualifications(c);
			Model.getInstance().save();
			finishHandler.onFinish(ViewState.COURSE);
		}
	}

	@Override
	public ViewState getViewState() {
		return ViewState.COURSE;
	}

}
