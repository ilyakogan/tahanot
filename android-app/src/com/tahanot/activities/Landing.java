package com.tahanot.activities;

import com.crashlytics.android.Crashlytics;
import java.util.*;

import android.appwidget.*;
import android.content.*;
import android.os.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.*;
import com.google.analytics.tracking.android.*;
import com.tahanot.*;
import com.tahanot.activities.fragments.*;
import com.tahanot.utils.*;

public class Landing extends SherlockFragmentActivity {

	public static final String SELECT_TAB_PARAM = "selectTab";
	public static final int TAB_INSTRUCTIONS = 0;
	public static final int TAB_ABOUT = 1;
	public HashMap<Integer, Tab> mTabs = new HashMap<Integer, Tab>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Crashlytics.start(this);
		setContentView(R.layout.generic);

		final ActionBar bar = getSupportActionBar();
		bar.setTitle(R.string.intro_title);
		bar.setDisplayShowTitleEnabled(true);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab about = bar.newTab().setText(getString(R.string.tab_title_about))
				.setTabListener(new TabListener<AboutFragment>(this, "About", AboutFragment.class));
		mTabs.put(TAB_ABOUT, about);

		Tab instructions = bar.newTab().setText(getString(R.string.tab_title_instructions))
				.setTabListener(new TabListener<InstructionsFragment>(this, "Instructions", InstructionsFragment.class));
		mTabs.put(TAB_INSTRUCTIONS, instructions);

		bar.addTab(about);
		bar.addTab(instructions);

		if (savedInstanceState != null) {
			bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			Intent intent = getIntent();
			int tabNumber = intent.getIntExtra(SELECT_TAB_PARAM, TAB_INSTRUCTIONS);
			getSupportActionBar().selectTab(mTabs.get(tabNumber));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Fix: If for some reason the widget is missing from the list of widgets to update, add it again.
		// new WidgetPersistence(this).addWidget(widgetId);
	}
}
