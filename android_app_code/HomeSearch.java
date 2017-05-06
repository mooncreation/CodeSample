package com.mapapp.mapid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.text.style.BulletSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mapid.adapter.HomeSearch_adapter;
import com.mapid.async.FetchPrivacyInbackground;
import com.mapid.bean.Profile_bean;
import com.mapid.bean.keywords_bean;
import com.mapid.database.DBAdapter;
import com.mapid.service.FollowMeSyncservice;
import com.mapid.service.GPSTracker;
import com.mapid.utils.CommonFunctionMapid;
import com.mapid.utils.MessageReceivingService;
import com.mapid.utils.SharedPrefs;
import com.navdrawer.SimpleSideDrawer;
import com.mapid.viewmodel.MapifyResponse;
import com.mapid.viewmodel.ModelMapping;
import com.greenfield.mapid.R;

/*

 This is Activity is about Mainhome Map Screen in this Scrren get
 your current location and Search list of locations get Notifications 
and to other option Setting. you can get Notification using GCM */

@SuppressLint("SetJavaScriptEnabled")
public class HomeSearch extends Activity implements OnQueryTextListener,
		OnScrollListener {

	ListView list1;
	ListView list_menu;
	private Handler handler;
	Button btnCeateMyMapID, btncreateMapid_plus, btn_Mymapid;
	HomeSearch_adapter adap;
	Activity context = this;
	
	ImageView image_menu;
	RelativeLayout relative_list;
	ProgressDialog pd;
	LocationManager locationManager;
	String lat, lng, locality, countrycode;
	Profile_bean pro_bean;
	private final int TRIGGER_SERACH = 1;
	SharedPrefs share;
	String streetAddress;
	Dialog alertDialog;
	String temp = "";
	private ActionBarDrawerToggle mDrawerToggle;
	boolean flag_loading = false;
	public int currentPage = 1;
	LinearLayout linear_cretaemapid;
	RelativeLayout relative_createmapid;
	DBAdapter db;
	GPSTracker gps;
	private final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;
	ArrayAdapter<String> adaptermenu;
	GoogleMap mMap;
	double latitude, longitude;
	private String[] mPlanetTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	
	private CharSequence title;
	
	SimpleSideDrawer slide_me;
	RelativeLayout relative_search;
	public static Boolean inBackground = true;
	private SharedPreferences savedValues;
	private String numOfMissedMessages;
	private int keyCode;
	private KeyEvent event;
	private int position;
	private CharSequence[] listArray;
	private ViewGroup frameLayout;

	public void onStop() {
		super.onStop();
		inBackground = true;
	}

	public void onRestart() {
		super.onRestart();
		
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	public void onResume() {
		super.onResume();
		getCurrentLocation();
		inBackground = true;

		savedValues = MessageReceivingService.savedValues;
		int numOfMissedMessages = 0;
		if (savedValues != null) {
			numOfMissedMessages = savedValues.getInt(this.numOfMissedMessages,
					0);
		}
		
		Log.d("newMessage", "newMessage");
		
	}

	// If messages have been missed, check the backlog. Otherwise check the
	// current intent for a new message.

	private String getMessage(int numOfMissedMessages) {

		String message = "";
		String linesOfMessageCount = getString(R.string.lines_of_message_count);
		if (numOfMissedMessages > 0) {
			String plural = numOfMissedMessages > 1 ? "s" : "";
			Log.i("onResume", "missed " + numOfMissedMessages + " message"
					+ plural);
			// tView.append("You missed " + numOfMissedMessages +" message" +
			// plural + ". Your most recent was:\n");
			for (int i = 0; i < savedValues.getInt(linesOfMessageCount, 0); i++) {
				String line = savedValues.getString("MessageLine" + i, "");
				message += (line + "\n");
			}
			NotificationManager mNotification = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotification.cancel(R.string.notification_number);
			SharedPreferences.Editor editor = savedValues.edit();
			editor.putInt(this.numOfMissedMessages, 0);
			editor.putInt(linesOfMessageCount, 0);
			editor.commit();
		} else {
			Log.i("onResume", "no missed messages");
			Intent intent = getIntent();
			if (intent != null) {
				Bundle extras = intent.getExtras();
				if (extras != null) {
					// for (String key : extras.keySet()) {
					// message += key + "=" + extras.getString(key) + "\n";
					// }
				}
			}
		}
		message += "\n";
		return message;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_search);

	

		ActionBar actionbar = getActionBar();
		 actionbar.setHomeButtonEnabled(true);
		 getActionBar().setDisplayHomeAsUpEnabled(true);
		 getActionBar().setIcon(
		 new ColorDrawable(getResources().getColor(
		 android.R.color.transparent)));
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		updateNavigationDrawerIcon(getResources().getDrawable(
				R.drawable.ic_drawer_white));
		slide_me = new SimpleSideDrawer(this);
		
		share = new SharedPrefs(HomeSearch.this);


             // Check if user is login or not 

		if (share.getUserId().equalsIgnoreCase("")) {
			slide_me.setLeftBehindContentView(R.layout.slider2);
			TextView txt_login_slider_another = (TextView) findViewById(R.id.txt_login_slider_another);
			// TextView txt_setting_slider_another = (TextView)
			// findViewById(R.id.txt_setting_slider_another);
			TextView txt_help_slider_another = (TextView) findViewById(R.id.txt_help_slider_another);
			txt_login_slider_another.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						Intent i10 = new Intent(HomeSearch.this,
								SignINsignUPScreen.class);
						startActivity(i10);

					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}
				}
			});
			
                    // Click on help screen

			txt_help_slider_another.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {

						Intent i12 = new Intent(HomeSearch.this, HelpNew.class);
						startActivity(i12);
					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}
				}
			});

		} else {

                       // If user Show this option in Siliding Menu Drawer

			slide_me.setLeftBehindContentView(R.layout.slider);
			TextView txt_email_slider = (TextView) findViewById(R.id.txt_email_slider);
			txt_email_slider.setText(share.getEmail().substring(0, 10)+"...");
			TextView txt_choosemapid_slider = (TextView) findViewById(R.id.txt_choosemapid_slider);

			

			TextView txt_mymapid_slider = (TextView) findViewById(R.id.txt_mymapid_slider);
			TextView txt_notification_slider = (TextView) findViewById(R.id.txt_notification_slider);
			TextView txt_setting_slider = (TextView) findViewById(R.id.txt_setting_slider);
			TextView txt_help_slider = (TextView) findViewById(R.id.txt_help_slider);

                       // Choose Your MapID

			txt_choosemapid_slider.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						Log.e("mapidcount",
								String.valueOf(share.getMapidcount()));
						Log.e("profilecount",
								String.valueOf(share.getProfileCount()));

                                    // Check if user profile count two or more

						if (share.getMapidcount() >= 2
								|| share.getProfileCount() >= 2) {

							StringBuilder build = new StringBuilder();
							build.append("Want to create more MapIDs ?")
									.append("\n").append("Get the Pro Version");
							AlertDialog.Builder builder = new AlertDialog.Builder(
									HomeSearch.this);
							builder.setMessage(build.toString());
							builder.setPositiveButton("Get Proversion",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.cancel();
										}
									});
							builder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.cancel();
										}
									});
							
							builder.show();

						} else {

							Intent i = new Intent(HomeSearch.this,
									ChooseMapid.class);

							startActivity(i);
						}
					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}
				}
			});
			

			txt_mymapid_slider.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						Intent i2 = new Intent(HomeSearch.this, MyMapID.class);
						startActivity(i2);

					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}

				}
			});
			txt_notification_slider.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {

						Intent i3 = new Intent(HomeSearch.this,
								UserNotification.class);
						startActivity(i3);
					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}

				}
			});
			txt_setting_slider.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {

						Intent i4 = new Intent(HomeSearch.this,
								SettingsNew.class);
						startActivity(i4);
					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}

				}
			});
			txt_help_slider.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {

						Intent i5 = new Intent(HomeSearch.this, HelpNew.class);
						startActivity(i5);
					} catch (Exception e) {
						// TODO: handle exception
						CommonFunctionMapid.displayToastshort(
								getApplicationContext(), e.getMessage(),
								Gravity.CENTER);
					}

				}
			});
		}

               // Check Location if GPS on or OFF

		try {

			db = new DBAdapter(HomeSearch.this);
			gps = new GPSTracker(HomeSearch.this);

			startService(new Intent(this, MessageReceivingService.class));
			intialiseUI();

			copyDBToSDCard();
			LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

			LocationListener locationlistener = new LocationListener() {

				@Override
				public void onLocationChanged(Location location) {
					// TODO Auto-generated method stub

					GPSTracker gps = new GPSTracker(HomeSearch.this);

					if (gps.canGetLocation()) {

					} else {

					}

				}
			};
                        
                          // Check location wifi or GPS

			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					|| locationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				// Get user Current location 

				getCurrentLocation();
			} else {
                               
                               // if user location off then show alert 
                                   
				showGPSDisabledAlertToUser();
			}

		} catch (Exception e) {
			// TODO: handle exception
			CommonFunctionMapid.displayToastshort(getApplicationContext(),
					e.getMessage(), Gravity.CENTER);
		}

	}

	protected void makeUseOfNewLocation(Location location) {
		// TODO Auto-generated method stub

	}

	private void updateNavigationDrawerIcon(Drawable drawable) {
		// TODO Auto-generated method stub
		Method setHomeAsUpIndicator;
		try {
			setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod(
					"setHomeAsUpIndicator", Drawable.class);
			setHomeAsUpIndicator.invoke(getActionBar(), drawable);
		} catch (NoSuchMethodException e) {
			Log.e("CHECK", "No Such Method");
			View home = findViewById(android.R.id.home);
			ViewGroup parent = (ViewGroup) home.getParent();
			int childCount = parent.getChildCount();
			if (childCount == 2) {
				final View first = parent.getChildAt(0);
				final View second = parent.getChildAt(1);
				final View up = first.getId() == android.R.id.home ? second
						: first;
				((ImageView) up).setImageDrawable(drawable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

      // Show GPS alert dialog

	private void showGPSDisabledAlertToUser() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						"GPS is disabled in your device. Would you like to enable it?")
				.setCancelable(false)
				.setPositiveButton("Goto Settings Page To Enable GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);

							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	
       // Set Current Loction

	private void getCurrentLocation() {

		GPSTracker gps = new GPSTracker(this);

		latitude = gps.getLatitude();
		longitude = gps.getLongitude();

		DisplayMetrics metrics = HomeSearch.this.getResources()
				.getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		
		mMap.getUiSettings().setZoomControlsEnabled(false);

		
               // Set Marer position 

		Marker marker = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.snippet("")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.address_pointer_marker))
				.draggable(true));

		marker.setDraggable(true);
		
		Projection projection = mMap.getProjection();

		mMap.clear();
		Marker marker1 = mMap.addMarker(new MarkerOptions()
				.position(new LatLng(latitude, longitude))
				.snippet("")
				.icon(BitmapDescriptorFactory
						.fromResource(R.drawable.address_pointer_marker))
				.draggable(true));

		marker.setDraggable(true);
		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub

				Marker marker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(latitude, longitude))
						.snippet("")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.address_pointer_marker))
						.draggable(true));

				marker.setDraggable(true);
			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
				// TODO Auto-generated method stub
				LatLng dragPosition = arg0.getPosition();
				double dragLat = dragPosition.latitude;
				double dragLong = dragPosition.longitude;
				Log.i("info", "on drag end :" + dragLat + " dragLong :"
						+ dragLong);
				Toast.makeText(getApplicationContext(), "Marker Dragged..!",
						Toast.LENGTH_LONG).show();

				Marker marker = mMap.addMarker(new MarkerOptions()
						.position(new LatLng(latitude, longitude))
						.snippet("")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.address_pointer_marker))
						.draggable(true));

				marker.setDraggable(true);
			}

			@Override
			public void onMarkerDragStart(Marker marker) {
				// TODO Auto-generated method stub

				String latitudemarker = String.valueOf(latitude);
				marker.setSnippet("");
				mMap.animateCamera(CameraUpdateFactory.newLatLng(marker
						.getPosition()));

			}

		});

		marker.showInfoWindow();
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);

		// mMap.moveCamera(center);
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(latitude, longitude)) // Sets..
															// the

				.zoom(17) // Sets the zoom
				.bearing(30) // Sets the orientation of the camera to east
				.tilt(0) // Sets the tilt of the camera to 30 degrees
				.build(); // Creates a CameraPosition from the builder
		mMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));

		Location location = new Location("dummy provider");
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		new GetAddressTask().execute(location);

		// } else {
		// // getCurrentLocation();
		// // gps.showSettingsAlert();
		// }
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	
      // Handle Back Button 

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// getCurrentLocation();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	

	private void Searchperformclick() {
		// TODO Auto-generated method stub
		// new SeachMapID(temp).execute();

	}

	@Override
	public void onBackPressed() {
		// // TODO Auto-generated method stub
		super.onBackPressed();
		

	}

	private void MethodCancel() {
		// TODO Auto-generated method stub
		alertDialog.cancel();
	}

	public void backpress(View v) {
		onBackPressed();
	}

      // Copy Databast to SD Card

	public void copyDBToSDCard() {
		try {
			InputStream myInput = new FileInputStream(
					"/data/data/com.greenfield.mapid/databases/" + "MapId");

			File file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/" + "MapId");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.i("FO", "File creation failed for " + file);
				}
			}

			OutputStream myOutput = new FileOutputStream(Environment
					.getExternalStorageDirectory().getPath() + "/" + "MapId");

			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
			Log.i("FO", "copied");

		} catch (Exception e) {
			Log.i("FO", "exception=" + e);
			e.printStackTrace();
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
			Geocoder geocoder = new Geocoder(HomeSearch.this,
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
				Log.e("address geo code", "" + addresses);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("Incorrect");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return ("Incorrect");
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

				streetAddress = address.getAddressLine(0) + ","
						+ address.getAddressLine(1) + ","
						+ address.getCountryName();

				// CountryNameProfile = address.getCountryName();
				countrycode = address.getCountryCode();
				// adlevel1 = address.getAdminArea();
				// adlevel2 = address.getSubAdminArea();

				// adminareacity = address.getAdminArea();
				// localitycity = address.getLocality();

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

				// Log.e("back country", "" + CountryNameProfile);

				Log.e("streetAddress", "" + streetAddress);
				// Return the text
				return addressMessage;
			} else {
				return "No address found";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.equalsIgnoreCase("incorrect")) {

				CommonFunctionMapid
						.showAlerts(HomeSearch.this,
								"No address found at the entered latitude longitude.Please enter valid values");

			} else {

			}

			// Toast.makeText(ChooseMapid.this, result, 1).show();
		}

	}

	private class GetAddressTaskforlist extends
			AsyncTask<Location, Void, String> {
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
			Geocoder geocoder = new Geocoder(HomeSearch.this,
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
				Log.e("address geo code", "" + addresses);
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("Incorrect");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments "
						+ Double.toString(loc.getLatitude()) + " , "
						+ Double.toString(loc.getLongitude())
						+ " passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return ("Incorrect");
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
				streetAddress = address.getAddressLine(0) + ","
						+ address.getAddressLine(1) + ","
						+ address.getCountryName();

				countrycode = address.getCountryCode();

				locality = address.getLocality();

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

				// Return the text
				return addressMessage;
			} else {
				return "No address found";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			// Toast.makeText(ChooseMapid.this, result, 1).show();
		}

	}

	public class SeachMapID extends
			AsyncTask<String, Void, ArrayList<Profile_bean>> {
		String signedup;
		String newText;

		public SeachMapID(String newText) {
			// TODO Auto-generated constructor stub
			this.newText = newText;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();
			try {

				pd = new ProgressDialog(context);
				pd.setMessage("Loading....");
				pd.setCancelable(false);
				pd.show();
			} catch (Exception e) {
				// TODO: handle exception
				CommonFunctionMapid.displayToastshort(getApplicationContext(),
						e.getMessage(), Gravity.CENTER);
			}
		}

		@Override
		protected ArrayList<Profile_bean> doInBackground(String... params) {
			// TODO Auto-generated method stub\\

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			ArrayList<Profile_bean> arraypro = new ArrayList<Profile_bean>();

			nameValuePairs.add(new BasicNameValuePair("SearchTerm", newText));

			nameValuePairs.add(new BasicNameValuePair("UserID", share
					.getUserId()));
			nameValuePairs.add(new BasicNameValuePair("Lat", lat));
			nameValuePairs.add(new BasicNameValuePair("Lng", lng));
			nameValuePairs.add(new BasicNameValuePair("ResultLimit", "20"));
			nameValuePairs.add(new BasicNameValuePair("PageNo", "1"));
			nameValuePairs.add(new BasicNameValuePair("countryNameShort",
					countrycode));
			nameValuePairs.add(new BasicNameValuePair("locality", locality));
			nameValuePairs.add(new BasicNameValuePair("IsExactMatch", "false"));

			Log.e("SearchTerm", "" + newText.toString());
			Log.e("UserID", "" + share.getUserId());
			Log.e("Latttt", "" + lat);
			Log.e("Lnggg", "" + lng);

			String response = CommonFunctionMapid.postStringResponse(
					"profile/SearchProfilesWeb", nameValuePairs, context);

			// Log.e("SearchMapID  responseeeeeee", "" + response);
			arraypro = parseJson(response);
			return arraypro;

		}

		@Override
		protected void onPostExecute(ArrayList<Profile_bean> result) {
			// TODO Auto-generated method stub
			
			super.onPostExecute(result);

			try {

				try {
					if ((pd != null) && pd.isShowing()) {
						pd.dismiss();

						try {

							if (CommonFunctionMapid.isInternetOn(context) == true) {

								result = new setDistanceInBackGround().execute(
										result).get();

							} else {
								CommonFunctionMapid
										.showAlerts(context,
												"Connection error !...Please turn on internet service");
							}

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Log.e("Home_searhchhhh", "" + result.size());
						if (result.size() <= 0) {
							relative_list.removeAllViews();
							LayoutInflater inflater = LayoutInflater
									.from(context);
							View vi = inflater.inflate(R.layout.nomapidfound,
									null);

							TextView txt = (TextView) vi
									.findViewById(R.id.textView_nomap);
							txt.setText("MapID not found");

							// list1.setVisibility(View.GONE);
							relative_list.addView(vi);
							// adap = new HomeSearch_adapter(
							// R.layout.home_search_inflater,
							//
							// context, result);
							// list1.setAdapter(adap);
							// CommonFunctionMapid.showAlerts(context,
							// "No Mapid Found");

						} else {
							relative_list.removeAllViews();
							relative_list.addView(list1);
							list1.setVisibility(View.VISIBLE);
							adap = new HomeSearch_adapter(
									R.layout.home_search_inflater,

									context, result);
							list1.setAdapter(adap);
						}
					}
				} catch (final IllegalArgumentException e) {
					// Handle or log or ignore
				} catch (final Exception e) {
					// Handle or log or ignore
				} finally {
					// pd = null;
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}

	}

	private class setDistanceInBackGround extends
			AsyncTask<ArrayList<Profile_bean>, Void, ArrayList<Profile_bean>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
		}

		@Override
		protected ArrayList<Profile_bean> doInBackground(
				ArrayList<Profile_bean>... params) {

			for (int i = 0; i < params[0].size(); i++) {
				String url = "http://maps.googleapis.com/maps/api/directions/json?origin="
						+ lat
						+ ","
						+ lng
						+ "&destination="
						+ params[0].get(i).getLat()
						+ ","
						+ params[0].get(i).getLng()
						+ "&sensor=true&mode="
						+ share.getTravellingMode();
				Log.e("TAG", "Response..." + url);
				String response = CommonFunctionMapid.getStringFromGoogle(url,
						context);
				JSONObject jobj;
				try {
					jobj = new JSONObject(response);

					// String distance = jobj.getJSONArray("routes")

					// .getJSONObject(0).getJSONObject("distance")
					// .getString("text");
					String distance = jobj.getJSONArray("routes")
							.getJSONObject(0).getJSONArray("legs")
							.getJSONObject(0).getJSONObject("distance")
							.getString("text");
					params[0].get(i).setDistance(distance);
					Log.e("TAG", "Response..." + distance);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// TODO Auto-generated method stub
			return params[0];
		}

		@Override
		protected void onPostExecute(ArrayList<Profile_bean> params) {
			// TODO Auto-generated method stub
			super.onPostExecute(params);

			
		}

	}

	public ArrayList<Profile_bean> parseJson(String response) {
		ArrayList<Profile_bean> arrayProfile = new ArrayList<Profile_bean>();
		try {
			JSONObject jobj = new JSONObject(response);

			JSONObject statusobj = jobj.getJSONObject("Status");

			String Success = statusobj.getString("Success");

			if (Success.equalsIgnoreCase("true")) {

				JSONObject dataobj = jobj.getJSONObject("Data");

				JSONArray profilearray = dataobj.getJSONArray("Profiles");

				Log.e("length of profile array", "" + profilearray.length());

				for (int i = 0; i < profilearray.length(); i++) {

					Profile_bean pro_bean = new Profile_bean();

					JSONObject pobj = profilearray.getJSONObject(i);

					String firstname = pobj.getString("FirstName");

					if (pobj.getString("FirstName") == null
							|| pobj.getString("FirstName").equalsIgnoreCase(
									"null")) {
						Log.e("first namee class", "null class");
						pro_bean.setFirstname("");
					} else {

						Log.e("first namee class", "" + firstname);
						pro_bean.setFirstname(firstname);
					}

					String LastName = pobj.getString("LastName");

					if (pobj.getString("LastName") == null
							|| pobj.getString("LastName").equalsIgnoreCase(
									"null")) {
						Log.e("LastName namee class", "null class");
						pro_bean.setLastname("");
					} else {
						Log.e("LastName namee class", "" + LastName);
						pro_bean.setLastname(LastName);
					}

					String ProfessionalName = pobj
							.getString("ProfessionalName");
					pro_bean.setProfessionalName(ProfessionalName);

					String ProfileType = pobj.getString("ProfileType");
					pro_bean.setProfileType(ProfileType);

					String ShortDescription = pobj
							.getString("ShortDescription");
					if (pobj.getString("ShortDescription") == null
							|| pobj.getString("ShortDescription")
									.equalsIgnoreCase("null")) {
						pro_bean.setShortdescription("");
					} else {
						pro_bean.setShortdescription(ShortDescription);
					}

					String MapId = pobj.getString("MapId");
					pro_bean.setMapid(MapId);

					// String Email = pobj.getString("Email");
					// pro_bean.setEmail(Email);

					String UserProfileId = pobj.getString("UserProfileId");
					pro_bean.setUserProfileid(UserProfileId);

					String UserId = pobj.getString("UserId");
					pro_bean.setUserid(UserId);

					db.open();
					if (db.getFavDependingOnId(UserProfileId).equalsIgnoreCase(
							"true")) {
						pro_bean.setIsFav("true");
					} else {
						pro_bean.setIsFav("false");
					}
					if (Integer.parseInt(db
							.getRecentDependingOnId(UserProfileId)) > 0) {
						pro_bean.setIsRecent("true");
					} else {
						pro_bean.setIsRecent("false");
					}

					String Distance = pobj.getString("Distance");
					double dist = Double.parseDouble(Distance);

					DecimalFormat df = new DecimalFormat("#");

					pro_bean.setDistance(Double.parseDouble(df.format(dist))
							+ "");

					if (pobj.getString("ProfilePic") == null
							|| pobj.getString("ProfilePic").equalsIgnoreCase(
									"null")) {

					} else {
						JSONObject profilepicObj = pobj
								.getJSONObject("ProfilePic");

						String Thumbpath = profilepicObj.getString("ThumbPath");
						pro_bean.setThumbpath(Thumbpath);
					}

					JSONObject addressObj = pobj.getJSONObject("Address");

					String AdminLevel1 = addressObj.getString("AdminLevel1");

					if (addressObj.getString("AdminLevel1") == null
							|| addressObj.getString("AdminLevel1")
									.equalsIgnoreCase("null")) {
						pro_bean.setAdminlevel1("");
					} else {
						pro_bean.setAdminlevel1(AdminLevel1);
					}

					String AdminLevel2 = addressObj.getString("AdminLevel2");
					if (addressObj.getString("AdminLevel2") == null
							|| addressObj.getString("AdminLevel2")
									.equalsIgnoreCase("null")) {
						pro_bean.setAdminlevel2("");
					} else {
						pro_bean.setAdminlevel2(AdminLevel2);
					}

					String Country = addressObj.getString("Country");

					if (addressObj.getString("Country") == null
							|| addressObj.getString("Country")
									.equalsIgnoreCase("null")) {
						pro_bean.setCountry("");
					} else {
						pro_bean.setCountry(Country);
					}

					String CountryCode = addressObj.getString("CountryCode");
					pro_bean.setCountryCode(CountryCode);

					Log.e("CountryCode",
							"" + addressObj.getString("CountryCode"));
					String Lat = addressObj.getString("Lat");
					pro_bean.setLat(Lat);

					String Lng = addressObj.getString("Lng");
					pro_bean.setLng(Lng);

					String Id = addressObj.getString("Id");
					pro_bean.setId(Id);

					String Locality = addressObj.getString("Locality");
					// if (addressObj.getString("Locality") == null
					// || addressObj.getString("Locality")
					// .equalsIgnoreCase("null")) {
					// pro_bean.setLocality("");
					// } else {
					// pro_bean.setLocality(Locality);
					// }
					pro_bean.setLocality(Locality);

					String PostalCode = addressObj.getString("PostalCode");

					if (addressObj.getString("PostalCode") == null
							|| addressObj.getString("PostalCode")
									.equalsIgnoreCase("null")) {
						pro_bean.setPostalcode("");
					} else {
						pro_bean.setPostalcode(PostalCode);
					}

					String Route = addressObj.getString("Route");

					if (addressObj.getString("Route") == null
							|| addressObj.getString("Route").equalsIgnoreCase(
									"null")) {
						pro_bean.setRoute("");
					} else {
						pro_bean.setRoute(Route);
					}

					String StreetAddress = addressObj
							.getString("StreetAddress");

					if (addressObj.getString("StreetAddress") == null
							|| addressObj.getString("StreetAddress")
									.equalsIgnoreCase("null")) {
						pro_bean.setStreetAdress("");
					} else {
						pro_bean.setStreetAdress(StreetAddress);
					}

					String Type = addressObj.getString("Type");
					pro_bean.setType(Type);

					String PermStatus = pobj.getString("PermStatus");
					pro_bean.setpermstatus(PermStatus);

					if (pobj.getString("Keywords") == null
							|| pobj.getString("Keywords").equalsIgnoreCase(
									"null")) {
						// pro_bean.setAlist_keywords(null);

					} else {

						JSONArray keywordsarraymap = pobj
								.getJSONArray("Keywords");

						ArrayList<keywords_bean> alistkeywordsmap = new ArrayList<keywords_bean>();

						for (int m = 0; m < keywordsarraymap.length(); m++) {
							keywords_bean keyword_bean = new keywords_bean();

							String Keywordsobjmap = keywordsarraymap
									.getString(m);
							// JSONObject Keywordsobjmap = pobj.getJSONArray(
							// "keywords").getJSONObject(m);

							// JSONObject Keywordsobjmap = (JSONObject)
							// keywordsarraymap
							// .getJSONObject(m).get("keywords");

							// String Keywords = Keywordsobjmap.get("keywords")
							// .toString();
							// HashMap<String, String> keywordshash = new
							// HashMap<String, String>();
							// keywordshash.put(Keywords, Keywords);
							keyword_bean.setKeywords(Keywordsobjmap);
							Log.e("Keywords", "" + Keywordsobjmap);

							alistkeywordsmap.add(keyword_bean);

						}

						pro_bean.setAlist_keywords(alistkeywordsmap);
					}

					boolean IsRedirected = pobj.getBoolean("IsRedirected");
					pro_bean.setRedirected(IsRedirected);

					boolean IsFollowMe = pobj.getBoolean("IsFollowMe");
					pro_bean.setIsfollowMe(IsFollowMe);

					boolean IsEnterprise = pobj.getBoolean("IsEnterprise");
					pro_bean.setIsEnterprise(IsEnterprise);

					boolean IsGResult = pobj.getBoolean("IsGResult");
					pro_bean.setIsGResult(IsGResult);

					arrayProfile.add(pro_bean);
				}
				Log.e("Sizeeee", "" + arrayProfile.size());
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return arrayProfile;

	}

	public void intialiseUI() {

		try {

			relative_search = (RelativeLayout) findViewById(R.id.relative_search);

			list_menu = (ListView) findViewById(R.id.list_menu);
			final String[] Menu = new String[] { "Home", "My MapIDs",
					"Redirect MapID", "Notification", "Settings", "About",
					"Contact Us", "Help" };
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1,
					Menu);

			list_menu.setAdapter(adapter);

			list_menu.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					// ListView Clicked item index
					int itemPosition = position;
					if (itemPosition == 0) {
						Intent i = new Intent(getBaseContext(),
								HomeSearch.class);
						startActivity(i);
					} else if (itemPosition == 1) {
						Intent i = new Intent(getBaseContext(), MyMapID.class);
						startActivity(i);
					}

					else if (itemPosition == 2) {
						Intent i = new Intent(getBaseContext(),
								RedirectMapid.class);
						startActivity(i);
					} else if (itemPosition == 3) {
						Intent i = new Intent(getBaseContext(),
								Notifications.class);
						startActivity(i);
						// } else if (itemPosition == 4) {
						// Intent i = new Intent(getBaseContext(),
						// ChangeEmail.class);
						// startActivity(i);
					} else if (itemPosition == 4) {
						Intent i = new Intent(getBaseContext(),
								com.greenfield.mapid.Settings.class);
						startActivity(i);
					} else if (itemPosition == 5) {
						Intent i = new Intent(getBaseContext(), AboutUs.class);
						startActivity(i);
					} else if (itemPosition == 6) {
						Intent i = new Intent(getBaseContext(), ContactUs.class);
						startActivity(i);

					} else if (itemPosition == 7) {
						Intent i = new Intent(getBaseContext(), HelpNotes.class);
						startActivity(i);
						// } else if (itemPosition == 8) {
						// Intent i = new Intent(getBaseContext(),
						// SignINsignUPScreen.class);
						// startActivity(i);
						// }
					}

					// // ListView Clicked item value
					String itemValue = (String) list_menu
							.getItemAtPosition(position);


				}

			});

			image_menu = (ImageView) findViewById(R.id.image_menu);
			share = new SharedPrefs(context);


			image_menu.setOnClickListener(new OnClickListener() {
				int count = 0;

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (count == 0) {
						list_menu.setVisibility(View.GONE);
						count = 1;
					} else {
						list_menu.setVisibility(View.GONE);
						count = 0;
					}

				}
			});

                         // Check Bulid OS Version

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
				// only for gingerbread and newer versions
				image_menu.setVisibility(View.GONE);
				list_menu.setVisibility(View.GONE);
			}

			if (isServiceRunning()) {

			} else {
				context.startService(new Intent(context,
						FollowMeSyncservice.class));
			}

			relative_createmapid = (RelativeLayout) findViewById(R.id.relative_createmymapid);
			linear_cretaemapid = (LinearLayout) findViewById(R.id.linear_createmymapid);
			// btncreateMapid_plus = (Button)
			// findViewById(R.id.btn_createmymapid_plus);
			btn_Mymapid = (Button) findViewById(R.id.btn_mymapid);

			Log.e("USER ID FROM SHARE PREFRENCE", "" + share.getUserId());
			Log.e("USER ID FROM SHARE PREFRENCE", "");
			// Log.e("share mapid count", "" + share.getMymapidCount());

			
			if (share.getProfileCount() > 0 || share.getMapidcount() > 0) {
				linear_cretaemapid.setVisibility(View.VISIBLE);
				btn_Mymapid.setVisibility(View.VISIBLE);
				relative_createmapid.setVisibility(View.GONE);
			} else {
				relative_createmapid.setVisibility(View.VISIBLE);
				linear_cretaemapid.setVisibility(View.GONE);
				btn_Mymapid.setVisibility(View.GONE);
			}
			GPSTracker gps = new GPSTracker(HomeSearch.this);

			if (gps.canGetLocation()) {

				double latitude = gps.getLatitude();
				double longitude = gps.getLongitude();

				lat = String.valueOf(latitude);
				lng = String.valueOf(longitude);

				DisplayMetrics metrics = HomeSearch.this.getResources()
						.getDisplayMetrics();
				int width = metrics.widthPixels;
				int height = metrics.heightPixels;
				mMap = ((MapFragment) getFragmentManager().findFragmentById(
						R.id.map)).getMap();
				mMap.getUiSettings().setZoomControlsEnabled(true);

				mMap.setOnMapClickListener(new OnMapClickListener() {

					@Override
					public void onMapClick(LatLng arg0) {
						if (list_menu.isShown()) {
							list_menu.setVisibility(View.GONE);
						}
					}
				});

				Projection projection = mMap.getProjection();

				LatLng markerLatLng = new LatLng(latitude, longitude);
				Point markerScreenPosition = projection
						.toScreenLocation(markerLatLng);
				Point pointHalfScreenAbove = new Point(markerScreenPosition.x,
						markerScreenPosition.y + 50000);

				LatLng aboveMarkerLatLng = projection
						.fromScreenLocation(pointHalfScreenAbove);

				Marker marker = mMap
						.addMarker(new MarkerOptions()
								.position(new LatLng(latitude, longitude))
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.address_pointer_marker)));

				marker.setDraggable(false);
				marker.setSnippet("Tap to change location.");
				marker.showInfoWindow();
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(80);

				
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(latitude, longitude)) // Sets
																	// the
						// center of the
						// map to
						// location user
						.zoom(13) // Sets the zoom
						.bearing(0) // Sets the orientation of the camera to
									// east
						.tilt(0) // Sets the tilt of the camera to 30 degrees
						.build(); // Creates a CameraPosition from the builder
				mMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
				Location location = new Location("dummy provider");
				location.setLatitude(latitude);
				location.setLongitude(longitude);
				new GetAddressTaskforlist().execute(location);

			} else {
				// getCurrentLocation();
				// gps.showSettingsAlert();
			}

			// int displayWidth =
			// getWindowManager().getDefaultDisplay().getHeight();
			list1 = (ListView) findViewById(R.id.list_search);

			list1.setOnScrollListener(this);
			

			relative_list = (RelativeLayout) findViewById(R.id.relative_listhome);

			list1.setVisibility(View.GONE);

			 

			btnCeateMyMapID = (Button) findViewById(R.id.btn_createmymapid);
			btn_Mymapid.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(HomeSearch.this, MyMapID.class);
					startActivity(i);
					// Intent i = new Intent(HomeSearch.this, CellID.class);
					// startActivity(i);
				}
			});

			btnCeateMyMapID.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (share.getUserId().equalsIgnoreCase("")) {
						Intent i = new Intent(HomeSearch.this,
								SignINsignUPScreen.class);

						startActivity(i);
					}

					else {

                                         // Check User Create one or more profile

						if (share.getMapidcount() >= 2
								|| share.getProfileCount() >= 2) {

							StringBuilder build = new StringBuilder();
							build.append("Want to create more MapIDs ?")
									.append("\n").append("Get the Pro Version");
							AlertDialog.Builder builder = new AlertDialog.Builder(
									HomeSearch.this);
							builder.setMessage(build.toString());
							builder.setPositiveButton("Get Proversion",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.cancel();
										}
									});
							builder.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											dialog.cancel();
										}
									});
							AlertDialog alert = builder.create();
							alert.show();

						} else {

							Intent i = new Intent(HomeSearch.this,
									ChooseMapid.class);
							i.putExtra("Streetadress", streetAddress);
							startActivity(i);
						}
						
					}
					
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			CommonFunctionMapid.displayToastshort(getApplicationContext(),
					e.getMessage(), Gravity.CENTER);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);

		

		// When using the support library, the setOnActionExpandListener()
		// method is
		// static and accepts the MenuItem object as an argument

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(
				R.id.action_search).getActionView();
		// searchView.setImeOptions(searchView.getImeOptions()
		// | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI
		// | EditorInfo.IME_FLAG_NO_FULLSCREEN);

		int id = searchView.getContext().getResources()
				.getIdentifier("android:id/search_src_text", null, null);
		AutoCompleteTextView textView = (AutoCompleteTextView) searchView
				.findViewById(id);

		// Set search text color
		textView.setTextColor(Color.WHITE);

		int magId = getResources().getIdentifier("android:id/search_mag_icon",
				null, null);
		ImageView magImage = (ImageView) searchView.findViewById(magId);
		magImage.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
		magImage.setVisibility(View.GONE);

		searchView.setQueryHint(Html.fromHtml("<font color = #FFFFFF>"
				+ getResources().getString(R.string.action_homesearch)
				+ "</font>"));

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		// searchView.setSubmitButtonEnabled(true);
		// searchView.onKeyDown(keyCode, event);
		searchView.setOnQueryTextListener(this);

		searchView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (arg2.getAction() == KeyEvent.KEYCODE_SEARCH
						|| arg2.getAction() == KeyEvent.KEYCODE_ENTER) {
					// new SeachMapID().execute();
					return true;

				}
				return true;
			}
		});

		
		return true;

	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		temp = query;
		if (query.length() <= 0) {
			list1.setVisibility(View.GONE);
			relative_search.setVisibility(View.GONE);
		} else if (query.length() < 0) {
			list1.setVisibility(View.GONE);
			relative_search.setVisibility(View.GONE);
		} else {
			new SeachMapID(query).execute();
		}
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub

		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		case R.id.action_search:
			// search action
			return true;
		case R.id.action_bell:

			Notificationcall();
			return true;
		case android.R.id.home:
			Log.d("CHECK", "Navigation Icon is selected");
			// Use for slide from left to right
			slide_me.toggleLeftDrawer();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void Notificationcall() {
		Intent i = new Intent(HomeSearch.this, UserNotification.class);
		startActivity(i);
	}

	private boolean isServiceRunning() { 
                // checking if 
		// service is running

		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (FollowMeSyncservice.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	// ON scrool load listview Data

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (firstVisibleItem + visibleItemCount == totalItemCount
				&& totalItemCount != 0) {
			if (flag_loading == false) {
				flag_loading = true;
				currentPage++;
				new SeachMapID(temp.toString()).execute();

			}

		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}



