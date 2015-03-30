package com.tahanot.entities;

import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MultipleStopMonitoringExtendedInfo {
	public String error;
	public ArrayList<StopMonitoringExtendedInfo> stops = new ArrayList<StopMonitoringExtendedInfo>();
	public Date responseTimestamp;

	public MultipleStopMonitoringExtendedInfo(JSONObject root) {
		if (root == null)
			return;
		Object stopsObj = root.get("Stops");
		if (stopsObj == null)
			return;

		String s = (String) root.get("ResponseTimestamp");
		long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
		responseTimestamp = new Date(msecSince1970);	
		
		JSONArray jsonArray = (JSONArray) stopsObj;
		int len = jsonArray.size();
		for (int i = 0; i < len; i++) {
			Object stopObj = jsonArray.get(i);
			stops.add(new StopMonitoringExtendedInfo((JSONObject) stopObj, responseTimestamp));
		}
		
		error = (String) root.get("Error");
	}
}