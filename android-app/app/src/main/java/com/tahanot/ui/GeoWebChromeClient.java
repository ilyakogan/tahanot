package com.tahanot.ui;

import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;

public class GeoWebChromeClient extends WebChromeClient {
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, true);
    }
}
