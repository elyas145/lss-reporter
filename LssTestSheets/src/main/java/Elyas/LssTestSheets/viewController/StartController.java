package Elyas.LssTestSheets.viewController;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.App;
import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class StartController extends Controller implements Initializable {

	@FXML
	TableView<Course> table;

	@FXML
	TableColumn<Course, String> colCourse;

	@FXML
	TableColumn<Course, String> colExamDate;

	@FXML
	TableColumn<Course, Integer> colClients;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		List<Course> courses = CourseFactory.getSimpleSavedCourses();

		ObservableList<Course> obsCourses = FXCollections.observableList(courses);

		colCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
		colExamDate.setCellValueFactory(new PropertyValueFactory<Course, String>("examDate"));
		colClients.setCellValueFactory(new PropertyValueFactory<Course, Integer>("clientsCount"));

		table.setItems(obsCourses);
		table.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
					Course course = table.getSelectionModel().getSelectedItem();
					if (course != null) {
						courseSelected(course);
					}
				}

			}

		});
	}

	@FXML
	protected void browseClicked(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Course File");
		File file = fileChooser.showOpenDialog(App.getMainStage());		
		Course course = CourseFactory.getFullCourse(file);
		Model.getInstance().setCourse(course);
		finishHandler.onFinish(ViewState.COURSE_IMPORT);

	}

	@FXML
	protected void newCourseClicked(ActionEvent event) {
		finishHandler.onFinish(ViewState.COURSE_TYPE);
	}

	@FXML
	protected void selectCourseClicked(ActionEvent event) {
		Course course = table.getSelectionModel().getSelectedItem();
		if (course != null) {
			courseSelected(course);
		}
	}

	private void courseSelected(Course course) {
		Course c = CourseFactory.getFullCourse(course);
		Model.getInstance().setCourse(c);
		finishHandler.onFinish(ViewState.COURSE);
	}

	@Override
	public ViewState getViewState() {
		return ViewState.START;
	}
}
