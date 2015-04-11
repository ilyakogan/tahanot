package com.tahanot.ui;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.RemoteViews;

import com.google.common.base.Optional;
import com.tahanot.R;
import com.tahanot.WidgetContentCreator;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.widgetupdate.WidgetUpdateService;

public class StopSelectionActivity extends Activity implements AndroidBridge.StopSelectionEventHandler {
    private int widgetId;
    WebViewHolder webViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webViewHolder = new WebViewHolder((WebView) findViewById(R.id.webView1), this, Optional.of(this));
        webViewHolder.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webViewHolder.stop();
    }

    public void onStopSelected(double lat, double lng, String name) {
        runOnUiThread(() -> {
            int stopCode = new StopConverter(this).coordinatesToStopCode(lat, lng);
            Stop stop = new Stop(stopCode, name);
            succeed(stop);
        });
    }

    private void succeed(Stop stop) {
        saveStopDetails(stop);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new WidgetContentCreator().markWidgetAsLoading(widgetId, stop.Name);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        startWidgetUpdateService(stop);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void startWidgetUpdateService(Stop selectedStop) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new WidgetContentCreator().markWidgetAsLoading(widgetId, selectedStop.Name);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);
        WidgetUpdateService.startService(this, true);
    }

    private void saveStopDetails(Stop stop) {
        WidgetPersistence persistence = new WidgetPersistence();
        persistence.setStopCode(widgetId, stop.Code);
        persistence.setStopRealName(widgetId, stop.Name);
        persistence.setStopDisplayName(widgetId, stop.Name);
    }


}
