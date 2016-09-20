package vlc;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class VlcVlmParser {

	private static Logger logger = Logger.getLogger(VlcVlmParser.class);
	private static final String PROPERTY_SEPARATOR = " : ";
	private static final String EXTRA_SPACES = "    ";

	public static VlcVlmState parse(String output) {

		VlcVlmState state = new VlcVlmState();
		StringTokenizer st = new StringTokenizer(output, "\r\n");
		logger.debug("*********** START VLC STATE OUTPUT *************");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			parseLine(state, line, st);

		}
		logger.debug("************ END VLC STATE OUTPUT **************");
		return state;

	}

	public static void parseLine(VlcVlmState channelState, String line,
			StringTokenizer st) {

		logger.info("line: " + line);
		int propertySeparatorPos = line.indexOf(PROPERTY_SEPARATOR);
		if (propertySeparatorPos != -1) {
			// Parseando las properties
			parseProperty(channelState, line, st);
		} else {
			String trimLine = line.trim();
			if (trimLine.equals("inputs")) {
				// Parseando inputs
				parseInputs(channelState, line, st);
			} else if (trimLine.equals("options")) {
				// Parseando options
				parseOptions(channelState, line, st);

			}
		}

	}

	public static void parseProperty(VlcVlmState channelState, String line,
			StringTokenizer st) {
		int propertySeparatorPos = line.indexOf(PROPERTY_SEPARATOR);
		String property = line.substring(0, propertySeparatorPos).trim();
		String value = line.substring(
				propertySeparatorPos + PROPERTY_SEPARATOR.length()).trim();
		if (property.equals("enabled")) {
			
		} else if (property.equals("loop")) {
			
		} else if (property.equals("output")) {
			
		} else if (property.equals("enabled")) {
			
		} else if (property.equals("state")) {
			if (value.equals("playing")) {
				channelState.setPlaying(true);
			} else if (value.equals("paused")) {
				
			} else if (value.equals("stopped")) {
				/* Stopped but with errors */
				channelState.setPlaying(false);
			} else {
				logger.error("unknown status for channel : " + value);
				
			}
		} else if (property.equals("position")) {
			channelState.setPosition(Float.valueOf(value));
		} else if (property.equals("time")) {
			channelState.setTime(Long.valueOf(value));
		} else if (property.equals("length")) {
			channelState.setLength(Long.valueOf(value));
		} else if (property.equals("playlistindex")) {
			
		} else if (property.equals("seekable")) {
			
		}

	}

	private static void parseInputs(VlcVlmState channelState, String line,
			StringTokenizer st) {
		String trimLine = line.trim();
		String leadingSpaces = line.substring(0, line.indexOf(trimLine));
		
		String subPropertyLeadingSpaces = leadingSpaces + EXTRA_SPACES;

		while (st.hasMoreTokens()) {
			String inputLine = st.nextToken();
			if (inputLine.startsWith(subPropertyLeadingSpaces)) {
				logger.debug("inputLine: " + inputLine);
				
					
				

			} else {
				/* Parse the property as it is already consumed */
				// logger.debug("Parse line and break");
				parseLine(channelState, inputLine, st);
				break;
			}
		}

	}

	private static void parseOptions(VlcVlmState channelState, String line,
			StringTokenizer st) {
		String trimLine = line.trim();
		String leadingSpaces = line.substring(0, line.indexOf(trimLine));
		String subPropertyLeadingSpaces = leadingSpaces + EXTRA_SPACES;
	

		while (st.hasMoreTokens()) {
			String inputLine = st.nextToken();
			if (inputLine.startsWith(subPropertyLeadingSpaces)) {
				logger.debug("optionsLine: " + inputLine);
				int posEquals = inputLine.indexOf('=');
				String key,value;
				if (posEquals != -1) {
					key = inputLine.substring(0, posEquals).trim();
					value = inputLine.substring(posEquals + 1);

					
				} else {
					key = inputLine.trim();
					value = null;
				}
				
			} else {
				/* Parse the property as it is already consumed */
				parseLine(channelState, inputLine, st);
				break;
			}
		}
	}

}
