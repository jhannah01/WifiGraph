package com.blueodin.wifigraphs;


import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.data.NetworkListAdapter;
import com.blueodin.wifigraphs.data.NetworkListAdapter.NetworkResultEntry;
import com.blueodin.wifigraphs.data.NetworkListAdapter.NetworkScanGroup;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ExpandableListView;

import wei.mark.standout.StandOutWindow;

public class MainActivity extends Activity {
	private boolean mIsWifiScanning = false;
	private NetworkListAdapter mListAdapter;
	private ArrayList<NetworkScanGroup> mScanGroups;
	private ExpandableListView mDiscoveredListView = null;
	private MenuItem mToggleScanningMenuItem;
	private boolean mAutoStartScanning;
	private int mScanInverval;
	private String mDefaultActivity;
	
	public boolean isWifiScanning() {
		return this.mIsWifiScanning;
	}
	
	private WifiStateReciever mWifiScanReceiver = new WifiStateReciever() {
		@Override
		public void updateResults(List<ScanResult> results) {
	    	if(mListAdapter == null) {
	    		Log.w(TAG, "Bailing on updating the scan results (null list adapter).");
	    		return;
	    	}
	    	
	    	for(ScanResult result : results) {
	    		NetworkResultEntry entry = new NetworkResultEntry(result);
	    		NetworkScanGroup group = mListAdapter.getGroupByBSSID(result.BSSID);
				if(group == null)
	    			group = new NetworkScanGroup(result.BSSID, result.SSID);
				
	    		mListAdapter.addItem(entry, group);
	    	}
	    	
	    	mListAdapter.notifyDataSetChanged();
		}
	};
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity, menu);
		mToggleScanningMenuItem = menu.findItem(R.id.menu_main_toggle_scanning);
		return super.onCreateOptionsMenu(menu); 
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_main_toggle_scanning)
			.setIcon((mIsWifiScanning ? R.drawable.ic_action_wifi_green : R.drawable.ic_action_wifi))
			.setChecked(mIsWifiScanning);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_main_settings:
			openSettings();
			return true;
			
		case R.id.menu_main_toggle_scanning:
			toggleScanning();
			
			if(mIsWifiScanning) {
				item.setIcon(R.drawable.ic_orange_wifi);
				item.setChecked(true);
			} else {
				item.setIcon(R.drawable.ic_action_wifi);
				item.setChecked(false);
			}
			
			return true;
			
		case R.id.menu_main_open_graph_activity:
			openGraphsActivity();
			return true;
			
		case R.id.menu_main_new_graph_window:
			openNewGraphWindow();
			return true;
			
		case R.id.menu_main_close_graph_windows:
			closeGraphWindows();
			return true;
		
		case R.id.menu_main_temp_graphs_demo:
			openGraphDemoActivity();
			return true;
			
		case R.id.menu_main_exit:
			exitActivity();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        readSettings();
        
        if(mDefaultActivity == "graphsPage") {
        	openGraphsActivity();
        	finish();
        	return;
        }
        
        setContentView(R.layout.main_activity);
        
        mDiscoveredListView = (ExpandableListView)findViewById(R.id.listview_discovered);
                
        mScanGroups = new ArrayList<NetworkScanGroup>();
        mListAdapter = new NetworkListAdapter(MainActivity.this, mScanGroups);
        mDiscoveredListView.setAdapter(mListAdapter);
        
		if(mAutoStartScanning)
			toggleScanning();
    }
	
	private void readSettings() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		mAutoStartScanning = sharedPref.getBoolean(SettingsActivity.KEY_AUTOSTART_SCANNING, false);
		mDefaultActivity = sharedPref.getString(SettingsActivity.KEY_DEFAULT_ACTIVITY, "mainPage");
		
		try {
			mScanInverval = Integer.parseInt(sharedPref.getString(SettingsActivity.KEY_SCAN_INTERVAL, getString(R.string.pref_default_scan_interval)));
		} catch (ClassCastException e) {
			mScanInverval = WifiLockService.DEFAULT_SCAN_INTERVAL;
		}
		
	}
	
	public void openNewGraphWindow() {
        StandOutWindow.closeAll(this, GraphWindow.class);
        StandOutWindow.show(this, GraphWindow.class, StandOutWindow.DEFAULT_ID);
    }

    public void closeGraphWindows() {
        StandOutWindow.closeAll(this, GraphWindow.class);
    }
    
    public void exitActivity() {
    	closeGraphWindows();
    	finish();
    }
    
    public void openGraphsActivity() {
    	startActivity(new Intent(this, GraphActivity.class));
    }
    
    public void openGraphDemoActivity() {
    	startActivity(new Intent(this, GraphDemoActivity.class));
    }
    
    public void openSettings() {
    	startActivity(new Intent(this, SettingsActivity.class));
    }
    
    public void toggleScanning() {
    	if(!mIsWifiScanning) {
    		registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    		Intent serviceIntent = new Intent(this, WifiLockService.class);
    		serviceIntent.putExtra(WifiLockService.SET_SCAN_INTERVAL, mScanInverval);
    		startService(serviceIntent);
    		
    		if(mToggleScanningMenuItem != null) {
    			mToggleScanningMenuItem.setChecked(true);
    			mToggleScanningMenuItem.setIcon(R.drawable.ic_action_wifi_green);
    		}
    	} else {
    		unregisterReceiver(mWifiScanReceiver);
    		stopService(new Intent(this, WifiLockService.class));
    		
    		if(mToggleScanningMenuItem != null) {
    			mToggleScanningMenuItem.setChecked(false);
    			mToggleScanningMenuItem.setIcon(R.drawable.ic_action_wifi);
    		}
    	}
    	
    	mIsWifiScanning = !mIsWifiScanning;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if(mIsWifiScanning)
    		registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if(mIsWifiScanning)
    		unregisterReceiver(mWifiScanReceiver);
    }
}