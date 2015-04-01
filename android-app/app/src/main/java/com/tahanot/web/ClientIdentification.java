package com.tahanot.web;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings.Secure;

import com.tahanot.BusStopApplication;
import com.tahanot.utils.Logging;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static Map<? extends String,?> getQueryParamsAsMap(Context context, int widgetId) {
        Map<String, Object> params = new HashMap<>();
        try {
            params.put("installationId", getInstallationId(context));
            params.put("deviceId", getAndroidId(context));
            params.put("widgetId", widgetId);
            params.put("deviceName",getDeviceName());
            params.put("appVersion", getAppVersion(context));
        } catch (Exception ex) {
            Logging.e(context, ex.getMessage());
            params.put("installationId", "ErrorIdentifyingClient");
        }
        return params;
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
