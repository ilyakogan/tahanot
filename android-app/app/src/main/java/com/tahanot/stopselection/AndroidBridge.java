package com.tahanot.stopselection;

import android.webkit.JavascriptInterface;

public class AndroidBridge {
    private StopSelectionActivity activity;

    public AndroidBridge(StopSelectionActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void onStopSelected(final double lat, final double lng, final String name) {
        activity.runOnUiThread(() -> activity.onStopSelected(lat,lng, name));
    }

    @JavascriptInterface
    public void onStopsDisplayed() {
        activity.runOnUiThread(() -> activity.onStopsDisplayed());
    }
}
