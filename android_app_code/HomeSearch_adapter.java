package com.mapapp.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.greenfield.mapid.Dummymapidview;
import com.greenfield.mapid.Mapview;
import com.greenfield.mapid.ViewProfile;
import com.greenfield.mapid.ViewProfileNew;
import com.mapid.async.HomeSearchImageLoad;
import com.mapid.bean.Profile_bean;
import com.mapid.bean.keywords_bean;
import com.mapid.service.FavSyncService;
import com.mapid.utils.CommonFunctionMapid;
import com.mapid.utils.SharedPrefs;
import com.greenfield.mapid.R;

// This adapter is used to Display Home Screen listview Data using View holder

public class HomeSearch_adapter extends BaseAdapter {
	int homeSearchInflater;
	int flagadap;
	Activity context;
	LayoutInflater inflater;
	ArrayList<Profile_bean> arrayadapter = new ArrayList<Profile_bean>();
	Profile_bean pro_bean;
	SharedPrefs share;
	Context ctx;
	GoogleMap mMap;

	public HomeSearch_adapter(int homeSearchInflater, Activity ctx,
			ArrayList<Profile_bean> arrayProfile) {
		// TODO Auto-generated constructor stub
		this.homeSearchInflater = homeSearchInflater;
		this.context = ctx;
		this.arrayadapter = arrayProfile;
		inflater = LayoutInflater.from(context);
		share = new SharedPrefs(context);

	}

	public HomeSearch_adapter(int homeSearchInflater, Activity ctx,
			ArrayList<Profile_bean> arrayProfile, int flag) {
		// TODO Auto-generated constructor stub
		this.homeSearchInflater = homeSearchInflater;
		this.context = ctx;
		this.arrayadapter = arrayProfile;
		this.flagadap = flag;
		inflater = LayoutInflater.from(context);
		share = new SharedPrefs(context);
	}

	public class ViewHolder {
		
		TextView address;
		TextView id;
		
		TextView distance;
		TextView userid;
		TextView profileid;
		TextView email;
		TextView txt_permstatus;
		ImageView imagelogohome;
		Button btnFav, btnRecent;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Log.e("adpater get count size", "" + arrayadapter.size());
		return arrayadapter.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder holder;
		View vi = convertView;
		Log.e("check3", "get view");
		if (vi == null) {
			vi = inflater.inflate(R.layout.home_searchnew, null);

			holder = new ViewHolder();

			holder.id = (TextView) vi.findViewById(R.id.txt_Mapid);
			
			holder.address = (TextView) vi.findViewById(R.id.txt_address);
			holder.userid = (TextView) vi.findViewById(R.id.txt_userid);
			holder.profileid = (TextView) vi.findViewById(R.id.txt_profileid);
			holder.email = (TextView) vi.findViewById(R.id.txt_email);
			holder.txt_permstatus = (TextView) vi
					.findViewById(R.id.txt_permstatus);
			holder.imagelogohome = (ImageView) vi
					.findViewById(R.id.imagelogohome);

			holder.distance = (TextView) vi.findViewById(R.id.txt_dsitance);
			
			holder.btnFav = (Button) vi.findViewById(R.id.btn_fav);
			holder.btnRecent = (Button) vi.findViewById(R.id.btn_recent);
			vi.setTag(holder);

		} else {
			holder = (ViewHolder) vi.getTag();
		}
		vi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {

					if (isServiceRunning()) {

					} else {
						context.startService(new Intent(context,
								FavSyncService.class));
					}

					if (flagadap == 1) {

						Intent i = new Intent(context, ViewProfileNew.class);

						i.putExtra("MapId", holder.id.getText().toString());
						i.putExtra("UserId", holder.userid.getText().toString());
						i.putExtra("Email", holder.email.getText().toString());
						i.putExtra("UserProfileId", holder.profileid.getText()
								.toString());
						i.putExtra("PermStatus", holder.txt_permstatus
								.getText().toString());

						context.startActivity(i);
						context.overridePendingTransition(0, 0);

					} else if (arrayadapter.get(position).isIsGResult() == true) {
						Intent i = new Intent(context, Mapview.class);
						
						i.putExtra("profilelat", arrayadapter.get(position)
								.getLat());
						i.putExtra("profilelong", arrayadapter.get(position)
								.getLng());
						i.putExtra("profileaddress", arrayadapter.get(position)
								.getStreetAdress());
						i.putExtra("FactualNapID", arrayadapter.get(position)
								.getMapid());
						i.putExtra("PermStatus", holder.txt_permstatus
								.getText().toString());
						
						i.putParcelableArrayListExtra("keywords", arrayadapter
								.get(position).getAlist_keywords());
						context.startActivity(i);

						// Log.d("Keywords",
						// arrayadapter.get(position).getAlist_keywords());
					} else if (arrayadapter.get(position).isIsEnterprise() == true) {

						Intent i = new Intent(context, Dummymapidview.class);

						i.putExtra("MapId", holder.id.getText().toString());
						i.putExtra("UserId", holder.userid.getText().toString());
						i.putExtra("Email", holder.email.getText().toString());
						i.putExtra("UserProfileId", holder.profileid.getText()
								.toString());
						i.putExtra("PermStatus", holder.txt_permstatus
								.getText().toString());

						context.startActivity(i);
					}

					else {

						Intent i = new Intent(context, ViewProfileNew.class);

						i.putExtra("MapId", holder.id.getText().toString());
						i.putExtra("UserId", holder.userid.getText().toString());
						i.putExtra("Email", holder.email.getText().toString());
						i.putExtra("UserProfileId", holder.profileid.getText()
								.toString());
						i.putExtra("PermStatus", holder.txt_permstatus
								.getText().toString());

						context.startActivity(i);

					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					CommonFunctionMapid.displayToastshort(context,
							e.getMessage(), Gravity.CENTER);
				}

			}
		});
		if (share.getUserId().equalsIgnoreCase("")) {
			holder.btnRecent.setVisibility(View.GONE);
			holder.btnFav.setVisibility(View.GONE);
		} else {

			holder.btnRecent.setVisibility(View.VISIBLE);
			holder.btnFav.setVisibility(View.VISIBLE);
		}

		pro_bean = arrayadapter.get(position);

		holder.id.setText(pro_bean.getMapid());
		holder.userid.setText(pro_bean.getUserid());
		holder.profileid.setText(pro_bean.getUserProfileid());
		holder.email.setText(pro_bean.getEmail());

		if (pro_bean.getDistance().equalsIgnoreCase("0.0")) {
			holder.distance.setText("");
		} else {
			holder.distance.setText(pro_bean.getDistance());
		}

		holder.txt_permstatus.setText(pro_bean.getpermstatus());

		new HomeSearchImageLoad(context, holder.imagelogohome).execute(pro_bean
				.getThumbpath());


		if (pro_bean.getIsFav().equalsIgnoreCase("true")) {
			// unfav means coloured
			holder.btnFav.setVisibility(View.VISIBLE);
			holder.btnFav.setBackgroundResource(R.drawable.btn_fav_blue);
		} else {
			holder.btnFav.setVisibility(View.GONE);
			// unfav means coloured
		}
		if (pro_bean.getIsRecent().equalsIgnoreCase("true")) {
			holder.btnRecent.setVisibility(View.VISIBLE);
		} else {
			holder.btnRecent.setVisibility(View.GONE);
		}

		if (pro_bean.getProfileType().equalsIgnoreCase("pers")) {

			
			if (pro_bean.getAdminlevel1().equalsIgnoreCase("")) {
				holder.address.setText(pro_bean.getAdminlevel2() + ","
						+ pro_bean.getCountry());
			} else if (pro_bean.getAdminlevel2().equalsIgnoreCase("")) {
				holder.address.setText(pro_bean.getAdminlevel1() + ","
						+ pro_bean.getCountry());
			} else if (pro_bean.getAdminlevel1().equalsIgnoreCase("")
					&& pro_bean.getAdminlevel2().equalsIgnoreCase("")) {
				holder.address.setText(pro_bean.getCountry());
			} else {

				holder.address.setText(pro_bean.getAdminlevel1()
						+ pro_bean.getAdminlevel2() + pro_bean.getCountry());
			}
		} else {
			
			holder.address.setText(pro_bean.getStreetAdress());
		}

		return vi;

	}

	private boolean isServiceRunning() { // checking if 
		// service is running
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (FavSyncService.class.getName().equals(
					service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}
