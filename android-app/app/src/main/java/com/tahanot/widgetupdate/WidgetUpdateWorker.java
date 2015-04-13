package com.tahanot.widgetupdate;

import android.appwidget.AppWidgetManager;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.tahanot.FillWidgetResult;
import com.tahanot.WidgetContentCreator;
import com.tahanot.WidgetIds;
import com.tahanot.persistence.WidgetPersistence;
import com.tahanot.timers.WidgetUpdateTimer;
import com.tahanot.utils.CollectionUtils;
import com.tahanot.utils.ExpirationToken;
import com.tahanot.utils.Logging;
import com.tahanot.utils.TaskExecutor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// Handler that receives messages from the thread
final class WidgetUpdateWorker {
	private static long UPDATE_TIMEOUT_SEC = 30;
	private Context mContext;

	public WidgetUpdateWorker(Context context) {
		mContext = context;
	}

	public void doWork(ExpirationToken expirationToken) {
		Logging.i(mContext, "WidgetUpdateWorker starting work with expirationToken = " + expirationToken);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
		int[] appWidgetIds = WidgetIds.getIds(mContext);

		for (int widgetId : appWidgetIds) {
			appWidgetManager.updateAppWidget(widgetId, new WidgetContentCreator().markWidgetAsLoading(widgetId));
		}
		
		FillWidgetTask task = null;
		try {
			task = new FillWidgetTask(appWidgetManager, expirationToken);
			boolean allSucceeded = runTask(task, appWidgetIds);

			if (!expirationToken.hasExpired()) {
				if (allSucceeded) {
					postponeNextUpdate();
				} else {
					bringNextUpdateForward();
				}
			}
		} catch (Exception e) {
            Crashlytics.logException(e);
			if (!expirationToken.hasExpired()) {
				WidgetUpdateTimer.restartSoon(mContext);
			}
		} finally {
			if (task != null) {
				task.cancel(true);
			}
		}
	}

	private void postponeNextUpdate() {
		WidgetUpdateTimer.restartNormal(mContext);
		new WidgetPersistence().setLastTimeWidgetsWereUpdated(System.currentTimeMillis());
	}

	private void bringNextUpdateForward() {
		WidgetUpdateTimer.restartSoon(mContext);
	}

	private boolean runTask(FillWidgetTask task, int[] widgetIds) {
		long timeoutLeft = UPDATE_TIMEOUT_SEC;

		// Wait for all tasks to finish, otherwise the current thread will die and onPostExecute will never run

		try {
			TaskExecutor.executeInThreadPool(task, CollectionUtils.toIntegerArray(widgetIds));
			FillWidgetResult result = task.get(timeoutLeft, TimeUnit.SECONDS);
			return result.mWidgetUpdatedWithData;
//			if (result.mWidgetUpdatedWithData) {
//				// // Fallback due to bug: sometimes onPostExeccute doesn't run
//				// // (but we still leave the update in onPostExecute too in order to update faster when possible).
//				// if (!task.updatedWidgets) {
//				// task.updateWidget(result.remoteViews);
//				// }
//				return true;
//			} else {
//				return false;
//			}
		} catch (TimeoutException e) {
			timeoutLeft = 0;
            Crashlytics.logException(e);
            task.cancel(false);
			return false;
		} catch (Exception e) {
            Crashlytics.logException(e);
            return false;
		}
	}
}