package com.tahanot.utils;

public class ExpirationKeeper {
	private int mLatestTokenVersion = 0;

	public boolean hasExpired(int tokenVersion) {
		return tokenVersion < mLatestTokenVersion;
	}

	public ExpirationToken getNewTokenAndMakeOldOnesExpire() {
		return new ExpirationToken(this, ++mLatestTokenVersion);
	}
	
	@Override
	public String toString() {
		return String.valueOf(mLatestTokenVersion);
	}
}