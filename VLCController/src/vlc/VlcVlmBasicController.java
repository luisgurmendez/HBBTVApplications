package vlc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;

import org.apache.log4j.Logger;

import exceptions.ControllerNotCreatedException;

public class VlcVlmBasicController extends Observable {
	private static Logger logger = Logger
			.getLogger(VlcVlmBasicController.class);
	private final Socket socket;
	private final Reader reader;
	private final Writer writer;
	private final DisconnectListener disconnectListener;

	private boolean connected;
	
	private static VlcVlmBasicController controller = null;

	private static final int READER_BUFFER_SIZE = 1000;

	private static final int NOTIFY_DELAY = 3;

	private VlcVlmBasicController(String vlcServer, int vlcTelnetPort,
			String vlcTelnetPassword, DisconnectListener disconnectListener) {
		Socket lSocket = null;
		Reader lReader = null;
		Writer lWriter = null;
		

		this.disconnectListener = disconnectListener;
		try {
			lSocket = new Socket(vlcServer, vlcTelnetPort);
			lWriter = new OutputStreamWriter(lSocket.getOutputStream(), "UTF-8");
			lReader = new BufferedReader(new InputStreamReader(
					lSocket.getInputStream(), "UTF-8"), READER_BUFFER_SIZE);
			connected = true;
		} catch (UnknownHostException uhex) {
			disconnect(lSocket, lReader, lWriter);
		} catch (IOException ioex) {
			disconnect(lSocket, lReader, lWriter);
		}
		this.socket = lSocket;
		this.reader = lReader;
		this.writer = lWriter;
		/*
		 * Logs in using password Keep in mind that if it tries to connect to a
		 * port that another application is listening that is not VLC it will
		 * probably block unless the application writes a line that starts with
		 * the prompt or with the character of end of login errors
		 */

		String output = writeLine(vlcTelnetPassword, -1);
		logger.debug("VLM connect response output: " + output);
		/*
		 * if (output.charAt(output.length()-1) == END_LOG_ERROR_CHAR ) { throw
		 * new ChannelException("Error while logging in: "+output); }
		 */
	}

	public  static VlcVlmBasicController getInstance(String vlcServer, int vlcTelnetPort,
			String vlcTelnetPassword, DisconnectListener disconnectListener){
		if(controller == null){
			controller = new VlcVlmBasicController(vlcServer, vlcTelnetPort, vlcTelnetPassword, disconnectListener);
		}
		return controller;
		
		
	}

	
	public static VlcVlmBasicController getInstance() throws ControllerNotCreatedException{
		if(controller == null){
			throw new ControllerNotCreatedException("Controller not created.");
		}
		return controller;
	}
	
	
	/**
	 * Writes a line and return the output till prompt
	 * 
	 * @param line
	 * @param specialResponseChar
	 *            If this special character is found in the response it returns
	 *            even that the prompt has not being found yet. NO_SPECIAL_CHAR
	 *            means no specialOutputChar
	 * @return
	 * @throws ChannelException
	 */
	private synchronized String writeLine(String line, int specialResponseChar) {
		if (connected) {
			final char PROMPT = '>';
			StringBuilder ret = new StringBuilder();
			try {
				writer.write(line + '\n');
				writer.flush();
				int c = reader.read();
				while (c != -1 && c != PROMPT && c != specialResponseChar) {
					ret.append((char) c);
					c = reader.read();
				}
				if (c == -1) {
					logger.error("End of file read while reading output of line: \""
							+ line
							+ "\"\n"
							+ "Output till end of file: \""
							+ ret + "\"");
				} else if (c == specialResponseChar) {
					/* Add the line to the end of the response */
					ret.append((char) c);
				}

			} catch (IOException ioex) {
				/*
				 * Disconnect as it is likely to have corrupted the session,
				 * even if it not that the connection is already lost
				 */
				disconnect();

			}

			return ret.toString();
		}else{
			return null;
		}
	}

	/**
	 * Executes a command that doesn't give any response message when there are
	 * no errors. An response message mean an error
	 * 
	 * @param command
	 * @throws ChannelException
	 */
	public void executeCommand(String command) {
		logger.debug("sendind command: " + command);
		String output = writeLine(command, -1);
		if (output.trim().length() != 0) {
			logger.warn("Command return unexpected output: " + output);
		}
	}

	public String executeCommandWithResponse(String command) {
		logger.debug("sendind command with response: " + command);
		String output = writeLine(command, -1);
		return output;

	}

	public void controlCommand(String vlcChannelName, String command) {
		logger.debug("Sending control command: " + command);
		StringBuilder sb = new StringBuilder(100);
		executeCommand(sb.append("control ").append(vlcChannelName).append(" ")
				.append(command).toString());
		notify(command, NOTIFY_DELAY);
	}

	private void notify(final String command, final int seconds) {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(seconds * 1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setChanged();
				notifyObservers(command);

			}
		});
		t.setName("Notify");
		t.start();

	}

	public void disconnect() {
		disconnect(socket, reader, writer);
		if (disconnectListener != null) {
			disconnectListener.disconnected();
		}
	}

	public VlcVlmState state(String s) {
		String resp = executeCommandWithResponse("show " + s);
		return VlcVlmParser.parse(resp);
	}

	private void disconnect(Socket s, Reader r, Writer w) {
		logger.info("Disconnecting");
		connected = false;
		try {
			if (w != null) {
				w.close();
			}
		} catch (IOException e) {
			logger.warn("error closing writer", e);
		}
		try {
			if (r != null) {
				r.close();
			}
		} catch (IOException e) {
			logger.warn("error closing reader", e);
		}
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException e) {
			logger.warn("error closing socket", e);
		}
	}

}
