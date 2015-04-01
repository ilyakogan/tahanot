package com.tahanot.adapters.mapoverlays;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.tahanot.activities.interfaces.StopSelectedListener;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapView;
import org.osmdroid.views.overlay.ItemizedOverlay;

import java.util.ArrayList;

public class StopsOverlay extends ItemizedOverlay<StopOverlayItem> {
	private ArrayList<StopOverlayItem> mOverlays = new ArrayList<StopOverlayItem>();
	public StopSelectedListener mStopSelectedListener;

	public StopsOverlay(Activity activity, Drawable pDefaultMarker, ResourceProxy pResourceProxy) {
		super(pDefaultMarker, pResourceProxy);
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		return false;
	}

	@Override
	protected StopOverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addOverlay(StopOverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected boolean onTap(int index) {
		final StopOverlayItem item = (StopOverlayItem) mOverlays.get(index);		
		mStopSelectedListener.onStopCodeSelected(item.Stop.Code);		
		return true;
	}

	public boolean containsStopCode(int code) {
		for (StopOverlayItem item : mOverlays)
		{
			if (item.Stop.Code == code)
			{
				return true;
			}
		}
		return false;
	}
}