package com.rik.mylife;

import org.apache.http.client.CookieStore;

import android.app.Application;
import android.util.Log;

public class MyLifeApp extends Application {

	private final static String TAG = "MyLifeApp";
	
	private String sessionID ;   
    

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "onCreate");	
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
	
	
}
