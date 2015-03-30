package com.tahanot.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tahanot.R;
import com.tahanot.activities.interfaces.StopSelectedListener;
import com.tahanot.entities.StopSignResult;
import com.tahanot.web.StopSignProvider;

import java.net.UnknownHostException;

public class BringStopTask extends AsyncTask<Integer, Void, StopSignResult> {
	int mWidgetId;
	private Activity mActivity;
	private StopSelectedListener mStopSelectedListener;		
	
	public BringStopTask(int widgetId, Activity activity, StopSelectedListener stopSelectedListener) {
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
		mActivity.setProgressBarIndeterminateVisibility(true);
	}

	private void onStopLoadingEnded() {
		mActivity.setProgressBarIndeterminateVisibility(false);
	}
}