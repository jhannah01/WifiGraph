package com.blueodin.wifigraphs.providers;

import java.util.Arrays;
import java.util.HashSet;

import com.blueodin.wifigraphs.db.DBHelper;
import com.blueodin.wifigraphs.db.RecordsDataSource;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class ResultsProvider extends ContentProvider {
	// database
	private RecordsDataSource database;

	// Used for the UriMacher
	private static final int RECORDS = 10;
	private static final int RECORD_ID = 20;
	private static final String AUTHORITY = "com.blueodin.wifigraphs.providers.ResultsProvider";

	private static final String BASE_PATH = "records";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/records";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/record";

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	@Override
	public boolean onCreate() {
		database = new RecordsDataSource(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// Check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(DBHelper.TABLE_READINGS);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case RECORDS:
			break;
		case RECORD_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(DBHelper.COLUMN_ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		long id = 0;
		switch (uriType) {
		case RECORDS:
			id = sqlDB.insert(DBHelper.TABLE_READINGS, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, RECORDS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", RECORD_ID);
	}

	public ResultsProvider() {
	}

	 @Override
	  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsUpdated = 0;
	    switch (uriType) {
	    case RECORDS:
	      rowsUpdated = sqlDB.update(DBHelper.TABLE_READINGS, 
	          values, 
	          selection,
	          selectionArgs);
	      break;
	    case RECORD_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsUpdated = sqlDB.update(DBHelper.TABLE_READINGS, 
	            values,
	            DBHelper.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsUpdated = sqlDB.update(DBHelper.TABLE_READINGS,
	            values,
	            DBHelper.COLUMN_ID + "=" + id  + " and "  + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsUpdated;
	  }


	  @Override
	  public int delete(Uri uri, String selection, String[] selectionArgs) {
	    int uriType = sURIMatcher.match(uri);
	    SQLiteDatabase sqlDB = database.getWritableDatabase();
	    int rowsDeleted = 0;
	    switch (uriType) {
	    case RECORDS:
	      rowsDeleted = sqlDB.delete(DBHelper.TABLE_READINGS, selection, selectionArgs);
	      break;
	    case RECORD_ID:
	      String id = uri.getLastPathSegment();
	      if (TextUtils.isEmpty(selection)) {
	        rowsDeleted = sqlDB.delete(DBHelper.TABLE_READINGS,
	            DBHelper.COLUMN_ID + "=" + id, 
	            null);
	      } else {
	        rowsDeleted = sqlDB.delete(DBHelper.TABLE_READINGS,
	            DBHelper.COLUMN_ID + "=" + id 
	            + " and " + selection,
	            selectionArgs);
	      }
	      break;
	    default:
	      throw new IllegalArgumentException("Unknown URI: " + uri);
	    }
	    getContext().getContentResolver().notifyChange(uri, null);
	    return rowsDeleted;
	  }
	 
	 private void checkColumns(String[] projection) {
		String[] available = { DBHelper.COLUMN_ID, DBHelper.COLUMN_BSSID,
				DBHelper.COLUMN_SSID, DBHelper.COLUMN_FREQUENCY,
				DBHelper.COLUMN_LEVEL, DBHelper.COLUMN_CAPABILITIES,
				DBHelper.COLUMN_TIMESTAMP };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns))
				throw new IllegalArgumentException("Unknown columns in projection");
		}
	}

}
