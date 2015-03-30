package com.tahanot.timers;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.Log;

public class WidgetUpdateTimer {

	private static String FREQUENT_UPDATE_INTENT_NAME = "com.tahanot.widget.FREQUENT_WIDGET_UPDATE";
	private static final int INTERVAL_SEC = 30;
	private static final int INTERVAL_SOON_SEC = 5;
	
	public static void restartNormal(Context context)
	{
		restart(context, INTERVAL_SEC);
	}

	public static void restartSoon(Context context)
	{
		restart(context, INTERVAL_SOON_SEC);
	}

	public static void stop(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(createFrequentUpdateIntent(context));
	}

	private static void restart(Context context, int intervalSec) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		long nextTime = SystemClock.elapsedRealtime() + intervalSec * 1000;
		alarmManager.set(AlarmManager.ELAPSED_REALTIME, nextTime, createFrequentUpdateIntent(context));
	}

	static private PendingIntent createFrequentUpdateIntent(Context context) {		
		Intent intent = new Intent(FREQUENT_UPDATE_INTENT_NAME);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}
}
