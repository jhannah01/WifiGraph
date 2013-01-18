package com.blueodin.wifigraphs.activities;


import java.util.List;

import com.blueodin.graphs.services.WifiLockService;
import com.blueodin.graphs.services.WifiStateReciever;
import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.WifiNetworkEntry;
import com.blueodin.wifigraphs.db.RecordsDataSource;
import com.blueodin.wifigraphs.providers.NetworkExpandedListAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import wei.mark.standout.StandOutWindow;

public class MainActivity extends Activity {
	private boolean mIsWifiScanning = false;
	private NetworkExpandedListAdapter mListAdapter;
	private ExpandableListView mDiscoveredListView = null;
	private Menu mMenu;
	private boolean mAutoStartScanning;
	private int mScanInverval;
	private String mDefaultActivity;
	private RecordsDataSource mRecordsDataSource = new RecordsDataSource(this);
	
	
	public final static String FLAG_FROM_NOTIFICATION = "from_notification";
	
	public boolean isWifiScanning() {
		return this.mIsWifiScanning;
	}
	private WifiStateReciever mWifiScanReceiver = new WifiStateReciever() {
		@Override
		public void updateResults(final List<ScanResult> results) {
			for(ScanResult result : results) {
				WifiNetworkEntry newResult = mRecordsDataSource.createRecord(result);
				mListAdapter.updateGraph(newResult);
			}
		}
	};
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		mMenu = menu;
		return super.onCreateOptionsMenu(menu); 
    }
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_toggle_scanning)
			.setIcon(mIsWifiScanning ? R.drawable.ic_wifi_green : R.drawable.ic_wifi_grey)
			.setChecked(mIsWifiScanning);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_settings:
			openSettings();
			return true;
			
		case R.id.menu_toggle_scanning:
			toggleScanning();
			return true;
			
		case R.id.menu_open_overview_activity:
			openOverviewActivity();
			return true;
			
		case R.id.menu_open_graph_activity:
			openGraphsActivity();
			return true;
			
		case R.id.menu_new_graph_window:
			newGraphWindow();
			return true;
			
		case R.id.menu_close_graph_windows:
			closeGraphWindows();
			return true;
		
		case R.id.menu_open_debug_activity:
			openDebugActivity();
			return true;
			
		case R.id.menu_exit:
			exitActivity();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	private void openServiceAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setMessage(R.string.dialog_service_stop)
    		.setPositiveButton(R.string.dialog_button_yes_and_exit, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int id) {
    				if(!mIsWifiScanning)
    					stopScanning();
    				else
    					toggleScanning();
    				
    				finish();
    			}
    		})
    		.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { }
			})
			.setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(!mIsWifiScanning)
    					stopScanning();
    				else
    					toggleScanning();
				}
			})
    		.create()
    		.show();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
        	if(bundle.containsKey(FLAG_FROM_NOTIFICATION) && bundle.getBoolean(FLAG_FROM_NOTIFICATION)) {
        		mIsWifiScanning = true;
        		openServiceAlertDialog();
        	}
        }
        
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        readSettings();
        
        mRecordsDataSource.open();
        
        if(mDefaultActivity == "graphsPage") {
        	openGraphsActivity();
        	finish();
        	return;
        }
        
        setContentView(R.layout.main_activity);
        
        mDiscoveredListView = (ExpandableListView)findViewById(R.id.listview_discovered);
                
        mListAdapter = new NetworkExpandedListAdapter(MainActivity.this, mRecordsDataSource);
        mDiscoveredListView.setAdapter(mListAdapter);

		if(mAutoStartScanning && !mIsWifiScanning)
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
	
	public void openOverviewActivity() {
		startActivity(new Intent(this, OverviewActivity.class));
	}
	
	public void newGraphWindow() {
        StandOutWindow.closeAll(this, GraphWindow.class);
        StandOutWindow.show(this, GraphWindow.class, StandOutWindow.DEFAULT_ID);
    }

    public void closeGraphWindows() {
        StandOutWindow.closeAll(this, GraphWindow.class);
    }
    
    private void openDebugActivity() {
		startActivity(new Intent(this, DebugActivity.class));
	}
    
    public void exitActivity() {
    	closeGraphWindows();
    	
    	if(mIsWifiScanning)
    		toggleScanning();
    	
    	finish();
    }
    
    public void openGraphsActivity() {
    	startActivity(new Intent(this, GraphActivity.class));
    }
    
    public void openSettings() {
    	startActivity(new Intent(this, SettingsActivity.class));
    }
    
    public void toggleScanning() {
    	if(!mIsWifiScanning)
    		startScanning();
    	else
    		stopScanning();
    	
    	mIsWifiScanning = !mIsWifiScanning;
    	
    	this.mMenu.findItem(R.id.menu_toggle_scanning)
    		.setIcon((mIsWifiScanning ? R.drawable.ic_wifi_green : R.drawable.ic_wifi_grey))
			.setChecked(mIsWifiScanning);
    }
    public void startScanning() {
    	registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		Intent serviceIntent = new Intent(this, WifiLockService.class);
		serviceIntent.putExtra(WifiLockService.SET_SCAN_INTERVAL, mScanInverval);
		startService(serviceIntent);
    }
    
    public void stopScanning() {
    	unregisterReceiver(mWifiScanReceiver);
		stopService(new Intent(this, WifiLockService.class));
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
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mRecordsDataSource.close();
    }
    
    
    

}