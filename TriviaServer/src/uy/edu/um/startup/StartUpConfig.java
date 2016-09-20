package uy.edu.um.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import uy.edu.um.managers.TimeManager;
import uy.edu.um.reader.FileParser;
import uy.edu.um.websockets.WebSocketConnection;
import vlc.VlcVlmBasicController;
import vlc.VlcVlmServer;

@WebListener
public class StartUpConfig implements ServletContextListener {
	
	public static final String CHANNEL_NAME = "UM-Channel";


    public void contextInitialized(ServletContextEvent event) {
        FileParser fileParser = FileParser.getInstance();
        fileParser.parseTriviaFile("/Users/Luis/Desktop/trivia.json");
        WebSocketConnection.setIndexOfCurrentQuestion(0); // Seteo el indice de pregunta en 0
        
        //Start VLC controller
        String vlcExe = "/Applications/VLC.app/Contents/MacOS/VLC";
		String video = "/Users/Luis/Desktop/dobleCara.mp4";
		
		int port = 9997;
		String pass = "UM-Hybrid";
		utils.Process p = VlcVlmServer.initVLCLocally(vlcExe, port, pass, "1");
		
		
		
		if(p != null){
			
			VlcVlmBasicController controller = VlcVlmServer.connectToServer("localhost", port, pass);
			controller.executeCommand("new "+CHANNEL_NAME+" broadcast enabled loop");
			controller.executeCommand("setup "+CHANNEL_NAME+" output #rtp{mux=ts,dst=224.0.0.0,port=1234}");
			controller.executeCommand("setup "+CHANNEL_NAME+" input "+video);
		}
        
        
    }

    public void contextDestroyed(ServletContextEvent event) {
        // Webapp shutdown.
    }

}