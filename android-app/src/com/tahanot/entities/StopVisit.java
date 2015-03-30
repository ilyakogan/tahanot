package com.tahanot.entities;

import java.text.*;
import java.util.Date;

import org.json.simple.JSONObject;

public class StopVisit {    
	private static final long MSEC_IN_SEC = 1000;
	public String lineRef;
    public String routeShortName;
    public String routeLongName;
    //public Date expectedArrivalTime;
    public String publishedLineName;
    public String destinationRef;
    public long expectedInSeconds;

    public StopVisit(JSONObject jsonObject, Date responseTimestamp) {
		lineRef = (String) jsonObject.get("LineRef");
		routeShortName = (String) jsonObject.get("RouteShortName");
		routeLongName = (String) jsonObject.get("RouteLongName");

		publishedLineName = (String) jsonObject.get("PublishedLineName");
		destinationRef = (String) jsonObject.get("DestinationRef");	
		
		String s = (String) jsonObject.get("ExpectedArrivalTime");
		long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
		Date expectedArrivalTime = new Date(msecSince1970);	
		
		expectedInSeconds = (expectedArrivalTime.getTime() - responseTimestamp.getTime()) / MSEC_IN_SEC;
	}
}
