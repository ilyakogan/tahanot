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
        JSONArray stopsObj = root.getJSONArray("Stops");
		if (stopsObj == null)
			return;

		String s = (String) root.getString("ResponseTimestamp");
		long msecSince1970 = Long.parseLong(s.substring(6, s.length() - 2));
		responseTimestamp = new Date(msecSince1970);	
		
		JSONArray jsonArray = (JSONArray) stopsObj;
		int len = jsonArray.length();
		for (int i = 0; i < len; i++) {
			Object stopObj = jsonArray.get(i);
			stops.add(new StopMonitoringExtendedInfo((JSONObject) stopObj, responseTimestamp));
		}
		
		error = (String) root.get("Error");
	}
}