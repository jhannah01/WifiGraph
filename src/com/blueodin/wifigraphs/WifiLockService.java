package com.blueodin.wifigraphs;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WifiLockService extends Service {
	public static final String SET_SCAN_INTERVAL = "SET_SCAN_INTERVAL";
	public static final int DEFAULT_SCAN_INTERVAL = 5*1000;
	
	private static final String TAG = "WifiLockService";
    
	private WifiScanner wifiScanner;
    private int scanInterval = DEFAULT_SCAN_INTERVAL;
 
    @Override
    public IBinder onBind(Intent arg0) { 
        return null;
    }
    /*
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scanInterval = intent.getExtras().getInt(SET_SCAN_INTERVAL, DEFAULT_SCAN_INTERVAL);
    	Log.i(TAG, "Received start id " + startId + ": " + intent + " [scanInterval: " + scanInterval + "]");
    	return START_STICKY;
    }*/
    /*
    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
 	*/
    @Override
    public void onCreate() {
        super.onCreate();
        wifiScanner = new WifiScanner(this, scanInterval);
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
 
        wifiScanner.stop();
        wifiScanner = null;
    }
}