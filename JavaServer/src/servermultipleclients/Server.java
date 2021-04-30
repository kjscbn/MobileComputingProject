package servermultipleclients;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static final int port = 4444;
	private ServerSocket ss = null;
	
	public void runServer() throws IOException, ClassNotFoundException {
		ss = new ServerSocket(port);
		
		while(true) {
			//Defines server, starts thread for each connection
			Socket socket = ss.accept();
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			DataInputStream is = new DataInputStream(socket.getInputStream());
			new ServerThread(socket, os, is).start();
		}
	}
}
