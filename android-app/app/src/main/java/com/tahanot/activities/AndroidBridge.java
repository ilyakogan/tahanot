package com.tahanot.activities;

import android.webkit.JavascriptInterface;

public class AndroidBridge {
    private StopSelection activity;

    public AndroidBridge(StopSelection activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void onStopSelected(final double lat, final double lng) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onStopSelected(lat,lng);
            }
        });
    }
}
