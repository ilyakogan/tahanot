package com.tahanot.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class MultipleStopMonitoringExtendedInfo {
	public String error;
	public ArrayList<StopMonitoringExtendedInfo> stops = new ArrayList<StopMonitoringExtendedInfo>();
	public Date responseTimestamp;

	public MultipleStopMonitoringExtendedInfo(JSONObject root) throws JSONException {
		if (root == null)
			return;
        JSONArray stopsArray = root.getJSONArray("Stops");
		if (stopsArray == null)
			return;

		String s = root.getString("ResponseTimestamp");
		long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
		responseTimestamp = new Date(msecSince1970);	
		
		int len = stopsArray.length();
		for (int i = 0; i < len; i++) {
			Object stopObj = stopsArray.get(i);
			stops.add(new StopMonitoringExtendedInfo((JSONObject) stopObj, responseTimestamp));
		}
		
		error = root.getString("Error");
        if (error.equals("null")) error = null;
	}
}