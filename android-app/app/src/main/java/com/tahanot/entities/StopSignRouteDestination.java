package com.tahanot.entities;

import org.simpleframework.xml.*;

@Root
public class StopSignRouteDestination {
	
	@Attribute(required=false)
	public String Text1;
	
	@Attribute(required=false)
    public String Text2;
	
	@Attribute(required=false)
    public int StopId;
}
