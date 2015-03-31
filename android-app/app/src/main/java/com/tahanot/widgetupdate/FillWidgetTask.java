package com.tahanot.widgetupdate;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;

import com.tahanot.BusStopApplication;
import com.tahanot.FillWidgetResult;
import com.tahanot.WidgetContentCreator;
import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;
import com.tahanot.entities.StopMonitoringExtendedInfo;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.utils.CancellationFunction;
import com.tahanot.utils.ExpirationToken;
import com.tahanot.utils.Logging;
import com.tahanot.web.StopMonitoringProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class FillWidgetTask extends AsyncTask<Integer, Void, FillWidgetResult> {
	private final AppWidgetManager appWidgetManager;
	private Context context = BusStopApplication.getContext();
	boolean updatedWidgets = false;
	private ExpirationToken mExpirationToken;

	FillWidgetTask(AppWidgetManager appWidgetManager, ExpirationToken expirationToken) {
		this.appWidgetManager = appWidgetManager;
		mExpirationToken = expirationToken;
	}

	@Override
	protected FillWidgetResult doInBackground(Integer... widgetIds) {
		WidgetContentCreator contentCreator = new WidgetContentCreator();
		try {
			final FillWidgetTask task = this;
			CancellationFunction cancellationFunction = new CancellationFunction() {
				public boolean isCancelled() {
					return task.isCancelled() || mExpirationToken.hasExpired();
				}
			};

			WidgetPersistence persistence = new WidgetPersistence();

			ArrayList<Integer> stopCodes = new ArrayList<Integer>();
			for (int widgetId : widgetIds) {
				int stopCode = persistence.getStopCode(widgetId);
				stopCodes.add(stopCode);
			}

			MultipleStopMonitoringExtendedInfo multipleStopMonitoring = new StopMonitoringProvider().getMultipleStopMonitoring(stopCodes, context);

			if (cancellationFunction.isCancelled()) {
				Logging.e(context, "Error filling widgets: task was cancelled");
				return markAllWidgetsArsErroneous(contentCreator, widgetIds);
			}
			
			if (multipleStopMonitoring.error != null && multipleStopMonitoring.error != "") {
				Logging.e(context, "Error in multipleStopMonitoring: " + multipleStopMonitoring.error);
				return markAllWidgetsArsErroneous(contentCreator, widgetIds);
			}

			boolean allSucceeded = true;

			HashMap<Integer, RemoteViews> remoteViewsByWidget = new HashMap<Integer, RemoteViews>();
			for (int widgetId : widgetIds) {
				try {
					int stopCode = persistence.getStopCode(widgetId);
					if (stopCode == 0) {
						Logging.i(context, "Not filling widget yet: stop code is 0");
						remoteViewsByWidget.put(widgetId, contentCreator.markWidgetAsLoading(widgetId));
						continue;
					}

					StopMonitoringExtendedInfo infoAboutStop = null;
					for (StopMonitoringExtendedInfo s : multipleStopMonitoring.stops) {
						if (s.stopCode == stopCode) {
							infoAboutStop = s;
							break;
						}
					}
					
					if (infoAboutStop != null && infoAboutStop.error != null && infoAboutStop.error != "") {
						Logging.e(context, "Error in stop " + stopCode + ": " + infoAboutStop.error);
						allSucceeded = false;
						remoteViewsByWidget.put(widgetId, contentCreator.markWidgetAsErroneous(widgetId));
					}

					String stopDisplayName = persistence.getStopDisplayName(widgetId);					
					Logging.i(context, "Creating remote views for stop code " + stopCode);
					RemoteViews remoteViews = contentCreator.fillWidgetWithData(widgetId, persistence, stopCode, stopDisplayName, infoAboutStop);
					remoteViewsByWidget.put(widgetId, remoteViews);
				} catch (Exception e) {
					e.printStackTrace();
					allSucceeded = false;
					remoteViewsByWidget.put(widgetId, contentCreator.markWidgetAsErroneous(widgetId));
				}
			}
			Logging.i(context, "Filled " + remoteViewsByWidget.size() + " RemoteViews objects");
			return new FillWidgetResult(allSucceeded, remoteViewsByWidget);
		} catch (Exception e) {
			e.printStackTrace();
			return markAllWidgetsArsErroneous(contentCreator, widgetIds);
		}
	}

	private FillWidgetResult markAllWidgetsArsErroneous(WidgetContentCreator contentCreator, Integer... widgetIds) {
		HashMap<Integer, RemoteViews> remoteViewsByWidget = new HashMap<Integer, RemoteViews>();
		for (int widgetId : widgetIds) {
			remoteViewsByWidget.put(widgetId, contentCreator.markWidgetAsErroneous(widgetId));
		}
		return new FillWidgetResult(false, remoteViewsByWidget);
	}

	@Override
	protected void onPostExecute(FillWidgetResult result) {
		// If some other service invocation is already handing widgets, we're not allowed to touch them
		if (mExpirationToken.hasExpired())
			return;
		for (Map.Entry<Integer, RemoteViews> entry : result.mRemoteViewsByWidget.entrySet()) {
			appWidgetManager.updateAppWidget(entry.getKey(), entry.getValue());
		}
		updatedWidgets = true;
	}
}