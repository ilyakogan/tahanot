package com.tahanot.web;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.json.simple.*;
import org.simpleframework.xml.*;
import org.simpleframework.xml.core.*;

import android.content.Context;
import android.location.*;
import android.util.*;
import android.widget.*;

import com.tahanot.*;
import com.tahanot.entities.*;
import com.tahanot.utils.*;

public class StopSignProvider {
	private static int CONNECTION_TIMEOUT_MSEC = 40 * 1000;
	final String HOME = ...
	
	public StopSignResult getStopSignByCode(int stopCode, int widgetId) {
		String uri = HOME + "/gtfs/StopSignByStopCodeXml?stopCode=" + stopCode + "&ver=1"
				+ ClientIdentification.getQueryParams(widgetId);
		return getResponse(uri);
	}

	public StopSignResult getStopSignById(int stopId, int widgetId) {
		String uri = HOME + "/gtfs/StopSignXml?stopId=" + stopId + "&ver=1" + ClientIdentification.getQueryParams(widgetId);
		return getResponse(uri);
	}

	private StopSignResult getResponse(String uri) {
		try {
			Logging.i(BusStopApplication.getContext(), "getStopSign: " + uri);
			String jsonResult = HttpReader.getStringContent(uri, CONNECTION_TIMEOUT_MSEC, CONNECTION_TIMEOUT_MSEC);

			Serializer serializer = new Persister();
			StopSign result = serializer.read(StopSign.class, jsonResult);
			return new StopSignResult(result);
		} catch (Exception e) {
			e.printStackTrace();
			return new StopSignResult(e);
		}
	}

	public ArrayList<StopWithDistance> getNearbyStops(Location location, int maxStops, int widgetId) {
		try {
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			String uri = HOME + "/gtfs/NearbyStopsJson?lat=" + lat + "&lon=" + lon + "&maxStops=" + maxStops + "&ver=1"
					+ ClientIdentification.getQueryParams(widgetId);

			String jsonResult = HttpReader.getStringContent(uri, CONNECTION_TIMEOUT_MSEC, CONNECTION_TIMEOUT_MSEC);

			JSONArray jsonArray = (JSONArray) JSONValue.parse(jsonResult);
			int len = jsonArray.size();
			ArrayList<StopWithDistance> stops = new ArrayList<StopWithDistance>();
			for (int i = 0; i < len; i++) {
				Object obj = jsonArray.get(i);
				stops.add(new StopWithDistance((JSONObject) obj));
			}
			
			return stops;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public NearbyStopSignsResponse getNearbyStopSigns(Location location, int fromStopNumber, int toStopNumber, int widgetId) {
		try {
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			String uri = HOME + "/gtfs/NearbyStopSignsXml?lat=" + lat + "&lon=" + lon + "&maxStops=" + (toStopNumber + 1) + "&ver=1"
					+ ClientIdentification.getQueryParams(widgetId);
			if (fromStopNumber > 0) {
				uri += "&ignoreFirstStops=" + fromStopNumber;
			}

			String base64 = HttpReader.getStringContent(uri, CONNECTION_TIMEOUT_MSEC, CONNECTION_TIMEOUT_MSEC);

			String xml = decompress(base64);

			Serializer serializer = new Persister();
			NearbyStopSignsResponse nearbyStopsResponse = serializer.read(NearbyStopSignsResponse.class, xml);

			return nearbyStopsResponse;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String decompress(String base64) throws IOException {
		byte[] compressed = Base64.decode(base64, 0);
		if (compressed.length > 4) {
			GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressed, 4, compressed.length - 4));

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			for (int value = 0; value != -1;) {
				value = gzipInputStream.read();
				if (value != -1) {
					baos.write(value);
				}
			}
			gzipInputStream.close();
			baos.close();
			String sReturn = new String(baos.toByteArray(), "UTF-8");
			return sReturn;
		} else {
			return "";
		}
	}
}
