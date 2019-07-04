package fr.epita.quiz.services.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import fr.epita.quiz.datamodel.Question;
import fr.epita.quiz.exception.CreateFailedException;
import fr.epita.quiz.services.ConfigurationService;

public class QuestionJDBCDAO {
	
	private static QuestionJDBCDAO instance;
	
	private static final String INSERTQUES_QUERY = "INSERT INTO QUESTIONS (Question, Quiz,Difficulty) values(?, ?, ?)";
	private static final String UPDATEQUES_QUERY = "UPDATE QUESTIONS SET QUESTION=? WHERE QID = ?";
	private static final String DELETEQUES_QUERY = "DELETE FROM QUESTIONS WHERE QID = ?";
	private static final String DELETEQUESCHOICES_QUERY = "DELETE FROM MCQCHOICE WHERE QID = ?";
	private static final String SELECT_QUES = "SELECT * FROM QUESTIONS WHERE QUIZ = ? AND DIFFICULTY = ?";
	private static final String SELECT_QUES_ID = "SELECT QID FROM QUESTIONS WHERE QUESTION = ?";
	private static final String SELECT_MCQCHOICE = "SELECT CHOICES FROM MCQCHOICE WHERE QID = ?";
	private static final String UPDATEANSWER_QUERY = "UPDATE ANSWER SET TEXT=? WHERE QID = ?";
	private static final String INSERT_MCQ_CHOICES = "INSERT INTO MCQCHOICE(CHOICES, QID) VALUES(?, ?)";

	private QuestionJDBCDAO() {
	}

	public static QuestionJDBCDAO getInstance() {
		if (instance == null) {
			instance = new QuestionJDBCDAO();
		}
		return instance;
	}
	/**
	 * Returns a Database connection for the QuestionJDBCDAO Class
	 * @return connection
	 * @throws SQLException
	 */
	
	private Connection getConnection() throws SQLException {
		ConfigurationService conf = ConfigurationService.getInstance();
		String username = conf.getConfigurationValue("db.username", "");
		String password = conf.getConfigurationValue("db.password", "");
		String url = conf.getConfigurationValue("db.url", "");
		Connection connection = DriverManager.getConnection(url, username, password);
		return connection;
	}
	/**
	 * Creates questions with respect to the quiz
	 * @param question object
	 * @param difficulty level of the question
	 * @return auto generated id of the quiz
	 * @throws CreateFailedException
	 * @throws SQLException
	 */
	public int createQuestion(Question ques, String difficulty) throws CreateFailedException, SQLException {
			int idColVar = 0;
			try (Connection connection = getConnection();
					
			PreparedStatement pstmt1 = connection.prepareStatement(INSERTQUES_QUERY, new String[]{"QID"});) {
			pstmt1.setString(1, ques.getquestion());
			pstmt1.setString(2, ques.getTopics());
			pstmt1.setString(3, difficulty);
			pstmt1.executeUpdate();
			ResultSet rs = pstmt1.getGeneratedKeys();
			       
			while (rs.next()) {
				 idColVar = rs.getInt(1); 
				ques.setID(idColVar);
			}
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! PLease restart the application !!");
		
		}
			return idColVar;
	}
	/**
	 * Updates the question with respect to the quiz
	 * @param question object
	 * @param scanner to read input from console
	 */
	public void update(Question ques, Scanner scan)   {
		String newValue = "";
		String newAns = "";
		String[] choice = new String[3];
		int id = getQuestionID(ques.getquestion());
		
		while(newValue.equals("")) {
			newValue = "";
			System.out.println("Enter the updated question below!!");
			 newValue = scan.nextLine();
		}
		
		try (Connection connection = getConnection();
			PreparedStatement pstmt = connection.prepareStatement(UPDATEQUES_QUERY);
			PreparedStatement pstmt1 = connection.prepareStatement(UPDATEANSWER_QUERY);
			PreparedStatement pstmt2 = connection.prepareStatement(SELECT_MCQCHOICE);
			PreparedStatement pstmt3 = connection.prepareStatement(DELETEQUESCHOICES_QUERY);
			PreparedStatement pstmt4 = connection.prepareStatement(INSERT_MCQ_CHOICES);) {
			pstmt.setString(1, newValue);
			pstmt.setInt(2, id);
			pstmt.execute();
			System.out.println("Question updated Successfully !!");
			
			
			while(newAns.equals("")) {
				newAns = "";
				System.out.println("Enter the answer for the updated question !!");
				newAns = scan.nextLine();
			}
			
			pstmt1.setString(1, newAns);
			pstmt1.setInt(2, id);
			pstmt1.execute();
			System.out.println("Answer updated Successfully !!");
			
			pstmt2.setInt(1, id);
			ResultSet rs = pstmt2.executeQuery();
			pstmt3.setInt(1, id);
			if(rs.next()) {
				pstmt3.execute();
			System.out.println("Enter Updated Choices");
			System.out.println("Enter Choice 1");
			choice[0] = scan.nextLine();
			while(choice[0].equals("")) {
				System.out.println("Enter Choice 1");
				choice[0] = scan.nextLine();
			}
			System.out.println("Enter Choice 2");
			choice[1] = scan.nextLine();
			while(choice[1].equals("")) {
			System.out.println("Enter Choice 2");
			choice[1] = scan.nextLine();
			}
			
			System.out.println("Enter Choice 3");
			choice[2] = scan.nextLine();
			while(choice[2].equals("")) {
			System.out.println("Enter Choice 3");
			choice[2] = scan.nextLine();
			}
			
			List<String> list = Arrays.asList(choice);
			while(!list.contains(newAns)) {
			System.out.println("Please enter the given answer as one of the option");
			choice[2]= scan.nextLine();
			}
			
			for(int i=0; i<3; i++) {
				pstmt4.setString(1, choice[i]);
				pstmt4.setInt(2, id);
				pstmt4.execute();
			}
			}
			
			System.out.println("Quiz Updated Successfully !!");
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! PLease restart the application !!");
		}	
	}
	/**
	 * Deletes the question based on quiz
	 * @param question to be deleted
	 */
	public void delete(String ques) {
		int id = getQuestionID(ques);
		try (Connection connection = getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETEQUES_QUERY);
				PreparedStatement pstmt1 = connection.prepareStatement(DELETEQUESCHOICES_QUERY);){
				pstmt.setInt(1, id);
				pstmt.execute();
				pstmt1.setInt(1, id);
				pstmt1.execute();
				System.out.println("The question was deleted successfully !!");
				connection.close();
		}
		catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! PLease restart the application !!");
		}
	}
	
	/**
	 * Displays the question available with respect to Quiz
	 * @param title of the quiz
	 * @param difficulty level of question
	 * @return a list of questions
	 */
	public ArrayList<String> getQuestion(String title, String diff) {
		ArrayList<String> value =  new ArrayList<String>(); 
				
		try (Connection connection = getConnection();
			PreparedStatement pstmt = connection.prepareStatement(SELECT_QUES);) {
			pstmt.setString(1, title);
			pstmt.setString(2, diff);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String ques = rs.getString("Question");
				value.add(ques);
				System.out.println(ques);
				System.out.println("");
			}
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! PLease restart the application !!");
				}		
		return value;
	}
	/**
	 * Gets the id of the question
	 * @param question
	 * @return id of the question
	 */
	public int getQuestionID(String ques) {
		
		int id = 0;
		try (Connection connection = getConnection();
				
		PreparedStatement pstmt1 = connection.prepareStatement(SELECT_QUES_ID);) {
			pstmt1.setString(1, ques);
		ResultSet rs = pstmt1.executeQuery();
		       
		while (rs.next()) {
			 id = rs.getInt("QID");
		}
		connection.close();
		
	} catch (SQLException sqle) {
		System.out.println("Something unexpected happened !! PLease restart the application !!");
	
	}
		return id;
		
	}


}
