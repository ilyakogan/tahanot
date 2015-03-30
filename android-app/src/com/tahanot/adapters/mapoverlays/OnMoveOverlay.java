package com.tahanot.adapters.mapoverlays;

import org.osmdroid.api.*;
import org.osmdroid.util.*;
import org.osmdroid.views.*;
import org.osmdroid.views.overlay.*;

import android.content.*;
import android.graphics.*;
import android.view.*;

public class OnMoveOverlay extends Overlay {

	private static IGeoPoint lastLatLon = new GeoPoint(0, 0);
	private static IGeoPoint currLatLon;

	// Event listener to listen for map finished moving events
	private OnMapMoveListener eventListener = null;

	protected boolean isMapMoving = false;

	public OnMoveOverlay(OnMapMoveListener eventLis, Context context) {
		super(context);
		// Set event listener
		eventListener = eventLis;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		super.onTouchEvent(event, mapView);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// Added to example to make more complete
			isMapMoving = true;
		}
		// Fix: changed to false as it would handle the touch event and not pass back.
		return false;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (!shadow) {
			if (isMapMoving) {
				currLatLon = mapView.getProjection().fromPixels(mapView.getWidth() / 2, mapView.getHeight() / 2);
				if (currLatLon.equals(lastLatLon)) {
					isMapMoving = false;
					eventListener.mapMovingFinishedEvent(currLatLon);
				} else {
					lastLatLon = currLatLon;
				}
			}
		}
	}

	public interface OnMapMoveListener {
		public void mapMovingFinishedEvent(IGeoPoint currLatLon);
	}
}