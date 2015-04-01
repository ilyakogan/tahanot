package com.tahanot.web;

import android.content.Context;
import android.location.Location;
import android.util.Base64;

import com.tahanot.BusStopApplication;
import com.tahanot.R;
import com.tahanot.entities.NearbyStopSignsResponse;
import com.tahanot.entities.StopSign;
import com.tahanot.entities.StopSignResult;
import com.tahanot.entities.StopWithDistance;
import com.tahanot.utils.HttpReader;
import com.tahanot.utils.Logging;

import org.json.JSONArray;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class StopSignProvider {
	private static int CONNECTION_TIMEOUT_MSEC = 40 * 1000;

	public StopSignResult getStopSignByCode(int stopCode, int widgetId, Context context) {

        String uri = getBaseUrl(context) + "/gtfs/StopSignByStopCodeXml?StopCode=" + stopCode + "&ver=1"
				+ ClientIdentification.getQueryParams(widgetId);
		return getResponse(uri);
	}

    public ArrayList<StopWithDistance> getNearbyStops(Location location, int maxStops, int widgetId, Context context) {
        try {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String uri = getBaseUrl(context) + "/gtfs/NearbyStopsJson?lat=" + lat + "&lon=" + lon + "&maxStops=" + maxStops + "&ver=1"
                    + ClientIdentification.getQueryParams(widgetId);

            String jsonResult = HttpReader.getStringContent(uri, CONNECTION_TIMEOUT_MSEC, CONNECTION_TIMEOUT_MSEC);

            JSONArray jsonArray = new JSONArray(jsonResult);
            int len = jsonArray.length();
            ArrayList<StopWithDistance> stops = new ArrayList<StopWithDistance>();
            for (int i = 0; i < len; i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                stops.add(new StopWithDistance(obj));
            }

            return stops;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public NearbyStopSignsResponse getNearbyStopSigns(Location location, int fromStopNumber, int toStopNumber, int widgetId, Context context) {
        try {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String uri = getBaseUrl(context) + "/gtfs/NearbyStopSignsXml?lat=" + lat + "&lon=" + lon + "&maxStops=" + (toStopNumber + 1) + "&ver=1"
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

    private String getBaseUrl(Context context) {
        return context.getResources().getString(R.string.stop_static_base_url);
    }

////	public StopSignResult getStopSignById(int stopId, int widgetId) {
////		String uri = HOME + "/gtfs/StopSignXml?stopId=" + stopId + "&ver=1" + ClientIdentification.getQueryParams(widgetId);
////		return getResponse(uri);
//	}

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
