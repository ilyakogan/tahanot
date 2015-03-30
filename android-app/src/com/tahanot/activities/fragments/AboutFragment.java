package com.tahanot.activities.fragments;

import java.util.regex.*;

import android.os.*;
import android.text.*;
import android.text.method.*;
import android.text.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

import com.actionbarsherlock.app.*;
import com.google.analytics.tracking.android.*;
import com.tahanot.*;

public class AboutFragment extends SherlockFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mainView = (View) inflater.inflate(R.layout.webview, container, false);
		WebView webView = (WebView) mainView.findViewById(R.id.webView1);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}
		});
		// webView.loadUrl("http://www.tahanot.com");
		// return mainView;

		super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.about, container, false);
		TextView aboutText = (TextView) v.findViewById(R.id.aboutText);
		aboutText.setText(Html.fromHtml(getString(R.string.aboutHtml)));
		aboutText.setMovementMethod(LinkMovementMethod.getInstance());
		return v;
	}

}
