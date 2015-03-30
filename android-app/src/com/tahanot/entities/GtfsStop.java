package com.tahanot.entities;

import java.util.*;

import org.json.simple.*;
import org.simpleframework.xml.*;

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
	
	public GtfsStop(JSONObject jsonObject) {
		Code = ((Long) jsonObject.get("Code")).intValue();
		Latitude = (Double) jsonObject.get("Latitude");
		Longitude = (Double) jsonObject.get("Longitude");
		Address = (String)jsonObject.get("Address");
		Name = (String)jsonObject.get("Name");
	}
	
	public GtfsStop() {	
	}
}
