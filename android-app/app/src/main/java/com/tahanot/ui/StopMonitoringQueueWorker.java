package com.tahanot.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.tahanot.client.StopMonitoringProvider;
import com.tahanot.entities.MultipleStopMonitoringExtendedInfo;

import java.util.ArrayList;
import java.util.Collection;

public class StopMonitoringQueueWorker {
    private Context context;
    private Listener listener;
    private int mInterval = 2000;
    private Handler mHandler = new Handler();
    ArrayList<Integer> pendingStopCodes = new ArrayList<>();

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            processItems();
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    public StopMonitoringQueueWorker(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    synchronized public void enqueue(int stopCode) {
        pendingStopCodes.add(stopCode);
    }

    synchronized private void processItems() {
        AsyncTask<Collection<Integer>, Void, MultipleStopMonitoringExtendedInfo> task =
                new AsyncTask<Collection<Integer>, Void, MultipleStopMonitoringExtendedInfo>() {
                    Collection<Integer> stopCodes;
                    @Override
                    protected MultipleStopMonitoringExtendedInfo doInBackground(Collection<Integer>... stopCodes) {
                        this.stopCodes = stopCodes[0];
                        MultipleStopMonitoringExtendedInfo monitoringInfo = null;
                        try {
                            monitoringInfo = new StopMonitoringProvider().getMultipleStopMonitoring(stopCodes[0], context);
                        }
                        catch (Exception ex)
                        {
                            ex.printStackTrace();
                            Crashlytics.logException(ex);
                        }

                        return monitoringInfo;
                    }

                    @Override
                    protected void onPostExecute(MultipleStopMonitoringExtendedInfo monitoringInfo) {
                        if (monitoringInfo != null) {
                            listener.onMonitoringInfoArrived(stopCodes, monitoringInfo);
                        }
                    }
                };

        if (!pendingStopCodes.isEmpty()) {
            task.execute((ArrayList<Integer>)pendingStopCodes.clone());
            pendingStopCodes.clear();
        }
    }

    interface Listener {
        void onMonitoringInfoArrived(Collection<Integer> stopCodes, MultipleStopMonitoringExtendedInfo info);
    }
}
