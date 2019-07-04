package fr.epita.quiz.launcher;
import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.datamodel.Difficulty;
import fr.epita.quiz.datamodel.Question;
import fr.epita.quiz.datamodel.Quiz;
import fr.epita.quiz.datamodel.User;
import fr.epita.quiz.exception.DataAccessException;
import fr.epita.quiz.services.*;
import fr.epita.quiz.services.data.AnswerJDBCDAO;
import fr.epita.quiz.services.data.LoginValidate;
import fr.epita.quiz.services.data.QuestionJDBCDAO;
import fr.epita.quiz.services.data.QuizJDBCDAO;
import fr.epita.quiz.services.data.StudentJDBCDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Launcher {
	static User user = new User();
	
	private static Scanner scanner = new Scanner(System.in);
	private static boolean[] authenticated = new boolean[3];
	private static String difficult = "";

	public static void main(String[] args) throws SQLException, DataAccessException, IOException {
		
		while(!authenticated[0])
		{
		authenticated = authenticate(scanner);
		if (authenticated[0]) {  
			System.out.println("You're authenticated");
		}
		else
		{
		System.out.println("You're not authenticated !! Please enter valid credentials !!");
		}
		}
		applicationStart(authenticated, scanner);
	}
	/**
	 * Handles the response from the Display menu
	 * @param authenticated - array of boolean for login validation and ADMIN
	 * @param scanner to read input from console
	 * @throws SQLException
	 * @throws DataAccessException
	 * @throws IOException
	 */
	private static void applicationStart(boolean[] authenticated, Scanner scanner) throws SQLException, DataAccessException, IOException {
	 	
		String answer = "";
		
		if(authenticated[1])
		{
		while (!answer.equals("q")) {

			answer = displayMenu(scanner, authenticated[1]);

			switch (answer) {
			case "1":
				quizCreation(scanner);
				break;
			case "2":
				updateQuiz(scanner);
				break;
			case "3":
				deleteQuiz(scanner);
				break;
			case "4":
				addQuestion(scanner);
				break;
			case "5":
				updateQuestion(scanner);
				break;
			case "6":
				deleteQuestion(scanner);
				break;
			case "7":
				viewScore();
				break;
			case "8":
				viewAnswer();
				break;
			case "q":
				System.out.println("Good bye!");
				break;

			default:
				System.out.println("Option not recognized, please enter an other option");
				break;
			}
		}
		scanner.close();
		}
		
		else
		{
			while(!answer.equals("q")) {
				
				answer = displayMenu(scanner, authenticated[1]);
				switch(answer) {
				case "1":
					student(scanner);
					break;
				case "2":
					exportQuizToFile();
					break;
				case "q":
					System.out.println("Good bye!");
					break;

				default:
					System.out.println("Option not recognized, please enter an other option");
					break;
				}
			}
			scanner.close();
		}
		
	}
	/**
	 * ADMIN can add MCQ and open type questions to the existing quiz
	 * @param scanner to read input from console
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void addQuestion(Scanner scanner) throws SQLException, DataAccessException {
		ArrayList<String> list = new ArrayList<String>();
		int qid = 0;
		String[] choice = new String[3];
		String topic = "";
		String answer = "";
		String ques = "";
		String ans = "";
		list = QuizJDBCDAO.getInstance().getQuiz();
		while(topic.equals("")) {
			topic = "";
			System.out.println("Enter the topic in which you want to add Question");
			 topic = scanner.nextLine();
		}
		
		while(!list.contains(topic)) {
			System.out.println("Please Enter valid quiz name from the list");
			topic = scanner.nextLine();
		}
		
		while (!answer.equals("q")) {
			difficult = selectDifficulty(scanner);
			System.out.println("Enter 1 to add MCQ Question and 2 to add open Question and q to quit adding questions");
			System.out.println("Enter your choice 1|2|q :");
			answer = scanner.nextLine();
		switch(answer) {
		case "1" :
			ques = "";	
			ans = "";
			while(ques.equals("")) {
				ques = "";
				System.out.println("Enter the MCQ Question");
				 ques = scanner.nextLine();
			}
			
			qid = QuestionJDBCDAO.getInstance().createQuestion(new Question(ques, topic,0), difficult);
			while(qid!=0 && ans.equals("")) {
				ans = "";
				System.out.println("Question entered successfully !! Please enter the answer for the respective question");
				ans = scanner.nextLine();
			}
			
			AnswerJDBCDAO.getInstance().createAnswer(new Quiz(topic), new Question(ques, topic, qid), new Answer(ans));	
			System.out.println("Answer entered successfully !! Please enter the 3 choices for the respective question!! One choice should be the earlier given answer");
			System.out.println("Enter Choice 1");
			choice[0] = scanner.nextLine();
			while(choice[0].equals("")) {
				System.out.println("Enter Choice 1");
				choice[0] = scanner.nextLine();
			}
			
			System.out.println("Enter Choice 2");
			choice[1] = scanner.nextLine();
			while(choice[1].equals("")) {
				System.out.println("Enter Choice 2");
				choice[1] = scanner.nextLine();
			}
			
			System.out.println("Enter Choice 3");
			choice[2] = scanner.nextLine();	
			while(choice[2].equals("")) {
			System.out.println("Enter Choice 3");
			choice[2] = scanner.nextLine();	
			}
			List<String> choiceList = Arrays.asList(choice);
			while(!choiceList.contains(ans)) {
			System.out.println("Please enter the given answer as one of the option");
			choice[2]= scanner.nextLine();
			}
			AnswerJDBCDAO.getInstance().createMcqChoices(new Question(ques, topic, qid), choice);
			
			break;
			
		case "2" :
			ques = "";
			ans = "";
			difficult = selectDifficulty(scanner);
			while(ques.equals("")) {
				ques = "";
				System.out.println("Enter Open Question");
				ques = scanner.nextLine();
			}
			
			qid = QuestionJDBCDAO.getInstance().createQuestion(new Question(ques, topic, 0), difficult);
			
			while(qid!=0 && ans.equals("")) {
				ans = "";
				System.out.println("Question entered successfully !! Please enter the answer for the respective question");
				ans = scanner.nextLine();
			}
			
			AnswerJDBCDAO.getInstance().createAnswer(new Quiz(topic), new Question(ques, topic, qid), new Answer(ans));	
			break;
			
			
		default:
			System.out.println("Option not recognized, please enter an other option");
			break;
			
		}
		}
		
		
	}
	/**
	 * Method to call to the question creation DAO method
	 * @param scanner - to read input from console
	 * @param topic of quiz
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void questionCreation(Scanner scanner, String topic) throws SQLException, DataAccessException {
		int i = 0;
		int qid = 0;
		String ques = "";
		String ans = "";
		
		String[] choice = new String[3];
		System.out.println("Question creation ...");
		System.out.println("Total of 10 questions can be created for a particular topic !! First 7 will be MCQ followed by 3 Open type questions !!");
		difficult = selectDifficulty(scanner);
		for(i=0; i<7; i++)
		{
			 ques = "";
			 ans = "";
			while(ques.equals("")) {
				
				System.out.println("Enter the MCQ Question");
				ques = scanner.nextLine();
			}
			
			qid = QuestionJDBCDAO.getInstance().createQuestion(new Question(ques, topic, 0), difficult);
			while(qid!=0 && ans.equals("")) {
				
				System.out.println("Question entered successfully !! Please enter the answer for the respective question");
				ans = scanner.nextLine();
			}
			
			AnswerJDBCDAO.getInstance().createAnswer(new Quiz(topic), new Question(ques, topic, qid), new Answer(ans));	
			System.out.println("Answer entered successfully !! Please enter the 3 choices for the respective question!! One choice should be the earlier given answer");
			System.out.println("Enter Choice 1");
			choice[0] = scanner.nextLine();
			while(choice[0].equals("")) {
				System.out.println("Enter Choice 1");
				choice[0] = scanner.nextLine();
			}
			System.out.println("Enter Choice 2");
			choice[1] = scanner.nextLine();
			while(choice[1].equals("")) {
			System.out.println("Enter Choice 2");
			choice[1] = scanner.nextLine();
			}
			
			System.out.println("Enter Choice 3");
			choice[2] = scanner.nextLine();
			while(choice[2].equals("")) {
			System.out.println("Enter Choice 3");
			choice[2] = scanner.nextLine();
			}
			
			List<String> list = Arrays.asList(choice);
			while(!list.contains(ans)) {
			System.out.println("Please enter the given answer as one of the option");
			choice[2]= scanner.nextLine();
			}
			AnswerJDBCDAO.getInstance().createMcqChoices(new Question(ques, topic, qid), choice);
			
		}	
		System.out.println("Enter the 3 Open Questions");
		for(int k=0; k<3; k++) {
			ques = "";
			ans = "";
			while(ques.equals("")) {
				ques ="";
				System.out.println("Enter the Open Question");
				ques = scanner.nextLine();
			}
			
			qid = QuestionJDBCDAO.getInstance().createQuestion(new Question(ques, topic, 0), difficult);
			
			while(qid!=0 && ans.equals("") ) {
				ans ="";
				System.out.println("Question entered successfully !! Please enter the answer for the respective question");
				 ans = scanner.nextLine();
			}
			
			AnswerJDBCDAO.getInstance().createAnswer(new Quiz(topic), new Question(ques, topic,qid), new Answer(ans));	
			
		}
		System.out.println("Quiz created Successfully !!");
		
	}
	/**
	 * ADMIN can select difficulty level for question and student can select can select difficulty level to take quiz
	 * @param scanner - to read input from console
	 * @return difficulty
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static String selectDifficulty(Scanner scanner) throws SQLException, DataAccessException {
		String diff = "";
		List<Integer> list = new ArrayList<Integer>();
		try
		{
			while(diff.equals("")) {
				diff = "";
		System.out.println("Enter the difficulty level of question ");
		for (Difficulty s : Difficulty.values())  
		{
			list.add(s.getDifficulty());
			System.out.println(s.getDifficulty() +" " +"for " +s);  
		}
	
		 diff = scanner.nextLine();
			}
		 
			while(!list.toString().contains(diff)) {
				System.out.println("Please enter a valid Difficulty level from the list !!");
				diff = scanner.nextLine();
			}
			}
		catch (Exception e) {
			System.out.println("Invalid input");
		}					
		return  diff;
		
	}
	/**
	 * Method to call to the question update DAO method
	 * @param scanner - to read input from console
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void updateQuestion(Scanner scanner) throws SQLException, DataAccessException {
		ArrayList<String> list = new ArrayList<String>();
		String topic = "";
		String ques = "";
		list = QuizJDBCDAO.getInstance().getQuiz();
		while(topic.equals("")) {
			topic = "";
			System.out.println("Enter the Quiz Name in which you want to update Question");
			topic = scanner.nextLine();
		}
		
		while(!list.contains(topic)) {
			System.out.println("Please Enter valid quiz name from the list");
			topic = scanner.nextLine();
		}
		
		 String diff = selectDifficulty(scanner);
		 list = QuestionJDBCDAO.getInstance().getQuestion(topic, diff);
		
		 while(ques.equals("")) {
			 ques = "";
			 System.out.println("Enter the question which you want to update");
			 ques = scanner.nextLine();
		 }	
		 
		 while(!list.contains(ques)) {
			 System.out.println("Please Enter valid question which you want to update from the list");
			 ques = scanner.nextLine();
		 }
		 	 
		
		QuestionJDBCDAO.getInstance().update(new Question(ques, topic,0), scanner);
				
	}
	/**
	 * Method to call to the Question Delete DAO method
	 * @param scanner - to read input from console
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void deleteQuestion (Scanner scanner) throws SQLException, DataAccessException {
		ArrayList<String> list = new ArrayList<String>();
		String topic = "";
		String ques = "";
		System.out.println("Delete Question ...");
		list = QuizJDBCDAO.getInstance().getQuiz();
		while(topic.equals("")) {
			topic = "";
			System.out.println("Enter the quiz name from which you to delete questions !!");
			topic = scanner.nextLine();
		}
		
		while(!list.contains(topic)) {
			 
			System.out.println("Please Enter valid quiz name from the list");
			topic = scanner.nextLine();
		}
		
		String diff = selectDifficulty(scanner);
		list = QuestionJDBCDAO.getInstance().getQuestion(topic, diff);
		while(ques.equals("")) {
			ques = "";
			System.out.println("Enter the question which you want to delete");
			ques = scanner.nextLine();
		}
	
		while(!list.contains(ques)) {
			System.out.println("Please Enter valid question from the list");
			topic = scanner.nextLine();
		}
		QuestionJDBCDAO.getInstance().delete(ques);
				
	}
	/**
	 * Method to call to the Quiz creation DAO method
	 * @param scan - to read input from console
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void quizCreation(Scanner scan) throws SQLException, DataAccessException {
		String quiz = "";
		System.out.println("Quiz creation ...");
		while(quiz.equals("")) {
			quiz = "";
			System.out.println("Enter the topic for the Quiz !!");
			quiz = scan.nextLine();
		}
		
		QuizJDBCDAO.getInstance().create(new Quiz(quiz));
		questionCreation(scan, quiz);
		
	}	
	/**
	 * Method to call to the quiz update DAO method
	 * @param scan to read input from console
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void updateQuiz(Scanner scan) throws SQLException, DataAccessException {
		ArrayList<String> list = new ArrayList<String>();
		String topic = "";
		System.out.println("Update Quiz ...");
		list = QuizJDBCDAO.getInstance().getQuiz();
		while(topic.equals("")) {
			topic = "";
			System.out.println("Which quiz you want to update !!");
			topic = scan.nextLine();
		}
		
		while(!list.contains(topic)) {
			System.out.println("Please Enter valid quiz name from the list");
			topic = scan.nextLine();
		}			
		QuizJDBCDAO.getInstance().update(new Quiz(topic), scanner);
	
		
	}
	/**
	 * Method to call to the Quiz Delete DAO method
	 * @param scan
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private static void deleteQuiz(Scanner scan) throws SQLException, DataAccessException {
		ArrayList<String> list = new ArrayList<String>();
		String topic = "";
		System.out.println("Delete Quiz ...");
		list = QuizJDBCDAO.getInstance().getQuiz();
		while(topic.equals("")) {
			topic = "";
			System.out.println("Which quiz you want to delete !!");
			topic = scan.nextLine();
		}
		
		while(!list.contains(topic)) {
			System.out.println("Please Enter valid quiz name from the list");
			topic = scan.nextLine();
		}						
		QuizJDBCDAO.getInstance().delete(topic);
		
	}
	/**
	 * Displays the menu based on the user
	 * @param scanner to read input from console
	 * @param isAdmin - Validates if the user is ADMIN
	 * @return
	 */
	private static String displayMenu(Scanner scanner, boolean isAdmin) {
		String answer = "";
		if(isAdmin)
		{			
			System.out.println("-- Menu --");
			System.out.println("1. Create Quiz and Questions");
			System.out.println("2. Update Quiz");
			System.out.println("3. Delete Quiz");
			System.out.println("4. Add question to Quiz");
			System.out.println("5. Update Questions");
			System.out.println("6. Delete Questions");
			System.out.println("7. View test scores of students");
			System.out.println("8. View quiz answers of students");
			System.out.println("q. Quit the application");
			System.out.println("What is your choice ? (1|2|3|4|5|6|7|8|q) :");
			while(answer.equals(""))
			answer = scanner.nextLine();
		}
		else
		{
			System.out.println("-- Menu --");
			System.out.println("1. Select topics to take Quiz");
			System.out.println("2. Export Quiz");
			System.out.println("q. Quit the application");
			System.out.println("What is your choice ? (1|2|q) :");
			while(answer.equals(""))
			answer = scanner.nextLine();
		}
		return answer;
		}
	/**
	 * Method to call to authenticate user DAO method
	 * @param scanner to read input from console
	 * @return
	 */
	private static boolean[] authenticate(Scanner scanner) {
		
		ConfigurationService.getInstance();
			
		System.out.println("Please enter your login : ");
		String login = scanner.nextLine();
		System.out.println("Please enter your password : ");
		String password = scanner.nextLine();
		user.setUser(login);
		return LoginValidate.getInstance().validateLogin(login, password);
	}
	/**
	 * Method to call to the student Quiz taking DAO method
	 * @param scan
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	
	private static void student(Scanner scan) throws SQLException, DataAccessException {
		String topic =  "";
		
		String userName = user.getUser();
		ArrayList<String> list = new ArrayList<String>();
		System.out.println("-------------- Take Quiz -------------");
		list = QuizJDBCDAO.getInstance().getQuiz();
		while(topic.equals("")) {
			topic =  "";
			System.out.println("Enter the topic from the list!!");
			 topic = scan.nextLine();
		}
		
		while(!list.contains(topic)) {
			System.out.println("Please Enter valid quiz name from the list");
			topic = scan.nextLine();
		}
		String diff = selectDifficulty(scanner);
		
		StudentJDBCDAO.getInstance().takeQuiz(topic, userName, diff, scanner); 
	}
	/**
	 * Method to call to the ADMIN score view DAO method
	 */
	private static void viewScore() {
		StudentJDBCDAO.getInstance().getScore();
	}
	/**
	 * Method to call to the ADMIN student answer view DAO method
	 */
	private static void viewAnswer() {
		StudentJDBCDAO.getInstance().getStudentAnswer();
	}
	/**
	 * export quiz to file DAO method
	 * @throws IOException
	 */
	private static void exportQuizToFile() throws IOException {
		String userName = user.getUser();
		StudentJDBCDAO.getInstance().exportQuiz(userName);
	}
}
