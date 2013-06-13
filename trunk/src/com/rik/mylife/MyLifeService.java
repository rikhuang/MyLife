package com.rik.mylife;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MyLifeService extends Service {
	private final String TAG = "MyLifeService";
	
	private final IBinder mBinder = new MyLifeBinder();
	
	public class MyLifeBinder extends Binder {
		MyLifeService getService() {
			return MyLifeService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return this.mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG,"SERVICE oncreate.");
	}

}
