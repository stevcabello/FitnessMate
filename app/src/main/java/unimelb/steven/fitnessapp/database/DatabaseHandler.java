package unimelb.steven.fitnessapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import unimelb.steven.fitnessapp.models.Calories2;
import unimelb.steven.fitnessapp.models.Profile;
import unimelb.steven.fitnessapp.models.Sensors;

//import com.google.android.gms.maps.model.LatLng;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variable
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "FitnessAppDB";

	// Contacts table name
	private static final String TABLE_SENSING = "SensingData";

	private static final String TABLE_CALORIES = "CaloriesData";

	private static final String TABLE_SENSING2 = "SensingData2";

	private static final String TABLE_CALORIES2 = "CaloriesData2";

	private static final String TABLE_PROFILE = "Profile";


	private static final String KEY_HEIGHT = "height";
	private static final String KEY_WEIGHT = "weight";
	private static final String KEY_GENDER = "gender";


	private static final String KEY_ACTIVITY = "activity";
	private static final String KEY_DURATION = "duration";
	private static final String KEY_DISTANCE = "distance";
	private static final String KEY_SPEED = "speed";
	private static final String KEY_CALORIES = "calories";

	private static final String KEY_STEPS = "steps";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_SECONDS = "seconds";
	private static final String KEY_RATE = "rate";
	private static final String KEY_STATE = "state";
	private static final String KEY_TYPE_SENSOR = "sensor";
	private static final String KEY_PARAM1 = "param1";
    private static final String KEY_PARAM2 = "param2";
    private static final String KEY_PARAM3 = "param3";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
//    private static final String KEY_FINISHED = "finished";
//    private static final String KEY_RECORD = "record";


	private static final String KEY_AX = "ax";
	private static final String KEY_AY = "ay";
	private static final String KEY_AZ = "az";
	private static final String KEY_GX = "gx";
	private static final String KEY_GY = "gy";
	private static final String KEY_GZ = "gz";
	private static final String KEY_BP = "bp";
	private static final String KEY_TP = "tp";
	private static final String KEY_HM = "hm";

	//private static final String KEY_SC = "sc";


	//private static final String KEY_TIMESTAMP = "timestamp";

    private String TAG = DatabaseHandler.class.getSimpleName();

	

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		
//		Log.i(TAG, "onCreate");
//		String CREATE_SENSING_TABLE = "CREATE TABLE " + TABLE_SENSING + "("
//				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ KEY_DATE + "  TEXT,"
//				+ KEY_STATE + "  TEXT,"
//				+ KEY_TYPE_SENSOR + "  TEXT,"
//                + KEY_PARAM1 + "  TEXT,"
//                + KEY_PARAM2 + "  TEXT,"
//				+ KEY_PARAM3 + "  TEXT,"
//				+ KEY_LATITUDE + "  TEXT,"
//                + KEY_LONGITUDE + "  TEXT,"
//                + KEY_FINISHED + "  TEXT,"
//				+ KEY_RECORD + " TEXT" + ")";
//		db.execSQL(CREATE_SENSING_TABLE);


//		String CREATE_CALORIES_TABLE = "CREATE TABLE " + TABLE_CALORIES + "("
//				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
//				+ KEY_DATE + "  TEXT,"
//				+ KEY_ACTIVITY + "  TEXT,"
//				+ KEY_DISTANCE + "  TEXT,"
//				+ KEY_DURATION + "  TEXT,"
//				+ KEY_SPEED + "  TEXT,"
//				+ KEY_CALORIES + " TEXT" + ")";
//		db.execSQL(CREATE_CALORIES_TABLE);


		String CREATE_SENSING_TABLE2 = "CREATE TABLE " + TABLE_SENSING2 + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_DATE + "  TEXT,"
				+ KEY_SECONDS + "  TEXT,"
				+ KEY_RATE + "  TEXT,"
				+ KEY_STATE + "  TEXT,"
				+ KEY_AX + "  TEXT,"
				+ KEY_AY + "  TEXT,"
				+ KEY_AZ + "  TEXT,"
				+ KEY_GX + "  TEXT,"
				+ KEY_GY + "  TEXT,"
				+ KEY_GZ + "  TEXT,"
				+ KEY_BP + " TEXT" + ")";
		db.execSQL(CREATE_SENSING_TABLE2);


		String CREATE_CALORIES_TABLE2 = "CREATE TABLE " + TABLE_CALORIES2 + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ KEY_DATE + "  TEXT,"
				+ KEY_ACTIVITY + "  TEXT,"
				+ KEY_DISTANCE + "  TEXT,"
				+ KEY_STEPS + "  TEXT,"
				+ KEY_CALORIES + " TEXT" + ")";
		db.execSQL(CREATE_CALORIES_TABLE2);


		String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PROFILE + "("
				+ KEY_HEIGHT + "  TEXT,"
				+ KEY_WEIGHT + "  TEXT,"
				+ KEY_GENDER + " TEXT" + ")";
		db.execSQL(CREATE_PROFILE_TABLE);
		
	}


	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("dentro onUpgrade sqlite", "dentro de onupgrade");
		
		// Drop older table if existed
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATE_ENCUESTAS);
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSING);
		//db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALORIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSING2);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALORIES2);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	public void addProfileData(String height, String weight, String gender) {
		Log.i(TAG,"addProfileData");

		try{
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_HEIGHT, height);
			values.put(KEY_WEIGHT, weight);
			values.put(KEY_GENDER, gender);

			// Inserting Row
			db.insert(TABLE_PROFILE, null, values);
			//db.close(); // Closing database connection

		}catch (Exception e){
			e.printStackTrace();
		}


	}



	public ArrayList<Profile> getProfile() {

		SQLiteDatabase db = null;
		ArrayList<Profile> profile = null;

		Cursor cursor = null;
		try {
			db =this.getReadableDatabase();
			String query = "select * from Profile";
			cursor = db.rawQuery(query,null);
			if (cursor != null && cursor.moveToFirst()) {
				profile = new ArrayList<Profile>();
				do {
					String height = cursor.getString(cursor.getColumnIndex("height"));
					String weight = cursor.getString(cursor.getColumnIndex("weight"));
					String gender = cursor.getString(cursor.getColumnIndex("gender"));

					profile.add(new Profile(height,weight,gender));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
			profile = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
				//cursor = null;
			}
			db.close();
		}
		return profile;

	}


	public int updateProfile(String height, String weight, String gender) {

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_HEIGHT, height);
			values.put(KEY_WEIGHT, weight);
			values.put(KEY_GENDER, gender);

			int n = db.update(TABLE_PROFILE, values, null, null);

			db.close();
			// updating row
			return n;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}


//	// Adding new contact
//	public void addSensingData(String date, String state, String sensor, String param1, String param2,
//							   String param3, String latitude, String longitude, String finished) {
//
//        Log.i(TAG,"addSensingData");
//
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DATE, date);
//		values.put(KEY_STATE, state);
//		values.put(KEY_TYPE_SENSOR, sensor);
//		values.put(KEY_PARAM1, param1);
//        values.put(KEY_PARAM2, param2);
//        values.put(KEY_PARAM3, param3);
//		values.put(KEY_LATITUDE, latitude);
//		values.put(KEY_LONGITUDE, longitude);
//        values.put(KEY_FINISHED, finished);
//
//		// Inserting Row
//		db.insert(TABLE_SENSING, null, values);
//		//db.close(); // Closing database connection
//	}


	public void addSensingData2(String date, String seconds, String rate ,String state, String ax, String ay, String az, String gx,
							    String gy, String gz,String bp) {

		Log.i(TAG,"addSensingData2");

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_DATE, date);
			values.put(KEY_SECONDS, seconds);
			values.put(KEY_RATE, rate);
			values.put(KEY_STATE, state);
			values.put(KEY_AX, ax);
			values.put(KEY_AY, ay);
			values.put(KEY_AZ, az);
			values.put(KEY_GX, gx);
			values.put(KEY_GY, gy);
			values.put(KEY_GZ, gz);
			values.put(KEY_BP, bp);
//		values.put(KEY_SC, sc);
//		values.put(KEY_FINISHED, finished);

			// Inserting Row
			db.insert(TABLE_SENSING2, null, values);
			//db.close(); // Closing database connection
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public int updateActivity(String activity, String idIni, String idFin) {

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			//String newrecord = getLastRecord();

			ContentValues values = new ContentValues();
			values.put(KEY_STATE, activity);


			int n = db.update(TABLE_SENSING2, values, KEY_ID + " > ? and " + KEY_ID + " < ?",
					new String[] { idIni, idFin });


			db.close();
			// updating row
			return n;
		}catch (Exception e) {
			e.printStackTrace();

			return 0;
		}

	}


//	public void addCaloriesData(String date, String activity, String duration, String distance, String speed,
//							   String calories) {
//
//		Log.i(TAG,"addCaloriesData");
//
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(KEY_DATE, date);
//		values.put(KEY_ACTIVITY, activity);
//		values.put(KEY_DURATION, duration);
//		values.put(KEY_DISTANCE, distance);
//		values.put(KEY_SPEED, speed);
//		values.put(KEY_CALORIES, calories);
//
//		// Inserting Row
//		db.insert(TABLE_CALORIES, null, values);
//		//db.close(); // Closing database connection
//	}

	//5 new rows corresponding to each activity ... inserted per day
	public void addCaloriesData2(String date, String activity, String distance, String steps,
								String calories) {

		Log.i(TAG,"addCaloriesData2");

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_DATE, date);
			values.put(KEY_ACTIVITY, activity);
			values.put(KEY_DISTANCE, distance);
			values.put(KEY_STEPS, steps);
			values.put(KEY_CALORIES, calories);

			// Inserting Row
			db.insert(TABLE_CALORIES2, null, values);
			//db.close(); // Closing database connection

		}catch (Exception e) {
			e.printStackTrace();
		}

	}


	public ArrayList<Calories2> getCalories2(String strdate) {

		SQLiteDatabase db = null;

		ArrayList<Calories2> caloriesList = null;

		Cursor cursor = null;
		try {
			db= this.getReadableDatabase();
			String query = "select * from CaloriesData2 where date = '"+strdate+"'";
			Log.i("AAA",query);
			cursor = db.rawQuery(query,null);
			if (cursor != null && cursor.moveToFirst()) {
				caloriesList = new ArrayList<Calories2>();
				do {
					String activity = cursor.getString(cursor.getColumnIndex("activity"));
					String date = cursor.getString(cursor.getColumnIndex("date"));
					String distance = cursor.getString(cursor.getColumnIndex("distance"));
					String steps = cursor.getString(cursor.getColumnIndex("steps"));
					String calories = cursor.getString(cursor.getColumnIndex("calories"));

					Log.i(TAG, activity);
					Log.i(TAG, date);

					caloriesList.add(new Calories2(activity,date,calories,distance,steps));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
			caloriesList = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
				//cursor = null;
			}
			db.close();
		}
		return caloriesList;

	}


	// Deleting single contact
	public void deleteCalories(String date) {

		try {
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_CALORIES2, KEY_DATE + " = ?",
					new String[] { date });

			db.delete(TABLE_SENSING2, KEY_DATE + " like ?",
					new String[] { date+"%" });

			db.close();
		}catch (Exception e) {
			e.printStackTrace();
		}

	}



	public ArrayList<String> getCalories2Titles() {

		SQLiteDatabase db = null;

		ArrayList<String> caloriesListTitle = null;

		Cursor cursor = null;
		try {
			db= this.getReadableDatabase();
			String query = "select distinct date from CaloriesData2";
			cursor = db.rawQuery(query,null);
			if (cursor != null && cursor.moveToFirst()) {
				caloriesListTitle = new ArrayList<String>();
				do {
					String date = cursor.getString(cursor.getColumnIndex("date"));

					caloriesListTitle.add(date);

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
			caloriesListTitle = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
				//cursor = null;
			}
			db.close();
		}
		return caloriesListTitle;

	}



	public ArrayList<Calories2> getCaloriesConsumed(String strdate, String stractivity) {

		SQLiteDatabase db = null;

		ArrayList<Calories2> caloriesList = null;

		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			String query = "select * from CaloriesData2 where date = '"+strdate+"' and activity = '"+stractivity+"'";
			Log.i("ARService",query);
			cursor = db.rawQuery(query,null);
			if (cursor != null && cursor.moveToFirst()) {
				caloriesList = new ArrayList<Calories2>();
				do {
					String activity = cursor.getString(cursor.getColumnIndex("activity"));
					String date = cursor.getString(cursor.getColumnIndex("date"));
					String distance = cursor.getString(cursor.getColumnIndex("distance"));
					String steps = cursor.getString(cursor.getColumnIndex("steps"));
					String calories = cursor.getString(cursor.getColumnIndex("calories"));

					caloriesList.add(new Calories2(activity,date,calories,distance,steps));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("ARService",e.getMessage());
			caloriesList = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
				//cursor = null;
			}
			db.close();
		}
		return caloriesList;
	}


	public int updateCalories2(String activity, String date, String calories, String steps, String distance) {

		try {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(KEY_CALORIES, calories);
			values.put(KEY_STEPS, steps);
			values.put(KEY_DISTANCE, distance);


			int n = db.update(TABLE_CALORIES2, values, KEY_DATE + " = ? and " + KEY_ACTIVITY + " = ?",
					new String[] { date, activity });


			db.close();
			// updating row
			return n;
		}catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

//	public ArrayList<Calories> getCalories() {
//
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		ArrayList<Calories> caloriesList = null;
//
//		Cursor cursor = null;
//		try {
//			String query = "select * from CaloriesData";
//			cursor = db.rawQuery(query,null);
//			if (cursor != null && cursor.moveToFirst()) {
//				caloriesList = new ArrayList<Calories>();
//				do {
//					String activity = cursor.getString(cursor.getColumnIndex("activity"));
//					String date = cursor.getString(cursor.getColumnIndex("date"));
//					String duration = cursor.getString(cursor.getColumnIndex("duration"));
//					String distance = cursor.getString(cursor.getColumnIndex("distance"));
//					String speed = cursor.getString(cursor.getColumnIndex("speed"));
//					String calories = cursor.getString(cursor.getColumnIndex("calories"));
//
//					Log.i(TAG, activity);
//					Log.i(TAG, date);
//
//					caloriesList.add(new Calories(activity,date,calories,duration,distance,speed));
//
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			caloriesList = null;
//		} finally {
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//				cursor.close();
//				//cursor = null;
//			}
//			db.close();
//		}
//		return caloriesList;
//
//	}



//	public ArrayList<LatLng> getCoordinates() {
//
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		ArrayList<LatLng> namesList = null;
//
//		Cursor cursor = null;
//		try {
//			String query = "select latitude,longitude from SensingData where " + KEY_FINISHED + " = 0 and "
//					+ KEY_LATITUDE + " notnull and " + KEY_LONGITUDE + " notnull";
//			cursor = db.rawQuery(query,null);
//			if (cursor != null && cursor.moveToFirst()) {
//				namesList = new ArrayList<LatLng>();
//				do {
//                    String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
//                    String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
//
//                    Log.i(TAG,latitude);
//                    Log.i(TAG,longitude);
//
//                    namesList.add(new LatLng(Double.parseDouble(latitude),
//                            Double.parseDouble(longitude)));
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			namesList = null;
//		} finally {
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//				cursor.close();
//				//cursor = null;
//			}
//			db.close();
//		}
//		return namesList;
//
//	}


//    public int updateOnStop() {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        String newrecord = getLastRecord();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_FINISHED, "1");
//        values.put(KEY_RECORD, newrecord);
//
//
//
//        int n = db.update(TABLE_SENSING2, values, KEY_FINISHED + " = ?",
//                new String[] { "0" });
//
//        //Delete any non-updated change
//        db.delete(TABLE_SENSING2, KEY_FINISHED + " = ?",
//                new String[] { "0" });
//
//
//        db.close();
//        // updating row
//        return n;
//    }


//    public String getLastRecord() {
//        String countQuery = "SELECT  * FROM " + TABLE_SENSING2 + " WHERE " + KEY_FINISHED + " = 1";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.moveToLast(); //take the last record where finished equals 1
//
//        int n = cursor.getCount();
//
//        if (n < 1) {
//            cursor.close();
//           // db.close();
//            return "0";
//        } else {
//            String lastrecord = cursor.getString(cursor.getColumnIndex("record"));
//            Log.i(TAG,lastrecord);
//            cursor.close();
//           // db.close();
//            int newrecord = Integer.parseInt(lastrecord) + 1;
//            Log.i(TAG,String.valueOf(newrecord));
//            return String.valueOf(newrecord);
//        }
//
//    }

	public void deleteNullRows() {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
//		db.delete(TABLE_SENSING2, KEY_FINISHED + " = ?",
//				new String[]{"0"});

			db.delete(TABLE_SENSING2, KEY_STATE + " is ? or " + KEY_STATE + " = ?",
					new String[]{null,"Others"});

			db.close();
		}catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void deleteOthersRows(String fromdate, String todate) {
		try{
			SQLiteDatabase db = this.getWritableDatabase();

			db.delete(TABLE_SENSING2, KEY_DATE + " > ? and " + KEY_STATE + " < ?",
					new String[]{fromdate,todate});

			db.close();
		}catch (Exception e) {
			e.printStackTrace();
		}

	}


//	public ArrayList<Accelerometer> getSensingData(String lastdate){
//
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		ArrayList<Accelerometer> data = null;
//
//		Cursor cursor = null;
//		try {
//			String query = "select * from SensingData where date > '" + lastdate +"'";
//			cursor = db.rawQuery(query,null);
//			if (cursor != null && cursor.moveToFirst()) {
//				data = new ArrayList<Accelerometer>();
//				do {
//					String x_axis = cursor.getString(cursor.getColumnIndex("param1"));
//					String y_axis = cursor.getString(cursor.getColumnIndex("param2"));
//					String z_axis = cursor.getString(cursor.getColumnIndex("param3"));
//
//
//					data.add(new Accelerometer(x_axis,y_axis,z_axis));
//
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			//e.printStackTrace();
//			Log.i("MainActivity", e.getMessage());
//			data = null;
//		} finally {
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//				cursor.close();
//				//cursor = null;
//			}
//			db.close();
//		}
//		return data;
//	}

	public ArrayList<Sensors> getSensingData2(String fromdate, String todate ){

		//SQLiteDatabase db = this.getReadableDatabase();
		SQLiteDatabase db = null;
		String query;

		ArrayList<Sensors> data = null;

		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			query = "select * from SensingData2 where date > '" + fromdate +"' and date < '" + todate +"'";

			cursor = db.rawQuery(query,null);
			if (cursor != null && cursor.moveToFirst()) {
				data = new ArrayList<Sensors>();
				do {
					String id = cursor.getString(cursor.getColumnIndex("id"));
					String ax = cursor.getString(cursor.getColumnIndex("ax"));
					String ay = cursor.getString(cursor.getColumnIndex("ay"));
					String az = cursor.getString(cursor.getColumnIndex("az"));
					String gx = cursor.getString(cursor.getColumnIndex("gx"));
					String gy = cursor.getString(cursor.getColumnIndex("gy"));
					String gz = cursor.getString(cursor.getColumnIndex("gz"));
					String bp = cursor.getString(cursor.getColumnIndex("bp"));
//					String tp = cursor.getString(cursor.getColumnIndex("tp"));
//					String hm = cursor.getString(cursor.getColumnIndex("hm"));
					String seconds = cursor.getString(cursor.getColumnIndex("seconds"));
					String activity = cursor.getString(cursor.getColumnIndex("state"));


					data.add(new Sensors(id,Double.parseDouble(ax),Double.parseDouble(ay),Double.parseDouble(az),Double.parseDouble(gx),Double.parseDouble(gy),Double.parseDouble(gz),Double.parseDouble(bp),Double.parseDouble(seconds), activity));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Log.i("MainActivity", e.getMessage());
			data = null;
		} finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.deactivate();
				cursor.close();
				//cursor = null;
			}
			db.close();
		}
		return data;
	}




//	// Getting single contact
//	public int getNumeroNotificacionesxEncuesta(int id) {
//		SQLiteDatabase db = this.getReadableDatabase();
//
//		Cursor cursor = db.query(TABLE_SENSING, new String[] { KEY_ID,
//						KEY_STATE}, KEY_ID + "=?",
//				new String[] { String.valueOf(id) }, null, null, null, null);
//		if (cursor != null)
//			cursor.moveToFirst();
//
//		int nvez = Integer.parseInt(cursor.getString(1));
//		Log.i("numero notificaciones", cursor.getString(1));
//		cursor.close();
//		//db.close();
//		return nvez;
//	}
//
//	// Updating single contact
//		public int updateNotificacionesEncuesta(int Encuesta, int Nvez) {
//			SQLiteDatabase db = this.getWritableDatabase();
//
//			ContentValues values = new ContentValues();
//			values.put(KEY_STATE, String.valueOf(Nvez));
//
//			int n = db.update(TABLE_SENSING, values, KEY_ID + " = ?",
//					new String[] { String.valueOf(Encuesta) });
//
//			db.close();
//			// updating row
//			return n;
//		}
//
//
//        // Getting contacts Count
//		public int getEncuestaCount(int Encuesta) {
//			String countQuery = "SELECT  * FROM " + TABLE_SENSING + " WHERE " + KEY_ID + " = " + String.valueOf(Encuesta);
//			SQLiteDatabase db = this.getReadableDatabase();
//			Cursor cursor = db.rawQuery(countQuery, null);
//			//cursor.close();
//            int n = cursor.getCount();
//            cursor.close();
//            db.close();
//			// return count
//			return n;
//
//		}
//
//
//		// Deleting single contact
//	public void deleteContact(int Encuesta) {
//			SQLiteDatabase db = this.getWritableDatabase();
//			db.delete(TABLE_SENSING, KEY_ID + " = ?",
//					new String[] { String.valueOf(Encuesta) });
//			db.close();
//		}


}

