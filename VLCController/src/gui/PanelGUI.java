package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import vlc.VlcVlmBasicController;
import vlc.VlcVlmState;

public class PanelGUI extends JPanel implements Observer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(PanelGUI.class);
	private VlcVlmBasicController controller;
	private JButton botonPause;
	private JButton botonPlay;
	private JTextField textField;
	private JButton botonSeek;
	
	private final String PLAY_COMMAND = "play";
	private final String PAUSE_COMMAND = "pause";
	private final String STOP_COMMAND = "stop";
	private final String SEEK_COMMAND = "seek";
	
	public PanelGUI(VlcVlmBasicController con) {
		controller = con;
		controller.addObserver(this);
		init();
		
	}
	
	private void init(){
		setLayout(null);
		
		
		
		
		
		botonPause = new JButton(PAUSE_COMMAND);
		botonPause.setBounds(30, 200, 100, 50);
		botonPause.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.controlCommand(ControllerGUI.CHANNEL_NAME, botonPause.getText());
				disableButtons();
			}
		});
		add(botonPause);
		
		
		botonPlay = new JButton(PLAY_COMMAND);
		botonPlay.setBounds(170, 200, 100, 50);
		botonPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				controller.controlCommand(ControllerGUI.CHANNEL_NAME, botonPlay.getText());
				disableButtons();
				
			}
		});
		add(botonPlay);
		
		textField = new JTextField(5);
		textField.setBounds(30, 100, 100, 30);
		add(textField);
		
		botonSeek = new JButton(SEEK_COMMAND);
		botonSeek.setBounds(170, 100, 100, 30);
		botonSeek.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = botonSeek.getText() + " "+textField.getText();
				controller.controlCommand(ControllerGUI.CHANNEL_NAME, command);
				disableButtons();
				
			}
		});
		add(botonSeek);
		
		
		updatePanel();
	}
	
	private void disableButtons(){
		botonPause.setEnabled(false);
		botonPlay.setEnabled(false);
		botonSeek.setEnabled(false);
		textField.setEnabled(false);
	}
	
	private void updatePanel(){
		VlcVlmState state = controller.state(ControllerGUI.CHANNEL_NAME);
		botonPause.setEnabled(state.isPlaying());
		botonSeek.setEnabled(state.isPlaying());
		textField.setEnabled(state.isPlaying());
		
		if(state.isPlaying()){
			botonPlay.setText(STOP_COMMAND);
			logger.debug("legth: "+state.getLength());
			logger.debug("playing time: "+state.getTime());
			logger.debug("postion: "+state.getPosition());
		}else{
			botonPlay.setText(PLAY_COMMAND);
		}
		botonPlay.setEnabled(true);
	}
	

	@Override
	public void update(Observable o, Object arg) {
		logger.debug("Update GUI: "+arg);
		updatePanel();
		
	}
	
	
	
	
	
	
}
