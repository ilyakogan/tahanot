package com.tahanot.web.restclients;

import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface Siri {
    @GET("/siri/MultipleStopMonitoringExtendedJson")
    MultipleStopMonitoringExtendedInfo getMultipleStopMonitoringExtendedJson(@QueryMap Map<String, Object> parameters);
}
