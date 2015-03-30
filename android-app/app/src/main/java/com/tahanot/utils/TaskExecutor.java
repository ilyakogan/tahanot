package com.tahanot.utils;

import android.annotation.*;
import android.os.*;

public final class TaskExecutor {

	public static <TInput, TProgress, TResult> void executeInThreadPool(AsyncTask<TInput, TProgress, TResult> task, TInput[] parameter) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			executeOnHoneycomb(task, parameter);
		} else {
			task.execute(parameter);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static <TInput, TProgress, TResult> void executeOnHoneycomb( AsyncTask<TInput, TProgress, TResult> task, TInput[] parameter) {
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, parameter);
	}
}
