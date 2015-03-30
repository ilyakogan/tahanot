package com.tahanot.utils;

import java.io.*;
import java.util.*;

import com.tahanot.*;

import android.content.*;
import android.location.*;
import android.util.*;

public class Geocoding {
	public static String GetAddressByLocation(Location location) {			
		if (!Geocoder.isPresent()) {
			Logging.w(BusStopApplication.getContext(), "No geocoding");
			return returnIfFailed();
		}

		Geocoder geocoder = new Geocoder(BusStopApplication.getContext(), I8n.getLocale());

		List<Address> addresses = null;

		try {
			// Call the synchronous getFromLocation() method by passing in the lat/long values.
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		} catch (IOException e) {
			e.printStackTrace();
			return returnIfFailed();
		}

		if (addresses == null || addresses.size() == 0) {
			Logging.w(BusStopApplication.getContext(), "Reverse geocoding gave no addresses for location " + location);
			return returnIfFailed();
		}

		Address address = addresses.get(0);
		if (address.getMaxAddressLineIndex() > 0) {
			return String.format("%s, %s", address.getAddressLine(0), address.getLocality());
		} else
			return address.getLocality();
	}
	
	private static String returnIfFailed()
	{
		return BusStopApplication.getContext().getString(R.string.no_address);
	}
}
