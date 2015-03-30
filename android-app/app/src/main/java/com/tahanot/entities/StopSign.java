package com.tahanot.entities;

import java.util.ArrayList;

import org.simpleframework.xml.*;

import android.os.*;

import java.io.Serializable;

@Root
public class StopSign {

	@Element
	public GtfsStop RawStop;

	@ElementList
	public ArrayList<StopSignRoute> Routes;
}
