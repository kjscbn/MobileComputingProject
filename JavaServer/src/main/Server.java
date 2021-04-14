package main;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Server extends JFrame{

	private JButton startButton;
	private JButton stopButton;
	
	private static String userName;
	private static String ip;
	
	/*
	 * /Creates GUI, buttons, etc....
	 * Should be easy from here to add a console for information such as connections made
	 */
	public Server() {
		setLayout(new FlowLayout());
		
		theHandler startStop = new theHandler();
		
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
	
	//Server Code
	public class Servers{
		private Socket socket = null;
		private ServerSocket server = null;
		private DataInputStream in = null;
		
		public Servers(int port) {
			System.out.println("Server is on and listening!");
			try
	        {
	            server = new ServerSocket(port);
	            System.out.println("Server started");
	  
	            System.out.println("Waiting for a client ...");
	  
	            socket = server.accept();
	            System.out.println("Client accepted");
	  
	            // takes input from the client socket
	            in = new DataInputStream(
	                new BufferedInputStream(socket.getInputStream()));
	  
	            String line = "";
	  
	            // reads message from client until "Over" is sent
	            while (!line.equals("Over"))
	            {
	                try
	                {
	                    line = in.readUTF();
	                    //System.out.println(line);
	                    //Parses info passed from client. Could then be used for calculations. Could be modified to read from file.
	                    parseText(line);
	  
	                }
	                catch(IOException i)
	                {
	                    System.out.println(i);
	                }
	            }
	            System.out.println("Closing connection");
	  
	            // close connection
	            socket.close();
	            in.close();
	        }
	        catch(IOException i)
	        {
	            System.out.println(i);
	        }
		}
	}
	
	//Controls the starting and stopping of the server
	public class looper implements Runnable {
		//Bug when stopping server.
		Servers server = null;
		
		private AtomicBoolean keepRunning;
		
		public looper() {
			keepRunning = new AtomicBoolean(true);
		}
		
		public void stop() {
			keepRunning.set(false);
			server = null;
		}
		
		@Override
		public void run() {
			while(keepRunning.get()) {
				//This is where server code will go. Sockets, connections, all that stuff
				server = new Servers(5000);
			}
		}
	}
	
	/*
	 * Places the server on a seperate thread.
	 * Starts and stops it when buttons are pressed
	 */
	private class theHandler implements ActionListener{
		private looper Server;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == startButton) {
				if(Server == null) {
					Server = new looper();
					Thread t = new Thread(Server);
					t.start();
				}
			}else if(e.getSource() == stopButton){
				Server.stop();
				Server = null;
			}
		}
	}
	
	//Human Object, this would be information for a user of the app
	public static class Human {
		private int score;
		private String username;
		private String currentIP;
		
		public Human(String username, String currentIP, int score) {
			this.username = username;
			this.currentIP = currentIP;
			this.score = score;
		}
		
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
	
	//File Utilities, read, write, create, files etc....
	public static class FileUtils {
		public static void createFile(String filename) {
			try {
				File myObj = new File(filename);
				if(myObj.createNewFile()) {
					System.out.println("File created: " + myObj.getName());
				}else {
					System.out.println("File already exists");
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
		
		public static void readFile(String filename) {
			try {
				File myObj = new File(filename);
				Scanner myReader = new Scanner(myObj);
				while(myReader.hasNextLine()) {
					String data = myReader.nextLine();
					System.out.println(data);
				}
				myReader.close();
			}catch(FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		public static boolean checkIfFileExists(String filename) {
			File f = new File(filename);
			return (f.exists() && !f.isDirectory());
		}
	}
	
	public static class UserChecking {
		public boolean doesUserExist(String username) {
			return FileUtils.checkIfFileExists(username);
		}
		
		public boolean isHumanCreated(Human human) {
			if(human.username == null) {
				return false;
			}
			if(human.currentIP == null) {
				return false;
			}
			return true;
		}
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
		Server.FileUtils.writeToFile(getUsername() + ".txt", getUsername());
		Server.FileUtils.writeToFile(getUsername() + ".txt", getIP());
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
}
