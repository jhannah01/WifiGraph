package com.blueodin.wifigraphs;


import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.data.NetworkListAdapter;
import com.blueodin.wifigraphs.data.NetworkListAdapter.NetworkResultEntry;
import com.blueodin.wifigraphs.data.NetworkListAdapter.NetworkScanGroup;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ExpandableListView;

import wei.mark.standout.StandOutWindow;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private boolean isWifiScanning = false;

	private NetworkListAdapter expListAdapter;
	private ArrayList<NetworkScanGroup> expListItems;
	private ExpandableListView listViewDiscovered = null;
	private WifiStateReciever wifiScanReceiver;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        listViewDiscovered = (ExpandableListView)findViewById(R.id.listview_discovered);
        expListItems = new ArrayList<NetworkScanGroup>();
        expListAdapter = new NetworkListAdapter(MainActivity.this, expListItems);
        listViewDiscovered.setAdapter(expListAdapter);
        wifiScanReceiver = new WifiStateReciever() {
			
			@Override
			public void updateResults(List<ScanResult> results) {
				doUpdateResults(results);
			}
		};    
    }

    public void onOpenClick(View view) {
        StandOutWindow.closeAll(this, GraphWindow.class);
        StandOutWindow.show(this, GraphWindow.class, StandOutWindow.DEFAULT_ID);
    }

    public void onCloseClick(View view) {
        StandOutWindow.closeAll(this, GraphWindow.class);
    }
    
    public void onExitClick(View view) {
    	StandOutWindow.closeAll(this, GraphWindow.class);
    	finish();
    }
    
    public void onGraphsClick(View view) {
    	startActivity(new Intent(this, GraphActivity.class));
    }
    
    public void onGraphDemoClick(View view) {
    	startActivity(new Intent(this, GraphDemoActivity.class));
    }
    
    public void onToggleScanning(View view) {
    	TextView textScanningStatus = (TextView)findViewById(R.id.textview_scanning_status);
    	
    	if(!isWifiScanning) {
    		Log.i(TAG, "Starting the wifiReceiver [ @ onToggleScanning()]");
    		startService(new Intent(this, WifiLockService.class));
    		registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    		textScanningStatus.setText(getString(R.string.text_scanning_status_running));
    	} else {
    		Log.i(TAG, "Stopping the WiFi scanner service [ @ onToggleScanning()]");
    		unregisterReceiver(wifiScanReceiver);
    		stopService(new Intent(this, WifiLockService.class));
    		textScanningStatus.setText(getString(R.string.text_scanning_status_stopped));
    	}
    	
    	isWifiScanning = !isWifiScanning;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if(isWifiScanning)
    		registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if(isWifiScanning)
    		unregisterReceiver(wifiScanReceiver);
    }
    
    public void doUpdateResults(List<ScanResult> results) {
    	Log.d(TAG, "Got a call to update the results");
    	
    	if(expListAdapter == null) {
    		Log.i(TAG, "Bailing on updating the scan results (null list adapter).");
    		return;
    	}
    	
    	for(ScanResult result : results) {
    		NetworkResultEntry entry = new NetworkResultEntry(result);
    		NetworkScanGroup group = expListAdapter.getGroupByBSSID(result.BSSID);
			if(group == null)
    			group = new NetworkScanGroup(result.BSSID, result.SSID);
    		
    		expListAdapter.addItem(entry, group);
    	}
    	
    	expListAdapter.notifyDataSetChanged();
    }
    /*
    public ArrayList<NetworkEntry> SetStandardGroups() {
    	ArrayList<NetworkEntry> list = new ArrayList<NetworkEntry>();
    	ArrayList<NetworkScanEntry> list2 = new ArrayList<NetworkScanEntry>();
        
    	NetworkEntry gru1 = new NetworkEntry("00:11:22:33:44:55", "cwifi");
        NetworkScanEntry ch1_1 = new NetworkScanEntry("00:11:22:33:44:55", "cwifi", -55, "[WPA-PSK-TKIP+CCMP][WPA2-PSK-TKIP+CCMP][ESS]", 2412);
        ch1_1.setTimestamp(System.currentTimeMillis());
        list2.add(ch1_1);
        NetworkScanEntry ch1_2 = new NetworkScanEntry("00:11:22:33:44:55", "cwifi", -45, "[WPA-PSK-TKIP+CCMP][WPA2-PSK-TKIP+CCMP][ESS]", 2412);
        ch1_2.setTimestamp(System.currentTimeMillis()-500000); 
        list2.add(ch1_2);
        NetworkScanEntry ch1_3 = new NetworkScanEntry("00:11:22:33:44:55", "cwifi", -40, "[WPA-PSK-TKIP+CCMP][WPA2-PSK-TKIP+CCMP][ESS]", 2412);
        ch1_3.setTimestamp(System.currentTimeMillis()-800000);
        list2.add(ch1_3);
        
        gru1.setItems(list2);
        
        list2 = new ArrayList<NetworkScanEntry>();
        NetworkEntry gru2 = new NetworkEntry("aa:bb:cc:dd:ee:ff", "ng-N");
        NetworkScanEntry ch2_1 = new NetworkScanEntry("aa:bb:cc:dd:ee:ff", "ng-N", -95, "[WPA2-PSK-CCMP][WPS][ESS]", 2462);
        ch2_1.setTimestamp(System.currentTimeMillis()-10000000);
        list2.add(ch2_1);
        NetworkScanEntry ch2_2 = new NetworkScanEntry("aa:bb:cc:dd:ee:ff", "ng-N", -50, "[WPA2-PSK-CCMP][WPS][ESS]", 2462);
        ch2_2.setTimestamp(System.currentTimeMillis()-500000000);
        list2.add(ch2_2);
        NetworkScanEntry ch2_3 = new NetworkScanEntry("aa:bb:cc:dd:ee:ff", "ng-N", -60, "[WPA2-PSK-CCMP][WPS][ESS]", 2462);
        ch2_3.setTimestamp(System.currentTimeMillis()-800000000);
        list2.add(ch2_3);
        
        gru2.setItems(list2);
        
        list.add(gru1);
        list.add(gru2);
        
        return list;
    }*/
}