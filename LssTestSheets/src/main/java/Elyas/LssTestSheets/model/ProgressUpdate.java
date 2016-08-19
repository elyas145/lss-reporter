package Elyas.LssTestSheets.model;

public class ProgressUpdate {

	private String message;
	private float progress;

	public ProgressUpdate(String message, float progress) {
		this.message = message;
		this.progress = progress;
	}

	public String getMessage() {
		return message;
	}

	public float getProgress() {
		return progress;
	}

}
