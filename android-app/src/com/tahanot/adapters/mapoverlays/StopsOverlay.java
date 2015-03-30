package com.tahanot.adapters.mapoverlays;

import java.util.*;

import org.osmdroid.*;
import org.osmdroid.api.*;
import org.osmdroid.views.overlay.*;

import com.actionbarsherlock.app.*;
import com.tahanot.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.adapters.*;
import com.tahanot.entities.*;
import com.tahanot.tasks.*;

import android.app.*;
import android.content.*;
import android.content.DialogInterface.OnClickListener;
import android.graphics.*;
import android.graphics.drawable.*;
import android.widget.*;

public class StopsOverlay extends ItemizedOverlay<StopOverlayItem> {
	private ArrayList<StopOverlayItem> mOverlays = new ArrayList<StopOverlayItem>();
	public StopSelectedListener mStopSelectedListener;

	public StopsOverlay(SherlockFragmentActivity activity, Drawable pDefaultMarker, ResourceProxy pResourceProxy) {
		super(pDefaultMarker, pResourceProxy);
	}

	@Override
	public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
		// TODO Auto-generated method stub
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