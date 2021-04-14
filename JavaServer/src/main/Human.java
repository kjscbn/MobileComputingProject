package main;

public class Human {
	private int score;
	private String username;
	private String currentIP;

	public Human(String username, String currentIP, int score) {
		this.username = username;
		this.currentIP = currentIP;
		this.score = score;
	}

	// Getters and Setters
	public int getScore() {
		return this.score;
	}

	public void setScore(final int score) {
		this.score = score;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getCurrentIP() {
		return this.currentIP;
	}

	public void setCurrentIP(final String currentIP) {
		this.currentIP = currentIP;
	}
}
