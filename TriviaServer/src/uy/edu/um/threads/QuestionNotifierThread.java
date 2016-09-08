package uy.edu.um.threads;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.websocket.Session;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uy.edu.um.constants.JSONFields;
import uy.edu.um.constants.ServerJSONMessages;
import uy.edu.um.managers.TimeManager;
import uy.edu.um.model.Question;
import uy.edu.um.websockets.WebSocketConnection;

public class QuestionNotifierThread extends Thread {
	
	private String filepath = "/Users/Luis/Desktop/trivia.json";
	private boolean stopFlag = false;

	
	
	public void run(){
		System.out.println("Thread Run()");
		createHashMap(); // Cuando al menos un usuario se interesa por jugar, se crea el mapa.
		mainLoop();
		
		
	}
	
	public void createHashMap(){
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(filepath));
			JSONObject json = (JSONObject)obj;
			JSONArray arrayOfJsons = (JSONArray)json.get(JSONFields.QUESTIONS);
			for (Object object : arrayOfJsons) {
				JSONObject questionInFile = (JSONObject)object;
				long startTime = (Long) questionInFile.get(JSONFields.QUESTION_START_TIME);
				long number = (Long) questionInFile.get(JSONFields.QUESTION_NUMBER);
				String question = (String) questionInFile.get(JSONFields.QUESTION);
				JSONArray jsonArray = (JSONArray) questionInFile.get(JSONFields.OPTIONS);
				String[] options = new String[jsonArray.size()];
				for (int i = 0; i < options.length; i++) {
					options[i] = (String)jsonArray.get(i);
				}
				char rightAnswer = ((String) questionInFile.get(JSONFields.RIGHT_ANSWER)).charAt(0);
				long endTime = (Long) questionInFile.get(JSONFields.QUESTION_END_TIME);
				Question questionObject = new Question((int)number, question, options, rightAnswer);
				questionObject.setEndTime(endTime);
				WebSocketConnection.getQuestionsHashMap().put(startTime, questionObject);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public void mainLoop() {
		//while (TimeManager.getInstance(false) == null) ; // No hace nada mientras el programa no empieza
		
		System.out.println("Arranca el video.");
		long startTime = TimeManager.getInstance(true).getStartTime();
		while (!stopFlag){
			long currentTime = System.nanoTime();
			long timePassed = (currentTime - startTime) / 1000000;
			long timePassedInSecondsBeforeQuestion = (long)(Math.floor(timePassed / 1000));
			if (WebSocketConnection.getQuestionsHashMap().containsKey(timePassedInSecondsBeforeQuestion) && !WebSocketConnection.getQuestionsHashMap().get(timePassedInSecondsBeforeQuestion).getHasBeenSent()){
				try {
					WebSocketConnection.setPlayersThatAnsweredCurrentQuestion(Collections.synchronizedList(new ArrayList<Session>()));
					Question question = WebSocketConnection.getQuestionsHashMap().get(timePassedInSecondsBeforeQuestion);
					WebSocketConnection.setStartTimeOfLastQuestion(timePassedInSecondsBeforeQuestion);
					WebSocketConnection.setCurrentQuestionNumber(question.getNumber());
					sendQuestionToEveryone(timePassedInSecondsBeforeQuestion, question);
					long currentTimeAfterQuestion = (long)(Math.floor((System.nanoTime() - startTime) / 1000000000));
					while (currentTimeAfterQuestion < question.getEndTime()){
						currentTimeAfterQuestion = (long)(Math.floor((System.nanoTime() - startTime) / 1000000000));
					}
					System.out.println("Notifying..");
					notifyPlayersOfResult();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void sendQuestionToEveryone(long time, Question question) throws IOException{
		for (Session session : WebSocketConnection.getAllSessions()) {
			session.getBasicRemote().sendText(ServerJSONMessages.getQuestionMessage(question));
			System.out.println(ServerJSONMessages.getQuestionMessage(question));
		}
		WebSocketConnection.getQuestionsHashMap().get(time).setHasBeenSent(true); // Se mand� el mensaje
	}
	
	private void notifyPlayersOfResult() throws IOException{
		for (int i = 0; i<WebSocketConnection.getPlayersThatAnsweredCurrentQuestion().size(); i++){
			WebSocketConnection.getPlayersThatAnsweredCurrentQuestion().get(i).getBasicRemote()
			.sendText(ServerJSONMessages.getResultMessage(WebSocketConnection.getQuestionsHashMap().get(WebSocketConnection.getStartTimeOfLastQuestion()).getRightAnswer(), WebSocketConnection.getCurrentQuestionNumber()));
		}
		WebSocketConnection.setCurrentQuestionNumber(0);
	}





	public boolean isStopFlag() {
		return stopFlag;
	}





	public void setStopFlag(boolean stopFlag) {
		this.stopFlag = stopFlag;
	}
	
	
	

}
