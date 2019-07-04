package fr.epita.quiz.services.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import fr.epita.quiz.services.ConfigEntry;
import fr.epita.quiz.services.ConfigurationService;

public class LoginValidate {
	
	private static LoginValidate instance;

	private LoginValidate() {
		
	}

	public static LoginValidate getInstance() {
		if (instance == null) {
			instance = new LoginValidate();
		}
		return instance;
	}
	/**
	 * Returns a Database connection for the LoginValidate class
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
	 * Validates the login Credentials
	 * @param userName of the user
	 * @param password of the user
	 * @return true if valid, else false
	 */
	
		public boolean[] validateLogin(String userName, String password)  {
			
		boolean[] result = new boolean[3];
			
		String query = ConfigurationService.getInstance()
				.getConfigurationValue(ConfigEntry.DB_QUERIES_VALIDATE_LOGIN,"");
		
		try (Connection connection = getConnection();

			PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
			String user = rs.getString("USERNAME");
			String pw = rs.getString("PASSWORD");
			
			if(userName.equals(user) && password.equals(pw))
			{
				result[0] = true;
				if(result[0])
				{
					result[1] = rs.getBoolean("isAdmin");
				}
				break;
			}
			else
				continue;
			}
			rs.close();
			connection.close();
		} catch (SQLException e) {
			//throw new SearchFailedException(quizCriterion);
			System.out.println(e.getStackTrace());
		}
		
		return result;
	}


}
