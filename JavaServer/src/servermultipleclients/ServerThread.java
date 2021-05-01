package servermultipleclients;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

//Thread to run each client connection
public class ServerThread extends Thread {
	// Objects passed from server. Server declares them to ensure they are not null.
	private Socket socket;
	private DataOutputStream os;
	private DataInputStream is;
	
	//Index 0 is latitude, index 1 is longitude, index 2 is accuracy, index 3 is time
	static String oldLocationData[] = new String[4];
	static int locationValue = 1;
	static int oldScore = 0;

	// Constructor for Server Thread. Gets needed objects from Server
	public ServerThread(Socket socket, DataOutputStream os, DataInputStream is) {
		this.socket = socket;
		this.os = os;
		this.is = is;
	}

	// Run method for Server Thread. Contains code for everything after socket
	// accept.
	public void run() {
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

	// Logic for processing JSONObjects
	private void doSomething(JSONObject m) {
		String actType = JSONFunctions.getACTType(m);

		// Switch to process different requests
		switch (actType) {
		case "REQ":
			// Gets subject token and score from client message, calcs new score, and
			// creates response
			String subjectToken = JSONFunctions.getSubjectIP(m);
			String score = JSONFunctions.getScore(m);
			m = JSONFunctions.createReturnMessage(m, subjectToken, score);
			editScore(m);
			break;
		case "GEO":
			// Gets GEO data, and for now prints it out, sends original message back
			JSONObject data = m.getJSONObject("actData");

			String subjectIP = JSONFunctions.getSubjectIP(m);
			String filename = "(" + subjectIP + ").txt";

			String latitude = "";
			String longitude = "";
			String accuracy = "";
			latitude = JSONFunctions.getLatitude(data);
			longitude = JSONFunctions.getLongitude(data);
			accuracy = JSONFunctions.getAccuracy(data);

			System.out.println("LATITUDE: " + latitude);
			System.out.println("LONGITUDE: " + longitude);
			System.out.println("ACCURACY: " + accuracy);
			// Gets subject token and score from client message, calcs new score, and
			// creates response
			String subjectToken2 = JSONFunctions.getSubjectIP(m);
			String score2 = JSONFunctions.getScore(m);

			double speed = 0;
			
			//Checks if file for specific IP exists, if so calcs speed and stores data again, if not creates file and stores data.
			File f = new File(filename);
			if(f.exists()) {
				parseOldLocationData(filename);
				
				double oldLat = Double.valueOf(oldLocationData[0]);
				double oldLon = Double.valueOf(oldLocationData[1]);
				double curLat = Double.valueOf(latitude);
				double curLon = Double.valueOf(longitude);
				
				double distance = distance(curLat, curLon, oldLat, oldLon, 'M');
				double time = getTimeDifference(oldLocationData[3], getCurrentTimeStamp());
				speed = calcSpeed(distance, time);
				FileUtils.writeToFile(filename, latitude + "," + longitude + "," + accuracy+","+getCurrentTimeStamp()+score2);
				
				//Edits location value based on speed. Slower speed is more human like so higher location value.
				if(speed >= 0 && speed <= 33) {
					locationValue = 9;
				}else if(speed >= 34 && speed <= 66) {
					locationValue = 6;
				}else if(speed >= 67 && speed < 100) {
					locationValue = 3;
				}
				
				//Creates return message
				m = JSONFunctions.createReturnMessage(m, subjectToken2, score2);
				editScore(m);
				//Write data again with new score.
				String newScore = JSONFunctions.getScore(m);
				FileUtils.writeToFile(filename, latitude + "," + longitude + "," + accuracy+","+getCurrentTimeStamp()+newScore);
			}else {
				FileUtils.createFile(filename);
				FileUtils.writeToFile(filename, latitude + "," + longitude + "," + accuracy+","+getCurrentTimeStamp()+score2);
				
				//Creates return message
				m = JSONFunctions.createReturnMessage(m, subjectToken2, score2);
				editScore(m);
				//Write data again with new score
				String newScore = JSONFunctions.getScore(m);
				FileUtils.writeToFile(filename, latitude + "," + longitude + "," + accuracy+","+getCurrentTimeStamp()+newScore);
			}
			break;
		case "WEB":
			// Gets WEB data, and for now prints it out, sends original message back
			JSONObject webData = m.getJSONObject("actData");

			String url = "";
			String userAgent = "";

			url = JSONFunctions.getURL(webData);
			userAgent = JSONFunctions.getUserAgent(webData);

			System.out.println("URL: " + url);
			System.out.println("userAgent: " + userAgent);
			// Gets subject token and score from client message, calcs new score, and
			// creates response
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

	// Fixes JSONObject if score is missing. Adds one as a default score
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
			if (obj.isNull("score")) {
				// Add default score if it does not exist.
				System.out.println("Score does not exist. Adding default.");
				obj.put("score", new String("1"));
			}

			double temp = Double.valueOf((String) obj.get("score"));

			System.out.println("Current Score: " + temp);

			// Temp value just to check score formula.
			temp = temp + locationValue * ((100 - temp) / 200);

			System.out.println("New Score: " + temp);

			obj.remove("score");
			obj.put("score", temp);
		} catch (JSONException e) {
			System.out.println(e);
		}
	}
	
	double calcScore() {
		double score = 0;
		
		return score;
	}

	//Calculates distance between two points using longitude and latitude. Leave unit as M for miles.
	private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K') {
			dist = dist * 1.609344;
		} else if (unit == 'N') {
			dist = dist * 0.8684;
		}
		return (dist);
	}
	
	//Calcs speed
	private double calcSpeed(double distance, double time) {
		double speed = distance / time;
		return speed;
	}

	//Converts degrees to radians and the reverse
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
	
	//Parses old location data and saves in oldLocationData array
	public static void parseOldLocationData(String filename){
		int count = 0;
		try {
			File file = new File(filename);
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String data = scanner.nextLine();
				StringTokenizer st = new StringTokenizer(data, ",");
				while(st.hasMoreTokens()) {
					oldLocationData[count] = st.nextToken();
					count++;
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println(oldLocationData[0]);
		System.out.println(oldLocationData[1]);
		System.out.println(oldLocationData[2]);
		System.out.println(oldLocationData[3]);
	}
	
	//Gets current system time
	public String getCurrentTimeStamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
	}
	
	//Calculates difference between two times
	public static double getTimeDifference(String start_date, String end_date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
		long difference_in_hours = 0;
		try {
			Date d1 = sdf.parse(start_date);
			Date d2 = sdf.parse(end_date);
			
			long difference_In_Time = d2.getTime() - d1.getTime();
			
			difference_in_hours = (difference_In_Time / (1000 * 60)) % 24;
		}catch(ParseException e) {
			e.printStackTrace();
		}
		
		return difference_in_hours;
	}
}
