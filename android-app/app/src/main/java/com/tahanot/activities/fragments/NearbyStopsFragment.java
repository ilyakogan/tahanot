package com.tahanot.activities.fragments;

import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tahanot.BusStopApplication;
import com.tahanot.R;
import com.tahanot.StopCollectionPopulator;
import com.tahanot.activities.WidgetConfigActivity;
import com.tahanot.activities.interfaces.StopSelectedListener;
import com.tahanot.adapters.StopListAdapter;
import com.tahanot.entities.IStopInResponse;
import com.tahanot.entities.NearbyStopSignsResponse;
import com.tahanot.entities.StopSignWithDistance;
import com.tahanot.interfaces.SimpleLocationListener;
import com.tahanot.tasks.GetNearbyStopsTask;
import com.tahanot.utils.Geocoding;
import com.tahanot.utils.LocationProxy;
import com.tahanot.web.StopSignProvider;

import java.util.ArrayList;

public class NearbyStopsFragment extends Fragment implements SimpleLocationListener {
	StopCollectionPopulator mStopCollectionPopulator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

        // TODO: Analytics
//        EasyTracker.getInstance().setContext(getActivity());
//		EasyTracker.getTracker().sendView("Nearby Stops tab");

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
		Activity activity = getActivity();
		StopSelectedListener listener = (StopSelectedListener) activity;
		final int widgetId = WidgetConfigActivity.class.isInstance(activity) ? ((WidgetConfigActivity) activity).getWidgetId() : 0;
		
		if (mStopCollectionPopulator == null) {
			mStopCollectionPopulator = new StopCollectionPopulator(new StopListAdapter(activity, getListView()), listener);
		}
		
		GetNearbyStopsTask fillListTask = new GetNearbyStopsTask(2, 50) 
		{
			@Override
			protected ArrayList<IStopInResponse> getStops(int fromStopNumber, int toStopNumber) {
				NearbyStopSignsResponse stops = new StopSignProvider().getNearbyStopSigns(location, fromStopNumber, toStopNumber, widgetId, getActivity());
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
