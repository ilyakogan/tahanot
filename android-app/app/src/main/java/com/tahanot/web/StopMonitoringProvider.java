package com.tahanot.web;

import android.content.Context;

import com.tahanot.BusStopApplication;
import com.tahanot.R;
import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;
import com.tahanot.utils.CollectionUtils;
import com.tahanot.utils.HttpReader;
import com.tahanot.utils.Logging;

import org.json.JSONObject;

import java.util.Collection;

public class StopMonitoringProvider {
    private static int CONNECTION_TIMEOUT_MSEC = 20 * 1000;

    public MultipleStopMonitoringExtendedInfo getMultipleStopMonitoring(Collection<Integer> stopCodes, Context context) throws Exception {
        // TODO URL
        String home = context.getResources().getString(R.string.stop_monitoring_base_url);
        String concatStopCodes = CollectionUtils.join(",", CollectionUtils.convertToStrings(stopCodes));

        // TODO: Analytics
//        EasyTracker.getInstance().setContext(BusStopApplication.getContext());
//		EasyTracker.getTracker().sendEvent("client request", "MultipleStopMonitoringExtendedJson", null, (long)stopCodes.size());

        String uri = home + "/siri/MultipleStopMonitoringExtendedJson?monitoringRefs=" + concatStopCodes + "&ver=1" + ClientIdentification.getQueryParams(0);
        Logging.i(BusStopApplication.getContext(), uri);
        String jsonResult = HttpReader.getStringContent(uri, CONNECTION_TIMEOUT_MSEC, CONNECTION_TIMEOUT_MSEC);
        Logging.i(BusStopApplication.getContext(), "Got response from getMultipleStopMonitoring");

        JSONObject obj = new JSONObject(jsonResult);
        MultipleStopMonitoringExtendedInfo stopMonitoring = new MultipleStopMonitoringExtendedInfo(obj);
        Logging.i(BusStopApplication.getContext(), "Parsed MultipleStopMonitoringExtendedInfo with " + stopMonitoring.stops.size() + " stops");
        return stopMonitoring;
    }
}