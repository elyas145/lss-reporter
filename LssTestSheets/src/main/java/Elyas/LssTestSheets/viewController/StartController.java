package Elyas.LssTestSheets.viewController;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.App;
import Elyas.LssTestSheets.factory.CourseFactory;
import Elyas.LssTestSheets.model.Course;
import Elyas.LssTestSheets.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
	
	@FXML
	TableColumn<Course, String> colBarcodes;
	
	@FXML
	CustomTextField txtFilterField;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		List<Course> courses = CourseFactory.getSimpleSavedCourses();

		ObservableList<Course> obsCourses = FXCollections.observableList(courses);

		colCourse.setCellValueFactory(new PropertyValueFactory<Course, String>("name"));
		colExamDate.setCellValueFactory(new PropertyValueFactory<Course, String>("examDate"));
		colClients.setCellValueFactory(new PropertyValueFactory<Course, Integer>("clientsCount"));
		colBarcodes.setCellValueFactory(new PropertyValueFactory<Course, String>("barcodes"));

		FilteredList<Course> filteredData = new FilteredList<>(obsCourses, p->true);
		txtFilterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(course -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (course.toJSON().toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches.
                }
                return false; // Does not match.
            });
        });
		
		SortedList<Course> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(table.comparatorProperty());
		
		table.setItems(sortedData);
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
		if(course != null){
			Model.getInstance().setCourse(course);			
			finishHandler.onFinish(ViewState.COURSE_IMPORT);			
		}else{
			//something went wrong.
			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("Oops! it looks like the app cannot read the course you tried to open!");
			alert.setTitle("Error Opening File");
			alert.showAndWait();
		}
		

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
