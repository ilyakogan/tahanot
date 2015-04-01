package com.tahanot.activities;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.tahanot.R;
import com.tahanot.ResourceSaver;
import com.tahanot.WidgetContentCreator;
import com.tahanot.activities.fragments.FindStopByCodeFragment;
import com.tahanot.activities.fragments.MapFragment;
import com.tahanot.activities.fragments.NearbyStopsFragment;
import com.tahanot.activities.fragments.StopSignDialogFragment;
import com.tahanot.activities.interfaces.StopSelectedListener;
import com.tahanot.analytics.UiAnalytics;
import com.tahanot.entities.StopSign;
import com.tahanot.fragmentlisteners.StopConfirmedFragmentListener;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.tasks.BringStopTask;
import com.tahanot.utils.Logging;
import com.tahanot.utils.TabListener;
import com.tahanot.widgetupdate.WidgetUpdateService;

public class StopSelection extends WidgetConfigActivity implements StopSelectedListener, StopConfirmedFragmentListener {
    private int DEFAULT_TAB = 2;
    private int widgetId;
    private StopSignDialogFragment mDialog;
    private boolean mIsActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UiAnalytics.trackScreen(this, "StopSelection");

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        Logging.i(this, "StopSelection.onCreate");
        setContentView(R.layout.generic);

        final ActionBar bar = getActionBar();
        // Cannot be called before getActionBar - see
        // https://groups.google.com/forum/?fromgroups#!topic/actionbar/ixMX62VBcBY
        setProgressBarIndeterminateVisibility(false);
        setTitle(getResources().getString(R.string.stop_selection));
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        bar.addTab(bar.newTab().setText(getString(R.string.tab_title_nearby))
                .setTabListener(new TabListener<NearbyStopsFragment>(this, "NearbyStops", NearbyStopsFragment.class)));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_title_search_by_code))
                .setTabListener(new TabListener<FindStopByCodeFragment>(this, "FindStopByCode", FindStopByCodeFragment.class)));
        bar.addTab(bar.newTab().setText(getString(R.string.tab_title_map))
                .setTabListener(new TabListener<MapFragment>(this, "Map", MapFragment.class)));

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", DEFAULT_TAB));
        } else {
            bar.setSelectedNavigationItem(DEFAULT_TAB);
        }
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
