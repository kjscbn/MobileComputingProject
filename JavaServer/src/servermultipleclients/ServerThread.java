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

  //Index 0 is score, 1 is latitude, 2 is longitude, index 3 is accuracy, index 4 is time
  static String oldLocationData[] = new String[5];

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
      System.out.println("Error parsing JSON" + e.toString());
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
    String score = JSONFunctions.getScore(m);

    String actType = JSONFunctions.getACTType(m);
    String subjectToken = JSONFunctions.getSubjectToken(m);
    String filename = "SUBJECT_DATA_" + subjectToken + ".txt";

    double oldLat = 0;
    double oldLon = 0;
    String oldTime = "";
    boolean hasPreviousLocation = false;

    // if there is a file, read the score and last location (if any)
    // for now, we assume the only data being stored is location data
    // in a future version we might could store json strings so we can
    // more easily pluck out specific data values
    File f = new File(filename);

    if(f.exists()) {
      parseOldLocationData(filename);

      // if there was a score, update the current object
      score = oldLocationData[0];
      m.put("score", score);

      if(oldLocationData.length >= 5) {
        oldLat = Double.valueOf(oldLocationData[1]);
        oldLon = Double.valueOf(oldLocationData[2]);
        oldTime = oldLocationData[4];

        hasPreviousLocation = true;
      }
    }

    // Switch to process different requests
    switch (actType) {
      case "REQ":
        // No special processing for score request with new activity data
        break;
      case "GEO":
        // Location data - we will store the new location and calculate a new
        // score if there is a previous location
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
        // Gets subject token and score from client message, calcs new score, and
        // creates response

        double speed = 0;
        int locationValue = 1;

        // if there is previous location data calc speed and update score
        if(hasPreviousLocation) {
          double curLat = Double.valueOf(latitude);
          double curLon = Double.valueOf(longitude);

          double distance = distance(curLat, curLon, oldLat, oldLon, 'M');
          double time = getTimeDifference(oldTime, getCurrentTimeStamp());

          System.out.println("dist: " + distance + ", time:" + time);

          // treat tiny distance as no movement (precision error)
          if(distance > 0.0001) {
            speed = calcSpeed(distance, time);

            //Edits location value based on speed. Slower speed is more human like so higher location value.
            if(speed >= 0 && speed <= 33) {
              locationValue = 9;
            }else if(speed >= 34 && speed <= 66) {
              locationValue = 6;
            }else if(speed >= 67 && speed < 100) {
              locationValue = 3;
            }else if(speed > 1000) {  // > 1000 means we are being gamed
              locationValue = -10;
            }
          }
        }
        else { // no previous data - create a file to store the new data
          FileUtils.createFile(filename);
        }

        editScore(m, locationValue);
        score = JSONFunctions.getScore(m);

        // write score and location data
        FileUtils.writeToFile(filename, score + "," + latitude + "," + longitude + "," + accuracy + "," + getCurrentTimeStamp());
        break;
      case "WEB":
        // Gets WEB data, and for now prints it out, sends score back
        JSONObject webData = m.getJSONObject("actData");

        String url = "";
        String userAgent = "";

        url = JSONFunctions.getURL(webData);
        userAgent = JSONFunctions.getUserAgent(webData);

        System.out.println("URL: " + url);
        System.out.println("userAgent: " + userAgent);
        // Gets token and score from client message, calcs new score, and
        // creates response
        break;
      default:
        break;
    }

    m = JSONFunctions.createReturnMessage(m, subjectToken, score);
    System.out.println("returning: " + m);
  }

  // Fixes JSONObject if score is missing. Adds one as a default score
  public void fixJSONObject(JSONObject obj) {
    try {
      if (obj.has("score") == false) {
        obj.put("score", new String("0"));
      }
    } catch (JSONException e) {
      System.out.println(e);
    }
  }

  public void editScore(JSONObject obj, int value) {
    try {
      if (obj.isNull("score")) {
        // Add default score if it does not exist.
        System.out.println("Score does not exist. Adding default.");
        obj.put("score", new String("0"));
      }

      double temp = Double.valueOf((String) obj.get("score"));

      System.out.println("Current Score: " + temp);

      // Temp value just to check score formula.
      temp = temp + value * ((100 - (Math.abs(temp))) / 200);

      if(temp < -100) {
        temp = -100;
      }
      else if(temp > 100) {
        temp = 100;
      }

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

    System.out.print("Last location: ");
    for(int i = 0; i < count; i++) {
      System.out.print(oldLocationData[i] + " ");
    }
    System.out.println();
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
