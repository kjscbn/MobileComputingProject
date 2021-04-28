package workingserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;

public class Client {

		//Client created to quickly test server
		public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
			//Creates socked to look for connection on specified IP and port.
			Socket socket = new Socket("localhost", 4444);
			//Inform of connection found and made.
			System.out.println("Client connected");
			//Output stream opened to send JSONObject
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Ok"); 

			//Create JSONObject and add values.
			JSONObject obj = new JSONObject();
			obj.put("ip", "localhost");
			obj.put("score", "1");
			//Convert JSONObject to send to server.
			String send = obj.toString();

			//Send Object
			os.writeObject(send);
			System.out.println("Sending info ...");

			//Receive Object back from server
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			String m = (String) is.readObject();
			//Convert string received back to JSONObject
			JSONObject returnMessage = new JSONObject(m);
			//Print JSONObject
			System.out.println("return Message is=" + returnMessage.toString());
			//Close socket
			socket.close();
		}
}
