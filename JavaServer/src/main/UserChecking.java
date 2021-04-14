package main;

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
}
