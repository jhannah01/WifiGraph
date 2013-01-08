package com.blueodin.wifigraphs;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

public abstract class WifiStateReciever extends BroadcastReceiver {
	public static final String TAG = "WifiStateReceiver";
	
	public WifiStateReciever() { }

	@Override
	public void onReceive(final Context context, Intent intent) {
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
        	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    		updateResults(wifiManager.getScanResults());
        }
	}
	
	public abstract void updateResults(List<ScanResult> results);
}
