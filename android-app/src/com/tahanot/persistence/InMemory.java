package com.tahanot.persistence;

import java.util.*;

import com.tahanot.entities.*;

public final class InMemory {
	private static HashMap<Integer, StopSign> stopSigns = new HashMap<Integer, StopSign>();

	public static void putStopSign(StopSign stopSign) {
		int stopCode = stopSign.RawStop.Code;
		stopSigns.put(stopCode, stopSign);
	}
	
	public static StopSign getStopSign(int stopCode)
	{
		if (!stopSigns.containsKey(stopCode)) return null;
		return stopSigns.get(stopCode);
	}
}
