package uy.edu.um.websockets;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

import uy.edu.um.managers.TimeManager;
import uy.edu.um.model.Summary;

@ServerEndpoint("/addinfo")
public class WebSocketConnection {

	private static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());
	private String filepath = "C:\\Users\\tedym\\Desktop\\itsNotACat.json";
	private HashMap<Long, Summary> hashMap = new HashMap<Long, Summary>();
	
	@OnOpen
	public void openConnection(Session session) {
		allSessions.add(session);
		if (allSessions.size() == 1){
			createHashMap();
			mainLoop();
		}
	}

	private void createHashMap(){
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader(filepath));
			JSONObject json = (JSONObject)obj;
			JSONArray arrayOfJsons = (JSONArray)json.get("marks");
			for (Object object : arrayOfJsons) {
				JSONObject mark = (JSONObject)object;
				long timeMark = (Long) mark.get("time");
				String summaryText = (String) mark.get("summary");
				Summary summary = new Summary(summaryText);
				hashMap.put(timeMark, summary);
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void mainLoop() {
		while (TimeManager.getInstance(false) == null) ; // No hace nada mientras el programa no empieza
		long startTime = TimeManager.getInstance(false).getStartTime();
		while (true){
			long currentTime = System.nanoTime();
			long timePassed = (currentTime - startTime) / 1000000;
			long timePassedInSeconds = (long)(Math.floor(timePassed / 1000));
			if (hashMap.containsKey(timePassedInSeconds) && !hashMap.get(timePassedInSeconds).hasBeenSent()){
				try {
					System.out.println(hashMap.get(timePassedInSeconds).getSummary());
					sendMessage(timePassedInSeconds, hashMap.get(timePassedInSeconds).getSummary());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void sendMessage(long time, String message) throws IOException{
		for (Session session : allSessions) {
			session.getBasicRemote().sendText("{\"time\":"+time+", \"summary\":\"" + message + "\"}");
		}
		hashMap.get(time).setHasBeenSent(true); // Se mandó el mensaje
	}

	@OnMessage
	public void gotAMessage(Session session, String msg) {
		// Nunca tendría que recibir ningún mensaje
	}

	@OnClose
	public void closeConnection(Session session) {
		allSessions.remove(session);
	}

	@OnError
	public void error(Session userSession, Throwable t) {

	}

	public HashMap<Long, Summary> getHashMap() {
		return hashMap;
	}

	public void setHashMap(HashMap<Long, Summary> hashMap) {
		this.hashMap = hashMap;
	}

}