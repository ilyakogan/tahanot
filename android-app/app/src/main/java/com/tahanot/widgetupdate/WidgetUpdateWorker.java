package com.tahanot.widgetupdate;

import java.util.*;
import java.util.concurrent.*;

import android.appwidget.*;
import android.content.*;

import com.tahanot.*;
import com.tahanot.persistence.*;
import com.tahanot.timers.*;
import com.tahanot.utils.*;
import com.tahanot.widgetupdate.WidgetUpdateService.*;

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
			Logging.e(mContext, "Exception in WidgetUpdateService.onHandleIntent: " + e + ": " + e.getMessage());
			e.printStackTrace();
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
			Logging.e(mContext, "Timeout waiting for update task to finish. Exception: " + e + ": " + e.getMessage());
			task.cancel(false);
			return false;
		} catch (Exception e) {
			Logging.e(mContext, "Exception while waiting for update task to finish. Exception: " + e + ": " + e.getMessage());
			return false;
		}
	}
}