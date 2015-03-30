package com.tahanot.viewmodels;

import java.util.*;

import android.content.*;
import com.tahanot.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;
import gueei.binding.observables.*;

public class NearbyStopModel {
	public IntegerObservable StopId;
	public IntegerObservable StopCode;
	public StringObservable StopName;
	public StringObservable BigText;
	public StringObservable SmallText;
	public BooleanObservable ShowStop;
	public BooleanObservable ShowProgress;
	//public Command SelectStop;
	public StringObservable SelectStopText;
	
	public static NearbyStopModel ProgressBar(Context context)
	{
		NearbyStopModel model = new NearbyStopModel(context);
		model.ShowStop = new BooleanObservable(false);
		model.ShowProgress = new BooleanObservable(true);
		return model;
	}
	
	public static NearbyStopModel RealStop(StopSign stopSign, Context context) {
		NearbyStopModel model = new NearbyStopModel(stopSign, context);
		model.ShowStop = new BooleanObservable(true);
		model.ShowProgress = new BooleanObservable(false);
		return model;
	}
	
	private NearbyStopModel(Context context) {
		StopId = new IntegerObservable();
		StopCode = new IntegerObservable();
		StopName = new StringObservable();
		BigText = new StringObservable();
		SmallText = new StringObservable();
	}

	private NearbyStopModel(StopSign stopSign, Context context) {		
		StopId = new IntegerObservable(stopSign.RawStop.Id);
		StopCode = new IntegerObservable(stopSign.RawStop.Code);
		StopName = new StringObservable(stopSign.RawStop.Name);

		//SelectStop = selectNearbyStopCommandFactory.CreateCommand(StopCode.get(), StopName.get());
		SelectStopText = new StringObservable(context.getString(R.string.select_the_stop) + " " + stopSign.RawStop.Code);
		
		StopSignTexts texts = new StopSignTexts(stopSign, context);
		BigText = new StringObservable(texts.Line1);
		SmallText = new StringObservable(texts.Line2);
	}
}
