package com.blueodin.wifigraphs;


import java.util.List;

import com.blueodin.wifigraphs.data.NetworkListAdapter;
import com.blueodin.wifigraphs.data.NetworkListAdapter.NetworkListGroup;
import com.blueodin.wifigraphs.data.NetworkScanRecordsDataSource;
import com.jjoe64.graphview.LineGraphView;

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
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import wei.mark.standout.StandOutWindow;

public class MainActivity extends Activity {
	private boolean mIsWifiScanning = false;
	private NetworkListAdapter mListAdapter;
	private ExpandableListView mDiscoveredListView = null;
	private MenuItem mToggleScanningMenuItem;
	private boolean mAutoStartScanning;
	private int mScanInverval;
	private String mDefaultActivity;
	private NetworkScanRecordsDataSource mRecordsDataSource = new NetworkScanRecordsDataSource(this);
	public final static String FLAG_FROM_NOTIFICATION = "from_notification";
	
	public boolean isWifiScanning() {
		return this.mIsWifiScanning;
	}
	
	private WifiStateReciever mWifiScanReceiver = new WifiStateReciever() {
		@Override
		public void updateResults(List<ScanResult> results) {
			for(ScanResult result : results)
				mListAdapter.addRecord(mRecordsDataSource.createRecord(result));
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
			//.setIcon(R.drawable.ic_wifi_toggle);
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
				//item.setIcon(R.drawable.ic_wifi_green);
				item.setChecked(true);
			} else {
				//item.setIcon(R.drawable.ic_action_wifi);
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
        
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
        	if(bundle.containsKey(FLAG_FROM_NOTIFICATION) && bundle.getBoolean(FLAG_FROM_NOTIFICATION)) {
        		mIsWifiScanning = true;
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
                
        mListAdapter = new NetworkListAdapter(MainActivity.this, mRecordsDataSource.getAllRecords());
        mDiscoveredListView.setAdapter(mListAdapter);
        
        mDiscoveredListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
				
			}
		});
        
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
	
	public void openNewGraphWindow() {
        StandOutWindow.closeAll(this, GraphWindow.class);
        StandOutWindow.show(this, GraphWindow.class, StandOutWindow.DEFAULT_ID);
    }

    public void closeGraphWindows() {
        StandOutWindow.closeAll(this, GraphWindow.class);
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
    
    public void openGraphDemoActivity() {
    	startActivity(new Intent(this, GraphDemoActivity.class));
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
    	
    	if(mToggleScanningMenuItem != null) {
			mToggleScanningMenuItem.setChecked(mIsWifiScanning);
			//mToggleScanningMenuItem.setIcon((mIsWifiScanning ? R.drawable.ic_wifi_green : R.drawable.ic_action_wifi));
		}
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