package com.tahanot.stopselection;

import java.util.*;

import com.tahanot.*;

import android.content.*;
import android.location.*;
import android.os.*;

public class LocationProxy {

	private static final int MSEC_TO_REMEMBER = 10 * 1000;
	private LocationManager mLocationManager = (LocationManager) BusStopApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
	Set<SimpleLocationListener> mListenersToNotifyOnce = new HashSet<SimpleLocationListener>();
	Set<SimpleLocationListener> mListenersToNotifyAlways = new HashSet<SimpleLocationListener>();
	private boolean mIsGettingUpdates;
	private static LocationProxy instance = null;
	private Location mRememberedLocation;

	protected LocationProxy() {
	}

	synchronized public static LocationProxy get() {
		if (instance == null) {
			instance = new LocationProxy();
		}
		return instance;
	}
	
	synchronized public void subscribeToUpdates(SimpleLocationListener listener, boolean notifyImmediatelyEvenIfLocationIsOutdated) {
		notifyLastKnownLocation(listener, notifyImmediatelyEvenIfLocationIsOutdated);
		if (!mListenersToNotifyAlways.contains(listener))
		{
			mListenersToNotifyAlways.add(listener);
		}
		onListenerAdded();
	}

	synchronized public void subscribeToOneTimeUpdate(SimpleLocationListener listener) {
		notifyLastKnownLocation(listener, false);
		if (!mListenersToNotifyOnce.contains(listener))
		{
			mListenersToNotifyOnce.add(listener);
		}
		onListenerAdded();
	}

	synchronized public void unsubscribe(SimpleLocationListener listener) {
		if (mListenersToNotifyOnce.contains(listener)) {
			mListenersToNotifyOnce.remove(listener);
		}
		if (mListenersToNotifyAlways.contains(listener)) {
			mListenersToNotifyAlways.remove(listener);
		}
	}

	LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
        	onGotLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
        	onGotLocation(location);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

	synchronized private void onListenerAdded() {
		if (!mIsGettingUpdates) {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListenerNetwork);
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListenerGps);
			mIsGettingUpdates = true;
		}

		// boolean isDebuggable = (0 != (BusStopApplication.getContext().getApplicationInfo().flags &=
		// ApplicationInfo.FLAG_DEBUGGABLE));
		// if (isDebuggable) {
		// notifyFakeLocation();
		// }
	}
	
	synchronized protected void onListenerRemoved() {
		if (mListenersToNotifyOnce.isEmpty() && mListenersToNotifyAlways.isEmpty()) {
			mLocationManager.removeUpdates(locationListenerGps);
			mLocationManager.removeUpdates(locationListenerNetwork);
			mIsGettingUpdates = false;
		}
	}

	private void notifyLastKnownLocation(SimpleLocationListener listener, boolean evenIfLocationIsOutdated) {
		Location lastKnownLocation = getLastLocation(evenIfLocationIsOutdated);
		if (lastKnownLocation != null)
		{
			mRememberedLocation = lastKnownLocation;
			listener.onLocationChanged(lastKnownLocation);
			return;
		}
	}

	public Location getLastLocation(boolean evenIfLocationIsOutdated) {
		if (!evenIfLocationIsOutdated) {
			Location lastKnown = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (lastKnown == null)
				return null;
			if (lastKnown.getTime() < System.currentTimeMillis() - MSEC_TO_REMEMBER)
				return null;
			return lastKnown;
		}
		else {
			List<String> providers = mLocationManager.getProviders(true);
	        Location l = null;	        
	        for (int i=providers.size()-1; i>=0; i--) {
                l = mLocationManager.getLastKnownLocation(providers.get(i));
                if (l != null) return l;
	        }
	        return mRememberedLocation;
		}
			
	}

	synchronized public void onGotLocation(Location location) {
		mRememberedLocation = location;
		for (SimpleLocationListener listener : mListenersToNotifyOnce) {
			listener.onLocationChanged(location);
		}
		for (SimpleLocationListener listener : mListenersToNotifyAlways) {
			listener.onLocationChanged(location);
		}
		mListenersToNotifyOnce.clear();
		onListenerRemoved();
	}
	
	private void notifyFakeLocation() {
		Location fakeLocation = new Location(LocationManager.GPS_PROVIDER);
		fakeLocation.setLatitude(32);
		fakeLocation.setLongitude(34.8);
		onGotLocation(fakeLocation);
	}	
}
