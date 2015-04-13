package com.tahanot;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;

public class BusStopApplication extends MultiDexApplication {
	private static Application instance;

    public BusStopApplication() {
    	instance = this;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

	public static Context getContext() {
    	return instance;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
        Crashlytics.start(this);
	}

    private List<Tracker> mTrackers = new ArrayList<>();

    public synchronized Tracker getTracker() {
        if (mTrackers.isEmpty()) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = analytics.newTracker(R.xml.app_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.add(t);
        }
        return mTrackers.get(0);
    }
}