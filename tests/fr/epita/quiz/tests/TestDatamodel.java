package fr.epita.quiz.tests;

import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.datamodel.Difficulty;
import fr.epita.quiz.datamodel.Question;
import fr.epita.quiz.datamodel.Quiz;

public class TestDatamodel {

	public static void main(String[] args) {
		Quiz quiz = new Quiz("Java Fundamentals - 2019 - Final exam");
		String[] topics = {"OOP", "UML", "JAVA"};
	//	Question question = new Question("What is a class?", topics , Difficulty.EASY.getDifficulty()) ;
		
//		Answer answer = new Answer("It is a general structure that represents common characteristics of a set of individuals (or instances)"
//				+ ". It is defined by 3 main descriptions : Name, State, Behavior");
		
		
	//	answer.setQuestion(question);
//		answer.setQuiz(quiz);
//		
//		
//		System.out.println(answer);
//		System.out.println(new Difficulty(1));  
	 
			for (Difficulty s : Difficulty.values())  
				System.out.println(s.getDifficulty() +" " +"for " +s);  
	
	}

}
