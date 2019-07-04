package fr.epita.quiz.datamodel;

public class Question {
	
	private String question;
	private String topic;
	private int QID ;
			
	public Question(String question, String topic, int qid ) {
		this.question = question;
		this.topic = topic;		
		this.QID = qid;	
	}
		
	public String getquestion() {
		return question;
	}
	
	public void setquestion(String question) {
		this.question = question;
	}
	
	public int getID() {
		return QID;
	}
	
	public void setID(int id) {
		this.QID = id;
	}
	
	public String getTopics() {
		return topic;
	}
	
	public void setTopics(String topic) {
		this.topic = topic;
	}
		
			
	@Override
	public String toString() {
		return "Question [question=" + question + ", topic=" +topic + "]";
	}
}