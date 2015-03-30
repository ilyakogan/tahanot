package com.tahanot.utils;

import android.content.*;
import android.util.*;

public final class Logging {
	private static final boolean LOGGING_ENABLED = false;
	
	public static void d(Context context, String message)
	{
		if (LOGGING_ENABLED)
		{
			Log.d(context.getPackageName(), message);
		}
	}
	
	public static void i(Context context, String message)
	{
		if (LOGGING_ENABLED)
		{
			Log.i(context.getPackageName(), message);
		}
	}
	
	public static void w(Context context, String message)
	{
		if (LOGGING_ENABLED)
		{
			Log.w(context.getPackageName(), message);
		}
	}
	
	public static void e(Context context, String message)
	{
		if (LOGGING_ENABLED)
		{
			Log.e(context.getPackageName(), message);
		}
	}
}
