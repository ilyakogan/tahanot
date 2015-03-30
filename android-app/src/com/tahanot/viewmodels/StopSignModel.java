package com.tahanot.viewmodels;

import java.util.*;

import com.tahanot.entities.*;
import com.tahanot.utils.*;

import gueei.binding.*;
import gueei.binding.collections.*;
import gueei.binding.observables.*;
import android.app.*;
import android.util.*;

public class StopSignModel {
	public StringObservable StopCode;
	public StringObservable StopName;

	public ArrayListObservable<StopSignRouteModel> Routes = new ArrayListObservable<StopSignRouteModel>(StopSignRouteModel.class);

	public StopSignModel(StopSign stopSign) {
		StopCode = new StringObservable(String.valueOf(stopSign.RawStop.Code));
		StopName = new StringObservable(stopSign.RawStop.Name);
		for (StopSignRoute route : stopSign.Routes)
		{
			Routes.add(new StopSignRouteModel(route));
		}
	}
	
	public class StopSignRouteModel {
		public StringObservable RouteName;
		public StringObservable BigText;
		public StringObservable SmallText;

		public StopSignRouteModel(StopSignRoute route) {
			RouteName = new StringObservable(route.RouteName);
			if (route.Destination.Text2 != null && !route.Destination.Text2.isEmpty())
			{
				BigText = new StringObservable(route.Destination.Text1 + " - " + route.Destination.Text2);
			}
			else
			{
				BigText = new StringObservable(route.Destination.Text1);
			}
			SmallText = new StringObservable(CollectionUtils.join(", ", route.Midpoints));
		}
	}	
}
