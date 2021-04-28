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

	// Client created to quickly test server
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		// Creates socked to look for connection on specified IP and port.
		Socket socket = new Socket("localhost", 4447);
		// Inform of connection found and made.
		System.out.println("Client connected");
		// Output stream opened to send JSONObject
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());
		System.out.println("Ok");

		try {
			// Create JSONObject and add values, data used for testing.
			JSONObject obj = new JSONObject();
			obj.put("subjectToken", "karljake");
			obj.put("subjectIP", "192.168.1.1");
			obj.put("actType", "WEB");
			//obj.put("score", "9");

			JSONObject data = new JSONObject();

			//data.put("latitude", new String((String) "38.630280"));
			//data.put("longitude", new String((String) "-90.200310"));
			//data.put("accuracy", new String((String) "17.32"));
			
			data.put("url", "https://www.umsl.edu");
			data.put("userAgent", "Mozilla/5.0 (Linux; Android 7.0");

			obj.put("actData", data);
			// Convert JSONObject to send to server.
			String send = obj.toString();

			// Send Object
			MessageUtil.writeRequest(os, send);
			System.out.println("Sending info ..." + send);
			// Receive Object back from server
			DataInputStream is = new DataInputStream(socket.getInputStream());
			String m = (String) MessageUtil.readResponse(is);
			// Convert string received back to JSONObject
			JSONObject returnMessage = new JSONObject(m);

			// Print JSONObject
			System.out.println("return Message is=" + returnMessage.toString());
		} catch (JSONException e) {
			System.out.println("Error parsing JSON");
		}

		// Close socket
		socket.close();
	}
}

