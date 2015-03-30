package com.tahanot.viewmodels;

import java.util.*;

import gueei.binding.*;
import gueei.binding.collections.*;
import gueei.binding.observables.*;

import android.content.*;
import android.util.*;

import com.tahanot.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;

public class NearbyStopsModel {
	private Context context;
	//public Command ReloadStops;

	public ArrayListObservable<NearbyStopModel> Stops = new ArrayListObservable<NearbyStopModel>(NearbyStopModel.class);
	public StringObservable Address = new StringObservable();
	//public StringObservable PendingAddress = new StringObservable("");

	public NearbyStopsModel(Context context) {
		this.context = context;
		Stops.add(NearbyStopModel.ProgressBar(context));
	}

	public synchronized void populateWith(NearbyStopSignsResponse nearbyStopsResponse, boolean isStillInProgress) {
		if (nearbyStopsResponse == null) {
			Logging.e(context, "nearbyStopsResponse is null. Should not have got here.");
			return;
		}

		for (NearbyStopModel stopModel : Stops) {
			if (stopModel.ShowProgress.get()) {
				stopModel.ShowProgress.set(false);
			}
		}

		ArrayList<NearbyStopModel> newStops = new ArrayList<NearbyStopModel>();
		NearbyStopModel lastStop = null;
		for (StopSignWithDistance stop : nearbyStopsResponse.StopsByDistance) {
			NearbyStopModel stopModel = NearbyStopModel.RealStop(stop.StopSign, context);
			newStops.add(stopModel);
			lastStop = stopModel;
		}

		if (isStillInProgress && lastStop != null) {
			lastStop.ShowProgress.set(true);
		}

		Stops.addAll(newStops);
	}

	public synchronized void changeAddress(String address) {
		Address.set(address);
		Stops.clear();
		Stops.add(NearbyStopModel.ProgressBar(context));
	}

//	public void setPendingAddress(String pendingAddress) {
//		PendingAddress.set(pendingAddress);
//	}
}
