package com.tahanot.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root
public class GtfsStop {
	@Attribute
	public int Id;
	@Attribute
	public int Code;
	@Attribute
	public double Latitude;
	@Attribute
	public double Longitude;

	@Attribute
	public String Name;
	@Attribute
	public String Address;
	@Attribute
	public String StreetAddress;
	@Attribute
	public String Town;
	
	public GtfsStop(JSONObject jsonObject) throws JSONException {
		Code = jsonObject.getInt("Code");
		Latitude = jsonObject.getDouble("Latitude");
		Longitude = jsonObject.getDouble("Longitude");
		Address = jsonObject.getString("Address");
		Name = jsonObject.getString("Name");
	}
	
	public GtfsStop() {	
	}
}
