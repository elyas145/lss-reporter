package Elyas.LssTestSheets.viewController;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.BreadCrumbBar.BreadCrumbActionEvent;
import org.controlsfx.control.HiddenSidesPane;

import Elyas.LssTestSheets.factory.ViewFactory;
import Elyas.LssTestSheets.factory.ViewFactoryResult;
import Elyas.LssTestSheets.model.Model;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainController implements Initializable {

	@FXML
	Pane mainPane;

	@FXML
	VBox menuPane;

	@FXML
	HiddenSidesPane pane;

	@FXML
	Label lblMenu;
	@FXML
	VBox lblMenuBackground;

	private SideMenuController menuController;
	private ViewFactoryResult currentView;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// init the start view into mainPane
		ViewFactoryResult result = ViewFactory.getNextView(null);
		currentView = result;
		result.controller.setOnFinishHandler(new FinishHandler() {
			@Override
			public void onFinish(ViewState state) {
				result.controller.finalize();
				viewFinished(state);
			}
		});
		mainPane.getChildren().add(result.parent);

		// init the side menu, set the selection handler.

		ViewFactoryResult sideMenu = ViewFactory.getView("/fxml/sideMenuItems.fxml");
		menuController = (SideMenuController) sideMenu.controller;
		menuController.setMenuSelectionListener(new MenuSelectionListener() {

			@Override
			public void onMenuItemSelected(SideMenuAction action) {
				menuItemSelected(action);

			}

		});
		menuPane.getChildren().add(sideMenu.parent);

		pane.setTriggerDistance(0);
	}

	protected void menuItemSelected(SideMenuAction action) {
		ViewState nextState = null;

		switch (action) {
		case CLIENTS:
			nextState = ViewState.STUDENTS;
			break;
		case COURSE:
			nextState = ViewState.COURSE;
			break;
		case EXAMINERS:
			nextState = ViewState.EXAMINER;
			break;
		case INSTRUCTORS:
			nextState = ViewState.INSTRUCTOR;
			break;
		case MUST_SEES:
			nextState = ViewState.MUST_SEES;
			break;
		case REPORT:
			nextState = ViewState.REPORT;
			break;
		case TEST_SHEETS:
			nextState = ViewState.TEST_SHEETS;
			break;
		case SAVE_EXIT:
			Model.getInstance().save();
			System.exit(0);
			return;
		case SAVE:
			Model.getInstance().save();
			return;
		default:
			return;
		}

		viewFinished(nextState);

	}

	/**
	 * called when the client is finished with the current view. the view should
	 * have updated the model already.
	 */
	protected void viewFinished(ViewState nextState) {
		if(currentView != null)
			currentView.controller.finalize();
		
		if (ViewState.isMenuState(nextState)) {
			pane.setTriggerDistance(20);
			lblMenu.setVisible(true);
			lblMenuBackground.setStyle("-fx-background-color: #1a1a1a");
			menuController.setSelectedItem(ViewState.getSideMenuAction(nextState));
		}
		ViewFactoryResult result = ViewFactory.getNextView(nextState);
		if (result == null)
			return;

		result.controller.setOnFinishHandler(new FinishHandler() {
			@Override
			public void onFinish(ViewState state) {
				result.controller.finalize();
				viewFinished(state);
			}
		});
		currentView = result;
		mainPane.getChildren().clear();
		mainPane.getChildren().add(result.parent);

	}

	public boolean shutdown() {
		currentView.controller.finalize();
		if (Model.getInstance().isChanged()) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Changes Detected");
			alert.setContentText("Do you want to save the changes?  Cancel revokes the " + "exit request.");

			ButtonType btnYes = new ButtonType("Yes");
			ButtonType btnNo = new ButtonType("No");
			ButtonType btnCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
			alert.getButtonTypes().setAll(btnYes, btnNo, btnCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == btnYes) {
				Model.getInstance().save();
			} else if (result.get() == btnNo) {

			} else {
				return false;
			}
		}
		return true;
	}
}
