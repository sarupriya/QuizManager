package fr.epita.quiz.tests;

import fr.epita.quiz.datamodel.Quiz;
import fr.epita.quiz.services.ConfigurationService;
import fr.epita.quiz.services.data.QuizJDBCDAO;

public class TestQuizJDBCDAO {
	
	public static void main(String[] args) throws Exception {
		//Given
		ConfigurationService conf = ConfigurationService.getInstance();
		boolean confInit = conf.isInit();
		if (!confInit) {
			System.out.println("problem while initializing the conf");
			return;
		}
		
		QuizJDBCDAO dao = QuizJDBCDAO.getInstance();
		
		//when
		dao.create(new Quiz("Why is Java so cool?"));
		
		//then
//		List<Quiz> list = dao.search(new Quiz("Java"));
//		if (list.isEmpty()) {
//			throw new Exception("the list was empty");
//		}
//			System.out.println(list);
//		
	}

}
