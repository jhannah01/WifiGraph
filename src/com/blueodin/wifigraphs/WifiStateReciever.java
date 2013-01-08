package com.blueodin.wifigraphs;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public abstract class WifiStateReciever extends BroadcastReceiver {
	public static final String TAG = "WifiStateReceiver";
	
	public WifiStateReciever() { }

	@Override
	public void onReceive(Context context, Intent intent) {
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            List<ScanResult> scanResults = wifiManager.getScanResults();
 
            Log.i(TAG, "-- Processing " + scanResults.size() +" results found in last wifi scan --");
            // get list of silenced networks from preferences
            for(ScanResult result : scanResults) {
            	//Log.i(TAG, String.format(" -> Got a wifi scan result @ %d: SSID: %s (%s) level: %d dBm, Frequency: %d MHz, Capabilities: %s", result.timestamp, result.SSID, result.BSSID, result.level, result.frequency, result.capabilities));
            	Log.i(TAG, result.toString());
            }
            Log.i(TAG, "-- Done Processing --");
            synchronized (scanResults) {
            	updateResults(scanResults);
			}
            
        }
	}
	
	public abstract void updateResults(List<ScanResult> results);
}
