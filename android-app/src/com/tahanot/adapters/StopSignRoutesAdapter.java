package com.tahanot.adapters;

import java.util.*;

import android.app.*;
import android.view.*;
import android.widget.*;

import com.tahanot.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;

public class StopSignRoutesAdapter extends ArrayAdapter<StopSignRoute> {

	private Activity mActivity;
	private int mLayoutResourceId;

	public StopSignRoutesAdapter(Activity activity, int layoutResourceId, ArrayList<StopSignRoute> routes) {
		super(activity, layoutResourceId, routes);	
		mActivity = activity;
		mLayoutResourceId = layoutResourceId;		
	}
	
	@Override
	public View getView(int position, View row, ViewGroup parent) {
		StopSignRouteHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mActivity).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);

			holder = new StopSignRouteHolder();
			holder.routeNumberView = (TextView) row.findViewById(R.id.routeNumber);
			holder.bigTextView = (TextView) row.findViewById(R.id.routeTextLine1);
			holder.smallTextView = (TextView) row.findViewById(R.id.routeTextLine2);

			row.setTag(holder);
		} else {
			holder = (StopSignRouteHolder) row.getTag();
		}

		StopSignRoute route = this.getItem(position);
		
		holder.routeNumberView.setText(route.RouteName);
		if (route.Destination.Text2 != null && !route.Destination.Text2.isEmpty())
		{
			holder.bigTextView.setText(route.Destination.Text1 + " - " + route.Destination.Text2);
		}
		else
		{
			holder.bigTextView.setText(route.Destination.Text1);
		}
		holder.smallTextView.setText(CollectionUtils.join(", ", route.Midpoints));

		return row;
	}
	
	static class StopSignRouteHolder {
		public TextView routeNumberView;
		public TextView bigTextView;
		public TextView smallTextView;
	}
}
