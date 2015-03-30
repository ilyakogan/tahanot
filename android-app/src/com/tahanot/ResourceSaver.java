package com.tahanot;

import com.tahanot.utils.*;

import android.app.*;
import android.content.*;

public class ResourceSaver {
	private static long TIME_TO_KEEP_LIVE = 1000 * 60 * 5;
	
	private static KeyguardManager keyguardManager = null;
	private static long lastManualActionTime = System.currentTimeMillis();
	
	
	public static boolean shouldWidgetsBeActive()
	{
		if (isScreenLocked()) return false;
		Logging.i(BusStopApplication.getContext(), "resource manager: time is " + System.currentTimeMillis() + 
				", difference is " + (System.currentTimeMillis() - lastManualActionTime));
		if (System.currentTimeMillis() - lastManualActionTime > TIME_TO_KEEP_LIVE) return false;
		return true;
	}
	
	private static boolean isScreenLocked() {
		return getKeyguardManager().inKeyguardRestrictedInputMode();
	}

	private static KeyguardManager getKeyguardManager() {
		if (keyguardManager != null)
			return keyguardManager;
		keyguardManager = (KeyguardManager) BusStopApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
		return keyguardManager;
	}

	public static void userJustDidSomething() {
		Logging.i(BusStopApplication.getContext(), "resource manager: setting time to " + (System.currentTimeMillis()));
		lastManualActionTime = System.currentTimeMillis();
	}	
}
