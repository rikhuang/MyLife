package com.rik.mylife;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class UploadUtil {

	private final static String TAG = "UploadUtil";

	public static String uploadFile(String localPath, String remoteUrl,
			ITask task) throws Exception {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		// DataInputStream inputStream = null;
		String response = "";

		long length = 0;
		int progress;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 256 * 1024;// 256KB
		BufferedReader br = null;

		File uploadFile = null;
		long totalSize = 0;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		try {
			Log.v(TAG, " localPath== " + localPath);
			uploadFile = new File(localPath);
			totalSize = uploadFile.length(); // Get size of file, bytes

			FileInputStream fileInputStream = new FileInputStream(new File(
					localPath));

			URL url = new URL(remoteUrl);
			connection = (HttpURLConnection) url.openConnection();
			// Set size of every block for post
			connection.setChunkedStreamingMode(256 * 1024);// 256KB

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			outputStream = new DataOutputStream(connection.getOutputStream());
			// inputStream = new
			// DataInputStream(connection.getInputStream());

			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
							+ localPath + "\"" + lineEnd);
			outputStream.writeBytes(lineEnd);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				length += bufferSize;
				progress = (int) ((length * 100) / totalSize);
				task.publishMyProgress(progress);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens
					+ lineEnd);
			task.publishMyProgress(100);

			String input;
			// BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			// Log.v(TAG, "==== https server content ====");
			while ((input = br.readLine()) != null) {
				Log.v(TAG, "read https content: " + input);
				sb.append(input);
			}
			response = sb.toString();

			// Responses from the server (code and message)
			// int serverResponseCode = connection.getResponseCode();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (Exception ex) {
			Log.v(TAG, "Exception occurs. ", ex);
			throw ex;
		} finally {
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {

				}
				connection = null;
			}
		}

		return response;
	}

	public static String uploadFile2(String localPath, String remoteUrl,
			ITask task) throws Exception {
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		// DataInputStream inputStream = null;
		String response = "";

		long length = 0;
		int progress;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 256 * 1024;// 256KB
		BufferedReader br = null;
		final String CHARSET = "utf-8"; //设置编码
		File uploadFile = null;
		long totalSize = 0;

		String lineEnd = "\r\n";
		String twoHyphens = "--";
//		String boundary = "*****";
		String  BOUNDARY =  UUID.randomUUID().toString();  //边界标识   随机生成
		try {
			Log.v(TAG, " localPath== " + localPath);
			uploadFile = new File(localPath);
			totalSize = uploadFile.length(); // Get size of file, bytes

			FileInputStream fileInputStream = new FileInputStream(new File(
					localPath));

			URL url = new URL(remoteUrl);
			connection = (HttpURLConnection) url.openConnection();
			// Set size of every block for post
			connection.setChunkedStreamingMode(256 * 1024);// 256KB

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);

			// Enable POST method
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + BOUNDARY);

			outputStream = new DataOutputStream(connection.getOutputStream());
			// inputStream = new
			// DataInputStream(connection.getInputStream());

			outputStream.writeBytes(twoHyphens + BOUNDARY + lineEnd);
			outputStream
					.writeBytes("Content-Disposition: form-data; name=\"img\";filename=\""
							+ localPath + "\"" + lineEnd);
			outputStream.writeBytes("Content-Type: application/octet-stream; charset="+CHARSET+lineEnd);
			outputStream.writeBytes(lineEnd);
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				length += bufferSize;
				progress = (int) ((length * 100) / totalSize);
				task.publishMyProgress(progress);

				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + BOUNDARY + twoHyphens
					+ lineEnd);
			task.publishMyProgress(100);

			String input;
			// BufferedReader br = null;
			StringBuilder sb = new StringBuilder();
			br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			// Log.v(TAG, "==== https server content ====");
			while ((input = br.readLine()) != null) {
				Log.v(TAG, "read https content: " + input);
				sb.append(input);
			}
			response = sb.toString();

			// Responses from the server (code and message)
			// int serverResponseCode = connection.getResponseCode();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();

		} catch (Exception ex) {
			Log.v(TAG, "Exception occurs. ", ex);
			throw ex;
		} finally {
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {

				}
				connection = null;
			}
		}

		return response;
	}
	
	public static String executeHttpPost(Map<String, String> map,
			String remoteUrl) throws Exception {
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(remoteUrl);

			List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			for (String key : map.keySet()) {
				postParameters.add(new BasicNameValuePair(key, map.get(key)));
			}
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters, "UTF-8");

			request.setEntity(formEntity);

			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");

			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
			}
			in.close();

			String result = sb.toString();
			return result;

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					Log.v(TAG, "Exception occurs. ", e);
				}
			}
		}

	}

	/**
	 * 
	 * @param POST_URL
	 *            url地址
	 * @param content
	 *            key=value形式
	 * @return 返回结果
	 * @throws Exception
	 */
	public static String sendPostData(String POST_URL, Map<String, String> map)
			throws Exception {
		HttpURLConnection connection = null;
		DataOutputStream out = null;
		BufferedReader reader = null;
		String line = "";
		String result = "";
		String content = "";
		try {
			URL postUrl = new URL(POST_URL);
			connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);// Let the run-time system (RTS) know
											// that we want input
			connection.setDoInput(true);// we want to do output.
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);// Post 请求不能使用缓存
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type",// Specify the header
															// content type.
					"application/x-www-form-urlencoded");
			connection.connect();
			out = new DataOutputStream(connection.getOutputStream()); // Send
																		// POST
																		// output.
			// DataOutputStream.writeBytes将字符串中的16位的unicode字符变为utf-8的字符形式写道流里
			// content = URLEncoder.encode(content, "utf-8");
			// out.writeBytes(content);

			/**
			 * 如果url中带有多个key-value参数对，则采用下面的方式写到content中
			 * 正文内容其实跟get的URL中'?'后的参数字符串一致
			 * 
			 * String content = "name=" + URLEncoder.encode ("Hitesh Agrawal") +
			 * "&profession=" + URLEncoder.encode ("Software Engineer");
			 * out.flush(); out.close();
			 */
			int count = 0;
			for (String key : map.keySet()) {
				// postParameters.add(new BasicNameValuePair(key,
				// map.get(key)));
				if (count > 0) {
					content = content + "&" + key + "="
							+ URLEncoder.encode(map.get(key), "utf-8");
				} else {
					content = content + key + "="
							+ URLEncoder.encode(map.get(key), "utf-8");
				}
				
				count++;
			}
			
			out.writeBytes(content);
			
			// 获取结果
			reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));// 设置编码
			while ((line = reader.readLine()) != null) {
				result = result + line;
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (out != null) {
				out.close();
				out = null;
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
			connection.disconnect();
		}
	}
}
