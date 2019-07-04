package fr.epita.quiz.tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import fr.epita.quiz.datamodel.Quiz;

public class TestWriteDatamodelInstancesInFile {

	public static void main(String[] args) throws Exception {
		
		//given
		File file = initializeFile();
		Quiz quiz = new Quiz("Java Fundamentals - 2019 - Final exam");

		
		//when ?
	
		
		writeQuiz(quiz, file);
		
		//then ?
		Quiz readQuiz = readQuiz(file);
		if (!readQuiz.equals(quiz)) {
			throw new Exception("read value is different from written values");
		}else {
			System.out.println("test success!");
		}
		
	
	}

	private static Quiz readQuiz(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		
		String readLine = scanner.nextLine();
		Quiz readQuiz = new Quiz(readLine);
		scanner.close();
		return readQuiz;
	}

	private static void writeQuiz(Quiz quiz, File file) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		writer.println(quiz.getTitle());
		writer.flush();
		writer.close();
	}

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
