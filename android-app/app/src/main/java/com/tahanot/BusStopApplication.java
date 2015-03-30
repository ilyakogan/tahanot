package com.tahanot;

import gueei.binding.Binder;
import android.app.Application;
import android.content.*;

public class BusStopApplication extends Application {
	private static Application instance;

    public BusStopApplication() {
    	instance = this;
    }
	
	public static Context getContext() {
    	return instance;
    }
	
	@Override
	public void onCreate() {
		super.onCreate();
		Binder.init(this);
	}
}