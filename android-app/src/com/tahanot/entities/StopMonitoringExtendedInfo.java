package com.tahanot.entities;

import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.tahanot.*;
import com.tahanot.utils.*;

public class StopMonitoringExtendedInfo {
	public String error;
	public ArrayList<StopVisit> stopVisits = new ArrayList<StopVisit>();
	public long stopCode;

	public StopMonitoringExtendedInfo(JSONObject root, Date responseTimestamp) {
		if (root == null)
			return;
		Object stopVisitsObj = root.get("StopVisits");
		if (stopVisitsObj == null)
			return;
		JSONArray jsonArray = (JSONArray) stopVisitsObj;
		int len = jsonArray.size();
		for (int i = 0; i < len; i++) {
			Object stopVisitObj = jsonArray.get(i);
			stopVisits.add(new StopVisit((JSONObject) stopVisitObj, responseTimestamp));
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