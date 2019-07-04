package fr.epita.quiz.datamodel;

public class Answer {
	
	private String text;
	private Quiz quiz;
	private Question question;
	
	
	public Quiz getQuiz() {
		return quiz;
	}


	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}


	public Question getQuestion() {
		return question;
	}


	public void setQuestion(Question question) {
		this.question = question;
	}


	public Answer(String text) {
		this.text = text;
	}
	
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	@Override
	public String toString() {
		return "Answer \t[text=" + text + ", \n quiz=" + quiz + ", \n question=" + question + "\n]";
	}
	

}
