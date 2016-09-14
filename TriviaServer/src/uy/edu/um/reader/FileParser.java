package uy.edu.um.reader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import uy.edu.um.constants.JSONFields;
import uy.edu.um.model.Question;

public class FileParser {

	private static FileParser instance = null;
	private List<Question> questions;
	
	private FileParser(){}
	
	public static FileParser getInstance(){
		if (instance == null){
			instance = new FileParser();
		}
		return instance;
	}
	
	public void parseTriviaFile(String path){
		JSONParser parser = new JSONParser();
		questions = new ArrayList<Question>();
		try {
			Object obj = parser.parse(new FileReader(path));
			JSONObject json = (JSONObject)obj;
			JSONArray arrayOfJsons = (JSONArray)json.get(JSONFields.QUESTIONS);
			for (Object object : arrayOfJsons) {
				JSONObject questionInFile = (JSONObject)object;
				long number = (Long) questionInFile.get(JSONFields.QUESTION_NUMBER);
				String question = (String) questionInFile.get(JSONFields.QUESTION);
				JSONArray jsonArray = (JSONArray) questionInFile.get(JSONFields.OPTIONS);
				String[] options = new String[jsonArray.size()];
				for (int i = 0; i < options.length; i++) {
					options[i] = (String)jsonArray.get(i);
				}
				char rightAnswer = ((String) questionInFile.get(JSONFields.RIGHT_ANSWER)).charAt(0);
				long startTime = (Long) questionInFile.get(JSONFields.QUESTION_START_TIME);
				long answerTime = (Long) questionInFile.get(JSONFields.ANSWER_TIME);
				long endTime = (Long) questionInFile.get(JSONFields.QUESTION_END_TIME);
				Question questionObject = new Question((int)number, question, options, rightAnswer, startTime, answerTime, endTime);
				questions.add(questionObject);
			}
			Collections.sort(questions, new Comparator<Question>() {
				@Override
				public int compare(Question o1, Question o2) {
					if (o1.getStartTime() > o2.getStartTime()){
						return 1;
					}else if (o1.getStartTime() == o2.getStartTime()){
						return 0;
					}else{
						return -1;
					}
				}
			});
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

}
