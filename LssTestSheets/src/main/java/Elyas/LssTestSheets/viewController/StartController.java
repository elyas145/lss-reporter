package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Exam;
import Elyas.LssTestSheets.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class StartController extends Controller implements Initializable {

	@FXML
	TableView<Course> table;

	@FXML
	TableColumn<Course, String> colCourse;

	@FXML
	TableColumn<Course, String> colExamDate;

	@FXML
	TableColumn<Course, Integer> colClients;
	private List<Course> courses;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		List<Course> courses = CourseFactory.getSimpleSavedCourses();

		ObservableList<Course> obsCourses = FXCollections.observableList(courses);

		colCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
		colExamDate.setCellValueFactory(new PropertyValueFactory<Course, String>("examDate"));
		colClients.setCellValueFactory(new PropertyValueFactory<Course, Integer>("clientsCount"));

		table.setItems(obsCourses);
	}

	@FXML
	protected void browseClicked(ActionEvent event) {

	}

	@FXML
	protected void newCourseClicked(ActionEvent event) {
		finishHandler.onFinish(ViewState.COURSE_TYPE);
	}

	@FXML
	protected void selectCourseClicked(ActionEvent event) {

	}

	@Override
	public ViewState getViewState() {
		return ViewState.START;
	}
}
