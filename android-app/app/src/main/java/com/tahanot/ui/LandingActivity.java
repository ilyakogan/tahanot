package com.tahanot.ui;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.google.common.base.Optional;
import com.tahanot.R;

public class LandingActivity extends Activity {

    private WebViewHolder webViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Analytics
        //Crashlytics.start(this);
        setContentView(R.layout.webview);
        webViewHolder = new WebViewHolder((WebView) findViewById(R.id.webView1), this, Optional.absent(), "");
        webViewHolder.start();
    }
}


