package com.blueodin.wifigraphs.db;

import android.provider.BaseColumns;

public class RecordReaderContract {
	public abstract class RecordEntry implements BaseColumns {
	    public static final String COLUMN_NAME_BSSID = "bssid";
	    public static final String COLUMN_NAME_SSID = "ssid";
	    public static final String COLUMN_NAME_LEVEL = "level";
	    public static final String COLUMN_NAME_FREQUENCY = "frequency";
	    public static final String COLUMN_NAME_CAPABILITIES = "capabilities";
	    public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
	}

	private RecordReaderContract() { }
}
