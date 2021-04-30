package servermultipleclients;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

//Thread to run each client connection
public class ServerThread extends Thread {
	//Objects passed from server. Server declares them to ensure they are not null.
	private Socket socket;
	private DataOutputStream os;
	private DataInputStream is;

	//Constructor for Server Thread. Gets needed objects from Server
	public ServerThread(Socket socket, DataOutputStream os, DataInputStream is) {
		this.socket = socket;
		this.os = os;
		this.is = is;
	}

	//Run method for Server Thread. Contains code for everything after socket accept.
	public void run(){
		// JSONObject is read as a string
		String m = "";
		try {
			m = MessageUtil.readRequest(is);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Logic for processing JSONObjects
	private void doSomething(JSONObject m) {
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
	//Fixes JSONObject if score is missing. Adds one as a default score
	public void fixJSONObject(JSONObject obj) {
		try {
			if (obj.has("score") == false) {
				obj.put("score", new String("1"));
			}
		} catch (JSONException e) {
			System.out.println(e);
		}
	}
	
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
	
}
