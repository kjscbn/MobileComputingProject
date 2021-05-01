package workingserver;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONObject;
import org.json.JSONException;

import workingserver.MessageUtil;

public class Server {
	// Port number and Server Socket
	public static final int port = 4444;
	private ServerSocket ss = null;

	// Method to run server
	public void runServer() throws IOException, ClassNotFoundException {
		// Declare server socket with specified port number.
		ss = new ServerSocket(port);

		while (true) {
			System.out.println("Waiting for connections ...");

			// Socket accepts connection
			// TODO spawn a new thread to handle each connection
			Socket socket = ss.accept();
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			DataInputStream is = new DataInputStream(socket.getInputStream());

			// JSONObject is read as a string
			String m = MessageUtil.readRequest(is);
			System.out.println("message received: " + m);
			// Converted back to JSONObject
			try {
				JSONObject jsonObject = new JSONObject(m);
				fixJSONObject(jsonObject);
				// Method to check and edit JSONObject
				doSomething(jsonObject);
				// Convert back to string to send back to client
				String sendBack = jsonObject.toString();
				// Sends string back to client
				MessageUtil.writeResponse(os, sendBack);
				System.out.println("message sent: " + sendBack);
				// Close socket
			} catch (JSONException e) {
				System.out.println("Error parsing JSON");
			}

			socket.close();
		}
	}

	// Method that runs after connection is made and string is received.
	private void doSomething(JSONObject m) {
		/*
		 * if (checkJSONObject(m) == false) { fixJSONObject(m); }
		 */
		String actType = JSONFunctions.getACTType(m);

		//Switch to process different requests
		switch (actType) {
		case "REQ":
			//Gets subject token and score from client message, calcs new score, and creates response
			String subjectToken = JSONFunctions.getSubjectIP(m);
			String score = JSONFunctions.getScore(m);
			m = JSONFunctions.createReturnMessage(m, subjectToken, score);
			editScore(m);
			break;
		case "GEO":
			//Gets GEO data, and for now prints it out, sends original message back
			JSONObject data = m.getJSONObject("actData");
			
			String latitude = "";
			String longitude = "";
			String accuracy = "";
			latitude = JSONFunctions.getLatitude(data);
			longitude = JSONFunctions.getLongitude(data);
			accuracy = JSONFunctions.getAccuracy(data);
			
			
			
			System.out.println("LATITUDE: " + latitude);
			System.out.println("LONGITUDE: " + longitude);
			System.out.println("ACCURACY: " + accuracy);
			//Gets subject token and score from client message, calcs new score, and creates response
			String subjectToken2 = JSONFunctions.getSubjectIP(m);
			String score2 = JSONFunctions.getScore(m);
			m = JSONFunctions.createReturnMessage(m, subjectToken2, score2);
			editScore(m);
			break;
		case "WEB":
			//Gets WEB data, and for now prints it out, sends original message back
			JSONObject webData = m.getJSONObject("actData");
			
			String url = "";
			String userAgent = "";
			
			url = JSONFunctions.getURL(webData);
			userAgent = JSONFunctions.getUserAgent(webData);
			
			System.out.println("URL: " + url);
			System.out.println("userAgent: " + userAgent);
			//Gets subject token and score from client message, calcs new score, and creates response
			String subjectToken3 = JSONFunctions.getSubjectIP(m);
			String score3 = JSONFunctions.getScore(m);
			m = JSONFunctions.createReturnMessage(m, subjectToken3, score3);
			editScore(m);
			break;
		default:
			break;
		}

		// editScore(m);
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
		try {
			if (obj.has("score") == false) {
				obj.put("score", new String("1"));
			}
		} catch (JSONException e) {
			System.out.println(e);
		}
	}

	/*
	 * Edit score in JSONObject. Works by storing in temp value. Removing field from
	 * JSONObject, edit value, then add it back.
	 */
	public void editScore(JSONObject obj) {
		try {
			if(obj.isNull("score")) {
				//Add default score if it does not exist.
				System.out.println("Score does not exist. Adding default.");
				obj.put("score", new String("1"));
			}
			
			double temp = Double.valueOf((String) obj.get("score"));

			System.out.println("Current Score: " + temp);
			
			//Temp value just to check score formula.
			double locationValue = 5;
			temp = temp + locationValue*((100 - temp) / 200);
			
			System.out.println("New Score: " + temp);
			
			obj.remove("score");
			obj.put("score", temp);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}

	/*
	 * Edit score in JSONObject. Works by storing in temp value. Removing field from
	 * JSONObject, edit value, then add it back.
	 */
	public void editIP(JSONObject obj, String newIP) {
		try {
			String temp = (String) obj.get("ip");
			temp = newIP;
			obj.remove("ip");
			obj.put("ip", temp);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}
}
