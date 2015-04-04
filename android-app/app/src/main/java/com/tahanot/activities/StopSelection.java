package com.tahanot.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tahanot.R;
import com.tahanot.ResourceSaver;
import com.tahanot.WidgetContentCreator;
import com.tahanot.activities.fragments.StopSignDialogFragment;
import com.tahanot.activities.interfaces.GoogleStopSelectedListener;
import com.tahanot.activities.interfaces.StopSelectedListener;
import com.tahanot.entities.GtfsStop;
import com.tahanot.entities.StopSign;
import com.tahanot.fragmentlisteners.StopConfirmedFragmentListener;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.tasks.BringStopTask;
import com.tahanot.utils.LocationProxy;
import com.tahanot.utils.Logging;
import com.tahanot.widgetupdate.WidgetUpdateService;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StopSelection extends WidgetConfigActivity implements StopSelectedListener, StopConfirmedFragmentListener, GoogleStopSelectedListener {
    private int DEFAULT_TAB = 2;
    private int widgetId;
    private StopSignDialogFragment mDialog;
    private boolean mIsActive;

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
        mIsActive = true;

        Intent intent = getIntent();
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        // Fix: If for some reason the widget is missing from the list of widgets to update, add it again.
        // new WidgetPersistence(this).addWidget(widgetId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActive = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onStopSelected(double lat, double lng) {
        String resourceName = String.format("stop_%.6f_%.6f", round(lat, 6), round(lng, 6));
        int identifier = getResources().getIdentifier(resourceName, "integer", getPackageName());
        int stopCode = getResources().getInteger(identifier);

        StopSign stopSign = new StopSign();
        stopSign.RawStop = new GtfsStop();
        stopSign.RawStop.Code = stopCode;

        if (!mIsActive || isStopSignDialogShown()) return;
        mDialog = new StopSignDialogFragment();
        Logging.i(this, "onStopSelected: " + stopSign.RawStop.Code);
        mDialog.setStopSign(stopSign);
        mDialog.show(getFragmentManager(), "stop_sign");
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public synchronized void onStopSelected(StopSign selectedStop) {
        if (!mIsActive || isStopSignDialogShown()) return;
        mDialog = new StopSignDialogFragment();
        Logging.i(this, "onStopSelected: " + selectedStop.RawStop.Code);
        mDialog.setStopSign(selectedStop);
        mDialog.show(getFragmentManager(), "stop_sign");
    }

    @Override
    public void onStopCodeSelected(int stopCode) {
        if (!mIsActive || isStopSignDialogShown()) return;
        Toast.makeText(this, R.string.bringingStopDetails, Toast.LENGTH_SHORT).show();
        new BringStopTask(0, this, this).execute(new Integer[]{stopCode});
    }

    private boolean isStopSignDialogShown() {
        if (mDialog == null) return false;
        Dialog dialog = mDialog.getDialog();
        return dialog != null && dialog.isShowing();
    }

    @Override
    public void onStopConfirmed(DialogFragment sender, StopSign selectedStop) {
        ResourceSaver.userJustDidSomething();

        saveStopDetails(selectedStop);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new WidgetContentCreator().markWidgetAsLoading(widgetId, selectedStop.RawStop.Name);
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        startWidgetUpdateService(selectedStop);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    private void startWidgetUpdateService(StopSign selectedStop) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new WidgetContentCreator().markWidgetAsLoading(widgetId, selectedStop.RawStop.Name.toString());
        appWidgetManager.updateAppWidget(widgetId, remoteViews);

        WidgetUpdateService.startService(this, true);
    }

    private void saveStopDetails(StopSign selectedStop) {
        WidgetPersistence persistence = new WidgetPersistence();
        persistence.setStopCode(widgetId, selectedStop.RawStop.Code);
        persistence.setStopRealName(widgetId, selectedStop.RawStop.Name);
        persistence.setStopDisplayName(widgetId, selectedStop.RawStop.Name);
    }
}
