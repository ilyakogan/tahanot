package com.tahanot.entities;

import java.util.ArrayList;

import org.simpleframework.xml.*;

@Root
public class StopSignRoute {
	@Attribute
	public int TripsTotal;

	@Element
    public StopSignRouteDestination Destination;
	
	@ElementList
    public ArrayList<Integer> RouteIds;
	
	@Attribute
    public String RouteName;
	
	@Attribute(required=false)
    public boolean IsThisDestination;
	
	@ElementList
    public ArrayList<String> Midpoints;
}
