package com.tahanot.entities;

import org.json.simple.JSONObject;

public class NearbyStop {
	public Number id;
	public Number code;
	public Number latitude;
    public Number longitude;
    public String name;
    public String address;	
	public Number distanceMeters;
	
	public NearbyStop(JSONObject root) {  
		distanceMeters = (Number)root.get("DistanceFromPoiMeters");		
		JSONObject stopObj = (JSONObject)root.get("Stop");
    	id = (Number)stopObj.get("Id");
    	code = (Number)stopObj.get("Code");
    	latitude = (Number)stopObj.get("Latitude");
    	longitude = (Number)stopObj.get("Longitude");
    	name = (String)stopObj.get("Name");
    	address = (String)stopObj.get("Address");
    }
}
