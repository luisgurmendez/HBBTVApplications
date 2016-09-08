package uy.edu.um.websockets;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uy.edu.um.constants.JSONFields;
import uy.edu.um.constants.ServerJSONMessages;
import uy.edu.um.managers.TimeManager;
import uy.edu.um.model.Question;
import uy.edu.um.threads.QuestionNotifierThread;

@ServerEndpoint("/questionWs")
public class WebSocketConnection {
	
	private static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
	private static Map<Long, Question> questionsHashMap  = Collections.synchronizedMap(new HashMap<Long, Question>());
	private static long startTimeOfLastQuestion;
	private static int currentQuestionNumber;
	private static List<Session> playersThatAnsweredCurrentQuestion;
	private static Map<Session, Integer> resultsHashMap = Collections.synchronizedMap(new HashMap<Session, Integer>());
	private static QuestionNotifierThread thread;
	
	@OnOpen
	public void openConnection(Session session) {
		allSessions.add(session);
		System.out.println("Se abrió una connection.");
		if (allSessions.size() == 1){
			thread = new QuestionNotifierThread();
			thread.start();
		}
	}



	@OnMessage
	public void gotAMessage(Session session, String msg) {
		JSONParser parser = new JSONParser();
		System.out.println(msg);
		try {
			JSONObject json = (JSONObject) parser.parse(msg);
			if (!json.containsKey(JSONFields.TYPE_OF_MESSAGE) || 
				!json.get(JSONFields.TYPE_OF_MESSAGE).equals(JSONFields.ANSWER_MESSAGE) || 
				!json.containsKey(JSONFields.QUESTION_NUMBER) || 
				!json.containsKey(JSONFields.PLAYERS_ANSWER)){
				session.getBasicRemote().sendText(ServerJSONMessages.getErrorMessage("Hubo un error al enviar el mensaje."));
			} else {
				long questionNumberLong = Long.valueOf((String)json.get(JSONFields.QUESTION_NUMBER));
				int questionNumber = (int)questionNumberLong;
				char playersAnswer = ((String)json.get(JSONFields.PLAYERS_ANSWER)).charAt(0);
				if(!resultsHashMap.containsKey(session)){ // Si el usuario es la primera vez que contesta, entra en este if
					resultsHashMap.put(session, 0);
				}
				if(questionNumber == currentQuestionNumber){
					playersThatAnsweredCurrentQuestion.add(session);
					if (questionsHashMap.get(startTimeOfLastQuestion).getRightAnswer() == playersAnswer){
						resultsHashMap.put(session, resultsHashMap.get(session) + 1);
					}
				}else{
					session.getBasicRemote().sendText(ServerJSONMessages
							.getErrorMessage("Est�s intentando responder una pregunta que no es la que actualmente se est� mostrando en TV."));
				}
			}
		} catch (ParseException e) {
			try {
				session.getBasicRemote().sendText(ServerJSONMessages
						.getErrorMessage("Su respuesta no se envi� de manera correcta."));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: Mandarle un aviso al cliente que no se pudo procesar su respuesta correctamente
			e.printStackTrace();
		}
	}
	
	

	@OnClose
	public void closeConnection(Session session) {
		allSessions.remove(session);
		if(allSessions.size() == 0){
			thread.setStopFlag(true);
		}
	}

	@OnError
	public void error(Session session, Throwable t) {
		try {
			session.getBasicRemote().sendText(ServerJSONMessages.getErrorMessage("Hubo un error estableciendo la conexi�n."));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<Session> getAllSessions() {
		return allSessions;
	}

	public static void setAllSessions(Set<Session> allSessions) {
		WebSocketConnection.allSessions = allSessions;
	}

	public static Map<Long, Question> getQuestionsHashMap() {
		return questionsHashMap;
	}

	public static void setQuestionsHashMap(Map<Long, Question> questionsHashMap) {
		WebSocketConnection.questionsHashMap = questionsHashMap;
	}

	public static long getStartTimeOfLastQuestion() {
		return startTimeOfLastQuestion;
	}

	public static void setStartTimeOfLastQuestion(long startTimeOfLastQuestion) {
		WebSocketConnection.startTimeOfLastQuestion = startTimeOfLastQuestion;
	}

	public static int getCurrentQuestionNumber() {
		return currentQuestionNumber;
	}

	public static void setCurrentQuestionNumber(int currentQuestionNumber) {
		WebSocketConnection.currentQuestionNumber = currentQuestionNumber;
	}

	public static List<Session> getPlayersThatAnsweredCurrentQuestion() {
		return playersThatAnsweredCurrentQuestion;
	}

	public static void setPlayersThatAnsweredCurrentQuestion(List<Session> playersThatAnsweredCurrentQuestion) {
		WebSocketConnection.playersThatAnsweredCurrentQuestion = playersThatAnsweredCurrentQuestion;
	}

	public static Map<Session, Integer> getResultsHashMap() {
		return resultsHashMap;
	}

	public static void setResultsHashMap(Map<Session, Integer> resultsHashMap) {
		WebSocketConnection.resultsHashMap = resultsHashMap;
	}
	
	

}