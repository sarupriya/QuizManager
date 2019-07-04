package fr.epita.quiz.datamodel;

public enum Difficulty {
	VERY_EASY(0),
	EASY(1),
	MEDIUM(2),
	HARD(3),
	VERY_HARD(4),
	EXTREMELY_HARD(5),
	
	;
	
	private Integer numericDifficulty;
	
	
	private Difficulty(Integer difficulty) {
		this.numericDifficulty = difficulty;
	}
	
	public Integer getDifficulty() {
		return this.numericDifficulty;
	}

}
