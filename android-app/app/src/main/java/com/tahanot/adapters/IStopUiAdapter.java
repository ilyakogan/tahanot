package com.tahanot.adapters;

import java.util.*;

import com.tahanot.activities.interfaces.*;
import com.tahanot.entities.*;

public interface IStopUiAdapter {

	public boolean containsStopCode(int code);

	public void addStops(ArrayList<IStopInResponse> stopsToAdd, boolean isStillInProgress);

	public void registerClick(final StopSelectedListener listener);
}