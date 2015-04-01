package com.tahanot.entities;

import java.util.ArrayList;

public class StopMonitoringExtendedInfo {
	public String Error;
	public ArrayList<StopVisit> StopVisits = new ArrayList<StopVisit>();
	public long MonitoringRef;
    public long MotiroringRef;

    public long getStopCode()
    {
        return MonitoringRef != 0 ? MonitoringRef : MotiroringRef;
    }
}