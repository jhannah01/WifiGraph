package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RecordsDataSource {
	  private SQLiteDatabase database;
	  private DBHelper dbHelper;
	  private String[] allColumns = { DBHelper.COLUMN_ID, DBHelper.COLUMN_TIME, DBHelper.COLUMN_VALUE, DBHelper.COLUMN_SSID, DBHelper.COLUMN_BSSID };
	  private final static String TAG = "RecordsDataSource";
	  
	  public RecordsDataSource(Context context) {
	    dbHelper = new DBHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public SignalRecord createRecord(long time, int value, String ssid, String bssid) {
	    ContentValues values = new ContentValues();
	    values.put(DBHelper.COLUMN_TIME, time);
	    values.put(DBHelper.COLUMN_VALUE, value);
	    values.put(DBHelper.COLUMN_SSID, ssid);
	    values.put(DBHelper.COLUMN_BSSID, bssid);
	    
	    long insertId = database.insert(DBHelper.TABLE_READINGS, null, values);
	    
	    Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
	    cursor.moveToFirst();
	    SignalRecord newComment = cursorToRecord(cursor);
	    cursor.close();
	    return newComment;
	  }

	  public void deleteComment(SignalRecord record) {
	    long id = record.getId();
	    Log.i(TAG, "SignalRecord deleted with id: " + id);
	    database.delete(DBHelper.TABLE_READINGS, DBHelper.COLUMN_ID + " = " + id, null);
	  }

	  public List<SignalRecord> getAllRecords() {
	    List<SignalRecord> records = new ArrayList<SignalRecord>();

	    Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      SignalRecord record = cursorToRecord(cursor);
	      records.add(record);
	      cursor.moveToNext();
	    }
	    
	    cursor.close();
	    return records;
	  }

	  private SignalRecord cursorToRecord(Cursor cursor) {
	    return (new SignalRecord(cursor.getInt(0), cursor.getLong(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4)));
	  }
}