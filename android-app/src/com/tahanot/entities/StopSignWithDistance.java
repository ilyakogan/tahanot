package com.tahanot.entities;

import org.simpleframework.xml.*;

@Root
public class StopSignWithDistance implements IStopInResponse {
	
	@Attribute
	public double DistanceFromPoi;
    
	@Element
	public StopSign StopSign;

	@Override
	public int getCode() {
		return StopSign.RawStop.Code;
	}
}
