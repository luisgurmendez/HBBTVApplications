package uy.edu.um.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import gui.ControllerGUI;
import vlc.VlcVlmBasicController;

@Path("/VLCManagerWebService")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class VideoWebService {

	@POST
	@Path("/play")
	public void playVLC(@Context HttpServletRequest request){
		System.out.println("Play Web Server called");
		try{
			VlcVlmBasicController.getInstance().controlCommand(ControllerGUI.CHANNEL_NAME,"play");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@POST
	@Path("/stop")
	public void stopVLC(@Context HttpServletRequest request){
		System.out.println("Stop Web Server called");
		try{
			VlcVlmBasicController.getInstance().controlCommand(ControllerGUI.CHANNEL_NAME,"stop");
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	@GET
	@Path("/changeVideoTime")
	public void changeVideoTimeVLC(@QueryParam("time") int time, @Context HttpServletRequest request){
		System.out.println("Change Video Time Web Server called and time change to % : " + time);
		try{
			VlcVlmBasicController.getInstance().controlCommand(ControllerGUI.CHANNEL_NAME,"seek " + time);
		}catch(Exception e){
			e.printStackTrace();
		}

		
	}
	
	
	
}
