package uy.edu.um.model;

public class Question {
	
	private int number; // Número de pregunta, un identificador
	private String question;
	private String[] options;
	private char rightAnswer;
	private long startTime;
	private long answerTime;
	private long endTime;
	
	public Question(){
		
	}
	
	public Question(int number, String question, String[] options,
			char rightAnswer, long startTime, long answerTime, long endTime) {
		super();
		this.number = number;
		this.question = question;
		this.options = options;
		this.rightAnswer = rightAnswer;
		this.startTime = startTime;
		this.answerTime = answerTime;
		this.endTime = endTime;
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
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}
	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getAnswerTime() {
		return answerTime;
	}

	public void setAnswerTime(long answerTime) {
		this.answerTime = answerTime;
	}
	
}
