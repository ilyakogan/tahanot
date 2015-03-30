package com.tahanot.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class StopWithDistance implements IStopInResponse {
	
	@Attribute
	public double DistanceFromPoiMeters;
    
	@Element
	public GtfsStop Stop;
	
	public StopWithDistance(JSONObject jsonObject) throws JSONException {
		Stop = new GtfsStop(jsonObject.getJSONObject("Stop"));
	}

	@Override
	public int getCode() {
		return Stop.Code;
	}
}