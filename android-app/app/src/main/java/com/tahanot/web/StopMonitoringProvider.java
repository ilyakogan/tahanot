package com.tahanot.web;

import android.content.Context;

import com.tahanot.R;
import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;
import com.tahanot.utils.CollectionUtils;
import com.tahanot.web.restclients.Siri;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import retrofit.RestAdapter;

public class StopMonitoringProvider {
    //private static int CONNECTION_TIMEOUT_MSEC = 20 * 1000;

    public MultipleStopMonitoringExtendedInfo getMultipleStopMonitoring(Collection<Integer> stopCodes, Context context) {
        String concatStopCodes = CollectionUtils.join(",", CollectionUtils.convertToStrings(stopCodes));

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(context.getResources().getString(R.string.stop_monitoring_base_url))
                .build();
        Siri siri = restAdapter.create(Siri.class);
        Map<String, Object> params = new HashMap<>();
        params.put("monitoringRefs", concatStopCodes);
        params.put("ver", 1);
        params.putAll(ClientIdentification.getQueryParamsAsMap(context, 0));
        MultipleStopMonitoringExtendedInfo multipleStopMonitoringExtendedInfo = siri.getMultipleStopMonitoringExtendedJson(params);
        return multipleStopMonitoringExtendedInfo;

    }
}