package com.tahanot;

import com.tahanot.persistence.*;
import com.tahanot.timers.*;
import com.tahanot.utils.*;
import com.tahanot.widgetupdate.*;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		WidgetPersistence widgetPersistence = new WidgetPersistence();
		widgetPersistence.addWidgets(appWidgetIds);
		WidgetUpdateService.startService(context, false);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		WidgetPersistence widgetPersistence = new WidgetPersistence();
		widgetPersistence.removeWidgets(appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		WidgetUpdateService.startService(context, true);

	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		WidgetUpdateTimer.stop(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().contains("FREQUENT_WIDGET_UPDATE")) {
			Logging.i(BusStopApplication.getContext(), "WidgetProvider received FREQUENT_WIDGET_UPDATE");
			WidgetUpdateService.startService(context, false);
		}
	}
}