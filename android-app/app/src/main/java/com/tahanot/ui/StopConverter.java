package com.tahanot.ui;

import android.content.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

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
            // Locale.US should prevent Russian devices from converting "." to ","
            String resourceName = String.format(Locale.US, "stop_%.6f_%.6f", round(lat, 6), round(lng, 6));
            int identifier = context.getResources().getIdentifier(resourceName, "integer", context.getPackageName());
            int stopCode = context.getResources().getInteger(identifier);
            return stopCode;
        }
        catch (Exception ex) {
            throw new Exception("Cannot convert coordinates " + lat + ", " + lng + " to stop code", ex);
        }
    }

    public String stopCodeToName(int stopCode) {
        String resourceName = String.format(Locale.US, "stop_%d_name", stopCode);
        int identifier = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        String name = context.getResources().getString(identifier);

        resourceName = String.format(Locale.US, "stop_%d_town", stopCode);
        identifier = context.getResources().getIdentifier(resourceName, "string", context.getPackageName());
        String town = context.getResources().getString(identifier);

        return name + ", " + town;
    }
}
