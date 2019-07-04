package fr.epita.quiz.services.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import fr.epita.quiz.services.ConfigurationService;

public class StudentJDBCDAO {
	private static StudentJDBCDAO instance;
	
	
	private static final String SELECT_QUES = "SELECT * FROM QUESTIONS WHERE QUIZ = ? and DIFFICULTY = ?";
	private static final String INSERT_STUDENT_ANS = "INSERT INTO STUDENTANSWER(ANSWER, QUESTION, STUDENT) VALUES (?, ?, ?)";
	private static final String VALIDATE_ANS = "SELECT * FROM ANSWER WHERE QID = ?";
	private static final String INSERT_SCORE = "INSERT INTO SCORE (STUDENT, SCORE) VALUES (?,?)";
	private static final String VIEW_SCORE = "SELECT * FROM SCORE";
	private static final String VIEW_STUDENT_ANSWER = "SELECT * FROM STUDENTANSWER";
	private static final String STUDENT_ANSWER_QUERY= "SELECT * FROM STUDENTANSWER WHERE STUDENT = ?";
	private static final String STUDENT_SCORE_QUERY = "SELECT * FROM SCORE WHERE STUDENT = ?";
	private static final String SELECT_MCQCHOICE = "SELECT CHOICES FROM MCQCHOICE WHERE QID = ?";
	private StudentJDBCDAO() {
	
	}

	public static StudentJDBCDAO getInstance() {
		if (instance == null) {
			instance = new StudentJDBCDAO();
		}
		return instance;
	}
/**
 * Returns a Database connection for the StudentJDBC Class	
 * @return connection
 * @throws SQLException
 */
	private static Connection getConnection() throws SQLException {
		ConfigurationService conf = ConfigurationService.getInstance();
		String username = conf.getConfigurationValue("db.username", "");
		String password = conf.getConfigurationValue("db.password", "");
		String url = conf.getConfigurationValue("db.url", "");
		Connection connection = DriverManager.getConnection(url, username, password);
		return connection;
	}
	
	/**
	 * Student can take quiz and the relevant answer entered by the student is saved in the database and score is calculated accordingly
	 * @param title of the quiz
	 * @param userName of the Student
	 * @param difficulty level of quiz
	 * @param scanner to read input from console
	 * @throws SQLException
	 */
	public void takeQuiz(String title, String userName, String diff, Scanner scanner) throws SQLException {
		
		int count = 0;
		String dbAns = "";
		String ans = "";
		try (Connection connection = getConnection();
			PreparedStatement pstmt = connection.prepareStatement(SELECT_QUES);
			PreparedStatement pstmt1 = connection.prepareStatement(INSERT_STUDENT_ANS);
			PreparedStatement pstmt2 = connection.prepareStatement(VALIDATE_ANS);
			PreparedStatement pstmt3 = connection.prepareStatement(INSERT_SCORE);
			PreparedStatement pstmt4 = connection.prepareStatement(SELECT_MCQCHOICE);	) {
			pstmt.setString(1, title);
			pstmt.setString(2, diff);
			ResultSet rs = pstmt.executeQuery();
			
			
			while(rs.next()) {
				ans = "";
				String ques = rs.getString("Question");
				System.out.println(ques);
				System.out.println("");
				int id = QuestionJDBCDAO.getInstance().getQuestionID(ques);
				pstmt4.setInt(1, id);
				ResultSet rs1 = pstmt4.executeQuery();
				while(rs1.next()) {
					System.out.println(rs1.getString("CHOICES"));
					System.out.println("");
				}
				while(ans.equals("")) {
					
					System.out.println("Enter answer:");
					System.out.println("");
					ans = scanner.nextLine(); 
				}
				
				pstmt1.setString(1, ans);
				pstmt1.setString(2, ques);
				pstmt1.setString(3, userName);
				pstmt1.execute();
				pstmt2.setInt(1, id);
				ResultSet rs2 = pstmt2.executeQuery();
				if(rs2.next()) {
				 dbAns = rs2.getString("Text");}
				if(ans.equals(dbAns))
					count++;
			}
			
			pstmt3.setString(1, userName);
			pstmt3.setInt(2, count);
			pstmt3.execute();
			System.out.println("Your score is " +count);
			connection.close();
	}
		catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! Please restart the application !!");
		}		
		
}
	
	/**
	 * ADMIN can view the score of all the students who have taken the test
	 */
	public void getScore() {
				
		try (Connection connection = getConnection();
			PreparedStatement pstmt = connection.prepareStatement(VIEW_SCORE);) {
			ResultSet rs = pstmt.executeQuery();
			System.out.println("STUDENT" + "        " +"Score" );
			while(rs.next()) {
				String student = rs.getString("STUDENT");
				int score = rs.getInt("score");
				System.out.println(student +"              " +score);
			}
			connection.close();
		} catch (SQLException sqle) {
			System.out.println("Something unexpected happened !! Please restart the application !!");	
		}		
		
	}
	
	/**
	 * ADMIN can view all the answers entered by the students
	 */
	public void getStudentAnswer() {
		try (Connection connection = getConnection();
				PreparedStatement pstmt = connection.prepareStatement(VIEW_STUDENT_ANSWER);) {
				ResultSet rs = pstmt.executeQuery(); 
				while(rs.next()) {
					String student = rs.getString("STUDENT");
					String ques = rs.getString("QUESTION");
					String ans = rs.getString("ANSWER");
					System.out.println(student +"                    " +ques+"                                               "+ans);
				}
				connection.close();
			} catch (SQLException sqle) {
				System.out.println("Something unexpected happened !! Please restart the application !!");
			}		
	}
	/**
	 * Export the quiz details of the student to data.txt file in project path
	 * @param userName of the student
	 * @throws IOException
	 */
	
	public void exportQuiz(String userName) throws IOException {
		File file = initializeFile();
		 
		 try (Connection connection = getConnection();
				 
				 FileWriter fw = new FileWriter(file);
				 PreparedStatement pstmt1 = connection.prepareStatement(STUDENT_ANSWER_QUERY);
				 PreparedStatement pstmt2 = connection.prepareStatement(STUDENT_SCORE_QUERY);) {
						pstmt1.setString(1, userName );
						pstmt2.setString(1, userName );
					ResultSet rs = pstmt1.executeQuery();
					ResultSet rs1 = pstmt2.executeQuery();       
					while (rs.next()) {
			
						 while (rs.next()) {
							 
				                fw.append("\n Question: " +rs.getString("Question"));
				                fw.append('\n');
				                fw.append("\n Answer Entered: " +rs.getString("Answer"));
				                fw.append('\n');
				               }
						 while(rs1.next()) {
							 fw.append("\n" +" The score is "+rs1.getInt("SCORE"));
						 }
						 
				            fw.flush();
				            fw.close();
				            System.out.println("Text File is created successfully.");
					}					
				} 
	       catch (Exception e) {
	            System.out.println("Something unexpected happened!! Please contact administrator");
	        }		
	}
	
	/**
	 * Initialize the file for Quiz export
	 * @return the initialized file
	 * @throws IOException
	 */
	
	private static File initializeFile() throws IOException {
		File file = new File("data.txt");
		if (!file.exists()) {
			File parentFile = file.getAbsoluteFile().getParentFile();
			parentFile.mkdirs();
			file.createNewFile();
		}
		return file;
	}
	}

