package com.rik.mylife;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MyLifeActivity extends Activity {

	private final static String TAG = "MyLifeActivity";
	private boolean state = false;
	ProgressDialog p_dialog;

	private EditText username;
	private EditText password;
	private Button picShotBtn;
	private Button videoBtn;
	private Button callBtn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_life);
		picShotBtn = (Button) findViewById(R.id.btn_pic);
		videoBtn = (Button) findViewById(R.id.btn_video);
		callBtn = (Button) findViewById(R.id.btn_record);
		picShotBtn.setOnClickListener(onPicShotButton);
		videoBtn.setOnClickListener(onVideoButton);
		callBtn.setOnClickListener(onCallRecordButton);
		// showDialog(savedInstanceState);
		initFolder();
	}

	private void initFolder() {
		try {
			String photo_folder = Config.getRootPath() + "/photo/";
			File file = new File(photo_folder);
			if (!file.exists()) {
				file.mkdirs();
			}

			photo_folder = Config.getRootPath() + "/record/";
			file = new File(photo_folder);
			if (!file.exists()) {
				file.mkdirs();
			}

			photo_folder = Config.getRootPath() + "/download/";

			file = new File(photo_folder);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			Log.v(TAG, "Exception occurs. ", e);
		}
	}

	OnClickListener onPicShotButton = new OnClickListener() {
		public void onClick(View v) {
			try {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setClassName(MyLifeActivity.this,
						PicShotActivity.class.getName());
				startActivity(i);
			} catch (Exception e) {
				Log.e(TAG, "Exception occurs. ", e);
			}
		}
	};

	OnClickListener onVideoButton = new OnClickListener() {
		public void onClick(View v) {

		}
	};

	OnClickListener onCallRecordButton = new OnClickListener() {
		public void onClick(View v) {

			Intent i = new Intent(MyLifeActivity.this, PhoneRecordService.class);
			if (!state) {
				startService(i);
				callBtn.setText("停止");
			} else {
				stopService(i);
				callBtn.setText("录音");
			}
			state = !state;

			if (!Config.RECORD_ALREADY_RUN) {

			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_my_life, menu);
		return true;
	}

	private void showDialog(Bundle savedInstanceState) {
		AlertDialog dialog = new AlertDialog.Builder(MyLifeActivity.this)
				.setTitle("登录提示")
				.setMessage("是否登录")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						LayoutInflater factory = LayoutInflater
								.from(MyLifeActivity.this);
						final View DialogView = factory.inflate(
								R.layout.dialog, null);
						AlertDialog dlg = new AlertDialog.Builder(
								MyLifeActivity.this)
								.setTitle("登陆框")
								.setView(DialogView)
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												MyLifeActivity.this.username = (EditText) DialogView
														.findViewById(R.id.AccountEditText);
												MyLifeActivity.this.password = (EditText) DialogView
														.findViewById(R.id.PasswordEidtText);

												if (MyLifeActivity.this.username == null) {
													Log.v(TAG,
															"username is null!!!!!");
												}
												doLogin();
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												MyLifeActivity.this.finish();
											}
										}).create();
						dlg.show();

					}
				})
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						MyLifeActivity.this.finish();
					}
				}).create();
		dialog.show();
	}

	private void doLogin() {
		// TODO Auto-generated method
		// stub
		p_dialog = ProgressDialog.show(MyLifeActivity.this, "请等待", "正在为您登录...",
				true);
		new Thread() {
			public void run() {
				try {

					HttpPost httpPost = new HttpPost(Config.MYLIFE_SERVER);
					DefaultHttpClient client = new DefaultHttpClient();
					StringBuilder str = new StringBuilder();
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("username", username
							.getText().toString()));
					params.add(new BasicNameValuePair("password", password
							.getText().toString()));

					httpPost.setEntity(new UrlEncodedFormEntity(params,
							HTTP.UTF_8));
					HttpResponse httpRes = client.execute(httpPost);

					if (httpRes.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						HttpEntity entity = httpRes.getEntity();
						String ret = EntityUtils.toString(entity);

						Log.v(TAG, "ret= " + ret);

						CookieStore mCookieStore = client.getCookieStore();
						List<Cookie> cookies = mCookieStore.getCookies();
						for (int i = 0; i < cookies.size(); i++) {
							// 这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值

							if (cookies.get(i).getName().equals("JSESSIONID")) {
								MyLifeApp appCookie = ((MyLifeApp) getApplication());
								appCookie.setSessionID(cookies.get(i)
										.getValue());
							}
						}

					}

				} catch (Exception e) {
					Log.e(TAG, "Exception occurs when login.", e);
				} finally {
					p_dialog.dismiss();
				}
			}
		}.start();
	}

}
