package servermultipleclients;

import org.json.JSONObject;

public class JSONFunctions {
	//Reads data from incoming requests. Not currently used but left in for potentially being used later.
	public static void readInboundRequest(JSONObject obj, String subjectToken, String subjectIP, String actType, String actData) {	
		subjectToken = new String((String) obj.get("subjectToken"));
		subjectIP = new String((String) obj.get("subjectIP"));
		actType = new String((String) obj.get("actType"));
		actData = new String((String) obj.get("actData"));
	}
	//Reads data from score requests. Not currently used but left in for potentially being used later.
	public static void readScoreRequest(JSONObject obj, String subjectToken, String subjectIP, String actType) {
		subjectToken = new String((String) obj.get("subjectToken"));
		subjectIP = new String((String) obj.get("subjectIP"));
		actType = new String((String) obj.get("actType"));
	}
	//Reads data from web requests. Not currently used but left in for potentially being used later.
	public static void readWebsiteRequest(JSONObject obj, String requestorKey, String subjectToken, String subjectIP, String actType, String actData) {
		requestorKey = new String((String) obj.get("requestorKey"));
		subjectToken = new String((String) obj.get("subjectToken"));
		subjectIP = new String((String) obj.get("subjectIP"));
		actType = new String((String) obj.get("actType"));
		actData = new String((String) obj.get("actData"));
	}
	//Reads data from act data requests. Not currently used but left in for potentially being used later.
	public static void readACTData(JSONObject obj, String latitude, String longitude, String accuracy) {		
		latitude = new String((String) obj.get("latitude"));
		longitude = new String((String) obj.get("longitude"));
		accuracy = new String((String) obj.get("accuracy"));
	}
	//Creates return message
	public static JSONObject createReturnMessage(JSONObject obj, String subjectToken, String score) {
		//obj = new JSONObject();
		obj.clear();
		obj.put("subjectToken", subjectToken);
		obj.put("score", score);
		
		return obj;
	}
	//Gets subject token from JSONObject
	public static String getSubjectToken(JSONObject obj) {
		String subjectToken = "";
		
		subjectToken = new String((String) obj.get("subjectToken"));
		
		return subjectToken;
	}
	//Gets subjectIP from JSONObject
	public static String getSubjectIP(JSONObject obj) {
		String subjectIP = "";
		
		subjectIP = new String((String) obj.get("subjectIP"));
		
		return subjectIP;
	}
	/*
	 * Gets actTYpe from JSONObject
	 * REQ = score request
	 * GEO = location check
	 * WEB = web site request
	 */
	public static String getACTType(JSONObject obj) {
		String actType = "";
		
		actType = new String((String) obj.get("actType"));
		
		return actType;
	}
	//Gets latitude from JSONObject
	public static String getLatitude(JSONObject obj) {
		String latitude = "";
		
		latitude = new String((String) obj.get("latitude"));
		
		return latitude;
	}
	//Gets longitude from JSONObject
	public static String getLongitude(JSONObject obj) {
		String longitude = "";
		
		longitude = new String((String) obj.get("longitude"));
		
		return longitude;
	}
	//Gets accuracy from JSONObject
	public static String getAccuracy(JSONObject obj) {
		String accuracy = "";
		
		accuracy = new String((String) obj.get("accuracy"));
		
		return accuracy;
	}
	//Gets score from JSONObject
	public static String getScore(JSONObject obj) {
		String score = "";
		
		score = new String(obj.get("score").toString());
		
		return score;
	}
	//Gets URL from JSONObject
	public static String getURL(JSONObject obj) {
		String url = "";
		
		url = new String((String) obj.get("url"));
		
		return url;
	}
	//Gets userAgent from JSONObject
	public static String getUserAgent(JSONObject obj) {
		String userAgent = "";
		
		userAgent = new String((String) obj.get("userAgent"));
		
		return userAgent;
	}
	//Gets actData JSONObject from request
	public static JSONObject getActData(JSONObject obj) {
		return obj.getJSONObject("actData");
	}
}

