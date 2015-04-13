package com.tahanot.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.common.base.Optional;
import com.tahanot.R;

public class LandingActivity extends Activity {

    private WebViewHolder webViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        webViewHolder = new WebViewHolder((WebView) findViewById(R.id.webView1), this, Optional.absent(), "");
        try {
            webViewHolder.start();
        }
        catch (Exception ex) {
            Crashlytics.logException(ex);
            Toast.makeText(this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
        }
        Crashlytics.log("Main activity created");
    }
}


