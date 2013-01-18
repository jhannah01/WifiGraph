package com.blueodin.wifigraphs.db;

import android.database.sqlite.SQLiteDatabase;

public class RecordsDataSource {
	private SQLiteDatabase mDatabase;
	private RecordReaderDbHelper mDbHelper;
	private static final String[] mAllColumns = {
		RecordReaderContract.RecordEntry.COLUMN_NAME_BSSID,
		RecordReaderContract.RecordEntry.COLUMN_NAME_SSID,
		RecordReaderContract.RecordEntry.COLUMN_NAME_LEVEL,
		RecordReaderContract.RecordEntry.COLUMN_NAME_FREQUENCY,
		RecordReaderContract.RecordEntry.COLUMN_NAME_CAPABILITIES,
		RecordReaderContract.RecordEntry.COLUMN_NAME_TIMESTAMP'
	};
}

/*public class RecordsDataSource {
	private SQLiteDatabase database;
	private DBHelper dbHelper;

	private String[] allColumns = { DBHelper.COLUMN_ID, DBHelper.COLUMN_BSSID,
			DBHelper.COLUMN_SSID, DBHelper.COLUMN_LEVEL,
			DBHelper.COLUMN_FREQUENCY, DBHelper.COLUMN_CAPABILITIES,
			DBHelper.COLUMN_TIMESTAMP };

	private final static String TAG = "RecordsDataSource";

	public RecordsDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}

	public boolean isOpen() {
		return this.database.isOpen();
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public WifiNetworkEntry createRecord(ScanResult result) {
		return createRecord(result.BSSID, result.SSID, result.level,
				result.frequency, result.capabilities,
				System.currentTimeMillis());
	}

	public WifiNetworkEntry createRecord(WifiNetworkEntry result) {
		return createRecord(result.getBSSID(), result.getSSID(),
				result.getLevel(), result.getFrequency(),
				result.getCapabilities(), result.getTimestamp());
	}

	public WifiNetworkEntry createRecord(String bssid, String ssid, int level,
			int freqency, String capabilities, long timestamp) {
		ContentValues values = new ContentValues();
		values.put(DBHelper.COLUMN_BSSID, bssid);
		values.put(DBHelper.COLUMN_SSID, ssid);
		values.put(DBHelper.COLUMN_LEVEL, level);
		values.put(DBHelper.COLUMN_FREQUENCY, freqency);
		values.put(DBHelper.COLUMN_CAPABILITIES, capabilities);
		values.put(DBHelper.COLUMN_TIMESTAMP, timestamp);

		long insertId = database.insert(DBHelper.TABLE_READINGS, null, values);

		Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns,
				DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		WifiNetworkEntry newRecord = cursorToRecord(cursor);
		cursor.close();
		return newRecord;
	}

	public void deleteComment(WifiNetworkEntry record) {
		long id = record.getId();
		Log.i(TAG, "SignalRecord deleted with id: " + id);
		database.delete(DBHelper.TABLE_READINGS, DBHelper.COLUMN_ID + " = "
				+ id, null);
	}

	public List<WifiNetworkEntry> getAllRecords() {
		List<WifiNetworkEntry> records = new ArrayList<WifiNetworkEntry>();

		Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			WifiNetworkEntry record = cursorToRecord(cursor);
			records.add(record);
			cursor.moveToNext();
		}

		cursor.close();
		return records;
	}

	public List<WifiNetworkEntry> getRecordsFor(WifiNetworkEntry result) {
		return this.getRecordsFor(result.getBSSID());
	}

	public List<WifiNetworkEntry> getRecordsFor(String bssid) {
		Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns, 
				DBHelper.COLUMN_BSSID + " = ?", new String[] {bssid},
				null, null, null, null);
		List<WifiNetworkEntry> entries = WifiNetworkEntry.getListFromCursor(cursor);
		cursor.close();
		return entries;
	}
	
	public WifiNetworkEntry getLastRecordFor(String bssid) {
		Cursor cursor = database.query(DBHelper.TABLE_READINGS, allColumns, 
				DBHelper.COLUMN_BSSID + " = ?", new String[] {bssid}, 
				null, null, DBHelper.COLUMN_TIMESTAMP, "1"); 
		
		if((cursor == null) || (cursor.getCount() < 1))
			return null;
		
		cursor.moveToFirst();
		WifiNetworkEntry entry = new WifiNetworkEntry(cursor);
		cursor.close();
		
		return entry;
	}
	
	public List<String> getNetworkBSSIDs() {
		List<String> names = new ArrayList<String>();

		Cursor cursor = database.query(true, DBHelper.TABLE_READINGS, new String[] { DBHelper.COLUMN_BSSID }, 
				null, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			names.add(cursor.getString(0));
			cursor.moveToNext();
		}

		cursor.close();
		
		return names;
	}

	private WifiNetworkEntry cursorToRecord(Cursor cursor) {
		return (new WifiNetworkEntry(cursor));
	}

	public int getRecordsCount() {
		SQLiteStatement sqlStatement = this.database
				.compileStatement("SELECT COUNT(*) FROM " + DBHelper.TABLE_READINGS);
		return (int) sqlStatement.simpleQueryForLong();
	}

	public static class ResultList {
		private String mBSSID;
		private List<WifiNetworkEntry> mResults = new ArrayList<WifiNetworkEntry>();

		public ResultList(String bssid) {
			this.mBSSID = bssid;
		}
		
		public ResultList(String bssid, List<WifiNetworkEntry> results) {
			this(bssid);
			setResults(results);
		}

		public String getBSSID() {
			return this.mBSSID;
		}

		public List<WifiNetworkEntry> getResults() {
			return this.mResults;
		}

		public boolean setResults(List<WifiNetworkEntry> results) {
			this.mResults = new ArrayList<WifiNetworkEntry>();
			
			for(WifiNetworkEntry result : results) {
				if(result.getBSSID().equals(this.mBSSID))
					this.mResults.add(result);
			}
			
			if(this.mResults.size() > 0)
				return true;
			
			return false;
		}
		
		public boolean updateResults(List<WifiNetworkEntry> results) {
			List<WifiNetworkEntry> newValues = new ArrayList<WifiNetworkEntry>();
			
			for(WifiNetworkEntry result : results) {
				if(this.mBSSID.equals(result.getBSSID()))
					newValues.add(result);
			}
			if(newValues.size() > 0) {
				this.mResults.addAll(newValues);
				return true;
			}
			
			return false;
		}
		
		public GraphViewData[] getGraphData() {
			if(this.mResults.size() < 1)
				return new GraphViewData[] { new GraphViewData(0, 0) };
			
			GraphViewData[] data = new GraphViewData[this.mResults.size()];
			
			for(int i=0; i < this.mResults.size() ; i++) {
				WifiNetworkEntry result = this.mResults.get(i);
				data[i] = new GraphViewData(result.getTimestamp(), result.getLevel());
			}
			
			return data;
		}
		
	}

	public SQLiteDatabase getWritableDatabase() {
		return this.getWritableDatabase();
	}
}*/