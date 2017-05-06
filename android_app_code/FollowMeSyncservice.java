package com.mapapp.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.mapid.database.DBAdapter;
import com.mapid.utils.CommonFunctionMapid;

// This Service is used to Follow user current using timer and get follow location

public class FollowMeSyncservice extends Service {
	String lat, lng;
	private Timer mtTimer1;
	DBAdapter db;
	com.mapid.utils.SharedPrefs share;
	private Looper myLooper;
	String adlevel1, adlevel2, locality, streetadd, name, postalcode;
	String countryId, country;
	double latitude, longitude;
	String message;
	String Success;
	int flag;
	String profileid;

	public static boolean isRunning;
	private TimerTask mTasker1 = new TimerTask() {
		@Override
		public void run() {

			try {

				Looper.prepare();
				if (isRunning
						&& CommonFunctionMapid.isInternetOn(getBaseContext())) {
					// checking if time scheduled
					// matches current time
					syncToWebservice(); // call updates
				}

			} catch (Exception e) {
				// TODO: handle exception
				CommonFunctionMapid.displayToastshort(getApplicationContext(),
						e.getMessage(), Gravity.CENTER);
			}
			
		}
	};

	protected void syncToWebservice() {

		try {

			GPSTracker gps = new GPSTracker(getBaseContext());

			if (gps.canGetLocation()) {

				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
//
//				 lat = String.valueOf(latitude);
//				 lng = String.valueOf(longitude);
//				
//				 share.saveLatfollow(lat);
//				 share.saveLongfollow(lng);
				Location location = new Location("dummy provider");
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				new GetAddressTask().execute(location);

			}
		} catch (Exception e) {
			// TODO: handle exception
			CommonFunctionMapid.displayToastshort(getApplicationContext(),
					e.getMessage(), Gravity.CENTER);
		}

	}

	private class GetAddressTask extends AsyncTask<Location, Void, String> {
		Context mContext;

		/**
		 * Get a Geocoder instance, get the latitude and longitude look up the
		 * address, and return it
		 * 
		 * @params params One or more Location objects
		 * @return A string containing the address of the current location, or
		 *         an empty string if no address can be found, or an error
		 *         message
		 */
		@Override
		protected String doInBackground(Location... params) {

			Geocoder geocoder = new Geocoder(getBaseContext(),
					Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
				 */
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available), city, and
				 * country name.
				 */

				String addressMessage = address.getAddressLine(0) + " "
						+ address.getAddressLine(1);
				String addressText = String.format(
						"%s, %s, %s, %s",
						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "",
						// Locality is usually a city
						address.getLocality(), address.getPremises(),
						// The country of the address
						address.getCountryName());

				// streetAddress = address.getAddressLine(1);
				// Return the text

				streetadd = address.getAddressLine(0) + ","
						+ address.getAddressLine(1) + ","
						+ address.getCountryName();

				Log.e("TAG",
						address.getAddressLine(0) + ""
								+ address.getCountryCode() + " "
								+ address.getCountryName());
				countryId = address.getCountryCode();
				country = address.getCountryName();
				adlevel1 = address.getAdminArea();
				adlevel2 = address.getSubAdminArea();
				// streetadd = address.getAddressLine(0)
				// + address.getAddressLine(1);
				locality = address.getLocality();
				postalcode = address.getPostalCode();

				Log.e("check country code", "" + address.getCountryCode());
				Log.e("Locale", "" + address.getLocale());
				Log.e("Maxaddlineindex", "" + address.getMaxAddressLineIndex());
				Log.e("subadminarea", "" + address.getSubAdminArea());
				Log.e("addres line", "" + address.getAddressLine(0));
				Log.e("addres line 1", "" + address.getAddressLine(1));
				Log.e("adminarea", "" + address.getAdminArea());
				Log.e("featurename", "" + address.getFeatureName());
				Log.e("adminarea", "" + address.getAdminArea());
				Log.e("Locality", "" + address.getLocality());
				Log.e("premises", "" + address.getPremises());
				Log.e("postalcode", "" + address.getPostalCode());
				Log.e("sublocality", "" + address.getSubLocality());
				Log.e("extras", "" + address.getExtras());

				Log.e("back country", "" + country);

				Log.e("streetAddress", "" + streetadd);
				return addressMessage;
			} else {
				return "No address found";
			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {

				new UpdateRedirectInBackGround().execute();
			} catch (Exception e) {
				// TODO: handle exception
				CommonFunctionMapid.displayToastshort(getApplicationContext(),
						e.getMessage(), Gravity.CENTER);
			}

		}

	}

	public class UpdateRedirectInBackGround extends
			AsyncTask<Void, Void, String> {
		ProgressDialog pd;

		@Override
		protected String doInBackground(Void... params) {

			String url = "profile/UpdateFollowMeAddress";
			try {
				JSONObject jobj = new JSONObject();

				jobj.put("ProfileId", profileid);
				JSONObject address = new JSONObject();
				address.put("AdminLevel1", adlevel1);
				address.put("AdminLevel2", adlevel2);
				address.put("Country", country);
				address.put("CountryCode", countryId);
				address.put("Lat", String.valueOf(latitude));
				address.put("Lng", String.valueOf(longitude));
				address.put("StreetAddress", streetadd);
				address.put("Locality", locality);
				address.put("PostalCode", postalcode);
				jobj.put("Address", address);

				String response = CommonFunctionMapid.postJsonResponse(url,
						jobj.toString(), getBaseContext());
				Log.e("TAG", "Response..." + response);

				JSONObject responseget = new JSONObject(response);

				JSONObject statusobj = responseget.getJSONObject("Status");

				Success = statusobj.getString("Success");
				message = statusobj.getString("Message");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CommonFunctionMapid.displayToastshort(getApplicationContext(),
						e.getMessage(), Gravity.CENTER);
			}
			return null;
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();

		try {

			share = new com.mapid.utils.SharedPrefs(getBaseContext());
			profileid = share.getFollowingProfileid();
			// intent = new Intent(Constants.VIDEOBROADCAST);
			// share = new SharedPrefs(getBaseContext());
		} catch (Exception e) {
			// TODO: handle exception
			CommonFunctionMapid.displayToastshort(getApplicationContext(),
					e.getMessage(), Gravity.CENTER);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		if (mtTimer1 != null) {
			mtTimer1.cancel();
			mtTimer1.purge();
			mtTimer1 = null;
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		try {

			isRunning = true;
			mtTimer1 = new Timer();

			if (share.getFollowSync().equalsIgnoreCase(""))

			{

			} else {

				String timervalue = share.getFollowSync();
				String result = timervalue.replace(" ", "");
				int timer = Integer.parseInt(result);
				
				Log.e("timer service", "" + timer);

				int timeinmilli = timer * 60000;
				Log.e("timeinmilli service", "" + timeinmilli);
				

				mtTimer1.scheduleAtFixedRate(mTasker1, 0, timeinmilli);
			}
		} catch (Exception e) {
			// TODO: handle exception
			CommonFunctionMapid.displayToastshort(getApplicationContext(),
					e.getMessage(), Gravity.CENTER);
		}
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
