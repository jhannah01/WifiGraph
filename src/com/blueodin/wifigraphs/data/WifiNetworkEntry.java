package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.NetworkSecurity.SecurityType;

public class WifiNetworkEntry implements Comparable<WifiNetworkEntry>, Parcelable {
	private int mId = -1;
	protected String mBSSID;
	protected String mSSID;
	protected int mLevel;
	protected int mFrequency;
	protected String mCapabilities;
	protected long mTimestamp;
	
	public static final String KEY_BSSID = "bssid";
	public static final String KEY_SSID = "ssid";
	public static final String KEY_LEVEL = "level";
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_CAPABILITIES = "capabilities";
	public static final String KEY_TIMESTAMP = "timestamp";
	
	public static final String KEY_RESULTS_LIST = "results_list";
	
	public static final int FLAG_PARCEL_ITEM = 1;
	public static final int FLAG_PARCEL_LIST = 2;
	
	public WifiNetworkEntry(Parcel src) {
		this.mBSSID = src.readString();
		this.mSSID = src.readString();
		this.mLevel = src.readInt();
		this.mFrequency = src.readInt();
		this.mCapabilities = src.readString();
		this.mTimestamp = src.readLong();
	}
	
	public WifiNetworkEntry(int id, String bssid, String ssid, int level, int frequency, String capabilities, long timestamp) {
		this(bssid, ssid, level, frequency, capabilities, timestamp);
		this.mId = id;
	}
	
	public WifiNetworkEntry(String bssid, String ssid, int level, int frequency, String capabilities, long timestamp) {
		this.mBSSID = bssid;
		this.mSSID = ssid;
		this.mLevel = level;
		this.mFrequency = frequency;
		this.mCapabilities = capabilities;
		this.mTimestamp = timestamp;
	}

	public WifiNetworkEntry(ScanResult result) {
		this(result.BSSID, result.SSID, result.level, result.frequency, result.capabilities, System.currentTimeMillis());
	}
	
	public WifiNetworkEntry(Cursor cursor) {
		this.mId = cursor.getInt(0);	 // id
		this.mBSSID = cursor.getString(1); // bssid
		this.mSSID = cursor.getString(2); // ssid
    	this.mLevel = cursor.getInt(3);    // level
    	this.mFrequency = cursor.getInt(4);    // frequency
		this.mCapabilities = cursor.getString(5); // capabilities
		this.mTimestamp = cursor.getLong(6); // timestamp
	}
	
	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		this.mId = id; 
	}
	
	public boolean isIdValid() {
		return (this.mId != -1);
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
	
	public int getFrequency() {
		return mFrequency;
	}
	public void setFreqency(int freqency) {
		this.mFrequency = freqency;
	}
	
	public String getCapabilities() {
		return mCapabilities;
	}
	public void setCapabilities(String capabilities) {
		this.mCapabilities = capabilities;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}
	
	public List<SecurityType> getSecurities() {
		return SecurityType.parseCapabilities(this.mCapabilities);
	}
	
	public String getFriendlySecurityInfo() {
		return NetworkSecurity.getFriendlyString(this.mCapabilities);
	}
	
	public String getHumanReadableSecurityInfo() {
		return NetworkSecurity.stringFromCapabilities(this.mCapabilities);
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s) - Level: %d dBm, Frequency: %d MHz, Capabilities: %s", this.mSSID, this.mBSSID, this.mLevel, this.mFrequency, NetworkSecurity.getFriendlyString(this.mCapabilities), this.getFormattedTimestamp());
	}
	
	public String getFormattedTimestamp() {
		if(this.mTimestamp > 0)
			return ((String)DateFormat.format("MMM dd, yyyy h:mmaa", this.mTimestamp));
		
		return "Unknown";
	}

	@Override
	public int compareTo(WifiNetworkEntry o) {
		if(!this.mSSID.equals(o.getSSID()))
			return this.mSSID.compareTo(o.getSSID());
		
		return this.mBSSID.compareTo(o.getBSSID());
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof WifiNetworkEntry))
			return false;
		
		WifiNetworkEntry group = (WifiNetworkEntry)o;
		if(group.getBSSID().equals(this.mBSSID) && group.getSSID().equals(this.mSSID))
			return true;
		
		return false;
	}
	
	public int getIcon() {
		List<NetworkSecurity.SecurityType> securityTypes = this.getSecurities();
		
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

	public Bundle buildBundle() {
		Bundle bundle = new Bundle();
		
		bundle.putString(KEY_BSSID, this.mBSSID);
		bundle.putString(KEY_SSID, this.mSSID);
		bundle.putInt(KEY_LEVEL, this.mLevel);
		bundle.putInt(KEY_FREQUENCY, this.mFrequency);
		bundle.putString(KEY_CAPABILITIES, this.mCapabilities);
		bundle.putLong(KEY_TIMESTAMP, this.mTimestamp);
		
		return bundle;
	}
	
	public static Bundle buildBundleFromList(List<WifiNetworkEntry> results) {
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(KEY_RESULTS_LIST, (ArrayList<? extends Parcelable>) results);
		return bundle;
	}
	
	public static WifiNetworkEntry[] getArrayFromList(List<WifiNetworkEntry> results) {
		WifiNetworkEntry[] resultsArray = new WifiNetworkEntry[results.size()];
		return results.toArray(resultsArray);
	}
	
	public static List<WifiNetworkEntry> getListFromArray(WifiNetworkEntry[] results) {
		List<WifiNetworkEntry> resultsList = new ArrayList<WifiNetworkEntry>();
		
		for(WifiNetworkEntry result : results)
			resultsList.add(result);
		
		return resultsList;
	}
	
	public static List<WifiNetworkEntry> getListFromBundle(Bundle bundle) {
		if((bundle != null) && bundle.containsKey(KEY_RESULTS_LIST))
			return bundle.getParcelableArrayList(KEY_RESULTS_LIST);
		else
			return new ArrayList<WifiNetworkEntry>();
	}
	
	public static List<WifiNetworkEntry> getListFromCursor(Cursor cursor) {
		List<WifiNetworkEntry> results = new ArrayList<WifiNetworkEntry>();
		
		if(cursor == null)
			return results;
		
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			results.add(new WifiNetworkEntry(cursor));
			cursor.moveToNext();
	    }
	    
		cursor.close();
	    
		return results;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mBSSID);
		dest.writeString(this.mSSID);
		dest.writeInt(this.mLevel);
		dest.writeInt(this.mFrequency);
		dest.writeString(this.mCapabilities);
		dest.writeLong(this.mTimestamp);
	}
	
	public static final Parcelable.Creator<WifiNetworkEntry> CREATOR = new Creator<WifiNetworkEntry>() {
		@Override
		public WifiNetworkEntry[] newArray(int size) {
			return new WifiNetworkEntry[size];
		}
		
		@Override
		public WifiNetworkEntry createFromParcel(Parcel src) {
			return new WifiNetworkEntry(src);
		}
	};
}