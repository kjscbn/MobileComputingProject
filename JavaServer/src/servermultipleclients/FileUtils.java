package servermultipleclients;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileUtils {

	public static void createFile(String filename) {
		try {
			File file = new File(filename);
			if(file.createNewFile()) {
				System.out.println("File created: " + filename);
			}else {
				System.out.println("File already exists.");
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(String filename, String textToWrite) {
		try {
			FileWriter myWriter = new FileWriter(filename);
			myWriter.write(textToWrite);
			myWriter.close();
			System.out.println("Successfully wrote to file.");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readFromFile(String filename) {
		try {
			File file = new File(filename);
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String data = scanner.nextLine();
				System.out.println(data);
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
}