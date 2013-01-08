package com.blueodin.wifigraphs.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
  public static final String TABLE_READINGS = "readings";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_TIME = "time";
  public static final String COLUMN_VALUE = "value";
  public static final String COLUMN_SSID = "ssid";
  public static final String COLUMN_BSSID = "bssid";

  private static final String DATABASE_NAME = "wifireadings.db";
  private static final int DATABASE_VERSION = 1;

  private static final String TAG = "DBHelper";
  
  private static final String DATABASE_CREATE = "CREATE TABLE "
      + TABLE_READINGS + "(" 
      + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
      + COLUMN_TIME + " TIMESTAMP NOT NULL, "
      + COLUMN_VALUE + " INTEGER, "
      + COLUMN_SSID + " TEXT NOT NULL, "
      + COLUMN_BSSID + " TEXT UNIQUE NOT NULL);";

  public DBHelper(Context context) {
	  super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
	  database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);
    onCreate(db);
  }
}