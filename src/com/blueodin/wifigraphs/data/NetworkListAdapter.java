package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.NetworkSecurity.SecurityType;

import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.net.wifi.ScanResult;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkListAdapter extends BaseExpandableListAdapter {	
	private Context mContext;
	private ArrayList<NetworkScanGroup> mGroups;
	public static class NetworkResultEntry implements Comparable<NetworkScanGroup> {
		private String mBSSID;
		private String mSSID;
		private int mLevel = 0;
		private long mTimestamp = 0;
		private int mFreqency = 0;
		private String mCapabilities = "";
		
		public NetworkResultEntry(String bssid, String ssid) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
		}
		
		public NetworkResultEntry(ScanResult result) {
			this(result.BSSID, result.SSID, result.level, result.capabilities, result.frequency);
			
			/*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
				this.mTimestamp = result.timestamp;*/
		}
		
		public NetworkResultEntry(String bssid, String ssid, int level, String capabilities, int freqency) {
			this(bssid, ssid);
			this.mLevel = level;
			this.mCapabilities = capabilities;
			this.mFreqency = freqency;
			this.mTimestamp = System.currentTimeMillis();
		}
		
		public NetworkResultEntry(String bssid, String ssid, int level, String capabilities, int freqency, long timestamp) {
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
			return String.format("Frequency: %d MHz, SSID: %s, Capabilities: %s", this.mFreqency, this.mLevel, this.mSSID, this.getHumanReadableSecurityInfo());
		}
		
		public String getFormattedTimestamp() {
			if(this.mTimestamp > 0)
				return ((String)DateFormat.format("MMM dd, yyyy h:mmaa", this.mTimestamp));
			
			return "Unknown";
		}

		@Override
		public int compareTo(NetworkScanGroup o) {
			if(!this.mSSID.equals(o.getSSID()))
				return this.mSSID.compareTo(o.getSSID());
			
			return this.mBSSID.compareTo(o.getBSSID());
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof NetworkScanGroup))
				return false;
			
			NetworkScanGroup group = (NetworkScanGroup)o;
			if(group.getBSSID().equals(this.mBSSID) && group.getSSID().equals(this.mSSID))
				return true;
			
			return false;
		}
	}
	
	public static class NetworkScanGroup implements Comparable<NetworkScanGroup> {
		private String mBSSID;
		private String mSSID;
		private ArrayList<NetworkResultEntry> mItems;
		
		public NetworkScanGroup(String bssid, String ssid) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
			this.mItems = new ArrayList<NetworkResultEntry>();
		}
		
		public NetworkScanGroup(String bssid, String ssid, ArrayList<NetworkResultEntry> networks) {
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
		
		public ArrayList<NetworkResultEntry> getItems() {
			return mItems;
		}
		
		public void setItems(ArrayList<NetworkResultEntry> items) {
			this.mItems = items;
		}
		
		public NetworkResultEntry getLastEntry() {
			long ts = 0;
			NetworkResultEntry lastEntry = null;
			
			for(NetworkResultEntry entry : mItems) {
				if(entry.getTimestamp() > ts)
					lastEntry = entry;
				ts = entry.getTimestamp();
			}
					
			return lastEntry;
		}

		public String getLastTimestamp() {
			NetworkResultEntry lastEntry = getLastEntry();
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
		public int compareTo(NetworkScanGroup o) {
			if(!this.mSSID.equals(o.getSSID()))
				return this.mSSID.compareTo(o.getSSID());
			
			return this.mBSSID.compareTo(o.getBSSID());
		}
	}
	
	public NetworkListAdapter(Context context, ArrayList<NetworkScanGroup> networks) {
		this.mContext = context;
		this.mGroups = networks;
	}
	
	public void addItem(NetworkResultEntry item, NetworkScanGroup network) {
		if (!mGroups.contains(network))
			mGroups.add(network);

		int index = mGroups.indexOf(network);
		ArrayList<NetworkResultEntry> ch = mGroups.get(index).getItems();
		ch.add(item);
		mGroups.get(index).setItems(ch);
	}
	
	public NetworkResultEntry getChild(int groupPosition, int childPosition) {
		ArrayList<NetworkResultEntry> chList = mGroups.get(groupPosition).getItems();
		return chList.get(childPosition);
	}
	
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkResultEntry child = (NetworkResultEntry) getChild(groupPosition, childPosition);
		
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandlist_child_item, null);
		}
		
		//TextView textViewTimestamp = (TextView) view.findViewById(R.id.textview_child_timestamp);
		//textViewTimestamp.setText(child.getFormattedTimestamp());
		//TextView textViewCount = (TextView) view.findViewById(R.id.textview_child_count);
		//textViewCount.setText(String.format("#%d",childPosition));
		
		TextView textViewDetails = (TextView) view.findViewById(R.id.textview_child_details);
		textViewDetails.setText(child.toString());
		
		TextView textViewLevel = (TextView) view.findViewById(R.id.textview_child_level);
		textViewLevel.setText(String.format("%d dBm", child.getLevel()));
		
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		ArrayList<NetworkResultEntry> chList = mGroups.get(groupPosition).getItems();
		return chList.size();
	}

	public List<NetworkResultEntry> getAllChildren() {
		List<NetworkResultEntry> results = new ArrayList<NetworkListAdapter.NetworkResultEntry>();
		
		for(NetworkScanGroup group : mGroups)
			results.addAll(group.getItems());
		
		return results;
	}
	
	public NetworkScanGroup getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}
	
	public NetworkScanGroup getGroup(String bssid, String ssid) {
		for(NetworkScanGroup group : mGroups)
			if(group.getBSSID().equals(bssid) && group.getSSID().equals(ssid))
				return group;
		
		return null;
	}
	
	public int getGroupCount() {
		return mGroups.size();
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	public NetworkScanGroup getGroupByBSSID(String bssid) {
		for(NetworkScanGroup entry : mGroups) {
			if(entry.getBSSID().equals(bssid))
				return entry;
		}
		
		return null;
	}
	
	public NetworkScanGroup getGroupBySSID(String ssid) {
		for(NetworkScanGroup entry : mGroups) {
			if(entry.getSSID().equals(ssid))
				return entry;
		}
		
		return null;
	}
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkScanGroup group = (NetworkScanGroup) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}
		
		TextView textViewLastSeen = (TextView) view.findViewById(R.id.textview_group_lastseen);
		TextView textViewBSSID = (TextView) view.findViewById(R.id.textview_group_bssid);
		TextView textViewSSID = (TextView) view.findViewById(R.id.textview_group_ssid);
		TextView textViewRecords = (TextView) view.findViewById(R.id.textview_group_records);
		ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageview_group_icon);
		
		textViewBSSID.setText(group.getBSSID());
		textViewSSID.setText(group.getSSID());
		textViewRecords.setText(String.format("%d", getChildrenCount(groupPosition)));
		textViewLastSeen.setText(group.getLastTimestamp());
		
		Drawable dr = mContext.getResources().getDrawable(group.getListIcon());
		dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
		imageViewIcon.setImageDrawable(dr);
		
		return view;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}