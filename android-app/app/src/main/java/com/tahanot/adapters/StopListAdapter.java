package com.tahanot.adapters;

import java.util.*;

import com.tahanot.*;
import com.tahanot.activities.interfaces.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;

import android.app.*;
import android.view.*;
import android.view.animation.*;
import android.view.animation.Animation.*;
import android.widget.*;
import android.widget.AdapterView.*;

public class StopListAdapter extends ArrayAdapter<StopSign> implements IStopUiAdapter {
	Activity mActivity;
	int mLayoutResourceId;
	private LoadingFooter mLoadingFooter = new LoadingFooter();
	private ListView mListView;
	static int layoutResourceId = R.layout.nearby_stop_menu_item;

	public StopListAdapter(Activity activity, ListView listView) {
		super(activity, layoutResourceId, new ArrayList<StopSign>());
		this.mLayoutResourceId = layoutResourceId;
		this.mActivity = activity;
		this.add(mLoadingFooter);
		mListView = listView;
		mListView.setAdapter(this);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		StopSign item = getItem(position);
		return isLoadingFooter(item) ? 1 : 0;
	}

	@Override
	public View getView(int position, View row, ViewGroup parent) {
		StopSign item = this.getItem(position);

		boolean isNewItem = (row == null);

		if (isNewItem) {
			LayoutInflater inflater = ((Activity) mActivity).getLayoutInflater();
			row = inflater.inflate(getLayoutId(isLoadingFooter(item)), parent, false);
		}

		if (!isLoadingFooter(item)) {
			fillStopSignViews(row, item, isNewItem);
		}

		return row;
	}

	@Override
	public boolean containsStopCode(int code) {
		try {
			for (int i = 0; i < getCount(); i++) {
				if (getItem(i).RawStop.Code == code) {
					return true;
				}
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void addStops(ArrayList<IStopInResponse> stopsToAdd, boolean isStillInProgress) {
		remove(mLoadingFooter);
		for (IStopInResponse s : stopsToAdd) {
			StopSign stopSign = ((StopSignWithDistance) s).StopSign;
			add(stopSign);
		}
		if (isStillInProgress) {
			add(mLoadingFooter);
		}
		notifyDataSetChanged();
	}

	@Override
	public void registerClick(final StopSelectedListener listener) {
		Logging.i(BusStopApplication.getContext(), "ONCLICK register");
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Logging.i(BusStopApplication.getContext(), "ONCLICK call");
				final StopSign selectedStop = getItem(position);
				if (selectedStop == mLoadingFooter)
					return;
				ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.97f, 1.0f, 0.95f, Animation.RELATIVE_TO_SELF, (float) 0.5,
						Animation.RELATIVE_TO_SELF, (float) 0.5);
				scaleAnimation.setDuration(50);
				scaleAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation animation) {
						listener.onStopSelected(selectedStop);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});

				view.startAnimation(scaleAnimation);
			}
		});
	}

	private boolean isLoadingFooter(StopSign item) {
		return LoadingFooter.class.isInstance(item);
	}

	private int getLayoutId(boolean isLoadingFooter) {
		if (isLoadingFooter) {
			return R.layout.loading_footer;
		} else {
			return R.layout.nearby_stop_menu_item;
		}
	}

	private void fillStopSignViews(View row, StopSign stopSign, boolean isNewItem) {
		NearbyStopHolder holder;
		if (isNewItem) {
			holder = new NearbyStopHolder();
			holder.stopCodeView = (TextView) row.findViewById(R.id.stopCode);
			holder.stopNameView = (TextView) row.findViewById(R.id.stopName);
			holder.bigTextView = (TextView) row.findViewById(R.id.routeTextLine1);
			holder.smallTextView = (TextView) row.findViewById(R.id.routeTextLine2);
			row.setTag(holder);
		} else {
			holder = (NearbyStopHolder) row.getTag();
		}

		StopSignTexts texts = new StopSignTexts(stopSign, mActivity);

		holder.stopCodeView.setText(String.valueOf(stopSign.RawStop.Code));
		holder.stopNameView.setText(stopSign.RawStop.Name);
		holder.bigTextView.setText(texts.Line1);
		holder.smallTextView.setText(texts.Line2);
	}

	static class NearbyStopHolder {
		public TextView stopCodeView;
		public TextView stopNameView;
		public TextView bigTextView;
		public TextView smallTextView;
	}
}
