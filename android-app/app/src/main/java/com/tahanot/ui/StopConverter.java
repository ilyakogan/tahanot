package com.tahanot.ui;

import android.content.Context;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StopConverter {
    private Context context;

    public StopConverter(Context context) {
        this.context = context;
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public int coordinatesToStopCode(double lat, double lng) throws Exception {
        try {
            lat = (double)Math.round(lat * 1000000) / 1000000;
            lng = (double)Math.round(lng * 1000000) / 1000000;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("stops");
            query.whereEqualTo("lat", lat).whereEqualTo("lon", lng);
            ParseObject stop = query.getFirst();
            return stop.getInt("code");
        }
        catch (Exception ex) {
            throw new Exception("Cannot convert coordinates " + lat + ", " + lng + " to stop code", ex);
        }
    }

    public String stopCodeToName(int stopCode) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("stops");
        query.whereEqualTo("code", stopCode);
        ParseObject stop = query.getFirst();
        return stop.getString("name") + ", " + stop.getString("town");
    }
}
