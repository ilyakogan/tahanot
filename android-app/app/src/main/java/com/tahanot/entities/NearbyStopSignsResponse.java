package com.tahanot.entities;

import java.util.*;

import org.simpleframework.xml.*;

@Root
public class NearbyStopSignsResponse {
	
	@ElementList
    public ArrayList<StopSignWithDistance> StopsByDistance;
}
