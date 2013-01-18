package com.blueodin.wifigraphs.activities;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.blueodin.graphs.services.WifiLockService;
import com.blueodin.graphs.services.WifiLockService.WifiServiceBinder;
import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.WifiNetworkEntry;
import com.blueodin.wifigraphs.db.RecordReaderDbHelper;
import com.blueodin.wifigraphs.db.RecordsDataSource;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DebugActivity extends Activity {
	private RecordReaderDbHelper mDbHelper = new RecordReaderDbHelper(getContext());
	private WifiLockService mService;
	private boolean mBound = false;
	
    private ServiceConnection mConnection = new ServiceConnection() {
		@Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WifiServiceBinder binder = (WifiServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    
    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, WifiLockService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.mDataSource.open();
		
		setContentView(R.layout.debug);
		
		updateRecordCount();
		updateScanInterval();
		
		ListView listRecords = (ListView)findViewById(R.id.listview_debug_records);
		List<WifiNetworkEntry> cwifiRecords = this.mDataSource.getRecordsFor("58:6d:8f:53:7d:80");
		List<String> timestampList = new ArrayList<String>();
		DateFormat dateFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
		for(WifiNetworkEntry entry : cwifiRecords)
			timestampList.add(String.format("%d - %s", entry.getTimestamp(), dateFormatter.format(new Date(entry.getTimestamp()))));
		
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, timestampList);
		listRecords.setAdapter(listAdapter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(this.mDataSource.isOpen())
			this.mDataSource.close();
	}
	
	public void handleButtonClick(View view) {
		switch(view.getId()) {
			case R.id.btn_debug_exit:
				finish();
				break;
			case R.id.btn_debug_main:
				startActivity(new Intent(this, MainActivity.class));
				finish();
				break;
			case R.id.btn_debug_graph:
				startActivity(new Intent(this, GraphActivity.class));
				finish();
				break;
			case R.id.btn_debug_overview:
				startActivity(new Intent(this, OverviewActivity.class));
				finish();
				break;
			case R.id.btn_debug_add_fake_nets:
				int c = addFakeNetworks();
				Toast.makeText(this, "Added fake networks to the database. Total count: " + c, Toast.LENGTH_SHORT).show();
				break;
			case R.id.btn_debug_update_database_records_count:
				updateRecordCount();
				break;
			case R.id.btn_debug_update_scan_interval:
				updateScanInterval();
				break;
		}
	}
	
	private void updateRecordCount() {
		((TextView)findViewById(R.id.textview_debug_database_count))
			.setText("There are #" + this.mDataSource.getRecordsCount() + " records in the database currently.");
	}
	
	private void updateScanInterval() {
		if(!this.mBound)
			return;
		
		((TextView)findViewById(R.id.textview_debug_scan_interval))
			.setText("Service Scan Interval: " + DateUtils.getRelativeTimeSpanString(this, this.mService.getScanInterval()));
	}

	private int addFakeNetworks() {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		Date now = new Date();
		cal.setTime(now);
		long currentTime = cal.getTimeInMillis(), fiveMinutesAgo, oneHourAgo;
		cal.set(Calendar.MINUTE, -5);
		fiveMinutesAgo = cal.getTimeInMillis();
		cal.setTime(now);
		cal.set(Calendar.HOUR, -1);
		oneHourAgo = cal.getTimeInMillis();
		
		WifiNetworkEntry[] fakeResults = {
			new WifiNetworkEntry("00:11:22:33:44:55", "cwifi", -45, 2045, "[WPA2-PSK-TKIP][WPA][ESS]", currentTime),
			new WifiNetworkEntry("ab:cd:ef:01:23:45", "fiosAP", -74, 2150, "[WPA][ESS]", currentTime - 3000),
			new WifiNetworkEntry("99:aa:88:bb:77:cc", "ng-G", -25, 2250, "[WPA2-PSK-TKIP-CCMP][WPA][WPA][ESS]", currentTime - 1000),
			
			new WifiNetworkEntry("00:11:22:33:44:55", "cwifi", -66, 2045, "[WPA2-PSK-TKIP][WPA][ESS]", fiveMinutesAgo),
			new WifiNetworkEntry("ab:cd:ef:01:23:45", "fiosAP", -74, 2150, "[WPA][ESS]", fiveMinutesAgo - 2000),
			new WifiNetworkEntry("00:11:22:33:44:55", "cwifi", -45, 2045, "[WPA2-PSK-TKIP][WPA][ESS]", fiveMinutesAgo - 3000),
			
			new WifiNetworkEntry("ab:cd:ef:01:23:45", "fiosAP", -80, 2150, "[WPA][ESS]", oneHourAgo - 2000),
			new WifiNetworkEntry("00:11:22:33:44:55", "cwifi", -45, 2045, "[WPA2-PSK-TKIP][WPA][ESS]", oneHourAgo - 3000),
			new WifiNetworkEntry("99:aa:88:bb:77:cc", "ng-G", -25, 2250, "[WPA2-PSK-TKIP-CCMP][WPA][WPA][ESS]", oneHourAgo - 4000),
			
			new WifiNetworkEntry("ab:cd:ef:01:23:45", "fiosAP", -80, 2150, "[WPA][ESS]", oneHourAgo - (30*60*1000)),
			new WifiNetworkEntry("00:11:22:33:44:55", "cwifi", -45, 2045, "[WPA2-PSK-TKIP][WPA][ESS]", oneHourAgo - (30*60*1000) - 3000),
			new WifiNetworkEntry("99:aa:88:bb:77:cc", "ng-G", -25, 2250, "[WPA2-PSK-TKIP-CCMP][WPA][WPA][ESS]", oneHourAgo - (30*60*1000)- 4000)
		};
		
		for(WifiNetworkEntry r : fakeResults)
			this.mDataSource.createRecord(r);
		
		return this.mDataSource.getRecordsCount(); 
	}
	
}
