package com.tahanot.entities;


import org.json.JSONException;
import org.json.JSONObject;

public class NearbyStop {
	public Number id;
	public Number code;
	public Number latitude;
    public Number longitude;
    public String name;
    public String address;	
	public Number distanceMeters;
	
	public NearbyStop(JSONObject root) throws JSONException {
		distanceMeters = root.getDouble("DistanceFromPoiMeters");
		JSONObject stopObj = root.getJSONObject("Stop");
    	id = stopObj.getInt("Id");
    	code = stopObj.getInt("Code");
    	latitude = stopObj.getDouble("Latitude");
    	longitude = stopObj.getDouble("Longitude");
    	name = stopObj.getString("Name");
    	address = stopObj.getString("Address");
    }
}
