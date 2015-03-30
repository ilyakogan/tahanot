package com.tahanot.entities;

import org.json.simple.*;
import org.simpleframework.xml.*;

@Root
public class StopWithDistance implements IStopInResponse {
	
	@Attribute
	public double DistanceFromPoiMeters;
    
	@Element
	public GtfsStop Stop;
	
	public StopWithDistance(JSONObject jsonObject) {
		Stop = new GtfsStop((JSONObject) jsonObject.get("Stop"));
	}

	@Override
	public int getCode() {
		return Stop.Code;
	}
}