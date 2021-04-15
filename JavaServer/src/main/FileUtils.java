package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

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
	
	public static void createIPDatabaseFile() {
		if(checkIfFileExists("ipdatabase.txt") == false) {
			createFile("ipdatabase.txt");
		}
	}
	
	public static int checkScoreForIP(String ip) {
		int score = 0;
		
		try {
			File myObj = new File("ipdatabase.txt");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				if(data.equals(ip)) {
					StringTokenizer st = new StringTokenizer(":");
					int count = 1;
					while(st.hasMoreTokens()) {
						//We wait for count to be equal to 1, for token number 2,
						//Because data will be formatted as ip:score
						if(count == 1) {
							score = Integer.valueOf(st.nextToken());
						}
						count++;
					}
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return score;
	}
	
	public static void writeScoreToFile(String filename, String ip, int score) {
		writeToFile(filename, ip + String.valueOf(score));
	}
}
