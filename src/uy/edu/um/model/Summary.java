package uy.edu.um.model;

public class Summary {

	private String summary;
	private boolean hasBeenSent;
	
	public Summary(String summary){
		this.summary = summary;
		setHasBeenSent(false);
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public boolean hasBeenSent() {
		return hasBeenSent;
	}

	public void setHasBeenSent(boolean hasBeenSent) {
		this.hasBeenSent = hasBeenSent;
	}

}
