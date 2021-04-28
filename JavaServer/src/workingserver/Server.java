package workingserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;

public class Server {
	// Port number and Server Socket
		public static final int port = 4444;
		private ServerSocket ss = null;

		// Method to run server
		public void runServer() throws IOException, ClassNotFoundException {
			// Declare server socket with specified port number.
			ss = new ServerSocket(port);
			System.out.println("Waiting for connections ...");
			// Socket accepts connection
			Socket socket = ss.accept();
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());

			// JSONObject is read as a string
			String m = (String) is.readObject();
			// Converted back to JSONObject
			JSONObject jsonObject = new JSONObject(m);
			// Method to check and edit JSONObject
			doSomething(jsonObject);
			// Convert back to string to send back to client
			String sendBack = jsonObject.toString();
			// Sends string back to client
			os.writeObject(sendBack);
			// Close socket
			socket.close();
		}

		// Method that runs after connection is made and string is received.
		private void doSomething(JSONObject m) {
			if (checkJSONObject(m) == false) {
				fixJSONObject(m);
			}

			editScore(m);
		}

		// Main method
		public static void main(String[] args) throws ClassNotFoundException, IOException {
			new Server().runServer();
		}

		// Checks JSONObject to make sure needed fields are there.
		public boolean checkJSONObject(JSONObject obj) {
			boolean valid = true;

			if (obj.has("ip") == false) {
				valid = false;
			}

			if (obj.has("score") == false) {
				valid = false;
			}

			return valid;
		}

		// Adds missing values to JSONObject if need be. Default values can be
		// specified.
		public void fixJSONObject(JSONObject obj) {
			if (obj.has("ip") == false) {
				obj.put("ip", "");
			}
			if (obj.has("score") == false) {
				obj.put("score", new Integer(0));
			}
		}

		/*
		 * Edit score in JSONObject. Works by storing in temp value. Removing field from
		 * JSONObject, edit value, then add it back.
		 */
		public void editScore(JSONObject obj) {
			int temp = Integer.valueOf((String) obj.get("score"));
			temp = temp + 1;
			obj.remove("score");
			obj.put("score", temp);
		}

		/*
		 * Edit score in JSONObject. Works by storing in temp value. Removing field from
		 * JSONObject, edit value, then add it back.
		 */
		public void editIP(JSONObject obj, String newIP) {
			String temp = (String) obj.get("ip");
			temp = newIP;
			obj.remove("ip");
			obj.put("ip", temp);
		}
}
