package com.tahanot.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class StopMonitoringExtendedInfo {
	public String error;
	public ArrayList<StopVisit> stopVisits = new ArrayList<StopVisit>();
	public long stopCode;

	public StopMonitoringExtendedInfo(JSONObject stopObj, Date responseTimestamp) throws JSONException {
		if (stopObj == null)
			return;
        JSONArray stopVisitsArray = stopObj.getJSONArray("StopVisits");
		int len = stopVisitsArray.length();
		for (int i = 0; i < len; i++) {
            JSONObject stopVisitObj = stopVisitsArray.getJSONObject(i);
			stopVisits.add(new StopVisit(stopVisitObj, responseTimestamp));
		}

        stopCode = stopObj.optLong("MonitoringRef", stopObj.getLong("MotiroringRef"));
		error = stopObj.getString("Error");
        if (error.equals("null")) error = null;
	}
}