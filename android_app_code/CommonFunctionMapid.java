package com.mapapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;

import com.mapid.bean.Userfiles_bean;
import com.mapid.viewmodel.FetchPrivacyRequest;
import com.greenfield.mapid.R;

public class CommonFunctionMapapp {

	
	String timezone;

	public static String postStringResponse(String url,
			ArrayList<NameValuePair> nameValuePairs, Context ctx) {
		HttpClient httpclient = new DefaultHttpClient();
		SharedPrefs share;
		share = new SharedPrefs(ctx);
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		HttpPost httppost = new HttpPost(Constants.mainUrl + url);
		Log.e("url", "" + url);
		httppost.setHeader("X-Mobile", "true");
		httppost.setHeader("X-Device",
				String.valueOf(telephonyManager.getDeviceId()));
		httppost.setHeader("Authorization", "Basic " + share.getToken());

		Log.e("tokenn", "" + share.getToken());

		String responseStr = null;
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			org.apache.http.HttpResponse response = httpclient
					.execute(httppost);
			responseStr = EntityUtils.toString(response.getEntity());

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		}

		return responseStr;
	}

	public static boolean isInternetOn(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
		boolean haveConnectedEthernet = false;

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
			if (ni.getType() == cm.TYPE_ETHERNET) {
				haveConnectedEthernet = true;
			}
		}
		return haveConnectedWifi || haveConnectedMobile
				|| haveConnectedEthernet;
	}

	public static String postStringResponseWithout(String url,
			MultipartEntity entity2, String value) {
		StringBuilder s;
		ch.boye.httpclientandroidlib.client.HttpClient httpclient = new ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient();
		ch.boye.httpclientandroidlib.client.methods.HttpPost httppost = new ch.boye.httpclientandroidlib.client.methods.HttpPost(
				Constants.mainUrl + url);

		
		httppost.setHeader("IsProfilePic", value);
		httppost.setEntity(entity2);
		String responseStr = null;
		try {
			// httppost.setEntity(new UrlEncodedFormEntity(entity2));

			ch.boye.httpclientandroidlib.HttpResponse response = (ch.boye.httpclientandroidlib.HttpResponse) httpclient
					.execute(httppost);
			ch.boye.httpclientandroidlib.HttpEntity resEntity = response
					.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		}

		return s.toString();
	}

	public static String postStringResponseWithout1(String url,
			MultipartEntity entity3) {
		StringBuilder s;
		ch.boye.httpclientandroidlib.client.HttpClient httpclient = new ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient();
		ch.boye.httpclientandroidlib.client.methods.HttpPost httppost = new ch.boye.httpclientandroidlib.client.methods.HttpPost(
				Constants.mainUrl + url);

		

		httppost.setHeader("IsProfilePic", "false");
		httppost.setEntity(entity3);
		String responseStr = null;
		try {
			// httppost.setEntity(new UrlEncodedFormEntity(entity2));

			ch.boye.httpclientandroidlib.HttpResponse response = (ch.boye.httpclientandroidlib.HttpResponse) httpclient
					.execute(httppost);
			ch.boye.httpclientandroidlib.HttpEntity resEntity = response
					.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		}

		return s.toString();
	}

	public static String getStringFromGoogle(String url, Context ctx) {
		BufferedReader inputStream = null;
		StringBuffer sb = null;
		String result = "";
		URL jsonUrl;
		SharedPrefs share;
		share = new SharedPrefs(ctx);
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			jsonUrl = new URL(url);

			sb = new StringBuffer("");
			URLConnection dc = jsonUrl.openConnection();

			dc.addRequestProperty("X-Mobile", "true");
			dc.addRequestProperty("X-Device",
					String.valueOf(telephonyManager.getDeviceId()));
			dc.addRequestProperty("Authorization", "Basic " + share.getToken());
			dc.setConnectTimeout(50000);
			dc.setReadTimeout(50000);
			inputStream = new BufferedReader(new InputStreamReader(
					dc.getInputStream()));

			String line = "";
			while ((line = inputStream.readLine()) != null) {
				sb.append(line);
			}

			inputStream.close();
			result = sb.toString();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = "TimeOut";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "TimeOut";
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "TimeOut";
			e.printStackTrace();
		} catch (Exception e) {
			result = "TimeOut";
			e.printStackTrace();
		}
		if (result.equalsIgnoreCase("")) {
			result = "TimeOut";
		}
		return result;
	}

	public static Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
		// TODO Auto-generated method stub

		int targetWidth = 125;
		int targetHeight = 125;
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2,
				(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
				Path.Direction.CCW);

		canvas.clipPath(path);
		Bitmap sourceBitmap = scaleBitmapImage;
		canvas.drawBitmap(sourceBitmap, new Rect(0, 0, sourceBitmap.getWidth(),
				sourceBitmap.getHeight()), new Rect(0, 0, targetWidth,
				targetHeight), null);
		return targetBitmap;
	}

	public static Bitmap getBitmapFromURL(String image, Context context) {
		Bitmap b;
		try {
			if (image.contains("'")) {
				image = image.replace("'", "");
			}

			URL url = new URL(image);

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Log.e("from urrll input streammmmmmmmm", "" + input);

			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			Log.e("from urrll bbbbbbbbbbb", "" + myBitmap);
			return myBitmap;

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return b = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.no_image_blue);
		} catch (IOException e) {
			e.printStackTrace();
			return b = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.no_image_blue);

		} catch (Exception e) {

			e.printStackTrace();
			return b = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.no_image_blue);

		}

	}

	

	public static String postJsonResponse(String url, String json, Context ctx) {
		String stringifiedResponse;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.mainUrl + url);
		httppost.setHeader("Content-Type", "application/json");
		SharedPrefs share;
		share = new SharedPrefs(ctx);
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		httppost.setHeader("X-Mobile", "true");
		httppost.setHeader("X-Device",
				String.valueOf(telephonyManager.getDeviceId()));
		httppost.setHeader("Authorization", "Basic " + share.getToken());
		

		HttpResponse response;
		try {

			((HttpPost) httppost).setEntity(new StringEntity(json));
			response = httpclient.execute(httppost);

			stringifiedResponse = EntityUtils.toString(response.getEntity());

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		}

		return stringifiedResponse;
	}

	public static String postJsonResponse1(String url, String json, Context ctx) {
		String stringifiedResponse;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(Constants.mainUrl1 + url);
		httppost.setHeader("Content-Type", "application/json");
		SharedPrefs share;
		share = new SharedPrefs(ctx);
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);

		httppost.setHeader("X-Mobile", "true");
		httppost.setHeader("X-Device",
				String.valueOf(telephonyManager.getDeviceId()));
		httppost.setHeader("Authorization", "Basic " + share.getToken());
		
		HttpResponse response;
		try {

			((HttpPost) httppost).setEntity(new StringEntity(json));
			response = httpclient.execute(httppost);

			stringifiedResponse = EntityUtils.toString(response.getEntity());

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Timeout";
		}

		return stringifiedResponse;
	}

	public static String getStringResponse(String url, Context ctx) {
		BufferedReader inputStream = null;
		StringBuffer sb = null;
		String result = "";
		URL jsonUrl;
		SharedPrefs share;
		share = new SharedPrefs(ctx);
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			jsonUrl = new URL(Constants.mainUrl + url);
			Log.e("URL Common Function", "" + jsonUrl);

			sb = new StringBuffer("");
			URLConnection dc = jsonUrl.openConnection();

			dc.addRequestProperty("X-Mobile", "true");
			dc.addRequestProperty("X-Device",
					String.valueOf(telephonyManager.getDeviceId()));
			dc.addRequestProperty("Authorization", "Basic " + share.getToken());
			dc.setConnectTimeout(50000);
			dc.setReadTimeout(50000);
			inputStream = new BufferedReader(new InputStreamReader(
					dc.getInputStream()));

			String line = "";
			while ((line = inputStream.readLine()) != null) {
				sb.append(line);
			}

			inputStream.close();
			result = sb.toString();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = "TimeOut";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "TimeOut";
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "TimeOut";
			e.printStackTrace();
		} catch (Exception e) {
			result = "TimeOut";
			e.printStackTrace();
		}
		if (result.equalsIgnoreCase("")) {
			result = "TimeOut";
		}
		return result;
	}

	public static String getStringResponsepaypal(String url, Context ctx) {
		BufferedReader inputStream = null;
		StringBuffer sb = null;
		String result = "";
		URL jsonUrl;
		SharedPrefs share;
		share = new SharedPrefs(ctx);
		TelephonyManager telephonyManager = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			jsonUrl = new URL(Constants.mainUrl1 + url);

			sb = new StringBuffer("");
			URLConnection dc = jsonUrl.openConnection();

			dc.addRequestProperty("X-Mobile", "true");
			dc.addRequestProperty("X-Device",
					String.valueOf(telephonyManager.getDeviceId()));
			dc.addRequestProperty("Authorization", "Basic " + share.getToken());
			dc.setConnectTimeout(50000);
			dc.setReadTimeout(50000);
			inputStream = new BufferedReader(new InputStreamReader(
					dc.getInputStream()));

			String line = "";
			while ((line = inputStream.readLine()) != null) {
				sb.append(line);
			}

			inputStream.close();
			result = sb.toString();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = "TimeOut";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result = "TimeOut";
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result = "TimeOut";
			e.printStackTrace();
		} catch (Exception e) {
			result = "TimeOut";
			e.printStackTrace();
		}
		if (result.equalsIgnoreCase("")) {
			result = "TimeOut";
		}
		return result;
	}

	public static void displayToastshort(Context context, String Message,
			int position) {

		Toast t1 = Toast.makeText(context, Message, Toast.LENGTH_SHORT);
		t1.setGravity(position, 0, 0);

		t1.show();

	}

	public static void displayToastlong(Context context, String Message,
			int position) {

		Toast t1 = Toast.makeText(context, Message, Toast.LENGTH_LONG);
		t1.setGravity(position, 0, 0);
		t1.show();

	}

	public static void showAlerts(Context context, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void GetPrivacyData() {

		new FetchPrivacyRequest();

	}

	public static void SaveTimeZoneCanged(Bundle extras, Context context,
			String userId) {
		// TODO Auto-generated method stub
		String timezones;
		timezones = extras.getString("time-zone");

		new SaveTimeChanged(timezones, context, userId).execute();

	}

	public static ArrayList<Userfiles_bean> pasrJson(String response) {
		// TODO Auto-generated method stub

		ArrayList<Userfiles_bean> userfilemodel = new ArrayList<Userfiles_bean>();
		try {
			JSONObject jobj = new JSONObject(response);

			JSONObject statusobj = jobj.getJSONObject("Status");

			JSONObject dataobj = jobj.getJSONObject("Data");

			String Success = statusobj.getString("Success");

			if (Success.equalsIgnoreCase("true")) {
				JSONArray dataarray = dataobj.getJSONArray("FileList");
				Log.e("dataarray.length()", "" + dataarray.length());

				for (int i = 0; i < dataarray.length(); i++) {
					Userfiles_bean filemodel = new Userfiles_bean();

					JSONObject fileObj = dataarray.getJSONObject(i);
					String id = fileObj.getString("Id");
					filemodel.setId(id);

					String ContentType = fileObj.getString("ContentType");
					filemodel.setContentType(ContentType);

					String ThumbPath = fileObj.getString("ThumbPath");
					filemodel.setThumbPath(ThumbPath);

					String FileName = fileObj.getString("FileName");
					filemodel.setFileName(FileName);

					String OriginalFileName = fileObj
							.getString("OriginalFileName");
					filemodel.setOriginalFileName(OriginalFileName);

					String WebPath = fileObj.getString("WebPath");
					filemodel.setWebPath(WebPath);

					userfilemodel.add(filemodel);

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();

		}
		return userfilemodel;
	}


}
