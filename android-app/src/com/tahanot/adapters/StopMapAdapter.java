package com.tahanot.adapters;

import java.util.*;

import org.osmdroid.*;
import org.osmdroid.api.*;
import org.osmdroid.util.*;
import org.osmdroid.views.*;
import org.osmdroid.views.overlay.*;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.RelativeLayout.*;

import com.actionbarsherlock.app.*;
import com.tahanot.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.adapters.mapoverlays.*;
import com.tahanot.entities.*;

public class StopMapAdapter implements IStopUiAdapter {
	MapView mMapView;
	StopsOverlay mItemizedOverlay;
	
	public StopMapAdapter(MapView openStreetMapView, SherlockFragmentActivity activity) {
		mMapView = openStreetMapView;
		ResourceProxy resourceProxy = mMapView.getResourceProxy();
		Drawable drawable = BusStopApplication.getContext().getResources().getDrawable(R.drawable.bus_marker);
		mItemizedOverlay = new StopsOverlay(activity, drawable, resourceProxy);
		mMapView.getOverlays().add(mItemizedOverlay);
	}

	@Override
	public boolean containsStopCode(int code) {
		return false;
	}

	@Override
	public void addStops(ArrayList<IStopInResponse> stopsToAdd, boolean isStillInProgress) {		
		for (IStopInResponse stop : stopsToAdd) {
			if (mItemizedOverlay.containsStopCode(stop.getCode()))
			{
				continue;
			}
			GtfsStop rawStop = ((StopWithDistance)stop).Stop;
			StopOverlayItem overlayItem = new StopOverlayItem(rawStop);
			mItemizedOverlay.addOverlay(overlayItem);
		}	
		mMapView.postInvalidate();
	}

	@Override
	public void registerClick(StopSelectedListener listener) {
		mItemizedOverlay.mStopSelectedListener = listener;
	}
}
