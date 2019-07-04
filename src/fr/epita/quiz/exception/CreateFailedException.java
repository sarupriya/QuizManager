package fr.epita.quiz.exception;

public class CreateFailedException extends DataAccessException{

	
	public CreateFailedException(Object beanThatWasNotCreated) {
		super(beanThatWasNotCreated, null);
	}
	
	public CreateFailedException(Object beanThatWasNotCreated, Exception initialCause) {
		super(beanThatWasNotCreated, initialCause);
	}

}
