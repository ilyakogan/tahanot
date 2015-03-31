package com.tahanot.analytics;

import android.app.Activity;

import com.google.android.gms.analytics.HitBuilders;
import com.tahanot.BusStopApplication;

public class UiAnalytics {
    public static void trackScreen(Activity activity, String screenName)
    {
        ((BusStopApplication)activity.getApplication()).getTracker().setScreenName(screenName);
        ((BusStopApplication)activity.getApplication()).getTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }
}
