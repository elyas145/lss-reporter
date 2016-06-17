package Elyas.LssTestSheets.factory;

import Elyas.LssTestSheets.viewController.Controller;
import javafx.scene.Parent;

public class ViewFactoryResult{
	public Parent parent;
	public Controller controller;
	
	public ViewFactoryResult(Parent pane, Controller controller){
		this.parent = pane;
		this.controller = controller;
	}
}
