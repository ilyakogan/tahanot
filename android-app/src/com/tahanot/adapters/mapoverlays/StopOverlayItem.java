package com.tahanot.adapters.mapoverlays;

import org.osmdroid.util.*;
import org.osmdroid.views.overlay.*;

import com.tahanot.entities.*;

public class StopOverlayItem extends OverlayItem {
	public GtfsStop Stop;
	
	public StopOverlayItem(GtfsStop stop)
	{
		super(makeTitle(stop), null, makePoint(stop));
		Stop = stop;
	}

	private static String makeTitle(GtfsStop stop) {
		return stop.Code + "\n" + stop.Name + "\n" + stop.Address;
	}

	private static GeoPoint makePoint(GtfsStop stop) {
		double lat = stop.Latitude;
		double lng = stop.Longitude;
		GeoPoint point = new GeoPoint((int) (lat * 1E6), (int) (lng * 1E6));
		return point;
	}
}