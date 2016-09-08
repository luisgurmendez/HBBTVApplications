package uy.edu.um.model;

public class Question {
	

	private int number; // Número de pregunta, un identificador
	private String question;
	private String[] options;
	private char rightAnswer;
	private long endTime;
	
	private boolean hasBeenSent = false;

	public Question(int number, String question, String[] options,
			char rightAnswer) {
		super();
		this.number = number;
		this.question = question;
		this.options = options;
		this.rightAnswer = rightAnswer;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public char getRightAnswer() {
		return rightAnswer;
	}

	public void setRightAnswer(char rightAnswer) {
		this.rightAnswer = rightAnswer;
	}

	public boolean getHasBeenSent() {
		return hasBeenSent;
	}

	public void setHasBeenSent(boolean hasBeenSent) {
		this.hasBeenSent = hasBeenSent;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}
