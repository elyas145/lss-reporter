package Elyas.LssTestSheets.viewController;

public abstract class Controller {
	
	protected FinishHandler finishHandler;
	
	/**
	 * sets the handler to be called when this view is finished, and is
	 * requesting a new view to show on the main screen. the new view is passed
	 * to the handler's function as a <code>ViewState</code> variable
	 * 
	 * @param handler
	 */
	public void setOnFinishHandler(FinishHandler handler){
		this.finishHandler = handler;
	}

	/**
	 * finalizes the view. called when the view is about to be switched, or the
	 * instance is about to be destroyed. this method should save the changed
	 * data, and get ready to be destroyed.
	 */
	public void finalize() {

	}

	public ViewState getViewState(){
		return null;
	}
}
