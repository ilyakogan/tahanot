package com.tahanot.widgetupdate;

import java.util.*;
import java.util.concurrent.*;

import com.tahanot.*;
import com.tahanot.persistence.*;
import com.tahanot.timers.*;
import com.tahanot.utils.*;

import android.app.*;
import android.appwidget.AppWidgetManager;
import android.content.*;
import android.os.*;

public class WidgetUpdateService extends Service {
	private static long MIN_SPACE_BETWEEN_UPDATES_MSEC = 20 * 1000;
	Context mContext = this;
	ExpirationKeeper mExpirationKeeper = new ExpirationKeeper();
	boolean mServiceIsAlreadyRunning = false;

	{ // http://stackoverflow.com/questions/4280330/onpostexecute-not-being-called-in-asynctask-handler-runtime-exception
		Looper looper = Looper.getMainLooper();
		Handler handler = new Handler(looper);
		handler.post(new Runnable() {
			public void run() {
				try {
					Class.forName("android.os.AsyncTask");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void startService(Context context, boolean forceUpdateEvenIfTooFrequent) {
		// Build the intent to call the service
		Intent intent = new Intent(context, WidgetUpdateService.class);
		intent.putExtra("forceUpdateEvenIfTooFrequent", forceUpdateEvenIfTooFrequent);
		intent.putExtra("createdAt", System.currentTimeMillis());

		// Update the widgets via the service
		context.startService(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) return START_NOT_STICKY;
		boolean reactivate = intent.getBooleanExtra("reactivate", false);
		boolean forceUpdateEvenIfTooFrequent = intent.getBooleanExtra("forceUpdateEvenIfTooFrequent", false);

		if (reactivate) {
			ResourceSaver.userJustDidSomething();
		}

		if (!forceUpdateEvenIfTooFrequent) {
			if (mServiceIsAlreadyRunning)
			{
				return START_REDELIVER_INTENT;
			}
			if (isUpdateTooFrequent()) {
				stopSelf();
				return START_REDELIVER_INTENT;
			}
		}
		mServiceIsAlreadyRunning = true;

		if (!ResourceSaver.shouldWidgetsBeActive()) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
			int[] appWidgetIds = WidgetIds.getIds(this);
			for (int widgetId : appWidgetIds) {
				appWidgetManager.updateAppWidget(widgetId, new WidgetContentCreator().markWidgetAsSavingResources(widgetId));
			}
			WidgetUpdateTimer.stop(this);
			stopSelf();
			return START_REDELIVER_INTENT;
		}

		WidgetUpdateTimer.restartNormal(this);

		final ExpirationToken expirationToken = mExpirationKeeper.getNewTokenAndMakeOldOnesExpire();

		AsyncTask<Void, Void, Void> handlingTask = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				Logging.i(mContext, "handlingTask started in background");
				new WidgetUpdateWorker(mContext).doWork(expirationToken);
				if (!expirationToken.hasExpired()) {
					stopSelf();
				}
				return null;
			}
		};

		Logging.i(this, "Executing handlingTask, expiration token is " + expirationToken);
		TaskExecutor.executeInThreadPool(handlingTask, new Void[0]);

		// If we get killed, after returning from here, restart
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
	}

	private boolean isUpdateTooFrequent() {
		long lastUpdate = new WidgetPersistence().getLastTimeWidgetsWereUpdated();
		if (System.currentTimeMillis() < lastUpdate + MIN_SPACE_BETWEEN_UPDATES_MSEC) {
			Logging.i(this, "Not updating widgets again because it's too soon");
			return true;
		}
		return false;
	}
}
