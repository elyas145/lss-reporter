package Elyas.LssTestSheets.viewController;

import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SideMenuController extends Controller implements Initializable {

	@FXML
	VBox sideMenu;

	@FXML
	HBox courseInformation;

	@FXML
	HBox instructors;

	@FXML
	HBox examiners;

	@FXML
	HBox clients;

	@FXML
	HBox mustSees;

	@FXML
	HBox report;

	@FXML
	HBox testSheets;
	
	@FXML
	HBox saveAndExit;

	@FXML
	HBox save;
	
	private Node currentlySelected;
	private List<HBox> items;

	private MenuSelectionListener onClickListener;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		items = new ArrayList<>();
		items.add(courseInformation);
		items.add(instructors);
		items.add(examiners);
		items.add(clients);
		items.add(mustSees);
		items.add(report);
		items.add(testSheets);
		
	}

	@FXML
	protected void onMouseEnter(MouseEvent event) {
		HBox obj = (HBox) event.getSource();
		obj.setStyle("-fx-background-color: #333333;");
	}

	@FXML
	protected void onMouseExit(MouseEvent event) {
		HBox obj = (HBox) event.getSource();
		if (obj.equals(currentlySelected)) {
			obj.setStyle("-fx-background-color: #595959;");
		} else {
			obj.setStyle("-fx-background-color: #404040;");
		}
	}

	@FXML
	protected void onMouseClick(MouseEvent event) {
		HBox obj = (HBox) event.getSource();
		setSelectedItem(obj);
		
		if(onClickListener != null){
			onClickListener.onMenuItemSelected(getSideMenuAction(obj));
		}
		// Node obj = (Node)event.getSource();
		// obj.getStyleClass().add("-fx-background-color: #808080;");
	}

	private SideMenuAction getSideMenuAction(HBox selectedItem) {
		if(selectedItem.equals(this.clients)){
			return SideMenuAction.CLIENTS;
		}
		if(selectedItem.equals(this.courseInformation)){
			return SideMenuAction.COURSE;
		}
		if(selectedItem.equals(this.examiners)){
			return SideMenuAction.EXAMINERS;
		}
		if(selectedItem.equals(this.instructors)){
			return SideMenuAction.INSTRUCTORS;
		}
		if(selectedItem.equals(this.mustSees)){
			return SideMenuAction.MUST_SEES;
		}
		if(selectedItem.equals(this.report)){
			return SideMenuAction.REPORT;
		}
		if(selectedItem.equals(this.testSheets)){
			return SideMenuAction.TEST_SHEETS;
		}
		if(selectedItem.equals(this.saveAndExit)){
			return SideMenuAction.SAVE_EXIT;
		}
		if(selectedItem.equals(this.save)){
			return SideMenuAction.SAVE;
		}
		return null;
	}

	private void resetBackgrounds() {
		for(HBox node : items){
			if(!node.equals(currentlySelected)){
				node.setStyle("-fx-background-color: #404040;");
			}
		}
	}

	public void setMenuSelectionListener(MenuSelectionListener menuSelectionListener) {
		this.onClickListener = menuSelectionListener;
		
	}

	public void setSelectedItem(SideMenuAction action) {
		switch(action){
		case CLIENTS:
			setSelectedItem(this.clients);
			break;
		case COURSE:
			setSelectedItem(this.courseInformation);
			break;
		case EXAMINERS:
			setSelectedItem(this.examiners);
			break;
		case INSTRUCTORS:
			setSelectedItem(this.instructors);
			break;
		case MUST_SEES:
			setSelectedItem(this.mustSees);
			break;
		case REPORT:
			setSelectedItem(this.report);
			break;
		case TEST_SHEETS:
			setSelectedItem(this.testSheets);
			break;
		default:
			break;
		
		}
		
	}

	private void setSelectedItem(HBox item) {
		item.setStyle("-fx-background-color: #737373;");
		currentlySelected = item;
		resetBackgrounds();
		
	}

}
