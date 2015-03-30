package com.tahanot.activities.fragments;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.tahanot.*;
import com.tahanot.adapters.*;
import com.tahanot.entities.*;
import com.tahanot.fragmentlisteners.*;

public class StopSignDialogFragment extends SherlockDialogFragment {

	private StopSignDialogFragment mFragment;
	private StopSign mStopSign;

	public StopSignDialogFragment() {
		mFragment = this;
	}

	public void setStopSign(StopSign stopSign) {
		mStopSign = stopSign;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.stop_sign, null);

		final StopSignRoutesAdapter adapter = new StopSignRoutesAdapter(getActivity(), R.layout.stop_sign_route, mStopSign.Routes);
		((ListView) v.findViewById(R.id.routesListView)).setAdapter(adapter);

		((TextView) v.findViewById(R.id.stopCode)).setText(String.valueOf(mStopSign.RawStop.Code));
		((TextView) v.findViewById(R.id.stopName)).setText(mStopSign.RawStop.Name);

		return createDialog(v);
	}

	private AlertDialog createDialog(final View v) {
		return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.Theme_Sherlock_Light))
				// On old Androids, make the dialog light
				.setInverseBackgroundForced(true)
				//
				.setTitle(R.string.is_this_the_right_stop).setView(v).setCancelable(true)
				.setPositiveButton(R.string.this_is_the_right_stop, new YesClickListener())
				// .setNegativeButton(R.string.select_different_stop, new NoClickListener())
				.create();
	}

	final class YesClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			FragmentActivity activity = mFragment.getActivity();
			mFragment.dismiss();
			((StopConfirmedFragmentListener) activity).onStopConfirmed(mFragment, mStopSign);
		}
	}
}
