package fr.epita.quiz.services.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import fr.epita.quiz.datamodel.Answer;
import fr.epita.quiz.datamodel.Question;
import fr.epita.quiz.datamodel.Quiz;
import fr.epita.quiz.exception.CreateFailedException;
import fr.epita.quiz.services.ConfigurationService;

public class AnswerJDBCDAO {

	private static AnswerJDBCDAO instance;
	
	
	private static final String INSERT_ANSWER_QUERY = "INSERT INTO ANSWER(QID, TEXT) VALUES(?, ?)";
	private static final String INSERT_MCQ_CHOICES = "INSERT INTO MCQCHOICE(CHOICES, QID) VALUES(?, ?)";

	private AnswerJDBCDAO() {
	
	}

	public static AnswerJDBCDAO getInstance() {
		if (instance == null) {
			instance = new AnswerJDBCDAO();
		}
		return instance;
	}
	/**
	 * Returns a Database connection for the AnswerJDBCDAO Class
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
	 * Creates answer with respect to question
	 * @param quiz object
	 * @param question object
	 * @param answer object
	 * @throws CreateFailedException
	 * @throws SQLException
	 */
	public void createAnswer(Quiz quiz, Question ques, Answer ans) throws CreateFailedException, SQLException {
		
		try (Connection connection = getConnection();
		PreparedStatement pstmt = connection.prepareStatement(INSERT_ANSWER_QUERY);) {
		pstmt.setInt(1, ques.getID());
		pstmt.setString(2, ans.getText());
		
		pstmt.execute();
		connection.close();
	} catch (SQLException sqle) {
		System.out.println("Something unexpected happened !! Please restart the application !!");	
	}
}
	/**
	 * Creates choices for the MCQ type questions
	 * @param question object
	 * @param array of choices
	 * @throws CreateFailedException
	 * @throws SQLException
	 */
	public void createMcqChoices( Question ques, String[] choice ) throws CreateFailedException, SQLException {
		
		
		try (Connection connection = getConnection();
		PreparedStatement pstmt = connection.prepareStatement(INSERT_MCQ_CHOICES);) {
			for(int i=0; i<3; i++) {
				pstmt.setString(1, choice[i]);
				pstmt.setInt(2, ques.getID());
				pstmt.execute();
			}
			connection.close();
	} catch (SQLException sqle) {
		System.out.println("Something unexpected happened !! Please restart the application !!");	
	} 
	}	
}
