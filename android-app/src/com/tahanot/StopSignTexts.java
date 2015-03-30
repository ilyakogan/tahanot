package com.tahanot;

import java.util.*;

import android.content.*;

import com.tahanot.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;

public class StopSignTexts
{
	public String Line1;
	public String Line2;
	
	public StopSignTexts(StopSign stopSign, Context context)
	{
		switch (stopSign.Routes.size()) {
		case 0:
			Line1 = context.getString(R.string.no_routes);
			break;
		case 1:
			StopSignRoute singleRoute = stopSign.Routes.get(0);
			Line1 = context.getString(R.string.route) + " " + singleRoute.RouteName;
			Line2 = context.getString(R.string.towards) + " " + singleRoute.Destination.Text1;
			break;
		default:
			Set<String> routeNamesDistinct = new LinkedHashSet<String>();
			Set<String> destinationsDistinct = new LinkedHashSet<String>();
			for (StopSignRoute route : stopSign.Routes) {
				routeNamesDistinct.add(route.RouteName);
				destinationsDistinct.add(route.Destination.Text1);
			}
			String routeNamesCombined = CollectionUtils.join(", ", routeNamesDistinct);
			String destinationsCombined = CollectionUtils.join(", ", destinationsDistinct);

			Line1 = context.getString(R.string.routes) + " " + routeNamesCombined;
			Line2 = context.getString(R.string.towards) + " " + destinationsCombined;
			break;
		}	
	}
}