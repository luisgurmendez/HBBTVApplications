package vlc;



import java.io.IOException;

import org.apache.log4j.Logger;

import utils.ProcessUtil;


public class VlcVlmServer {
	
	private static final Logger log = Logger.getLogger(VlcVlmServer.class);
	
	private static int addLongArg(String[] args, int pos, String longOpt, String argLongOpt) {
		
			args[pos] = longOpt + "="+ argLongOpt;
			return pos+1;
		
	}

	public static utils.Process initVLCLocally(String vlcExe, int vlcTelnetPort, String vlcTelnetPassword, String ttl) {

		int extraLongParam = 4;

		String [] args = new String[1+1+extraLongParam];

		args[0] =  vlcExe;
		args[1] = "--no-stats";
		//args[2] = "--no-one-instance";
		int pos=1+1;

		pos= addLongArg(args,pos,"--intf","telnet");
		pos= addLongArg(args,pos, "--telnet-port",Integer.toString(vlcTelnetPort));
		pos= addLongArg(args,pos,"--telnet-password",vlcTelnetPassword);
		pos= addLongArg(args,pos,"--ttl",ttl );


		try {
			return ProcessUtil.launchProcess(args);
		} catch (IOException e) {
			System.err.println("ERROR");
			return null;
		}
	}




	public static VlcVlmBasicController connectToServer(String vlcServer,int vlcTelnetPort, String vlcTelnetPassword)  {
		VlcVlmBasicController vlcVlmController =VlcVlmBasicController.getInstance(vlcServer,vlcTelnetPort,vlcTelnetPassword, new DisconnectListener() {
			public void disconnected() {
				log.info("Disconnected!");
			}

		});
		
		return vlcVlmController;
	}

	


}
