package com.tahanot.ui;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.google.common.base.Optional;
import com.tahanot.utils.Logging;

public class AndroidBridge {
    private Context context;
    private MapEventHandler mapEventHandler;
    private Optional<StopSelectionEventHandler> stopSelectionEventHandler;
    private StopMonitoringQueueWorker stopMonitoringQueueWorker;

    public AndroidBridge(Context context, MapEventHandler mapEventHandler, Optional<StopSelectionEventHandler> stopSelectionEventHandler, StopMonitoringQueueWorker stopMonitoringQueueWorker) {
        this.context = context;
        this.mapEventHandler = mapEventHandler;
        this.stopSelectionEventHandler = stopSelectionEventHandler;
        this.stopMonitoringQueueWorker = stopMonitoringQueueWorker;
    }

    @JavascriptInterface
    public void onStopSelected(final double lat, final double lng, final String name) {
        if (stopSelectionEventHandler.isPresent()) {
            stopSelectionEventHandler.get().onStopSelected(lat, lng, name);
        }
    }

    @JavascriptInterface
    public void onFirstStopDisplayed() {
        mapEventHandler.onFirstStopDisplayed();
    }

    @JavascriptInterface
    public int getStopCode(final double lat, final double lng) {
        try {
            return new StopConverter(context).coordinatesToStopCode(lat, lng);
        } catch (Exception ex) {
            Logging.e(context, "Cannot get stop ID by coordinates for Javascript");
            return 0;
        }
    }

    @JavascriptInterface
    public void requestStopMonitoring(int stopCode) {
        stopMonitoringQueueWorker.enqueue(stopCode);
    }

    public interface MapEventHandler {
        void onFirstStopDisplayed();
    }

    public interface StopSelectionEventHandler {
        void onStopSelected(double lat, double lng, String name);
    }
}
