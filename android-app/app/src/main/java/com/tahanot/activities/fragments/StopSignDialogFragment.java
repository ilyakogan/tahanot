package com.tahanot.activities.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.tahanot.R;
import com.tahanot.adapters.StopSignRoutesAdapter;
import com.tahanot.entities.StopSign;
import com.tahanot.fragmentlisteners.StopConfirmedFragmentListener;

public class StopSignDialogFragment extends DialogFragment {

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
		return new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light))
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
			Activity activity = mFragment.getActivity();
			mFragment.dismiss();
			((StopConfirmedFragmentListener) activity).onStopConfirmed(mFragment, mStopSign);
		}
	}
}
