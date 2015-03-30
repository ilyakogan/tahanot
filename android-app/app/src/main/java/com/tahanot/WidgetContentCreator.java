package com.tahanot;

import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.tahanot.activities.*;
import com.tahanot.entities.*;
import com.tahanot.persistence.*;
import com.tahanot.utils.*;
import com.tahanot.web.*;
import com.tahanot.widgetupdate.*;

public class WidgetContentCreator {
	private static final String LOG = "com.tahanot";

	private static int[] lineNumberIds = new int[] { R.id.lineNumberTextViewId0, R.id.lineNumberTextViewId1, R.id.lineNumberTextViewId2,
			R.id.lineNumberTextViewId3, R.id.lineNumberTextViewId4, R.id.lineNumberTextViewId5, R.id.lineNumberTextViewId6,
			R.id.lineNumberTextViewId7, R.id.lineNumberTextViewId8, R.id.lineNumberTextViewId9, R.id.lineNumberTextViewId10,
			R.id.lineNumberTextViewId11, R.id.lineNumberTextViewId12 };
	private static int[] timeIds = new int[] { R.id.timeTextViewId0, R.id.timeTextViewId1, R.id.timeTextViewId2, R.id.timeTextViewId3,
			R.id.timeTextViewId4, R.id.timeTextViewId5, R.id.timeTextViewId6, R.id.timeTextViewId7, R.id.timeTextViewId8, R.id.timeTextViewId9,
			R.id.timeTextViewId10, R.id.timeTextViewId11, R.id.timeTextViewId12 };

	StopMonitoringProvider data = new StopMonitoringProvider();

	private Context context = BusStopApplication.getContext();

	public RemoteViews markWidgetAsLoading(int widgetId, String stopDisplayName) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		remoteViews.setTextViewText(R.id.stop_code, stopDisplayName);

		showMessageInWidget(remoteViews, widgetId, R.string.widget_message_loading, true);
		return remoteViews;
	}

	public RemoteViews markWidgetAsLoading(int widgetId) {
		WidgetPersistence persistence = new WidgetPersistence();
		String stopDisplayName = persistence.getStopDisplayName(widgetId);
		return markWidgetAsLoading(widgetId, stopDisplayName);
	}

	public RemoteViews markWidgetAsErroneous(int widgetId) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		showMessageInWidget(remoteViews, widgetId, R.string.widget_message_error, true);
		return remoteViews;
	}

	public RemoteViews markWidgetAsSavingResources(int widgetId) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		showMessageInWidget(remoteViews, widgetId, R.string.widget_message_saving_resources, false);
		return remoteViews;
	}

	public void showMessageInWidget(RemoteViews remoteViews, int widgetId, int messageId, boolean shouldAClickOpenActivity) {
		remoteViews.setViewVisibility(R.id.waiting_for_data, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.border, View.INVISIBLE);
		remoteViews.setTextViewText(R.id.waiting_for_data, context.getString(messageId));
		makeRowsSemiTransparent(remoteViews);
		if (shouldAClickOpenActivity) {
			remoteViews.setOnClickPendingIntent(R.id.stop_widget, createPendingClickHandlerToShowActivity(widgetId));
		} else {
			remoteViews.setOnClickPendingIntent(R.id.stop_widget, createPendingClickHandlerToReactivateWidgets(widgetId));
		}
	}

	public RemoteViews fillWidgetWithData(int widgetId, WidgetPersistence persistence, int stopCode, String stopDisplayName,
			StopMonitoringExtendedInfo stopMonitoring) throws Exception {
		
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			updateDataInWidget(remoteViews, stopMonitoring, stopDisplayName);
			Logging.i(context, "fillWidgetWithData: " + widgetId + " - " + stopDisplayName);

			remoteViews.setViewVisibility(R.id.waiting_for_data, View.INVISIBLE);
			remoteViews.setViewVisibility(R.id.border, View.VISIBLE);
			remoteViews.setOnClickPendingIntent(R.id.stop_widget, createPendingClickHandlerToShowActivity(widgetId));

			if (stopCode != persistence.getStopCode(widgetId)) {
				// A different stop was selected under our fingers
				return markWidgetAsErroneous(widgetId);
			} else {
				return remoteViews;
			}
	}

	private PendingIntent createPendingClickHandlerToShowActivity(int widgetId) {
		Intent clickIntent = new Intent(context, WidgetContextMenu.class);
		clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		// Make the pending intent unique
		clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private PendingIntent createPendingClickHandlerToReactivateWidgets(int widgetId) {
		Intent clickIntent = new Intent(context, WidgetUpdateService.class);
		clickIntent.putExtra("forceUpdateEvenIfTooFrequent", true);
		clickIntent.putExtra("reactivate", true);
		clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private void updateDataInWidget(RemoteViews remoteViews, StopMonitoringExtendedInfo stopMonitoring, String stopDisplayName) throws Exception {

		remoteViews.setTextViewText(R.id.stop_code, stopDisplayName);

		if (stopMonitoring == null || stopMonitoring.stopVisits == null || stopMonitoring.stopVisits.size() == 0) {
			showNoDataMessage(remoteViews, 0);
			for (int row = 1; row < lineNumberIds.length; row++) {
				hide(remoteViews, row);
			}
		} else {
			for (int row = 0; row < lineNumberIds.length; row++) {
				if (row < stopMonitoring.stopVisits.size()) {
					show(remoteViews, row, stopMonitoring.stopVisits.get(row));
				} else {
					hide(remoteViews, row);
				}
			}
		}

		makeRowsOpaque(remoteViews);
	}

	private void makeRowsSemiTransparent(RemoteViews remoteViews) {
		for (int row = 0; row < lineNumberIds.length; row++) {
			remoteViews.setTextColor(timeIds[row], 0x20ffffff);
			remoteViews.setTextColor(lineNumberIds[row], 0x20ffffff);
		}
	}

	private void makeRowsOpaque(RemoteViews remoteViews) {
		for (int row = 0; row < lineNumberIds.length; row++) {
			remoteViews.setTextColor(timeIds[row], 0xffffffff);
			remoteViews.setTextColor(lineNumberIds[row], 0xffffffff);
		}
	}

	private void show(RemoteViews remoteViews, int row, StopVisit visit) {
		remoteViews.setTextViewText(lineNumberIds[row], visit.publishedLineName);
		remoteViews.setViewVisibility(lineNumberIds[row], View.VISIBLE);

		int minutesFromNow = (int) (visit.expectedInSeconds / 60);
		remoteViews.setTextViewText(timeIds[row], Integer.toString(minutesFromNow) + "'");
		remoteViews.setViewVisibility(timeIds[row], View.VISIBLE);
	}

	private void showNoDataMessage(RemoteViews remoteViews, int row) {
		remoteViews.setViewVisibility(lineNumberIds[row], View.INVISIBLE);
		remoteViews.setTextViewText(timeIds[row], context.getResources().getString(R.string.no_data));
		remoteViews.setViewVisibility(timeIds[row], View.VISIBLE);
	}

	private void hide(RemoteViews remoteViews, int row) {
		remoteViews.setViewVisibility(lineNumberIds[row], View.INVISIBLE);
		remoteViews.setViewVisibility(timeIds[row], View.INVISIBLE);
	}
}