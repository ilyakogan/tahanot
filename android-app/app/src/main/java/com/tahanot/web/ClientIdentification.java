package com.tahanot.web;

import java.io.*;
import java.net.*;
import java.util.*;

import com.tahanot.*;
import com.tahanot.utils.*;

import android.content.*;
import android.content.SharedPreferences.Editor;
import android.content.pm.*;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.*;
import android.provider.Settings.Secure;

public class ClientIdentification {

	public static String getQueryParams(int widgetId) {
		Context context = BusStopApplication.getContext();
		try {
			return "&installationId=" + urlEncode(getInstallationId(context))
					// + "&installationIdCreatedAt=" + "2000-01-01-00-00-00"
					+ "&deviceId=" + urlEncode(getAndroidId(context)) + "&widgetId=" + widgetId + "&deviceName=" + urlEncode(getDeviceName())
					+ "&appVersion=" + urlEncode(getAppVersion(context));
		} catch (Exception ex) {
			Logging.e(context, ex.getMessage());
			return "&installationId=ErrorIdentifyingClient";
		}
	}

	private static String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return value;
		}
	}

	private static String getAppVersion(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			String appVersion = pInfo.versionName;
			return appVersion;
		} catch (NameNotFoundException ex) {
			return "Name not found";
		}
	}

	private static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private static String androidId;

	private synchronized static String getAndroidId(Context context) {
		if (androidId == null) {
			androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		return androidId;
	}

	private static String installationId = null;
	private static final String INSTALLATION_ID = "INSTALLATION_ID";

	private synchronized static String getInstallationId(Context context) {
		if (installationId == null) {
			SharedPreferences sharedPrefs = context.getSharedPreferences(INSTALLATION_ID, Context.MODE_PRIVATE);
			installationId = sharedPrefs.getString(INSTALLATION_ID, null);
			if (installationId == null) {
				installationId = UUID.randomUUID().toString();
				Editor editor = sharedPrefs.edit();
				editor.putString(INSTALLATION_ID, installationId);
				editor.commit();
			}
		}
		return installationId;
	}

	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

}
