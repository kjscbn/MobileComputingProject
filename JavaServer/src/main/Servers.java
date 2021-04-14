package main;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servers {
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
                    //parseText(line);
  
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
		//Catches exceptions
        catch(IOException i)
        {
            System.out.println(i);
        }
	}
}
