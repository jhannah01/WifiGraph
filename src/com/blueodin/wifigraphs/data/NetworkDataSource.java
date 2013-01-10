package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.util.Log;

public class NetworkDataSource {
	  private SQLiteDatabase database;
	  private DBHelper dbHelper;
	  
	  private String[] allColumns = { 
		  DBHelper.COLUMN_ID, 
		  DBHelper.COLUMN_BSSID, 
		  DBHelper.COLUMN_SSID, 
		  DBHelper.COLUMN_LEVEL, 
		  DBHelper.COLUMN_FREQUENCY, 
		  DBHelper.COLUMN_CAPABILITIES, 
		  DBHelper.COLUMN_TIMESTAMP
	  };
	  
	  private final static String TAG = "NetworkDataSource";
	  
	  public NetworkDataSource(Context context) {
		  dbHelper = new DBHelper(context);
	  }

	  public void open() throws SQLException {
		  database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
		  dbHelper.close();
	  }
	  
	  public NetworkScanRecord createRecord(ScanResult result) {
		  return createRecord(result.BSSID, result.SSID, result.level, result.frequency, result.capabilities, System.currentTimeMillis()); 
	  }
	  
	  public NetworkScanRecord createRecord(String bssid, String ssid, int level, int freqency, String capabilities, long timestamp) {
	      ContentValues values = new ContentValues();
	      values.put(DBHelper.COLUMN_BSSID, bssid);
	      values.put(DBHelper.COLUMN_SSID, ssid);
	      values.put(DBHelper.COLUMN_LEVEL, level);
	      values.put(DBHelper.COLUMN_FREQUENCY, freqency);
	      values.put(DBHelper.COLUMN_CAPABILITIES, capabilities);
	      values.put(DBHelper.COLUMN_TIMESTAMP, timestamp);
	    
	      long insertId = database.insert(DBHelper.TABLE_READINGS, null, values);
	    
	      Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
	      cursor.moveToFirst();
	      NetworkScanRecord newRecord = cursorToRecord(cursor);
	      cursor.close();
	      
	      return newRecord;
	  }

	  public void deleteRecord(NetworkScanRecord record) {
	      long id = record.getId();
	      Log.i(TAG, "Record deleted with id: " + id);
	      database.delete(DBHelper.TABLE_READINGS, DBHelper.COLUMN_ID + " = " + id, null);
	  }
	  
	  public List<NetworkScanRecord> getAllRecords() {
	      List<NetworkScanRecord> records = new ArrayList<NetworkScanRecord>();

  	      Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns, null, null, null, null, null);

	      cursor.moveToFirst();
	      
	      while (!cursor.isAfterLast()) {
	    	  NetworkScanRecord record = cursorToRecord(cursor);
	      
	    	  records.add(record);
	    	  
	    	  cursor.moveToNext();
	      }
	    
	      cursor.close();
	    
	      return records;
	  }

	  private NetworkScanRecord cursorToRecord(Cursor cursor) {
		  return (new NetworkScanRecord(
				cursor.getInt(0),	 // id
				cursor.getString(1), // bssid
				cursor.getString(2), // ssid
		    	cursor.getInt(3),    // level
		    	cursor.getInt(4),    // freqency
	    		cursor.getString(5), // capabilities
	    		cursor.getLong(6))); // timestamp
	  }
}