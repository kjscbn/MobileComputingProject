package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileUtils {
	public static void createFile(String filename) {
		try {
			File myObj = new File(filename);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeToFile(String filename, String textToWrite) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write(textToWrite);
			myWriter.close();
			System.out.println("Successfully wrote to file.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readFile(String filename) {
		try {
			File myObj = new File(filename);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				System.out.println(data);
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean checkIfFileExists(String filename) {
		File f = new File(filename);
		return (f.exists() && !f.isDirectory());
	}
}
