package servermultipleclients;

import java.io.IOException;

public class Main {

	//Class runs server
	public static void main(String args[]) {
		
		try {
			new Server().runServer();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
