package uy.edu.um.constants;

import uy.edu.um.model.Question;

public class ServerJSONMessages {

	private static String QUESTION_MESSAGE;
	private static String RESULT_MESSAGE;
	private static String ERROR_MESSAGE;

	public static String getQuestionMessage(Question question){
		QUESTION_MESSAGE = "{\"" + JSONFields.TYPE_OF_MESSAGE + "\":\"" + JSONFields.QUESTION_MESSAGE + "\", \"" 
								 + JSONFields.QUESTION_NUMBER + "\":\"" + question.getNumber() + "\", \""
								 + JSONFields.QUESTION + "\":\""+ question.getQuestion() + "\", \"" 
								 + JSONFields.OPTIONS + "\":[";
		for (int i = 0; i<question.getOptions().length; i++){
			if (i != 0){
				QUESTION_MESSAGE = QUESTION_MESSAGE + ",";
			}
			QUESTION_MESSAGE = QUESTION_MESSAGE + " \"" + question.getOptions()[i] +"\"";
		}
		QUESTION_MESSAGE = QUESTION_MESSAGE + "]}";
		return QUESTION_MESSAGE;
	}
	
	public static String getResultMessage(char rightAnswer, int number){
		RESULT_MESSAGE = "{\"" + JSONFields.TYPE_OF_MESSAGE + "\":\"" + JSONFields.RESULT_MESSAGE + "\", \"" 
						 	   + JSONFields.QUESTION_NUMBER + "\":\"" + number + "\", \""
							   + JSONFields.RIGHT_ANSWER + "\":\"" + rightAnswer + "\"}";
		return RESULT_MESSAGE;
	}
	
	public static String getErrorMessage(String message){
		ERROR_MESSAGE = "{\"" + JSONFields.TYPE_OF_MESSAGE + "\":\"" + JSONFields.ERROR_MESSAGE + "\", \"" 
							  + JSONFields.MESSAGE + "\":\"" + message + "\"}";
		return ERROR_MESSAGE;
	}

}
