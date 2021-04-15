package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class UserChecking {
	public static boolean doesUserExist(String username) {
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

	// Creates file for new user.
	public static void createNewFile(String username) {
		if (doesUserExist(username) == false) {
			FileUtils.createFile(username + ".txt");
		} else {
			System.out.println("File already exists.");
		}
	}

	// Fills user data in file. Can also be used to update changed data simply by
	// calling again
	public static void populateUserData(String username, Human human) {
		FileUtils.writeToFile(username + ".txt", "Username:" + human.getUsername());
		FileUtils.writeToFile(username + ".txt", "CurrentIP: " + human.getCurrentIP());
		FileUtils.writeToFile(username + ".txt", "Score:" + String.valueOf(human.getScore()));
	}

	public static Human loadHumanFromFile(String filename) {
		Human human2 = new Human(null, null, 0);
		int count = 0;

		try {
			File myObj = new File(filename);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				switch (count) {
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

	public static boolean validIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	// Formula for calculating score, subject to change
	public int calculateScore(int currentScore, int timeConnectedToCurrentIP) {
		return currentScore * timeConnectedToCurrentIP;
	}
}
