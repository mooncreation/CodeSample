package com.mapapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	private Context context = null;
	private DatabaseHelper DBHelper;
	public SQLiteDatabase db;
	private static final String DATABASE_NAME = "MapId";
	private static final int DATABASE_VERSION = 1;
	private static final String CONTACTTABLE = "ContactTable";
	private static final String SOCIALLINKS = "socialLinkTable";
	private static final String COUNTRYTABLE = "CountryTable";
	private static final String TERMSCONDITION = "termscondition";
	private static final String HELPNOTES = "helpnotes";
	private static final String TRAVELLIST = "travelListTable";
	

	private static final String FOLLOWMETABLE = "FollowMeTable";
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String URL = "url";

	private static final String PROFILEID = "profileId";
	private static final String FAVTABLE = "favTable";
	private static final String MAPID = "mapId";

	private static final String RECENTTABLE = "recentTable";

	private static final String CreateTableContact = "create table "
			+ CONTACTTABLE + "(key text ,value text);";
	private static final String CreateTableFollowMe = "create table "
			+ FOLLOWMETABLE + "(profileId text ,value text);";

	private static final String CreateSocialLinks = "create table "
			+ SOCIALLINKS
			+ "( id INTEGER PRIMARY KEY AUTOINCREMENT,key text ,value text,url text);";

	private static final String CreateCountry = "create table " + COUNTRYTABLE
			+ "( id INTEGER PRIMARY KEY AUTOINCREMENT,key text ,value text);";

	private static final String CreateTravel = "create table "
			+ TRAVELLIST
			+ "( id INTEGER PRIMARY KEY AUTOINCREMENT ,key text ,value text);";

	private static final String CreateTermsCondition = "create table "
			+ TERMSCONDITION + "(key text ,value text);";

	private static final String CreateHelptables = "create table " + HELPNOTES
			+ "(key text ,value text);";

	private static final String CreateFavTable = "create table " + FAVTABLE
			+ "(profileId text , mapId text , flag text);";

	private static final String CreateRecentTable = "create table "
			+ RECENTTABLE + "(profileId text , mapId text , counter text);";

	public DBAdapter(Context con) {
		this.context = con;
		DBHelper = new DatabaseHelper(context);
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			// TODO Auto-generated constructor stub
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(CreateTableContact);
				db.execSQL(CreateSocialLinks);
				db.execSQL(CreateTravel);
				db.execSQL(CreateCountry);
				db.execSQL(CreateTableFollowMe);
				db.execSQL(CreateFavTable);
				db.execSQL(CreateRecentTable);
				db.execSQL(CreateTermsCondition);
				db.execSQL(CreateHelptables);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			
			

		}
	}

	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public void insertVauesInContacts(String Key, String Value) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + CONTACTTABLE
				+ " where " + KEY + " = " + "'" + Key + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(KEY, Key);
			init.put(VALUE, Value);
			db.insert(CONTACTTABLE, null, init);
		}
		// Toast.makeText(Context.this,"Data Inserted Successfuly",3000).show();
	}

	public void insertValuesInFollowMe(String profileId, String Value) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + FOLLOWMETABLE
				+ " where profileId = " + "'" + profileId + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(PROFILEID, profileId);
			init.put(VALUE, Value);
			db.insert(FOLLOWMETABLE, null, init);
		} else {
			ContentValues init = new ContentValues();
			init.put(VALUE, Value);
			db.update(FOLLOWMETABLE, init, PROFILEID + "='" + profileId + "'",
					null);
		}
		// Toast.makeText(Context.this,"Data Inserted Successfuly",3000).show();
	}

	public Cursor getAllFollowMeIds() {
		String status = "follow";
		String selection = VALUE + "='" + status + "'";
		Cursor c = db.query(FOLLOWMETABLE, new String[] { PROFILEID },
				selection, null, null, null, null);
		Log.e("TAG",
				"inside..."
						+ db.query(FOLLOWMETABLE, new String[] { PROFILEID },
								selection, null, null, null, null));

		// if (c != null) {
		// return db.query(FOLLOWMETABLE, new String[] { PROFILEID },
		// selection, null, null, null, null);
		// } else
		return c;
	}

	public void insertVauesInSocial(String Key, String Value, String url) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + SOCIALLINKS
				+ " where " + KEY + " = " + "'" + Key + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(KEY, Key);
			init.put(VALUE, Value);
			init.put(URL, url);
			db.insert(SOCIALLINKS, null, init);
		}
		// Toast.makeText(Context.this,"Data Inserted Successfuly",3000).show();
	}

	public void insertVauesInTravelList(String Key, String Value) {

		Cursor result = db.rawQuery("SELECT Count(*) FROM " + TRAVELLIST
				+ " where " + KEY + " = " + "'" + Key + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(KEY, Key);
			init.put(VALUE, Value);
            db.insert(TRAVELLIST, null, init);
		}

	}

	public void insertVauesInTermCondition(String Key, String Value) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + TERMSCONDITION
				+ " where " + KEY + " = " + "'" + Key + "'", null);
		result.moveToFirst();
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(KEY, Key);
			init.put(VALUE, Value);
			db.insert(TERMSCONDITION, null, init);
		}
		// Toast.makeText(context,"Data Inserted Successfuly",3000).show();
	}

	public void insertHelpnotes(String Key, String Value) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + HELPNOTES
				+ " where " + KEY + " = " + "'" + Key + "'", null);
		result.moveToFirst();
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(KEY, Key);
			init.put(VALUE, Value);
			db.insert(HELPNOTES, null, init);
			Log.e("help notes", "help");
		}
		// Toast.makeText(context,"Data Inserted Successfuly",3000).show();
	}

	public void insertVauesInCountry(String Key, String Value) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + COUNTRYTABLE
				+ " where " + KEY + " = " + "'" + Key + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {

			ContentValues init = new ContentValues();
			init.put(KEY, Key);
			init.put(VALUE, Value);
			db.insert(COUNTRYTABLE, null, init);
		}
		// Toast.makeText(Context.this,"Data Inserted Successfuly",3000).show();
	}

	public Cursor getAllCountry() {

		Log.d("db", "" + db.rawQuery("Select * from " + COUNTRYTABLE, null));
		// return db.query(COUNTRYTABLE, new String[] { KEY, VALUE }, null,
		// null,
		// null, null, null);
		return db.rawQuery("Select * from " + COUNTRYTABLE, null);
	}

	public String SelectedCountry(String value) {

		String query = "SELECT * FROM " + COUNTRYTABLE + " where value like"
				+ "'" + value + "'";
		Log.e("TAG", query);
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				String result = c.getString(c.getColumnIndex("id"));
				return result;
			}
		}

		return "0";

	}
	public String SelectedTravel(String value) {

		String query = "SELECT * FROM " + TRAVELLIST + " where value like"
				+ "'" + value + "'";
		Log.e("TAG", query);
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				String result = c.getString(c.getColumnIndex("id"));
				return result;
			}
		}

		return "0";

	}
	
	public Cursor getAllSocialLinks() {
		Log.d("db", "" + db.rawQuery("Select * from " + SOCIALLINKS, null));
		// return db.query(COUNTRYTABLE, new String[] { KEY, VALUE }, null,
		// null,
		// null, null, null);
		return db.rawQuery("Select * from " + SOCIALLINKS, null);
	}
	
	public Cursor getAlltravelList() {
		Log.d("db", "" + db.rawQuery("Select * from " + TRAVELLIST, null));
		// return db.query(COUNTRYTABLE, new String[] { KEY, VALUE }, null,
		// null,
		// null, null, null);
		return db.rawQuery("Select * from " + TRAVELLIST, null);
	}

	
	

	public Cursor getAllTermscondition() {
		Log.d("db", "" + db.rawQuery("Select * from " + TERMSCONDITION, null));
		// return db.query(COUNTRYTABLE, new String[] { KEY, VALUE }, null,
		// null,
		// null, null, null);
		return db.rawQuery("Select * from " + TERMSCONDITION, null);
	}

	public Cursor getAllContacts() {
		Log.d("db", "" + db.rawQuery("Select * from " + CONTACTTABLE, null));
		// return db.query(COUNTRYTABLE, new String[] { KEY, VALUE }, null,
		// null,
		// null, null, null);
		return db.rawQuery("Select * from " + CONTACTTABLE, null);
	}

	public String getContactKeyFromValue(String value) {
		String query = "SELECT * FROM ContactTable where value= " + "'" + value
				+ "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(0);
			return result;
		}

		return "";
	}

	public String getTermsconditionvalue(String keyterms) {
		String query = "SELECT * FROM " + TERMSCONDITION + " where key= " + "'"
				+ keyterms + "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("value"));
			return result;
		}

		return "";
	}

	public String getHelpnotesvalue(String keyterms) {
		String query = "SELECT * FROM " + HELPNOTES + " where key= " + "'"
				+ keyterms + "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("value"));
			return result;
		}

		return "";
	}

	public String getCountryCodeFromValue(String value) {
		String query = "SELECT * FROM " + COUNTRYTABLE + " where value= " + "'"
				+ value + "'";
		Log.e("Query", query);
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("key"));
			return result;
		}

		return "";
	}
	
	public String getTravelKeyFromValue(String value) {
		String query = "SELECT * FROM " + TRAVELLIST.toString();

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("key"));
			return result;
		}

		return "";
	}

	public String getLinkKeyFromValue(String value) {
		String query = "SELECT * FROM " + SOCIALLINKS + " where value= " + "'"
				+ value + "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("key"));
			return result;
		}

		return "";
	}

	public String getValueFromKeyContacts(String kString) {
		String query = "SELECT * FROM " + CONTACTTABLE + " where key= " + "'"
				+ kString + "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(1);
			return result;
		}

		return "";
	}
	
	
	public String getValueFromKeytravel(String k2String) {
		String query = "SELECT * FROM " + TRAVELLIST + " where value= " + "'"
				+ k2String + "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(1);
			return result;
		}

		return "";
	}
	
	public String getValueFromKeySocial(String k1String) {
		String query = "SELECT * FROM " + SOCIALLINKS + " where key= " + "'"
				+ k1String + "'";

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(1);
			return result;
		}

		return "";
	}
	

	public String getlink_Urlvalue(int idget) {
		String query = "SELECT * FROM " + SOCIALLINKS + " where rowid= " + "'"
				+ idget + "'";

		Log.e("idget", "" + idget);

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("url"));
			Log.e("result link value", "" + result);
			return result;

		}

		return "";
	}

	public String getlink_type(int idget) {
		String query = "SELECT * FROM " + SOCIALLINKS + " where rowid= " + "'"
				+ idget + "'";

		Log.e("idget", "" + idget);

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("key"));
			Log.e("result link value", "" + result);
			return result;

		}

		return "";
	}
	
	public String gettravel_type(int idget) {
		String query = "SELECT * FROM " + TRAVELLIST + " where rowid= " + "'"
				+ idget + "'";

		Log.e("idget", "" + idget);

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("value"));
			Log.e("result link value", "" + result);
			return result;

		}

		return "";
	}

	public String getlink_Value(int idget) {
		String query = "SELECT * FROM " + SOCIALLINKS + " where rowid= " + "'"
				+ idget + "'";

		Log.e("idget", "" + idget);

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("value"));
			Log.e("result link value", "" + result);
			return result;

		}

		return "";
	}
	public String gettravel_Value(int idget) {
		String query = "SELECT * FROM " + TRAVELLIST + " where rowid= " + "'"
				+ idget + "'";

		Log.e("idget", "" + idget);

		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("value"));
			Log.e("result travel value", "" + result);
			return result;

		}

		return "";
	}

	public String getCountryFromkey(String keyvalue) {
		String query = "SELECT * FROM " + COUNTRYTABLE + " where key= " + "'"
				+ keyvalue + "'";
		Log.e("Query", query);
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			c.moveToFirst();
			String result = c.getString(c.getColumnIndex("value"));
			return result;
		}

		return "";
	}

	public void insertValuesInFavTable(String profileId, String mapId,
			String Value) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + FAVTABLE
				+ " where " + PROFILEID + " = " + "'" + profileId + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(PROFILEID, profileId);
			init.put("flag", Value);
			init.put(MAPID, mapId);
			db.insert(FAVTABLE, null, init);
		} else {
			ContentValues init = new ContentValues();
			init.put("flag", Value);
			init.put(MAPID, mapId);
			db.update(FAVTABLE, init, PROFILEID + "=" + "'" + profileId + "'",
					null);
		}
		// Toast.makeText(Context.this,"Data Inserted Successfuly",3000).show();
	}

	public void insertValuesInRecentTable(String profileId, String mapId) {
		Cursor result = db.rawQuery("SELECT Count(*) FROM " + RECENTTABLE
				+ " where " + PROFILEID + " = " + "'" + profileId + "'", null);
		result.moveToFirst();
		Log.d("CURSOR", "cursor..." + result.getString(0));
		if (Integer.parseInt(result.getString(0)) < 1) {
			ContentValues init = new ContentValues();
			init.put(PROFILEID, profileId);
			init.put("counter", "1");
			init.put(MAPID, mapId);
			db.insert(RECENTTABLE, null, init);
		} else {
			Cursor c = db.rawQuery("SELECT counter FROM " + RECENTTABLE
					+ " where " + PROFILEID + " = " + "'" + profileId + "'",
					null);
			if (c != null && c.getCount() > 0) {
				{
					c.moveToFirst();
					String count = c.getString(c.getColumnIndex("counter"));
					int counter = Integer.parseInt(count) + 1;
					ContentValues init = new ContentValues();
					init.put("counter", String.valueOf(counter));
					init.put(MAPID, mapId);
					db.update(RECENTTABLE, init, PROFILEID + "=" + "'"
							+ profileId + "'", null);
				}

			}

		}
		// Toast.makeText(Context.this,"Data Inserted Successfuly",3000).show();
	}

	public String getFavDependingOnId(String profileId) {
		String query = "SELECT * FROM " + FAVTABLE + " where profileId= " + "'"
				+ profileId + "'";
		Log.e("TAG", "" + db.rawQuery(query, null));
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				String result = c.getString(c.getColumnIndex("flag"));
				return result;
			} else {
				return "";
			}
		}

		return "";
	}

	public String getRecentDependingOnId(String profileId) {
		String query = "SELECT * FROM " + RECENTTABLE + " where profileId= "
				+ "'" + profileId + "'";
		Log.e("TAG", query);
		Cursor c = db.rawQuery(query, null);
		if (c != null) {
			if (c.getCount() > 0) {
				c.moveToFirst();
				String result = c.getString(c.getColumnIndex("counter"));
				return result;
			}
		}

		return "0";
	}

	public Cursor getAllFavData() {
		return db.rawQuery("Select * from " + FAVTABLE + " where flag = " + "'"
				+ "true" + "'", null);
	}

	public Cursor getAllUnFavData() {
		return db.rawQuery("Select * from " + FAVTABLE + " where flag = " + "'"
				+ "false" + "'", null);
	}

}
