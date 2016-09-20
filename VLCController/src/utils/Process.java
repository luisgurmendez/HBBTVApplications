package utils;

import org.apache.log4j.Logger;

public class Process {
	private static final Logger log = Logger.getLogger(Process.class);
	private java.lang.Process process;
	private int pid;
	
	private boolean deadManWalking;
	private String[] args;
	
	
	
	public Process(String[] args) {
		this.args = args;
	}
	
	
	public java.lang.Process getProcess() {
		return process;
	}

	public void setProcess(java.lang.Process process) {
		this.process = process;
	}
	

	

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public void destroy(boolean newThread){
		
		if(newThread){
			Thread t = new Thread(new Runnable() {
				public void run() {
					internalDestroy();		
				}
			},"KILL PROCESS THREAD");
			t.start();	
		}else{
			internalDestroy();
		}
		
	}
	
	private String commandLine(){
		StringBuffer sb = new StringBuffer();
		for (String elem : args) {
			sb.append(elem+" ");
		}
		return sb.toString();
	}
	
	private void internalDestroy(){
		deadManWalking = true;
		log.debug("Destruyendo proceso "+commandLine());
		process.destroy();
		try {
			process.waitFor();
			log.debug("Proceso destruido! "+commandLine());
		} catch (InterruptedException e) {
			log.error("Interrupted!"+e);
		}
	}
	
	public void monitor(final String tag){
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					if(process != null){
						int code = process.waitFor();
						if(deadManWalking){
							log.info("Finalizo el proceso "+tag+", code: "+code);	
						}else{
							log.warn("El proceso "+tag+" finalizo inesperadamente!, code: "+code);
						}
						
					}
					
				} catch (InterruptedException e) {
					log.error("Interrupted!"+e);
				}		
			}
		},"PROCESS MONITOR");
		t.start();	
	}
	

}
