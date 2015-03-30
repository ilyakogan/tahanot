package com.tahanot;

import java.util.*;

import android.appwidget.*;
import android.content.*;

import com.tahanot.persistence.*;
import com.tahanot.utils.*;

public class WidgetIds {
	static public int[] getIds(Context context) {

		ComponentName name = new ComponentName(context, WidgetProvider.class);
		
		// Using getAppWidgetIds is an attempt to fix the bug on Ofer's device.
		// However, now there's a different problem: after removing a widget, it still gets updated as "loading", 
		// thus wasting resources.
		int[] frameworkWidgets = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
		int[] savedWidgets = new WidgetPersistence().getActiveWidgetIds();

		int[] union = union(frameworkWidgets, savedWidgets);

		Logging.i(context, "frameworkWidgets: " + frameworkWidgets.length + ", savedWidgets: " + savedWidgets.length + ", union: " + union.length);

		return union;
	}

	static private int[] union(int[] aInt, int[] bInt) {

		Integer[] aInteger = new Integer[aInt.length];
		for (int i = 0; i < aInt.length; i++) {
			aInteger[i] = aInt[i];
		}

		Integer[] bInteger = new Integer[bInt.length];
		for (int i = 0; i < bInt.length; i++) {
			bInteger[i] = bInt[i];
		}

		Set<Integer> set = new HashSet<Integer>(Arrays.asList(aInteger));
		set.addAll(Arrays.asList(bInteger));
		Integer[] unionArrayInteger = set.toArray(new Integer[set.size()]);

		int[] unionArrayInt = new int[unionArrayInteger.length];
		for (int i = 0; i < unionArrayInteger.length; i++) {
			unionArrayInt[i] = unionArrayInteger[i];
		}

		return unionArrayInt;
	}
}
