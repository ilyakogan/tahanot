package com.tahanot.activities.interfaces;

import com.tahanot.entities.*;

public interface StopSelectedListener {
	void onStopSelected(StopSign selectedStop);
	void onStopCodeSelected(int code);
}
