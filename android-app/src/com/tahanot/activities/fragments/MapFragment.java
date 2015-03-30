package com.tahanot.activities.fragments;

import java.util.*;

import org.osmdroid.*;
import org.osmdroid.api.*;
import org.osmdroid.tileprovider.util.*;
import org.osmdroid.util.*;
import org.osmdroid.views.*;
import org.osmdroid.views.overlay.*;

import com.actionbarsherlock.app.*;
import com.google.analytics.tracking.android.*;
import com.tahanot.*;
import com.tahanot.activities.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.adapters.*;
import com.tahanot.adapters.mapoverlays.*;
import com.tahanot.adapters.mapoverlays.OnMoveOverlay.OnMapMoveListener;
import com.tahanot.entities.*;
import com.tahanot.fragmentlisteners.*;
import com.tahanot.interfaces.*;
import com.tahanot.tasks.*;
import com.tahanot.utils.*;
import com.tahanot.web.*;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.location.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

public class MapFragment extends SherlockFragment implements SimpleLocationListener {
	private MapView mMapView;
	private SimpleLocationOverlay mMyLocationOverlay;

	StopCollectionPopulator mStopCollectionPopulator;
	private boolean mEverCenteredOnMe = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		super.onCreate(savedInstanceState);

		ResourceProxy resourceProxy = new ResourceProxyImpl(getActivity().getApplicationContext());

		final RelativeLayout rl = new RelativeLayout(getActivity());

		CloudmadeUtil.retrieveCloudmadeKey(getActivity().getApplicationContext());

		mMapView = new MapView(getActivity(), 512);
		mMapView.getController().setZoom(16);
		rl.addView(mMapView, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mMyLocationOverlay = new SimpleLocationOverlay(getActivity(), resourceProxy);
		mMapView.getOverlays().add(mMyLocationOverlay);
		
		mMapView.setBuiltInZoomControls(true);
		//mMapView.setMultiTouchControls(true);
		
		onMoveRefreshStops();

		ImageView goToMyLocation = new ImageView(getActivity());
		goToMyLocation.setImageResource(R.drawable.go_to_my_location);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		    RelativeLayout.LayoutParams.WRAP_CONTENT,
		    RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 10, 0, 0);
		goToMyLocation.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
			}
		});
		rl.addView(goToMyLocation, lp);
		
		EasyTracker.getInstance().setContext(getActivity());
		EasyTracker.getTracker().sendView("Map tab");
		
		return rl;
	}
	
	@Override
	public void onResume() {
		mEverCenteredOnMe = false;
		
		LocationProxy.get().subscribeToUpdates(this, true);
		super.onResume();
		
		//Toast.makeText(getActivity(), R.string.tapOnStop, Toast.LENGTH_SHORT).show();		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		LocationProxy.get().unsubscribe(this);
		
		super.onPause();
		
		if (mStopCollectionPopulator != null) {
			mStopCollectionPopulator.interrupt();
			// Make sure the map adapter is re-initialized on next Resume
			mStopCollectionPopulator = null;
		}
	}

	private void onMoveRefreshStops() {
		OnMapMoveListener mapListener = new OnMapMoveListener() {
			@Override
			public void mapMovingFinishedEvent(IGeoPoint currLatLon) {
				double latitude = currLatLon.getLatitudeE6() / 1000000F;
				double longitude = currLatLon.getLongitudeE6() / 1000000F;

				Location location = new Location(LocationManager.GPS_PROVIDER);
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				beginBringStops(location);
		    }
		};

	    OnMoveOverlay mOnMoveOverlay = new OnMoveOverlay(mapListener, getActivity());

	    // Make sure you add as the last overlay so its on the top. 
	    // Otherwise other overlays could steal the touchEvent;
	    mMapView.getOverlays().add(mOnMoveOverlay);
	}

	@Override
	public void onLocationChanged(final Location location) {
		GeoPoint geoPoint = new GeoPoint(location);
		mMyLocationOverlay.setLocation(geoPoint);
		if (!mEverCenteredOnMe) {
			mEverCenteredOnMe = true;
			//mMapView.getController().setCenter(geoPoint);
			mMapView.getController().animateTo(geoPoint);
			beginBringStops(location);
		}
	}

	private synchronized void beginBringStops(final Location location) {
		FragmentActivity activity = getActivity();
		StopSelectedListener listener = (StopSelectedListener) activity;
		final int widgetId = WidgetConfigActivity.class.isInstance(activity) ? ((WidgetConfigActivity) activity).getWidgetId() : 0;
		
		if (mStopCollectionPopulator == null) {
			mStopCollectionPopulator = new StopCollectionPopulator(new StopMapAdapter(mMapView, getSherlockActivity()), listener);
		}
		
		GetNearbyStopsTask fillListTask = new GetNearbyStopsTask(10, 100) 
		{
			@Override
			protected ArrayList<IStopInResponse> getStops(int fromStopNumber, int toStopNumber) {
				ArrayList<StopWithDistance> stops = new StopSignProvider().getNearbyStops(location, toStopNumber, widgetId);
				if (stops == null)
				{
					//Toast.makeText(BusStopApplication.getContext(), R.string.error_getting_nearby_stops, Toast.LENGTH_SHORT).show();
					return new ArrayList<IStopInResponse>();
				}
				ArrayList<IStopInResponse> stopsToReport = new ArrayList<IStopInResponse>();
				for (StopWithDistance stop : stops) {
					stopsToReport.add(stop);
				}
				return stopsToReport;
			}
		};
		
		mStopCollectionPopulator.beginPopulate(fillListTask);
	}

	public class ResourceProxyImpl extends DefaultResourceProxyImpl {

		private final Context mContext;

		public ResourceProxyImpl(final Context pContext) {
			super(pContext);
			mContext = pContext;
		}

		@Override
		public String getString(final string pResId) {
			try {
				final int res = R.string.class.getDeclaredField(pResId.name()).getInt(null);
				return mContext.getString(res);
			} catch (final Exception e) {
				return super.getString(pResId);
			}
		}

		@Override
		public Bitmap getBitmap(final bitmap pResId) {
			try {
				final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
				return BitmapFactory.decodeResource(mContext.getResources(), res);
			} catch (final Exception e) {
				return super.getBitmap(pResId);
			}
		}

		@Override
		public Drawable getDrawable(final bitmap pResId) {
			try {
				final int res = R.drawable.class.getDeclaredField(pResId.name()).getInt(null);
				return mContext.getResources().getDrawable(res);
			} catch (final Exception e) {
				return super.getDrawable(pResId);
			}
		}
	}
}


///* Scale Bar Overlay */
//{
//	this.mScaleBarOverlay = new ScaleBarOverlay(getActivity());
//	this.mMapView.getOverlays().add(mScaleBarOverlay);
//	// Scale bar tries to draw as 1-inch, so to put it in the top center, set x offset to
//	// half screen width, minus half an inch.
//	this.mScaleBarOverlay.setScaleBarOffset(getResources().getDisplayMetrics().widthPixels / 2 - getResources().getDisplayMetrics().xdpi / 2,
//			10);
//}

///* ZoomControls */
//{
//	/* Create a ImageView with a zoomIn-Icon. */
//	final ImageView ivZoomIn = new ImageView(getActivity());
//	ivZoomIn.setImageResource(R.drawable.zoom_in);
//	/* Create RelativeLayoutParams, that position it in the top right corner. */
//	final RelativeLayout.LayoutParams zoominParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//			RelativeLayout.LayoutParams.WRAP_CONTENT);
//	zoominParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//	zoominParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//	rl.addView(ivZoomIn, zoominParams);
//
//	ivZoomIn.setOnClickListener(new OnClickListener() {
//		@Override
//		public void onClick(final View v) {
//			MapFragment.this.mOsmvController.zoomIn();
//		}
//	});
//
//	/* Create a ImageView with a zoomOut-Icon. */
//	final ImageView ivZoomOut = new ImageView(getActivity());
//	ivZoomOut.setImageResource(R.drawable.zoom_out);
//
//	/* Create RelativeLayoutParams, that position it in the top left corner. */
//	final RelativeLayout.LayoutParams zoomoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
//			RelativeLayout.LayoutParams.WRAP_CONTENT);
//	zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//	zoomoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//	rl.addView(ivZoomOut, zoomoutParams);
//
//	ivZoomOut.setOnClickListener(new OnClickListener() {
//		@Override
//		public void onClick(final View v) {
//			MapFragment.this.mOsmvController.zoomOut();
//		}
//	});
//}

///* MiniMap */
//{
//	mMiniMapOverlay = new MinimapOverlay(getActivity(), mOsmv.getTileRequestCompleteHandler());
//	this.mOsmv.getOverlays().add(mMiniMapOverlay);
//}	