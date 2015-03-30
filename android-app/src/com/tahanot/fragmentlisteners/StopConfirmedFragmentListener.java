package com.tahanot.fragmentlisteners;

import android.support.v4.app.*;

import com.actionbarsherlock.app.*;
import com.tahanot.entities.*;

public interface StopConfirmedFragmentListener {
	void onStopConfirmed(DialogFragment sender, StopSign selectedStop);
}