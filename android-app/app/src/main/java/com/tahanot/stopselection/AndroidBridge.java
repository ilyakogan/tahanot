package com.tahanot.stopselection;

import android.webkit.JavascriptInterface;

import com.tahanot.utils.Logging;

public class AndroidBridge {
    private StopSelectionActivity activity;

    public AndroidBridge(StopSelectionActivity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void onStopSelected(final double lat, final double lng, final String name, String source) {
        boolean showAreYouSure = (source.equals("map"));
        activity.runOnUiThread(() -> activity.onStopSelected(lat, lng, name, showAreYouSure));
    }

    @JavascriptInterface
    public void onFirstStopDisplayed() {
        activity.runOnUiThread(() -> activity.onFirstStopDisplayed());
    }

    @JavascriptInterface
    public int getStopCode(final double lat, final double lng) {
        try {
            return new StopConverter(activity).coordinatesToStopCode(lat, lng);
        } catch (Exception ex) {
            Logging.e(activity, "Cannot get stop ID by coordinates for Javascript");
            return 0;
        }
    }
}
