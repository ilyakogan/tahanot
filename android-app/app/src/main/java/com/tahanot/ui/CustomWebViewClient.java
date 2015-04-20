package com.tahanot.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.MailTo;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;

public class CustomWebViewClient extends WebViewClient {
    private final LocationProxy locationProxy;
    private Context context;
    private boolean isForWidget;

    public CustomWebViewClient(Context context, boolean isForWidget) {
        this.context = context;
        this.isForWidget = isForWidget;
        this.locationProxy = LocationProxy.get();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        try {
            super.onPageStarted(view, url, favicon);
            locationProxy.subscribeToOneTimeUpdate(location ->
                    view.loadUrl("javascript:setInitialLocation(" + location.getLatitude() + ", " + location.getLongitude() + ")"));
        }
        catch (Exception e) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url)  {
        super.onPageFinished(view, url);
        try {
            Location location = locationProxy.getLastLocation(true);
            if (location != null) {
                view.loadUrl("javascript:setInitialLocation(" + location.getLatitude() + ", " + location.getLongitude() + ")");
            }
        }
        catch (Exception e) {
            Crashlytics.logException(ex);
        }

        try {
            if (isForWidget) {
                view.loadUrl("javascript:setIsForWidget(true)");
            }
        }
        catch (Exception e) {
            Crashlytics.logException(ex);
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            if (url != null && url.startsWith("market://")) {
                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            } else if (url != null && url.startsWith("mailto:")) {
                MailTo mt = MailTo.parse(url);
                Intent i = newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                context.startActivity(i);
                view.reload();
                return true;
            } else if (url != null && url.startsWith("http")) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
                return true;
            } else {
                return false;
            }
        }
        catch (Exception ex)
        {
            Crashlytics.logException(ex);
            return false;
        }
    }

    static Intent newEmailIntent(String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
}
