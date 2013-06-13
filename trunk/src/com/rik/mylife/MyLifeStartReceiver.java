package com.rik.mylife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyLifeStartReceiver extends BroadcastReceiver {

	static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	private final String TAG = "MyLifeStartReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "MyLifeStartReceiver onReceive " + intent.getAction());
		if (intent.getAction().equals(ACTION)) {
			Log.v(TAG, "MyLifeStartReceiver onReceive");
			context.startService(new Intent(context, PhoneRecordService.class));// 启动倒计时服务
			
			
			
			// Log.v(TAG,"GpsCollectionStartReceiver onReceive");
			// 这边可以添加开机自动启动的应用程序代码
		}

	}
}