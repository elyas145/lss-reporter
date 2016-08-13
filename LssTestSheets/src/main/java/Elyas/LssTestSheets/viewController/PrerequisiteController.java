package Elyas.LssTestSheets.viewController;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.CustomTextField;

import Elyas.LssTestSheets.model.Prerequisite;
import Elyas.LssTestSheets.model.Prerequisite.Type;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TitledPane;

public class PrerequisiteController extends Controller implements Initializable {
	@FXML
	CheckBox chkCheck;
	@FXML
	CustomTextField dteDate;
	@FXML
	CustomTextField txtLocation;
	@FXML
	TitledPane pneMain;

	private Prerequisite prerequisite;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

	public void setPrerequisite(Prerequisite prerequisite) {
		this.prerequisite = prerequisite;
		if (prerequisite.getType().equals(Type.CHECK)) {
			chkCheck.setSelected(prerequisite.isMet());
		} else {
			if (prerequisite.getDate() != null && !prerequisite.getDate().equals(""))
				dteDate.setText(prerequisite.getDate());
			if (prerequisite.getLocation() != null)
				txtLocation.setText(StringUtils.capitalize(prerequisite.getLocation()));
		}
		pneMain.setText(StringUtils.capitalize(prerequisite.getName()));
	}

	@Override
	public void finalize() {

	}

	public Prerequisite getPrerequisite() {

		if (prerequisite.getType().equals(Type.CHECK)) {
			prerequisite.setMet(chkCheck.isSelected());
		} else {
			if (dteDate.getText() != null)
				prerequisite.setDateEarned(dteDate.getText());

			if (txtLocation.getText() != null)
				prerequisite.setLocation(txtLocation.getText());
			prerequisite.setDefaultMet();
		}
		return prerequisite;
	}

	public String getKey() {
		return prerequisite.getKey();
	}

}
