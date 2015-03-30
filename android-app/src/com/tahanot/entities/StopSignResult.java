package com.tahanot.entities;

public class StopSignResult {
	public StopSign mStopSign;
	public Exception mException;
	
	public StopSignResult(StopSign stopSign) {
		mStopSign = stopSign;
	}
	
	public StopSignResult(Exception exception) {
		mException = exception;
	}
}
