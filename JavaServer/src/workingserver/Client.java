package workingserver;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONObject;
import org.json.JSONException;

import workingserver.MessageUtil;

public class Client {

		//Client created to quickly test server
		public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
			//Creates socked to look for connection on specified IP and port.
			Socket socket = new Socket("localhost", 4444);
			//Inform of connection found and made.
			System.out.println("Client connected");
			//Output stream opened to send JSONObject
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			System.out.println("Ok"); 

      try {
        //Create JSONObject and add values.
        JSONObject obj = new JSONObject();
        obj.put("ip", "localhost");
        obj.put("score", "1");
        //Convert JSONObject to send to server.
        String send = obj.toString();

        //Send Object
        MessageUtil.writeRequest(os, send);
        System.out.println("Sending info ..." + send);
        //Receive Object back from server
        DataInputStream is = new DataInputStream(socket.getInputStream());
        String m = (String) MessageUtil.readResponse(is);
        //Convert string received back to JSONObject
        JSONObject returnMessage = new JSONObject(m);

        //Print JSONObject
        System.out.println("return Message is=" + returnMessage.toString());
			}
			catch (JSONException e) {
			  System.out.println("error parsing JSON");
			}

			//Close socket
			socket.close();
		}
}
