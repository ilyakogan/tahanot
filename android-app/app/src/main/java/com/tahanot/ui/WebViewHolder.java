package com.tahanot.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.webkit.WebView;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.tahanot.R;
import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;

import java.util.Collection;

public class WebViewHolder implements AndroidBridge.MapEventHandler, StopMonitoringQueueWorker.Listener {
    private WebView webView;
    private Activity activity;
    private Optional<AndroidBridge.StopSelectionEventHandler> stopSelectionEventHandler;
    private boolean isForWidget;
    private ProgressDialog progressDialog;
    private StopMonitoringQueueWorker stopMonitoringQueueWorker;

    public WebViewHolder(WebView webView, Activity activity, Optional<AndroidBridge.StopSelectionEventHandler> stopSelectionEventHandler, boolean isForWidget) {
        this.webView = webView;
        this.activity = activity;
        this.stopSelectionEventHandler = stopSelectionEventHandler;
        this.isForWidget = isForWidget;
    }

    public void start() {
        stopMonitoringQueueWorker = new StopMonitoringQueueWorker(activity, this);

        webView.loadUrl(getMapUrl());
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(activity.getFilesDir().getPath());
        webView.setWebChromeClient(new GeoWebChromeClient());
        webView.setWebViewClient(new CustomWebViewClient(activity, isForWidget));
        webView.addJavascriptInterface(new AndroidBridge(activity, this, stopSelectionEventHandler, stopMonitoringQueueWorker), "AndroidBridge");

        progressDialog = ProgressDialog.show(activity, "", activity.getString(R.string.loading_map), true);

        stopMonitoringQueueWorker.startRepeatingTask();
    }

    public void stop() {
        stopMonitoringQueueWorker.stopRepeatingTask();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
   }

    private String getMapUrl() {
        return String.format("file:///android_asset/map.html");
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
    public void onMonitoringInfoArrived(Collection<Integer> stopCodes, MultipleStopMonitoringExtendedInfo info) {
        Gson gson = new Gson();
        webView.loadUrl("javascript:onMonitoringInfoArrived(" + gson.toJson(stopCodes) + ", " + gson.toJson(info) + ")");
    }
}
