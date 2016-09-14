package uy.edu.um.startup;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import uy.edu.um.managers.TimeManager;
import uy.edu.um.reader.FileParser;
import uy.edu.um.websockets.WebSocketConnection;

@WebListener
public class StartUpConfig implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        FileParser fileParser = FileParser.getInstance();
        fileParser.parseTriviaFile("/Users/Luis/Desktop/trivia.json");
        WebSocketConnection.setIndexOfCurrentQuestion(0); // Seteo el indice de pregunta en 0
        TimeManager.getInstance(true);
    }

    public void contextDestroyed(ServletContextEvent event) {
        // Webapp shutdown.
    }

}