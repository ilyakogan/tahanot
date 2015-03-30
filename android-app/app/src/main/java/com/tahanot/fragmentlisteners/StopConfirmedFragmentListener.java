package com.tahanot.fragmentlisteners;

import android.app.DialogFragment;

import com.tahanot.entities.StopSign;

public interface StopConfirmedFragmentListener {
	void onStopConfirmed(DialogFragment sender, StopSign selectedStop);
}