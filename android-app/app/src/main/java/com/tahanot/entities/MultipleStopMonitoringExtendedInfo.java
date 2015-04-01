package com.tahanot.entities;

import java.util.ArrayList;
import java.util.Date;


public class MultipleStopMonitoringExtendedInfo {
	public String Error;
	public ArrayList<StopMonitoringExtendedInfo> Stops = new ArrayList<StopMonitoringExtendedInfo>();
	public String ResponseTimestamp;

    public Date getResponseTimestamp()
    {
        String s = ResponseTimestamp;
		long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
		return new Date(msecSince1970);
    }
}