package com.blueodin.wifigraphs.data;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.R.drawable;
import com.blueodin.wifigraphs.data.NetworkSecurity.SecurityType;

import android.database.DataSetObserver;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.widget.ExpandableListAdapter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class NetworkListAdapter extends BaseExpandableListAdapter {	
	private Context mContext;
	private ArrayList<NetworkEntry> mNetworks;
	private HashMap<NetworkEntry,NetworkScanEntry> mNetworkList = new HashMap<NetworkListAdapter.NetworkEntry, NetworkListAdapter.NetworkScanEntry>();

	public static class NetworkScanEntry implements Comparable<NetworkEntry> {
		private String mBSSID;
		private String mSSID;
		private int mLevel = 0;
		private long mTimestamp = 0;
		private int mFreqency = 0;
		private String mCapabilities = "";
		
		public NetworkScanEntry(String bssid, String ssid) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
		}
		
		@SuppressLint("NewApi")
		public NetworkScanEntry(ScanResult result) {
			this(result.BSSID, result.SSID, result.level, result.capabilities, result.frequency);
			
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				this.mTimestamp = result.timestamp;
		}
		
		public NetworkScanEntry(String bssid, String ssid, int level, String capabilities, int freqency) {
			this(bssid, ssid);
			this.mLevel = level;
			this.mCapabilities = capabilities;
			this.mFreqency = freqency;
			this.mTimestamp = System.currentTimeMillis();
		}
		
		public NetworkScanEntry(String bssid, String ssid, int level, String capabilities, int freqency, long timestamp) {
			this(bssid, ssid, level, capabilities, freqency);
			this.mTimestamp = timestamp;
		}
		
		public String getBSSID() {
			return mBSSID;
		}

		public String getSSID() {
			return mSSID;
		}
		
		public int getLevel() {
			return mLevel;
		}
		
		public void setLevel(int level) {
			this.mLevel = level;
		}
		
		public long getTimestamp() {
			return mTimestamp;
		}
		
		public void setTimestamp(long timestamp) {
			this.mTimestamp = timestamp;
		}
		
		public int getFreqency() {
			return mFreqency;
		}
		
		public void setFreqency(int freqency) {
			this.mFreqency = freqency;
		}
		
		public String getCapabilities() {
			return mCapabilities;
		}
		
		public void setCapabilities(String capabilities) {
			this.mCapabilities = capabilities;
		}
		
		public List<SecurityType> getSecurityTypes() {
			return SecurityType.parseCapabilities(this.mCapabilities);
		}
		
		public String getHumanReadableSecurityInfo() {
			NetworkSecurity security = new NetworkSecurity(this.mCapabilities);
			return security.getHumanReadable();
		}
		
		@Override
		public String toString() {
			return String.format("Level: %d dBm | Frequency: %d MHz | Capabilities: %s | SSID: %s", this.mLevel, this.mFreqency, this.getHumanReadableSecurityInfo(), this.mSSID);
		}
		
		public String getFormattedTimestamp() {
			if(this.mTimestamp > 0)
				return ((String)DateFormat.format("MMM dd, yyyy h:mmaa", this.mTimestamp));
			
			return "Unknown";
		}

		@Override
		public int compareTo(NetworkEntry o) {
			if(!this.mSSID.equals(o.getSSID()))
				return this.mSSID.compareTo(o.getSSID());
			
			return this.mBSSID.compareTo(o.getBSSID());
		}
	}
	
	public static class NetworkEntry implements Comparable<NetworkEntry> {
		private String mBSSID;
		private String mSSID;
		private ArrayList<NetworkScanEntry> mItems;
		
		public NetworkEntry(String bssid, String ssid) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
			this.mItems = new ArrayList<NetworkScanEntry>();
		}
		
		public NetworkEntry(String bssid, String ssid, ArrayList<NetworkScanEntry> networks) {
			this(bssid, ssid);
			this.mItems = networks;
		}
		
		public String getBSSID() {
			return mBSSID;
		}
		
		public void setBSSID(String bssid) {
			this.mBSSID = bssid;
		}
		
		public String getSSID() {
			return mSSID;
		}
		
		public void setSSID(String ssid) {
			this.mSSID = ssid;
		}
		
		public ArrayList<NetworkScanEntry> getItems() {
			return mItems;
		}
		
		public void setItems(ArrayList<NetworkScanEntry> items) {
			this.mItems = items;
		}
		
		public NetworkScanEntry getLastEntry() {
			long ts = 0;
			NetworkScanEntry lastEntry = null;
			
			for(NetworkScanEntry entry : mItems) {
				if(entry.getTimestamp() > ts)
					lastEntry = entry;
				ts = entry.getTimestamp();
			}
					
			return lastEntry;
		}

		public String getLastTimestamp() {
			NetworkScanEntry lastEntry = getLastEntry();
			if(lastEntry == null)
				return "Unknown";
			
			return lastEntry.getFormattedTimestamp();
		}
		
		@Override
		public String toString() {
			return String.format("%s (%s)", this.mBSSID, this.mSSID);
		}
		
		public List<SecurityType> getSecurityTypes() {
			if(mItems.size() < 1)
				return (new ArrayList<SecurityType>());
			
			return getLastEntry().getSecurityTypes();
		}
		
		public int getListIcon() {
			List<SecurityType> securityTypes = getSecurityTypes();
			
			if(securityTypes.contains(SecurityType.WPA2))
				return R.drawable.ic_green_wifi;
			
			if(securityTypes.contains(SecurityType.WPA))
				return R.drawable.ic_orange_wifi;
			
			if(securityTypes.contains(SecurityType.WEP))
				return R.drawable.ic_blue_wifi;
			
			if(securityTypes.contains(SecurityType.Open))
				return R.drawable.ic_red_wifi;
			
			return R.drawable.ic_grey_wifi;
		}

		@Override
		public int compareTo(NetworkEntry o) {
			if(!this.mSSID.equals(o.getSSID()))
				return this.mSSID.compareTo(o.getSSID());
			
			return this.mBSSID.compareTo(o.getBSSID());
		}
	}
	
	public NetworkListAdapter(Context context, ArrayList<NetworkEntry> networks) {
		this.mContext = context;
		this.mNetworks = networks;
	}
	
	public void addItem(NetworkScanEntry item, NetworkEntry network) {
		if (!mNetworks.contains(network))
			mNetworks.add(network);

		int index = mNetworks.indexOf(network);
		ArrayList<NetworkScanEntry> ch = mNetworks.get(index).getItems();
		ch.add(item);
		mNetworks.get(index).setItems(ch);
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		ArrayList<NetworkScanEntry> chList = mNetworks.get(groupPosition).getItems();
		return chList.get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkScanEntry child = (NetworkScanEntry) getChild(groupPosition, childPosition);
		
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		
		TextView tvTimestamp = (TextView) view.findViewById(R.id.textview_child_timestamp);
		TextView tvContent = (TextView) view.findViewById(R.id.textview_child_details);
		
		tvTimestamp.setText(child.getFormattedTimestamp());
		tvContent.setText(child.toString());
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		ArrayList<NetworkScanEntry> chList = mNetworks.get(groupPosition).getItems();
		return chList.size();
	}

	public NetworkEntry getGroupByBSSID(String bssid) {
		for(NetworkEntry entry : mNetworks) {
			if(entry.getBSSID().equals(bssid))
				return entry;
		}
		
		return null;
	}
	
	public Object getGroup(int groupPosition) {
		return mNetworks.get(groupPosition);
	}

	public int getGroupCount() {
		return mNetworks.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkEntry group = (NetworkEntry) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}
		
		TextView textViewLastTimestamp = (TextView) view.findViewById(R.id.textview_group_lasttime);
		TextView textViewContent = (TextView) view.findViewById(R.id.textview_group_content);
		
		textViewLastTimestamp.setText(group.getLastTimestamp());
		textViewContent.setText(group.toString());

		Drawable dr = mContext.getResources().getDrawable(group.getListIcon());
		dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
		textViewContent.setCompoundDrawables(dr, null, null, null);
		return view;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}
}