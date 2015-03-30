package com.tahanot;

import java.util.*;

import com.tahanot.activities.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.adapters.*;
import com.tahanot.entities.*;
import com.tahanot.tasks.*;
import com.tahanot.utils.*;
import com.tahanot.web.*;

import android.app.*;
import android.location.*;
import android.widget.*;

public class StopCollectionPopulator {

	private GetNearbyStopsTask mGetStopsTask;
	IStopUiAdapter mAdapter;

	public StopCollectionPopulator(IStopUiAdapter stopsUiCollection, StopSelectedListener listener) {
		mAdapter = stopsUiCollection;
		mAdapter.registerClick(listener);
	}

	public synchronized void beginPopulate(GetNearbyStopsTask getNearbyStopsTask) {
		if (mGetStopsTask != null)
		{
			interrupt();
		}
		mGetStopsTask = getNearbyStopsTask;
		
		executeTask(mGetStopsTask);
	}

	private void executeTask(GetNearbyStopsTask getNeabyStopsTask) {
		mGetStopsTask.setHandler(new AdderOfNextStopsToList(this));
		TaskExecutor.executeInThreadPool(mGetStopsTask, new Void[0]);
	}

	public synchronized void interrupt() {
		if (mGetStopsTask != null) {
			mGetStopsTask.cancel(true);
		}
	}

	public void addToList(Iterable<IStopInResponse> stops, boolean isStillInProgress) {
		ArrayList<IStopInResponse> stopsToAdd = new ArrayList<IStopInResponse>();
		for (IStopInResponse s : stops) {
			if (!mAdapter.containsStopCode(s.getCode())) {
				stopsToAdd.add(s);
			}
		}

		mAdapter.addStops(stopsToAdd, isStillInProgress);
	}
}
