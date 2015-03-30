package com.tahanot.persistence;

import java.util.*;

import com.tahanot.*;
import com.tahanot.utils.*;

import android.content.*;
import android.util.Log;

public class WidgetPersistence {
	private static final String LOG = "com.tahanot";
	private static final String PREFERENCES_FILE = "StopWidgetPreferences";
	private SharedPreferences prefs;

	public WidgetPersistence() {
		prefs = BusStopApplication.getContext().getSharedPreferences(PREFERENCES_FILE, 0);
	}

	public void addWidgets(int[] appWidgetIds) {
		SharedPreferences.Editor editor = prefs.edit();
		for (int widgetId : appWidgetIds)
		{
			String key = Keys.widgetActive(widgetId);
			editor.putBoolean(key, true);
			//Logging.i(LOG, "Setting " + key + " = true");
		}
		editor.commit();
	}
	
	public void addWidget(int widgetId) {
		SharedPreferences.Editor editor = prefs.edit();
		String key = Keys.widgetActive(widgetId);
		editor.putBoolean(key, true);
		//Logging.i(LOG, "Setting " + key + " = true");
		editor.commit();
	}

	public void removeWidgets(int[] appWidgetIds) {
		SharedPreferences.Editor editor = prefs.edit();
		for (int widgetId : appWidgetIds)
		{
			String key = Keys.widgetActive(widgetId);
			editor.putBoolean(key, false);
			Logging.i(BusStopApplication.getContext(), "Setting " + key + " = false");
		}
		editor.commit();
	}
	
	public void removeWidget(int widgetId) {
		SharedPreferences.Editor editor = prefs.edit();
		String key = Keys.widgetActive(widgetId);
		editor.putBoolean(key, false);
		Logging.i(BusStopApplication.getContext(), "Setting " + key + " = false");
		editor.commit();
	}

	public int[] getActiveWidgetIds() { 
		ArrayList<Integer> widgetIds = new ArrayList<Integer>();
		Map<String, ?> items = prefs.getAll();
		for (String s : items.keySet()) {
			if (s.startsWith(Keys.WIDGET_ACTIVE_PREFIX)) {
				int widgetId = Integer.parseInt(s.substring(Keys.WIDGET_ACTIVE_PREFIX.length()));
				boolean isActive = Boolean.parseBoolean(items.get(s).toString());
				if (isActive) {
					widgetIds.add(widgetId);
				}
			}
		}
		
		return ToIntArray(widgetIds);
	}

	private int[] ToIntArray(ArrayList<Integer> widgetIds) {
		int[] ret = new int[widgetIds.size()];
	    Iterator<Integer> iterator = widgetIds.iterator();
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = iterator.next().intValue();
	    }
		return ret;
	}

	public void setStopCode(int widgetId, int stopCode) {
		SharedPreferences.Editor editor = prefs.edit();
		String key = Keys.stopCode(widgetId);
		editor.putInt(key, stopCode);
		Logging.i(BusStopApplication.getContext(), "Setting " + key + " = " + stopCode);
		editor.commit();
	}

	public int getStopCode(int widgetId) throws NoSuchElementException {
		String key = Keys.stopCode(widgetId);
		int stopCode = prefs.getInt(key, 0);
		return stopCode;
	}

	public void setStopDisplayName(int widgetId, String stopDisplayName) {
		String key = Keys.stopDisplayName(widgetId);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, stopDisplayName);
		Logging.i(BusStopApplication.getContext(), "Setting " + key + " = " + stopDisplayName);
		editor.commit();
	}

	public String getStopDisplayName(int widgetId) throws NoSuchElementException {
		String key = Keys.stopDisplayName(widgetId);
		String stopCode = prefs.getString(key, null);
		return stopCode;
	}
	
	public void setStopRealName(int widgetId, String stopRealName) {
		String key = Keys.stopRealName(widgetId);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, stopRealName);
		Logging.i(BusStopApplication.getContext(), "Setting " + key + " = " + stopRealName);
		editor.commit();
	}

	public String getStopRealName(int widgetId) throws NoSuchElementException {
		String key = Keys.stopRealName(widgetId);
		String stopCode = prefs.getString(key, null);
		return stopCode;
	}
	
	public long getLastTimeWidgetsWereUpdated() throws NoSuchElementException {
		String key = Keys.LAST_TIME_WIDGETS_WERE_UPDATED;
		return prefs.getLong(key, 0);
	}
	
	public void setLastTimeWidgetsWereUpdated(long value) {
		String key = Keys.LAST_TIME_WIDGETS_WERE_UPDATED;
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		//Logging.i(LOG, "Setting " + key + " = " + value);
		editor.commit();
	}

	private static class Keys {
		public static final String LAST_TIME_WIDGETS_WERE_UPDATED = "lastTimeWidgetsWereUpdated";

		public static String stopCode(int widgetId) {
			return "stopForWidget" + widgetId;
		}

		public static final String WIDGET_ACTIVE_PREFIX = "widgetActive";

		public static String widgetActive(int widgetId) {
			return WIDGET_ACTIVE_PREFIX + widgetId;
		}

		public static String stopDisplayName(int widgetId) {
			return "stopDisplayName" + widgetId;
		}
		
		public static String stopRealName(int widgetId) {
			return "stopRealName" + widgetId;
		}
	}

}
