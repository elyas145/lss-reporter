package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import Elyas.LssTestSheets.model.TestSheetProperties;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

public class TestSheetExportController extends Controller implements Initializable {

	@FXML
	CheckBox chkIncompleteInstructors;
	@FXML
	CheckBox chkIncompleteExaminers;
	@FXML
	CheckBox chkPassResult;
	@FXML
	CheckBox chkFailResult;
	@FXML
	CheckBox chkBarcodes;
	@FXML
	CheckBox chkDoubleSided;
	@FXML
	CheckBox chkTotalPassFail;
	@FXML
	CheckBox chkPageNumbers;
	@FXML
	CheckBox chkOneFile;
	@FXML
	CheckBox chkFlatten;

	private Stage stage;
	private boolean canceled = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalize() {

	}

	@FXML
	protected void onCancelAction(ActionEvent event) {
		canceled = true;
		if (stage != null) {
			stage.close();
		}
	}

	@FXML
	protected void onOkAction(ActionEvent event) {
		if (stage != null) {
			stage.close();
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;

	}

	public Properties getChosenProperties() {
		Properties properties = new Properties();
		properties.setProperty(TestSheetProperties.INCOMPLETE_INST_ITEMS.name(),
				chkIncompleteInstructors.isSelected() + "");
		properties.setProperty(TestSheetProperties.INCOMPLETE_EXAM_ITEMS.name(),
				chkIncompleteExaminers.isSelected() + "");
		properties.setProperty(TestSheetProperties.PASSED_RESULT.name(),
				chkPassResult.isSelected() + "");
		properties.setProperty(TestSheetProperties.FAILED_RESULT.name(),
				chkFailResult.isSelected() + "");
		properties.setProperty(TestSheetProperties.INCLUDE_BARCODES.name(),
				chkBarcodes.isSelected() + "");
		properties.setProperty(TestSheetProperties.DOUBLE_SIDED.name(),
				chkDoubleSided.isSelected() + "");
		properties.setProperty(TestSheetProperties.TOTAL_PASS_FAIL.name(),
				chkTotalPassFail.isSelected() + "");
		properties.setProperty(TestSheetProperties.INCLUDE_PAGE_NUMBERS.name(),
				chkPageNumbers.isSelected() + "");
		properties.setProperty(TestSheetProperties.GENERATE_SINGLE_FILE.name(),
				chkOneFile.isSelected() + "");
		properties.setProperty(TestSheetProperties.FLATTEN.name(),
				chkFlatten.isSelected() + "");

		return properties;
	}

	public boolean wasCanceled() {
		return canceled;
	}

}
