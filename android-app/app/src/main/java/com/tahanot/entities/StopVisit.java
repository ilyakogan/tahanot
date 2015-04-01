package com.tahanot.entities;

import java.util.Date;


public class StopVisit {    
	private static final long MSEC_IN_SEC = 1000;
	public String LineRef;
    public String RouteShortName;
    public String RouteLongName;
    public String ExpectedArrivalTime;
    public String PublishedLineName;
    public String DestinationRef;

    public long getExpectedInSeconds(Date responseTimestamp) {
        String s = ExpectedArrivalTime;
        long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
        Date expectedArrivalTime = new Date(msecSince1970);

        return (expectedArrivalTime.getTime() - responseTimestamp.getTime()) / MSEC_IN_SEC;
    };
}
