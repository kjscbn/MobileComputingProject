package main;

import java.awt.FlowLayout;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Server extends JFrame{

	private static JButton startButton;
	private static JButton stopButton;
	
	private static String userName;
	private static String ip;
	
	/*
	 * /Creates GUI, buttons, etc....
	 * Should be easy from here to add a console for information such as connections made
	 */
	public Server() {
		setLayout(new FlowLayout());
		
		ServerHandler startStop = new ServerHandler();
		
		startButton = new JButton("Start Server");
		add(startButton);
		startButton.addActionListener(startStop);
		
		stopButton = new JButton("Stop Server");
		add(stopButton);
		stopButton.addActionListener(startStop);
		
	}
	
	//Main method
	public static void main(String args[]) {
		Server server = new Server();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.setSize(300, 300);
		server.setVisible(true);
	}
	
	//Parses text and sets it to user information in human object
	public static void parseText(String string) {
		int count = 0;
		StringTokenizer st = new StringTokenizer(string, ":");
		while(st.hasMoreTokens()) {
			switch(count) {
			case 0:
				setUsername(st.nextToken());
				break;
			case 1:
				setIP(st.nextToken());
				break;
			default:
				break;
			}
			count++;
		}
		System.out.println("Username:" + getUsername());
		System.out.println("IP: "+  getIP());
		//Testing Code
		FileUtils.writeToFile(getUsername() + ".txt", getUsername());
		FileUtils.writeToFile(getUsername() + ".txt", getIP());
	}
	
	//Getters and setters for user information
	public static String getUsername() {
		return userName;
	}
	
	public static void setUsername(final String string) {
		userName = string;
	}
	
	public static String getIP() {
		return ip;
	}
	
	public static void setIP(final String string) {
		ip = string;
	}
	
	public static JButton getStartButton() {
		return startButton;
	}
	
	public static JButton getStopButton() {
		return stopButton;
	}
}
