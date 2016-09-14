package uy.edu.um.managers;

public class TimeManager {

	private static TimeManager timeManager = null;
	private long startTime;
	
	private TimeManager(){
		startTime = System.nanoTime();
	}
	
	public static TimeManager getInstance(boolean invocationFromPage){
		if (timeManager==null && invocationFromPage){
			timeManager = new TimeManager();
		}
		return timeManager;
	}
	
	public long getStartTime(){
		return startTime;
	}
	
	public long getCurrentTime(){
		return (System.nanoTime() - startTime)/1000000000;
	}
}
