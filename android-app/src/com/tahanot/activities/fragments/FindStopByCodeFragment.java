package com.tahanot.activities.fragments;

import android.os.*;
import android.os.AsyncTask.Status;
import android.view.*;
import com.actionbarsherlock.view.MenuInflater;
import android.view.inputmethod.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.*;
import com.tahanot.*;
import com.tahanot.activities.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.entities.*;
import com.tahanot.fragmentlisteners.*;
import com.tahanot.tasks.*;
import com.tahanot.web.*;

public class FindStopByCodeFragment extends SherlockFragment {// implements StopSelectedListener {
	private int mWidgetId;
	private BringStopTask mBringStopTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		setHasOptionsMenu(true);
		mWidgetId = WidgetConfigActivity.class.isInstance(getActivity()) ? ((WidgetConfigActivity) getActivity()).getWidgetId() : 0;

		getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.find_stop_by_code, container, false);

		TextView.OnEditorActionListener textViewEditorActionListener = new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_SEARCH) {
					onSearchAction(view);
					return true;
				}
				return false;
			}
		};

		getEditText(v).setOnEditorActionListener(textViewEditorActionListener);
		
		EasyTracker.getInstance().setContext(getActivity());
		EasyTracker.getTracker().sendView("Find Stop By Code tab");
		
		return v;
	}

	private EditText getEditText(View view) {
		return (EditText) view.findViewById(R.id.stopCode);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mBringStopTask != null) mBringStopTask.cancel(true);
		getEditText(getView()).setOnEditorActionListener(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.stop_search_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.search_item:
			EditText editText = (EditText) getView().findViewById(R.id.stopCode);
			onSearchAction(editText);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onSearchAction(TextView editText) {
		//if (mBringStopTask != null && mBringStopTask.getStatus() != Status.FINISHED) return;
		if (editText.getText().length() == 0) return;
		int stopCode = Integer.parseInt(editText.getText().toString());
		((StopSelectedListener) getActivity()).onStopCodeSelected(stopCode);
//		Integer params[] = new Integer[] { stopCode };
//		mBringStopTask = new BringStopTask(mWidgetId, getSherlockActivity(), this);
//		mBringStopTask.execute(params);
	}

//	@Override
//	public void onStopSelected(StopSign selectedStop) {
//		((StopSelectedFragmentListener) getActivity()).onStopSelected(this, selectedStop);
//	}

	
}
