package com.tahanot.tasks;

import java.net.*;

import android.os.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.tahanot.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.entities.*;
import com.tahanot.web.*;

public class BringStopTask extends AsyncTask<Integer, Void, StopSignResult> {
	int mWidgetId;
	private SherlockFragmentActivity mActivity;
	private StopSelectedListener mStopSelectedListener;		
	
	public BringStopTask(int widgetId, SherlockFragmentActivity activity, StopSelectedListener stopSelectedListener) {
		mWidgetId = widgetId;
		mActivity = activity;
		mStopSelectedListener = stopSelectedListener;
	}
	
	@Override
	protected void onPreExecute() {
		onStopLoadingStarted();
	}
	
	protected StopSignResult doInBackground(Integer... stopCodes) {
		return new StopSignProvider().getStopSignByCode(stopCodes[0], mWidgetId);
	}

	protected void onPostExecute(StopSignResult result) {
		if (isCancelled()) return;
		onStopLoadingEnded();
		if (result.mStopSign != null) {
			mStopSelectedListener.onStopSelected(result.mStopSign);
		}
		else if (result.mException != null && UnknownHostException.class.isInstance(result.mException)) {
			Toast.makeText(mActivity, R.string.error_bringing_stop_connection_error, Toast.LENGTH_LONG).show();
		}
		else if (result.mException != null) {
			Toast.makeText(mActivity, R.string.error_bringing_stop, Toast.LENGTH_LONG).show();
		}
		else { 
			Toast.makeText(mActivity, R.string.stop_not_found, Toast.LENGTH_LONG).show();
		}
	}
	
	private void onStopLoadingStarted() {
		mActivity.setSupportProgressBarIndeterminateVisibility(true);		
	}

	private void onStopLoadingEnded() {
		mActivity.setSupportProgressBarIndeterminateVisibility(false);
	}
}