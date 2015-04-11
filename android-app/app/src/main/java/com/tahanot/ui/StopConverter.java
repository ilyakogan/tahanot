package com.tahanot.ui;

import android.content.Context;

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

    public int coordinatesToStopCode(double lat, double lng) {
        String resourceName = String.format("stop_%.6f_%.6f", round(lat, 6), round(lng, 6));
        int identifier = context.getResources().getIdentifier(resourceName, "integer", context.getPackageName());
        int stopCode = context.getResources().getInteger(identifier);
        return stopCode;
    }
}
