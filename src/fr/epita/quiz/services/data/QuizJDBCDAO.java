package fr.epita.quiz.services.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import fr.epita.quiz.datamodel.Quiz;
import fr.epita.quiz.exception.CreateFailedException;
import fr.epita.quiz.exception.DataAccessException;
import fr.epita.quiz.services.ConfigurationService;

public class QuizJDBCDAO {

	
	private static QuizJDBCDAO instance;
	
	private static final String INSERTQUIZ_QUERY = "INSERT into QUIZ (name) values(?)";
	private static final String UPDATEQUIZ_QUERY = "UPDATE QUIZ SET NAME=? WHERE NAME = ?";
	private static final String UPDATEQUES_QUERY = "UPDATE QUESTIONS SET QUIZ=? WHERE QUIZ = ?";
	private static final String DELETEQUIZ_QUERY = "DELETE FROM QUIZ  WHERE NAME = ?";
	private static final String DELETEQUES_QUERY = "DELETE FROM QUESTIONS WHERE QUIZ = ?";
	private static final String SELECT_QUIZ = "SELECT * FROM QUIZ";

	private QuizJDBCDAO() {
	
	}

	public static QuizJDBCDAO getInstance() {
		if (instance == null) {
			instance = new QuizJDBCDAO();
		}
		return instance;
	}
	/** 
	 * Returns a Database connection for the QuestionJDBCDAO class
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
	 * Create a quiz in the database
	 * @param quiz Object
	 * @throws CreateFailedException
	 */
	public void create(Quiz quiz) throws CreateFailedException {
		try (Connection connection = getConnection();
				PreparedStatement pstmt = connection.prepareStatement(INSERTQUIZ_QUERY);) {
			pstmt.setString(1, quiz.getTitle());
			pstmt.execute();
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! Please restart the application !!");
		
		}
	}
	/**
	 * Updates the quiz title in the quiz table and also in the question table related to the quiz
	 * @param quiz object
	 * @param scanner to read input from console
	 * @throws DataAccessException
	 */
	
	public void update(Quiz quiz, Scanner scan) throws DataAccessException   {
		
		String newValue =  "";
		
		while(newValue.equals("")) {
			newValue =  "";
			System.out.println("Enter the quiz name to be updated !!");
			newValue = scan.nextLine();
		}
		
		try (Connection connection = getConnection();
			PreparedStatement pstmt = connection.prepareStatement(UPDATEQUIZ_QUERY);
			PreparedStatement pstmt1 = connection.prepareStatement(UPDATEQUES_QUERY);) {
			pstmt.setString(1, newValue);
			pstmt.setString(2, quiz.getTitle());
			pstmt1.setString(1, newValue);
			pstmt1.setString(2, quiz.getTitle());
			pstmt.execute();
			pstmt1.execute();
			System.out.println("Quiz updated Successfully !!");
			
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! Please restart the application !!");
		}
		
	}
	/**
	 * Deletes a quiz in the database and also the questions associated with the quiz
	 * @param topic of the quiz to be deleted
	 * @throws DataAccessException
	 */
	public void delete(String topic) throws DataAccessException {
		try (Connection connection = getConnection();
				PreparedStatement pstmt = connection.prepareStatement(DELETEQUIZ_QUERY);
				PreparedStatement pstmt1 = connection.prepareStatement(DELETEQUES_QUERY);){
				pstmt.setString(1, topic);
				pstmt.execute();
				pstmt1.setString(1, topic);
				pstmt1.execute();
				System.out.println("The quiz and the related questions were deleted successfully !!");
				connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! Please restart the application !!");
		}
	}
	
	/**
	 * Displays the list of quiz available in the database
	 * @return array of list of quiz
	 * @throws DataAccessException
	 */
	public ArrayList<String> getQuiz() throws DataAccessException {
		ArrayList<String> value =  new ArrayList<String>(); 
				
		try (Connection connection = getConnection();
			PreparedStatement pstmt = connection.prepareStatement(SELECT_QUIZ);) {
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				String topic = rs.getString("NAME");
				value.add(topic);
				System.out.println(topic);
				System.out.println("");
			}
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! Please restart the application !!");
		}		
		return value;
	}
	
	
	
}
