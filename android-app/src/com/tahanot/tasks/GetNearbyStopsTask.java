package com.tahanot.tasks;

import java.util.*;

import android.content.Context;
import android.location.Location;
import android.os.*;
import com.tahanot.entities.*;

public abstract class GetNearbyStopsTask extends AsyncTask<Void, Void, Void> {
	public final static int IN_PROGRESS = 1;
	public final static int NOT_IN_PROGRESS = 1;

	private Handler mFillListHandler;
	private int mStopsEachTime;
	private int mMaxStops;
	protected Location mAroundLocation;

	public GetNearbyStopsTask(int stopsEachTime, int maxStops) {
		mStopsEachTime = stopsEachTime;
		mMaxStops = maxStops;
	}
	
	public void setHandler(Handler fillListHandler) {
		mFillListHandler = fillListHandler;		
	}

	@Override
	protected Void doInBackground(Void... obj) {
		int i;
		for (i = 0; i + mStopsEachTime < mMaxStops - 1; i += mStopsEachTime) {
			if (isCancelled())
				return null;
			LoadStops(i, i + mStopsEachTime - 1, true);
		}
		if (isCancelled())
			return null;
		LoadStops(i, mMaxStops - 1, false);
		return null;
	}

	private void LoadStops(int fromStopNumber, int toStopNumber, boolean isStillInProgress) {
		ArrayList<IStopInResponse> stopsToReport = getStops(fromStopNumber, toStopNumber);
		Message.obtain(mFillListHandler, 0, isStillInProgress ? IN_PROGRESS : NOT_IN_PROGRESS, 0, stopsToReport).sendToTarget();
	}

	abstract protected ArrayList<IStopInResponse> getStops(int fromStopNumber, int toStopNumber);
	
//	abstract protected ArrayList<IStopInResponse> getStops(Location location, int fromStopNumber, int toStopNumber) {
//		NearbyStopSignsResponse stops = new StopSignProvider().getNearbyStopSigns(location, fromStopNumber, toStopNumber, widgetId);
//		ArrayList<IStopInResponse> stopsToReport = new ArrayList<IStopInResponse>();
//		for (StopSignWithDistance stop : stops.StopsByDistance) {
//			stopsToReport.add(stop);
//		}
//		return stopsToReport;
//	}
}