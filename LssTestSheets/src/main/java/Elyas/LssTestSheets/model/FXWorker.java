package Elyas.LssTestSheets.model;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

public class FXWorker<T, V, X> extends Thread{

	private T param;
	private List<Exception> exceptions;
	
	public X doInBackground(T param){return null;}
	public void updateProgress(V param){}
	public void onFinish(X param, List<Exception>exceptions){}
	
	public FXWorker(T param) {
		this.param = param;
		exceptions = new ArrayList<Exception>();
	}
	
	@Override
	public final void run() {
		X result = null;
		try {
			result = doInBackground(param);
			
		} finally {
			onFinish(result, exceptions);
		}
	}
	
	protected void addException(String description, Exception exception){
		exceptions.add(exception);
	}
	
	protected void onProgressUpdate(final V param){
		Platform.runLater(new Runnable() {
			
			public void run() {
				updateProgress(param);
			}
		});
	}
	
}