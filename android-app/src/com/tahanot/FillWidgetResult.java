package com.tahanot;

import java.util.*;

import android.widget.RemoteViews;

public class FillWidgetResult {

	public boolean mWidgetUpdatedWithData;
	//public RemoteViews remoteViews;
	public Map<Integer, RemoteViews> mRemoteViewsByWidget;

	public FillWidgetResult(boolean success, Map<Integer, RemoteViews> remoteViewsByWidget) {
		mWidgetUpdatedWithData = success;
		mRemoteViewsByWidget = remoteViewsByWidget;
	}

}
