package com.tahanot.stopselection;

import android.webkit.JavascriptInterface;

import com.tahanot.utils.Logging;

public class AndroidBridge {
    private StopSelectionActivity activity;
    private StopMonitoringQueueWorker stopMonitoringQueueWorker;

    public AndroidBridge(StopSelectionActivity activity, StopMonitoringQueueWorker stopMonitoringQueueWorker) {
        this.activity = activity;
        this.stopMonitoringQueueWorker = stopMonitoringQueueWorker;
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

    @JavascriptInterface
    public void requestStopMonitoring(int stopCode) {
        stopMonitoringQueueWorker.enqueue(stopCode);
    }
}
