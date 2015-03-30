package com.tahanot.web;

import java.util.*;

import org.json.simple.*;

import com.google.analytics.tracking.android.*;
import com.tahanot.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;

public class StopMonitoringProvider {
	private static int CONNECTION_TIMEOUT_MSEC = 20 * 1000;
	//final String HOME = 
	final String HOME = ...
	
	public MultipleStopMonitoringExtendedInfo getMultipleStopMonitoring(Collection<Integer> stopCodes) throws Exception {
		String concatStopCodes = CollectionUtils.join(",", CollectionUtils.convertToStrings(stopCodes));
		
        EasyTracker.getInstance().setContext(BusStopApplication.getContext());
		EasyTracker.getTracker().sendEvent("client request", "MultipleStopMonitoringExtendedJson", null, (long)stopCodes.size());
		
		String uri = HOME + "/siri/MultipleStopMonitoringExtendedJson?monitoringRefs=" + concatStopCodes + "&ver=1" + ClientIdentification.getQueryParams(0);
		Logging.i(BusStopApplication.getContext(), uri);
		String jsonResult = HttpReader.getStringContent(uri, CONNECTION_TIMEOUT_MSEC, CONNECTION_TIMEOUT_MSEC);
		Logging.i(BusStopApplication.getContext(), "Got response from getMultipleStopMonitoring");

		JSONObject obj = (JSONObject) JSONValue.parse(jsonResult);
		MultipleStopMonitoringExtendedInfo stopMonitoring = new MultipleStopMonitoringExtendedInfo(obj);
		Logging.i(BusStopApplication.getContext(), "Parsed MultipleStopMonitoringExtendedInfo with " + stopMonitoring.stops.size() + " stops");
		return stopMonitoring;
	}
}