package com.blueodin.wifigraphs;

import android.app.Service;
import android.content.Intent;
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
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting WifiLockService...");
        wifiScanner = new WifiScanner(this, scanInterval);
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Stopping WifiLockService...");
        wifiScanner.stop();
        wifiScanner = null;
    }
}