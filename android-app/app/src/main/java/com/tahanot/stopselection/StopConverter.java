package com.tahanot.stopselection;

import android.content.Context;

import static com.tahanot.stopselection.StopSelectionActivity.round;

public class StopConverter {
    private Context context;

    public StopConverter(Context context) {
        this.context = context;
    }

    public int coordinatesToStopCode(double lat, double lng) {
        String resourceName = String.format("stop_%.6f_%.6f", round(lat, 6), round(lng, 6));
        int identifier = context.getResources().getIdentifier(resourceName, "integer", context.getPackageName());
        int stopCode = context.getResources().getInteger(identifier);
        return stopCode;
    }
}
