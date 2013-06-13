package com.rik.mylife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PhoneRecordStartReceiver  extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	private final String TAG = "PhoneRecordStartReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "PhoneRecordStartReceiver onReceive " + intent.getAction());
		if (intent.getAction().equals(ACTION)) {
			Log.v(TAG, "PhoneRecordStartReceiver onReceive");
			context.startService(new Intent(context, PhoneRecordService.class));// 启动倒计时服务
		}

	}

}