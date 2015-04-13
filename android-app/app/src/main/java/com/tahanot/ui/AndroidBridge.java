package com.tahanot.ui;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Optional;
import com.tahanot.R;

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
        try {
            if (stopSelectionEventHandler.isPresent()) {
                stopSelectionEventHandler.get().onStopSelected(lat, lng, name);
            }
        }
        catch (Exception ex)
        {
            Crashlytics.logException(ex);
            Toast.makeText(context, R.string.error_occurred, Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void onFirstStopDisplayed() {
        try {
            mapEventHandler.onFirstStopDisplayed();
        }
        catch (Exception ex)
        {
            Crashlytics.logException(ex);
        }
    }

    @JavascriptInterface
    public int getStopCode(final double lat, final double lng) {
        try {
            return new StopConverter(context).coordinatesToStopCode(lat, lng);
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            return 0;
        }
    }

    @JavascriptInterface
    public void requestStopMonitoring(int stopCode) {
        try {
            Crashlytics.log("Stop monitoring requested by web view");
            stopMonitoringQueueWorker.enqueue(stopCode);
        } catch (Exception ex) {
            Crashlytics.logException(ex);
        }
    }

    public interface MapEventHandler {
        void onFirstStopDisplayed();
    }

    public interface StopSelectionEventHandler {
        void onStopSelected(double lat, double lng, String name);
    }
}
