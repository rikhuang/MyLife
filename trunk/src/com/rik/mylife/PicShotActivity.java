package com.rik.mylife;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class PicShotActivity extends Activity {

	private static final String TAG = "PicShotActivity";

	private SurfaceView surfaceView;

	private Camera camera;

	private boolean preview;

	private boolean mPreviewRunning = false;

	private String pathToOurFile = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Window window = getWindow();

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 没有标题

		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 高亮

		setContentView(R.layout.pic);

		// btn_take_photo = (Button) findViewById(R.id.btn_take);
		// btn_take_photo.setOnClickListener(onTakePhotoButton);
		//
		// btn_upload_photo = (Button) findViewById(R.id.btn_pic_upload);
		// btn_upload_photo.setOnClickListener(onUploadPhotoButton);

		surfaceView = (SurfaceView) this.findViewById(R.id.surface_camera);

		surfaceView.getHolder().addCallback(new SufaceListener());

		/* 下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前 */

		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		surfaceView.setOnClickListener(onTakePhotoButton);

		// surfaceView.getHolder().setFixedSize(176, 144); // 设置分辨率

	}

	OnClickListener onTakePhotoButton = new OnClickListener() {
		public void onClick(View v) {
			try {
				camera.takePicture(null, null, new PictureCallbackListener());
				// camera.stopPreview();
				// mPreviewRunning = false;
				// preview = false;
				if (!pathToOurFile.trim().equals("")) {
					Log.v(TAG, "Upload pictures ..");
					FileUploadTask fileuploadtask = new FileUploadTask();
					fileuploadtask.execute();
					Log.v(TAG, "Upload complete ..");
				}

				// camera.startPreview();
				// mPreviewRunning = true;
				// preview = true;
			} catch (Exception e) {
				Log.v(TAG, "Exception occurs. ", e);
			}
		}
	};

	private final class PictureCallbackListener implements
			Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				File file = new File(Config.getRootPath() + "/photo/",
						getDateString() + "_1_2.jpg");
				FileOutputStream outStream = new FileOutputStream(file);
				bitmap.compress(CompressFormat.JPEG, 100, outStream);
				outStream.close();
				pathToOurFile = file.getAbsolutePath();

				Log.v(TAG, "pathToOurFile=" + pathToOurFile);

				if (!new File(pathToOurFile).exists()) {
					Log.v(TAG, "File not exist!!!");
				}

				// 重新浏览
				camera.stopPreview();
				camera.startPreview();
				preview = true;
				// mPreviewRunning = false;
			} catch (Exception e) {
				Log.e(TAG, "Exception occurs!", e);
			}
		}

		private String getDateString() {
			String value = "";
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyyMMddHHmmss");
				value = formatter.format(new Date());
			} catch (Exception e) {

			}
			return value;
		}
	}

	private final class SufaceListener implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if (mPreviewRunning) {
				camera.stopPreview();
			}
			Camera.Parameters p = camera.getParameters();
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

			Display display = wm.getDefaultDisplay();
			p.setPreviewSize(width, height);
			p.setPreviewFrameRate(3);// 每秒3帧
			p.setPictureFormat(PixelFormat.JPEG);// 设置照片的输出格式
			p.set("jpeg-quality", 90);// 照片质量
			p.setPictureSize(display.getWidth(), display.getHeight());// 设置照片的大小

			camera.setParameters(p);
			camera.autoFocus(null);
			try {
				camera.setPreviewDisplay(holder);
			} catch (Exception e) {
				Log.e(TAG, e.toString(), e);
			}
			camera.startPreview();

			mPreviewRunning = true;
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open();// 打开摄像头

			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				if (preview)
					camera.stopPreview();
				camera.release();
				camera = null;
			}
		}
	}

	class FileUploadTask extends AsyncTask<Object, Integer, Void> implements
			ITask {

		private ProgressDialog dialog = null;

		String urlServer = "http://192.168.101.15:8080/CTR/Upload";

		File uploadFile = null;
		long totalSize = 0; // Get size of file, bytes
		String serverResponseMessage = "";
		String res = "";

		@Override
		protected void onPreExecute() {

			uploadFile = new File(pathToOurFile);
			totalSize = uploadFile.length(); // Get size of file, bytes

			dialog = new ProgressDialog(PicShotActivity.this);
			dialog.setMessage("正在上传...");
			dialog.setIndeterminate(false);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setProgress(0);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Object... arg0) {
			try {

				res = UploadUtil.uploadFile2(pathToOurFile, urlServer,
						FileUploadTask.this);
			} catch (Exception e) {
				Log.v(TAG, "Exception occurs. ", e);
			}
			return null;

		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			dialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				dialog.dismiss();
				if (!res.trim().equals("")) {
					Toast.makeText(getApplicationContext(), res,
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
			}
		}

		@Override
		public void publishMyProgress(int pos) {
			publishProgress(pos);

		}

	}
}
