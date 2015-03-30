package com.tahanot.utils;

import java.io.*;
import java.net.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.*;

public class HttpReader {

	public static String getStringContent(String uri, int timeoutConnectionMsec, int timeoutSocketMsec) throws Exception {
		URL url = new URL(uri);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		System.setProperty("http.keepAlive", "false");
		System.setProperty("java.net.preferIPv4Stack" , "true");
		try {
			int responseCode = urlConnection.getResponseCode();
			if (responseCode >= 400 && responseCode < 600) {
				throw new Exception("Bad status code: " + responseCode);
			}
			InputStream in = urlConnection.getInputStream();
			return readInputStream(in);
		} finally {
			urlConnection.disconnect();
		}
	}

	public static String getStringContentOld(String uri, int timeoutConnectionMsec, int timeoutSocketMsec) throws Exception {
		System.setProperty("networkaddress.cache.ttl", "0");
		System.setProperty("networkaddress.cache.negative.ttl", "0");

		HttpClient client = null;

		try {
			HttpParams httpParameters = new BasicHttpParams();

			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnectionMsec);

			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocketMsec);

			client = new DefaultHttpClient(httpParameters);
			HttpGet request = new HttpGet();
			request.setURI(new URI(uri));
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode >= 400 && statusCode < 600) {
				throw new Exception("Bad status code: " + statusCode);
			}

			InputStream ips = response.getEntity().getContent();
			return readInputStream(ips);

		} finally {
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
	}

	private static String readInputStream(InputStream ips) throws UnsupportedEncodingException, IOException {
		BufferedReader buf = new BufferedReader(new InputStreamReader(ips, "UTF-8"));

		StringBuilder sb = new StringBuilder();
		String s;
		while (true) {
			s = buf.readLine();
			if (s == null || s.length() == 0)
				break;
			sb.append(s);
		}
		buf.close();
		ips.close();
		return sb.toString();
	}
}
