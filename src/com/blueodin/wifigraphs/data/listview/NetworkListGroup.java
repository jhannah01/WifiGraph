package com.blueodin.wifigraphs.data.listview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.NetworkScanResult;
import com.blueodin.wifigraphs.data.NetworkSecurity;

public class NetworkListGroup {
	private String mBSSID;
	private String mSSID;
	private final NetworkListChild mChild;
	private List<NetworkScanResult> mResults;
	
	public NetworkListGroup(String bssid, String ssid, List<NetworkScanResult> results) {
		this.mBSSID = bssid;
		this.mSSID = ssid;
		this.mResults = results;
		this.mChild = new NetworkListChild(this);
	}
	
	public NetworkListGroup(String bssid, String ssid) {
		this(bssid, ssid, new ArrayList<NetworkScanResult>());
	}
	
	public DataSetObserver notifyDataSetChanged = new DataSetObserver() {
		public void onChanged() {
			mChild.updateGraph();
		}
	};
	
	public String getBSSID() {
		return this.mBSSID;
	}
	
	public String getSSID() {
		return this.mSSID;
	}
	
	public NetworkListChild getChild() {
		return this.mChild;
	}
	
	public boolean addResult(NetworkScanResult result) {
		this.mResults.add(result);
		//this.mChild.addGraphData(result, true);
		return true;
	}
	
	public List<NetworkScanResult> getResults() {
		return this.mResults;
	}
	
	public NetworkScanResult getLastResult(boolean byTimestamp) {
		if(this.mResults.size() < 1)
			return null;
		
		if(!byTimestamp)
			return this.mResults.get(this.mResults.size()-1);
		
		NetworkScanResult lastResult = this.mResults.get(0);
		
		for(NetworkScanResult result : this.mResults) {
			if(result.getTimestamp() > lastResult.getTimestamp())
				lastResult = result;
		}
		
		return lastResult;
	}
	
	public void drawView(Context context, View view, ViewGroup parent) {
		NetworkScanResult lastResult = (NetworkScanResult)getLastResult(true);
		
		TextView textViewRecords = (TextView) view.findViewById(R.id.textview_group_records);
		TextView textViewLastSeen = (TextView) view.findViewById(R.id.textview_group_lastseen);
		TextView textViewBSSID = (TextView) view.findViewById(R.id.textview_group_bssid);
		TextView textViewSSID = (TextView) view.findViewById(R.id.textview_group_ssid);
		ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageview_group_icon);
		
		textViewRecords.setText(String.format("%d", this.mResults.size()));
		textViewBSSID.setText(this.mBSSID);
		textViewSSID.setText(this.mSSID);
		textViewLastSeen.setText(lastResult.getFormattedTimestamp());
		
		Drawable dr = context.getResources().getDrawable(getListIcon());
		//dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
		imageViewIcon.setImageDrawable(dr);
	}
	
	public int getListIcon() {
		List<NetworkSecurity.SecurityType> securityTypes = getLastResult(false).getSecurityTypes();
		
		if(securityTypes.contains(NetworkSecurity.SecurityType.WPA2))
			return R.drawable.ic_wifi_green;
		
		if(securityTypes.contains(NetworkSecurity.SecurityType.WPA))
			return R.drawable.ic_wifi_orange;
		
		if(securityTypes.contains(NetworkSecurity.SecurityType.WEP))
			return R.drawable.ic_wifi_blue;
		
		if(securityTypes.contains(NetworkSecurity.SecurityType.Open))
			return R.drawable.ic_wifi_red;
		
		return R.drawable.ic_wifi_grey;
	}
}