package com.tahanot.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Location;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.tahanot.R;
import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;

public class WebViewHolder implements AndroidBridge.MapEventHandler, StopMonitoringQueueWorker.Listener {
    private WebView webView;
    private Activity activity;
    private Optional<AndroidBridge.StopSelectionEventHandler> stopSelectionEventHandler;
    private ProgressDialog progressDialog;
    private StopMonitoringQueueWorker stopMonitoringQueueWorker;

    public WebViewHolder(WebView webView, Activity activity, Optional<AndroidBridge.StopSelectionEventHandler> stopSelectionEventHandler) {
        this.webView = webView;
        this.activity = activity;
        this.stopSelectionEventHandler = stopSelectionEventHandler;
    }

    public void start() {
        stopMonitoringQueueWorker = new StopMonitoringQueueWorker(activity, this);

        Location location = LocationProxy.get().getLastLocation(true);
        webView.loadUrl(getMapUrl(location));
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new HelloWebViewClient(activity));
        webView.addJavascriptInterface(new AndroidBridge(activity, this, stopSelectionEventHandler, stopMonitoringQueueWorker), "AndroidBridge");

        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.loading_map), true);

        stopMonitoringQueueWorker.startRepeatingTask();
    }

    public void stop() {
        stopMonitoringQueueWorker.stopRepeatingTask();
    }

    private String getMapUrl(Location location) {
        if (location != null) {
            return String.format("file:///android_asset/map.html?lat=%s&lng=%s", location.getLatitude(), location.getLongitude());
        }
        else {
            return "file:///android_asset/map.html";
        }
    }

    @Override
    public void onFirstStopDisplayed() {
        activity.runOnUiThread(() -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        });
    }

    @Override
    public void onMonitoringInfoArrived(MultipleStopMonitoringExtendedInfo info) {
        webView.loadUrl("javascript:onMonitoringInfoArrived(" + new Gson().toJson(info) + ")");
    }
}
