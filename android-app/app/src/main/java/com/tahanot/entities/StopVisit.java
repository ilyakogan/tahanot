package com.tahanot.entities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class StopVisit {    
	private static final long MSEC_IN_SEC = 1000;
	public String lineRef;
    public String routeShortName;
    public String routeLongName;
    //public Date expectedArrivalTime;
    public String publishedLineName;
    public String destinationRef;
    public long expectedInSeconds;

    public StopVisit(JSONObject jsonObject, Date responseTimestamp) throws JSONException {
		lineRef = (String) jsonObject.getString("LineRef");
		routeShortName = (String) jsonObject.getString("RouteShortName");
		routeLongName = (String) jsonObject.getString("RouteLongName");

		publishedLineName = (String) jsonObject.getString("PublishedLineName");
		destinationRef = (String) jsonObject.getString("DestinationRef");
		
		String s = (String) jsonObject.getString("ExpectedArrivalTime");
		long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
		Date expectedArrivalTime = new Date(msecSince1970);	
		
		expectedInSeconds = (expectedArrivalTime.getTime() - responseTimestamp.getTime()) / MSEC_IN_SEC;
	}
}
