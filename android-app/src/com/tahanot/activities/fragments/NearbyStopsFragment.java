package com.tahanot.activities.fragments;

import java.util.*;

import android.location.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.google.analytics.tracking.android.*;
import com.tahanot.*;
import com.tahanot.activities.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.adapters.*;
import com.tahanot.entities.*;
import com.tahanot.fragmentlisteners.*;
import com.tahanot.interfaces.*;
import com.tahanot.tasks.*;
import com.tahanot.utils.*;
import com.tahanot.web.*;

public class NearbyStopsFragment extends SherlockFragment implements SimpleLocationListener {
	StopCollectionPopulator mStopCollectionPopulator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		EasyTracker.getInstance().setContext(getActivity());
		EasyTracker.getTracker().sendView("Nearby Stops tab");

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.nearby_stops, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();

		showLocation(getString(R.string.searching));
		LocationProxy.get().subscribeToOneTimeUpdate(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		LocationProxy.get().unsubscribe(this);
		if (mStopCollectionPopulator != null) {
			mStopCollectionPopulator.interrupt();
			mStopCollectionPopulator = null;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		showLocation(location);
		beginBringStops(location);
	}

	private void beginBringStops(final Location location) {
		FragmentActivity activity = getActivity();
		StopSelectedListener listener = (StopSelectedListener) activity;
		final int widgetId = WidgetConfigActivity.class.isInstance(activity) ? ((WidgetConfigActivity) activity).getWidgetId() : 0;
		
		if (mStopCollectionPopulator == null) {
			mStopCollectionPopulator = new StopCollectionPopulator(new StopListAdapter(activity, getListView()), listener);
		}
		
		GetNearbyStopsTask fillListTask = new GetNearbyStopsTask(2, 50) 
		{
			@Override
			protected ArrayList<IStopInResponse> getStops(int fromStopNumber, int toStopNumber) {
				NearbyStopSignsResponse stops = new StopSignProvider().getNearbyStopSigns(location, fromStopNumber, toStopNumber, widgetId);
				if (stops == null)
				{
					//Toast.makeText(BusStopApplication.getContext(), R.string.error_getting_nearby_stops, Toast.LENGTH_SHORT).show();
					return new ArrayList<IStopInResponse>();
				}
				ArrayList<IStopInResponse> stopsToReport = new ArrayList<IStopInResponse>();
				for (StopSignWithDistance stop : stops.StopsByDistance) {
					stopsToReport.add(stop);
				}
				return stopsToReport;
			}
		};		
		
		mStopCollectionPopulator.beginPopulate(fillListTask);
		Toast.makeText(BusStopApplication.getContext(), R.string.searching_for_nearby_stops, Toast.LENGTH_SHORT).show();
	}

	private ListView getListView() {
		return (ListView) getActivity().findViewById(R.id.nearby_stops_list);
	}

	private void showLocation(Location location) {
		String locationString = Geocoding.GetAddressByLocation(location);
		showLocation(locationString);
	}

	private void showLocation(String locationString) {
		((TextView) getActivity().findViewById(R.id.lastKnownLocation)).setText(locationString);
	}
}
