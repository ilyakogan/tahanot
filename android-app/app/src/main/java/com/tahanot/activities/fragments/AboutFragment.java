package com.tahanot.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.tahanot.R;

public class AboutFragment extends Fragment {

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
