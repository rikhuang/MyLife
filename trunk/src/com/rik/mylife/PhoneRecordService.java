package com.rik.mylife;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneRecordService extends Service {

	private final static String TAG = "PhoneRecordService";

	private String path;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "onCreate");
		path = Config.getRootPath() + "/calls/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onStart(Intent intent, int startId) { 
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.d(TAG, "onStart");

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(new RecListener(), PhoneStateListener.LISTEN_CALL_STATE);
	}

	private final class RecListener extends PhoneStateListener {
		private String phoneNum = "out";
		private MediaRecorder recorder = null;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// phoneNum = incomingNumber;
				phoneNum = "in";
				Log.d(TAG, "incomingNumber->" + incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd-hh-mm-ss");
				String d = sDateFormat.format(new Date());
				File file = new File(new File(path), phoneNum + "_" + d
						+ ".3gp");
				recorder = new MediaRecorder();
				recorder.setAudioSource(AudioSource.VOICE_CALL);
				// recorder.setAudioSource(AudioSource.VOICE_CALL);
				recorder.setOutputFormat(OutputFormat.THREE_GPP);
				recorder.setAudioEncoder(AudioEncoder.AMR_WB);
				recorder.setAudioSamplingRate(44100);
				recorder.setOutputFile(file.getAbsolutePath());
				Log.d(TAG, file.getAbsolutePath());
				try {
					recorder.prepare();
					recorder.start();
					Log.d(TAG, "start recorder");
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (recorder != null) {
					recorder.stop();
					recorder.reset();
					recorder.release();
					Log.d(TAG, "stop recorder");
				}
				break;
			default:
				Log.d(TAG, "error state");
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}

	}

}
