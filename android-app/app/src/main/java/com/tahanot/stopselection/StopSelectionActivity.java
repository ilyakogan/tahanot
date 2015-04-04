package com.tahanot.stopselection;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.RemoteViews;

import com.tahanot.R;
import com.tahanot.WidgetContentCreator;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.widgetupdate.WidgetUpdateService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StopSelectionActivity extends Activity implements GoogleStopSelectedListener {
    private int widgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Location location = LocationProxy.get().getLastLocation(true);
        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.loadUrl(String.format("file:///android_asset/map.html?lat=%s&lng=%s", location.getLatitude(), location.getLongitude()));
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(this), "AndroidBridge");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onStopSelected(double lat, double lng, String name) {
        String resourceName = String.format("stop_%.6f_%.6f", round(lat, 6), round(lng, 6));
        int identifier = getResources().getIdentifier(resourceName, "integer", getPackageName());
        int stopCode = getResources().getInteger(identifier);

        Stop stop = new Stop(stopCode, name);
        showAreYouSureDialog(stop);
    }

    private void showAreYouSureDialog(final Stop stop) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.is_this_the_right_stop)
                .setMessage(stop.Code + "\n" + stop.Name)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        succeed(stop);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.bus_stop_sign_icon_50)
                .show();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
