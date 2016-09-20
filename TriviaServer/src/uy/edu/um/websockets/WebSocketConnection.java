package uy.edu.um.websockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import exceptions.ControllerNotCreatedException;
import gui.ControllerGUI;
import uy.edu.um.constants.JSONFields;
import uy.edu.um.constants.ServerJSONMessages;
import uy.edu.um.managers.TimeManager;
import uy.edu.um.model.Question;
import uy.edu.um.reader.FileParser;
import uy.edu.um.startup.StartUpConfig;
import vlc.VlcVlmBasicController;
import vlc.VlcVlmState;

@ServerEndpoint("/questionWs")
public class WebSocketConnection {
	
	private static String messageToSend;
	private static Integer indexOfCurrentQuestion;
	private static Map<Session, Integer> resultsHashMap = Collections.synchronizedMap(new HashMap<Session, Integer>());
	
	
	
	@OnOpen
	public void openConnection(Session session) {
	}

	@OnMessage
	public void gotAMessage(Session session, String msg) {
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(msg);
			if (!json.containsKey(JSONFields.APP) || !json.containsKey(JSONFields.TYPE_OF_MESSAGE)){
				session.getBasicRemote().sendText(ServerJSONMessages.getErrorMessage("Hubo un error al enviar el mensaje."));
			} else {
				updateIndexOfCurrentQuestion();
				if (json.get(JSONFields.APP).equals(JSONFields.TRIVIA)){
					handleTriviaMessage(session, msg, json);
				}else if(json.get(JSONFields.APP).equals(JSONFields.SUMMARY)){
					handleSummaryMessages(session,json);
				}
			}
		} catch (ParseException e) {
			try {
				session.getBasicRemote().sendText(ServerJSONMessages
						.getErrorMessage("Su respuesta no se envio de manera correcta."));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// ???
	private void updateIndexOfCurrentQuestion(){
		//long currentTime = TimeManager.getInstance(false).getCurrentTime(); // TODO: getTimeFromVLCPlayer();
		long currentTime;
		try {
			currentTime = VlcVlmBasicController.getInstance().state(StartUpConfig.CHANNEL_NAME).getTime();
			List<Question> list = FileParser.getInstance().getQuestions();
			if(indexOfCurrentQuestion==-1){
				indexOfCurrentQuestion=0;
			}
			for (int i=indexOfCurrentQuestion; i<list.size(); i++){
				if ((i == list.size() - 1 && list.get(i).getEndTime() <= currentTime) || // Ya termin� el programa
						(i + 1 < list.size() && list.get(i).getEndTime() < currentTime && list.get(i+1).getStartTime() > currentTime)){ // Est� en medio de preguntas
					indexOfCurrentQuestion = -1;
					break;
				}else if (list.get(i).getStartTime() < currentTime && list.get(i).getEndTime() > currentTime){
					indexOfCurrentQuestion = i;
					break;
				}
			}
		} catch (ControllerNotCreatedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void handleTriviaMessage(Session session, String msg, JSONObject json) throws IOException{
		if (indexOfCurrentQuestion != -1){
			if(json.get(JSONFields.TYPE_OF_MESSAGE).equals(JSONFields.ANSWER_MESSAGE)){
				Question question = FileParser.getInstance().getQuestions().get(indexOfCurrentQuestion);
				if(json.get(JSONFields.QUESTION_NUMBER).equals(String.valueOf(question.getNumber()))){
					if(!resultsHashMap.containsKey(session)){ // Si el usuario es la primera vez que contesta, entra en este if
						resultsHashMap.put(session, 0);
					}
					char playersAnswer = ((String)json.get(JSONFields.PLAYERS_ANSWER)).charAt(0);
					if (question.getRightAnswer() == playersAnswer){
						resultsHashMap.put(session, resultsHashMap.get(session) + 1);
					}
				}else{
					session.getBasicRemote().sendText(ServerJSONMessages
							.getErrorMessage("Estas intentando responder una pregunta que no es la que actualmente se estan mostrando en TV."));
				}
			}else{ // Es un requestMessage
				//long currentTime = TimeManager.getInstance(false).getCurrentTime(); // TODO: getTimeFromVLCPlayer()
				long currentTime;
				try {
					currentTime = VlcVlmBasicController.getInstance().state(StartUpConfig.CHANNEL_NAME).getTime();
					Question question = FileParser.getInstance().getQuestions().get(indexOfCurrentQuestion);
					if (question.getAnswerTime() > currentTime){
						messageToSend = ServerJSONMessages.getQuestionMessage(question);
					}else{
						messageToSend = ServerJSONMessages.getResultMessage(question.getRightAnswer(), question.getNumber());
						
					}
					System.out.println(messageToSend);
					session.getBasicRemote().sendText(messageToSend);
				} catch (ControllerNotCreatedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}else{
			messageToSend = ServerJSONMessages.getAskMeLater();
			session.getBasicRemote().sendText(messageToSend);
		}
	}
	
	private void handleSummaryMessages(Session session, JSONObject json){
		
	}
	
	@OnClose
	public void closeConnection(Session session) {}

	@OnError
	public void error(Session session, Throwable t) {
		try {
			session.getBasicRemote().sendText(ServerJSONMessages.getErrorMessage("Hubo un error estableciendo la conexion."));
			t.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<Session, Integer> getResultsHashMap() {
		return resultsHashMap;
	}

	public static void setResultsHashMap(Map<Session, Integer> resultsHashMap) {
		WebSocketConnection.resultsHashMap = resultsHashMap;
	}

	public static String getMessageToSend() {
		return messageToSend;
	}

	public static void setMessageToSend(String messageToSend) {
		WebSocketConnection.messageToSend = messageToSend;
	}

	public static Integer getIndexOfCurrentQuestion() {
		return indexOfCurrentQuestion;
	}

	public static void setIndexOfCurrentQuestion(Integer indexOfCurrentQuestion) {
		WebSocketConnection.indexOfCurrentQuestion = indexOfCurrentQuestion;
	}

}