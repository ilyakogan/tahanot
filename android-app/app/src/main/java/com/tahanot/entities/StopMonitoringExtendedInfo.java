package com.tahanot.entities;

import com.tahanot.BusStopApplication;
import com.tahanot.utils.Logging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class StopMonitoringExtendedInfo {
	public String error;
	public ArrayList<StopVisit> stopVisits = new ArrayList<StopVisit>();
	public long stopCode;

	public StopMonitoringExtendedInfo(JSONObject root, Date responseTimestamp) throws JSONException {
		if (root == null)
			return;
        JSONObject stopVisitsObj = root.getJSONObject("StopVisits");
		if (stopVisitsObj == null)
			return;
		JSONArray jsonArray = new JSONArray(stopVisitsObj);
		int len = jsonArray.length();
		for (int i = 0; i < len; i++) {
            JSONObject stopVisitObj = jsonArray.getJSONObject(i);
			stopVisits.add(new StopVisit(stopVisitObj, responseTimestamp));
		}
		Object stopCodeObj = root.get("MonitoringRef");
		Logging.i(BusStopApplication.getContext(), "stopCodeObj (1): " + stopCodeObj);
		if (stopCodeObj == null) {
			stopCodeObj = root.get("MotiroringRef");
			Logging.i(BusStopApplication.getContext(), "stopCodeObj (2): " + stopCodeObj + ", " + stopCodeObj.getClass().getName());
		}
		
		stopCode = (Long)stopCodeObj;
		error = (String) root.get("Error");
	}
}