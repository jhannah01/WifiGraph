package com.blueodin.wifigraphs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

public class WifiScanner extends BroadcastReceiver {
	private PendingIntent pendingIntent;
	private AlarmManager alarmManager;
	
    private static WifiLock wifiLock;
    private static WakeLock wakeLock;
    
    private static final String TAG = "WifiScanner";
    
    public static void lock() {
        try {
            wakeLock.acquire();
            wifiLock.acquire();
        } catch(Exception e) {
            Log.e(TAG, "Error getting lock: " + e.getMessage());
        }
    }
 
    public static void unlock() {
        if(wakeLock.isHeld())
            wakeLock.release();
        if(wifiLock.isHeld())
            wifiLock.release();
    }
	
    public WifiScanner() { } 

    public WifiScanner(Context context, int msInterval){
    	wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                        .createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, TAG + "_WifiLock");
        
    	wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                        .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + "_WakeLock");
    	
    	alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    	
        // use this class as the receiver
        Intent intent = new Intent(context, WifiScanner.class);
        // create a PendingIntent that can be passed to the AlarmManager
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
 
        // create a repeating alarm, that goes of every x seconds
        // AlarmManager.ELAPSED_REALTIME_WAKEUP = wakes up the cpu only
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), msInterval, pendingIntent);
    }
    
    public void stop() {
    	alarmManager.cancel(pendingIntent);
    }
    
    @Override
	public void onReceive(Context context, Intent intent) {
    	WifiManager connManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        
        if(connManager.isWifiEnabled()) {
            lock();
            connManager.startScan();
        }
	}
}
