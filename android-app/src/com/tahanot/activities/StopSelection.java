package com.tahanot.activities;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.DialogFragment;

import com.actionbarsherlock.view.Window;
import android.widget.*;

import com.actionbarsherlock.app.ActionBar;
import com.google.analytics.tracking.android.*;
import com.tahanot.*;
import com.tahanot.activities.fragments.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.entities.*;
import com.tahanot.fragmentlisteners.*;
import com.tahanot.persistence.*;
import com.tahanot.tasks.*;
import com.tahanot.utils.*;
import com.tahanot.widgetupdate.*;

public class StopSelection extends WidgetConfigActivity implements StopSelectedListener, StopConfirmedFragmentListener {
	private int DEFAULT_TAB = 2;
	private int widgetId;
	private StopSignDialogFragment mDialog;
	private boolean mIsActive;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		EasyTracker.getInstance().setContext(this);
		EasyTracker.getTracker().sendView("StopSelection");	

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		Logging.i(this, "StopSelection.onCreate");
		setContentView(R.layout.generic);

		final ActionBar bar = getSupportActionBar();
		// Cannot be called before getSupportActionBar - see
		// https://groups.google.com/forum/?fromgroups#!topic/actionbarsherlock/ixMX62VBcBY
		setSupportProgressBarIndeterminateVisibility(false);
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
		}
		else {
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
		// TODO Auto-generated method stub
		super.onPause();
		mIsActive = false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
	}

	@Override
	public synchronized void onStopSelected(StopSign selectedStop) {		
		if (!mIsActive || isStopSignDialogShown()) return;
		mDialog = new StopSignDialogFragment();
		Logging.i(this, "onStopSelected: " + selectedStop.RawStop.Code);
		mDialog.setStopSign(selectedStop);
		mDialog.show(getSupportFragmentManager(), "stop_sign");
	}
	
	@Override
	public void onStopCodeSelected(int stopCode) {
		if (!mIsActive || isStopSignDialogShown()) return;
		Toast.makeText(this, R.string.bringingStopDetails, Toast.LENGTH_SHORT).show();
		new BringStopTask(0, this, this).execute(new Integer[] {stopCode});		
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
