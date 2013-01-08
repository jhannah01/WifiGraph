package com.blueodin.wifigraphs.data;

import java.util.List;

import android.content.Context;

public class LogData {
	private static LogData mInstance = null;
    
    private RecordsDataSource mRecordsDataSource;
    
    public static LogData getInstance(Context context) {
        synchronized(LogData.class) {
            if(mInstance == null)
                mInstance = new LogData(context.getApplicationContext());
        }

        return mInstance;
    }

    private LogData(Context context) {
        mRecordsDataSource = new RecordsDataSource(context);
    }

    public synchronized SignalRecord writeRecord(long time, int value, String ssid, String bssid) {
    	return mRecordsDataSource.createRecord(time, value, ssid, bssid);
    }
    
    public synchronized int readRecords(SignalRecord[] records) {
    	List<SignalRecord> recordList = mRecordsDataSource.getAllRecords();
    	recordList.toArray(records);
    	return recordList.size();
    }
}
