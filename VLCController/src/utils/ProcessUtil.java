package utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class ProcessUtil {

	private static final Logger log = Logger.getLogger(ProcessUtil.class);

	private static class ConsumeStream 
			implements Runnable {
		InputStream inputStream;
		OutputStream outputStream;

		ConsumeStream(InputStream inputStream, OutputStream outputStream) {
			this.inputStream = inputStream;
			this.outputStream = outputStream;
		}

		public void run() {
			try {
				BufferedInputStream bufferedInputStream = null;
				try {
					bufferedInputStream = new BufferedInputStream(inputStream);
					byte[] buffer = new byte[512];
					int n;
					while ((n = bufferedInputStream.read(buffer)) != -1) {
						if (outputStream != null) {
							outputStream.write(buffer, 0, n);
						}
					}
					if (outputStream != null) {
						outputStream.flush();
					}
				} catch (IOException ioe) {
					logException(ioe);

				} finally {
					// Close only input (not output)
					if (bufferedInputStream != null) {
						try {
							bufferedInputStream.close();
						} catch (IOException e) {
							logException(e);
						}
					}
				}
			} catch (Exception e) {
				logException(e);
			}
		}
	}

	private static void logException(Exception e) {
		log.debug(e.getLocalizedMessage());
	}

	public static Process launchProcess(String[] args) throws IOException {
		Process p = new Process(args);
		p.setProcess(launchProcess(args, System.out, System.err));
		return p;
	}


	public static java.lang.Process launchProcess(String[] args,
			OutputStream redirectOutput, OutputStream redirectError)
			throws IOException {
		logProcessArgs(args, true);
		ProcessBuilder builder = new ProcessBuilder(args);
		builder.redirectErrorStream(true);
		java.lang.Process process = builder.start();
		
		
		
		if (redirectOutput != null) {
			ConsumeStream outputConsumer = new ConsumeStream(
					process.getInputStream(), redirectOutput);
			new Thread(outputConsumer, "output consumer thread").start();
		}
		if (redirectError != null) {
			ConsumeStream errorConsumer = new ConsumeStream(process.getErrorStream(),
					redirectError);
			new Thread(errorConsumer, "error consumer thread").start();
		}
		return process;
	}

	private static void logProcessArgs(String[] args, boolean singleLine) {
		StringBuffer sb = new StringBuffer("");
		if (args != null) {
			for (String string : args) {
				if (singleLine) {
					sb.append(string);
					sb.append(" ");
				} else {
					log.debug(string);
				}

			}
		}
		if (singleLine) {
			log.info(sb);
		}

	}

	


}
