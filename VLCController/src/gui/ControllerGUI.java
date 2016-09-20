package gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import vlc.VlcVlmBasicController;
import vlc.VlcVlmServer;

public class ControllerGUI extends JFrame{
	
	private static Logger logger = Logger.getLogger(ControllerGUI.class);
	
	
	private static final long serialVersionUID = 1L;
	
	public static final String CHANNEL_NAME = "UM-Channel";
	
	private utils.Process vlcProcess;
	
	public ControllerGUI(utils.Process p) {
		vlcProcess = p;
		try {
			BufferedImage bi = ImageIO.read( ClassLoader.getSystemResource( "gui/vlc.png" ) );
			setIconImage(bi);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTitle("VLC Controller Window");
		
		addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(ControllerGUI.this, 
		            "Are you sure to close this window?", "Really Closing?", 
		            JOptionPane.YES_NO_OPTION,
		            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
		        	logger.info("Destroy VLC process and system exit");
		        	vlcProcess.destroy(false);
		            System.exit(0);
		        }
		    }
		});
	}

	public static void main(String[] args) {
		
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
			
						
			ControllerGUI gui = new ControllerGUI(p);
			
			gui.setContentPane(new PanelGUI(controller));
			gui.setSize(300,300);
			gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			gui.setLocationRelativeTo(null);
			gui.setResizable(false);
			gui.setVisible(true);
		}
		
		
		
	}
	
	

}
