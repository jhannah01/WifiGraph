package com.blueodin.wifigraphs.db;
	
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordReaderDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "ScanRecords.db";
	public static final String TABLE_NAME = "records";
	
	private static final String TEXT_TYPE = " TEXT";
	private static final String INT_TYPE = " INTEGER";
	private static final String UNIQUE_TYPE = " UNIQUE";
	private static final String COMMA_SEP = ",";
	
	private static final String SQL_CREATE_ENTRIES =
	    "CREATE TABLE " + TABLE_NAME + " (" + RecordReaderContract.RecordEntry._ID + " INTEGER PRIMARY KEY," +
	    RecordReaderContract.RecordEntry.COLUMN_NAME_BSSID + TEXT_TYPE + UNIQUE_TYPE + COMMA_SEP +
	    RecordReaderContract.RecordEntry.COLUMN_NAME_SSID + TEXT_TYPE + COMMA_SEP +
	    RecordReaderContract.RecordEntry.COLUMN_NAME_LEVEL + INT_TYPE + COMMA_SEP +
	    RecordReaderContract.RecordEntry.COLUMN_NAME_FREQUENCY + INT_TYPE + COMMA_SEP +
	    RecordReaderContract.RecordEntry.COLUMN_NAME_CAPABILITIES + TEXT_TYPE + COMMA_SEP +
	    RecordReaderContract.RecordEntry.COLUMN_NAME_TIMESTAMP + INT_TYPE + " )";

	private static final String SQL_DROP_RECORDS_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	
	public RecordReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP_RECORDS_TABLE);
        onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }	
}
