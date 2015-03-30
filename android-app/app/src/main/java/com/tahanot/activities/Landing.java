package com.tahanot.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tahanot.R;
import com.tahanot.activities.fragments.AboutFragment;
import com.tahanot.activities.fragments.InstructionsFragment;
import com.tahanot.utils.TabListener;

import java.util.HashMap;

public class Landing extends Activity {

	public static final String SELECT_TAB_PARAM = "selectTab";
	public static final int TAB_INSTRUCTIONS = 0;
	public static final int TAB_ABOUT = 1;
	public HashMap<Integer, ActionBar.Tab> mTabs = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // TODO: Analytics
		//Crashlytics.start(this);
		setContentView(R.layout.generic);

		final ActionBar bar = getActionBar();
		bar.setTitle(R.string.intro_title);
		bar.setDisplayShowTitleEnabled(true);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab about = bar.newTab().setText(getString(R.string.tab_title_about))
				.setTabListener(new TabListener<AboutFragment>(this, "About", AboutFragment.class));
		mTabs.put(TAB_ABOUT, about);

		ActionBar.Tab instructions = bar.newTab().setText(getString(R.string.tab_title_instructions))
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
			getActionBar().selectTab(mTabs.get(tabNumber));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Fix: If for some reason the widget is missing from the list of widgets to update, add it again.
		// new WidgetPersistence(this).addWidget(widgetId);
	}
}
