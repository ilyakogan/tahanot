package com.tahanot;

import java.lang.ref.*;
import java.util.*;

import android.os.*;

import com.tahanot.entities.*;
import com.tahanot.tasks.*;
import com.tahanot.utils.*;

final class AdderOfNextStopsToList extends Handler {

	WeakReference<StopCollectionPopulator> parent;

	public AdderOfNextStopsToList(StopCollectionPopulator parent) {
		this.parent = new WeakReference<StopCollectionPopulator>(parent);
	}

	@Override
	public void handleMessage(Message msg) {
		StopCollectionPopulator p = parent.get();
		if (p == null)
			return;

		// get the value from the Message
		ArrayList<IStopInResponse> stops = (ArrayList<IStopInResponse>) msg.obj;
		if (stops == null) {
			Logging.e(BusStopApplication.getContext(), "No stops to show - stops are null");
			return;
		}

		boolean isStillInProgress = (msg.arg1 == GetNearbyStopsTask.IN_PROGRESS);

		p.addToList(stops, isStillInProgress);
	}
}