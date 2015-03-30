package com.tahanot.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.tahanot.BusStopApplication;
import com.tahanot.R;
import com.tahanot.activities.interfaces.StopSelectedListener;
import com.tahanot.adapters.mapoverlays.StopOverlayItem;
import com.tahanot.adapters.mapoverlays.StopsOverlay;
import com.tahanot.entities.GtfsStop;
import com.tahanot.entities.IStopInResponse;
import com.tahanot.entities.StopWithDistance;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class StopMapAdapter implements IStopUiAdapter {
	MapView mMapView;
	StopsOverlay mItemizedOverlay;
	
	public StopMapAdapter(MapView openStreetMapView, Activity activity) {
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
