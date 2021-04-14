package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserChecking {
	public boolean doesUserExist(String username) {
		return FileUtils.checkIfFileExists(username);
	}

	public boolean isHumanCreated(Human human) {
		if (human.getUsername() == null) {
			return false;
		}
		if (human.getCurrentIP() == null) {
			return false;
		}
		return true;
	}
	
	public static Human loadHumanFromFile(String filename) {
		Human human2 = new Human(null, null, 0);
		int count = 0;
		
		try {
			File myObj = new File(filename);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				switch(count) {
				case 0:
					human2.setUsername(data);
					break;
				case 1:
					human2.setCurrentIP(data);
					break;
				case 2:
					human2.setScore(0);
					break;
				default:
					break;
				}
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return human2;
	}
	
	public int calculateScore(int currentScore, int timeConnectedToCurrentIP) {
		return currentScore * timeConnectedToCurrentIP;
	}
}
