package com.tahanot.utils;

import com.tahanot.*;

public class ExpirationToken {
	private ExpirationKeeper mGlobalState;
	private int mTokenVersion;

	ExpirationToken(ExpirationKeeper createdBy, int tokenVersion) {
		mGlobalState = createdBy;
		mTokenVersion = tokenVersion;
	}

	public boolean hasExpired() {
		boolean hasExpired = mGlobalState.hasExpired(mTokenVersion);
		if (hasExpired) Logging.i(BusStopApplication.getContext(), "Tried to use an expired token: " + this);
		return hasExpired;
	}
	
	@Override
	public String toString() {
		return String.valueOf(mTokenVersion) + " (keeper has " + mGlobalState + ")";
	}
}