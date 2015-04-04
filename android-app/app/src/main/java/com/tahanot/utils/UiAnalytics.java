package com.tahanot.utils;

import android.app.Activity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tahanot.BusStopApplication;

public class UiAnalytics {
    public static void trackScreen(Activity activity, String screenName)
    {
        Tracker tracker = ((BusStopApplication) activity.getApplication()).getTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
