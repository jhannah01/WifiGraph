package com.blueodin.wifigraphs.data;

import java.util.List;
import android.net.wifi.ScanResult;
import android.text.format.DateFormat;
import com.blueodin.wifigraphs.data.NetworkSecurity.SecurityType;

public class NetworkScanRecord implements Comparable<NetworkScanRecord> {
	private int mId = -1;
	private String mBSSID;
	private String mSSID;
	private int mLevel;
	private int mFreqency;
	private String mCapabilities;
	private long mTimestamp;
	
	public NetworkScanRecord(int id, String bssid, String ssid, int level, int frequency, String capabilities, long timestamp) {
		this(bssid, ssid, level, frequency, capabilities, timestamp);
		this.mId = id;
	}
	
	public NetworkScanRecord(String bssid, String ssid, int level, int frequency, String capabilities, long timestamp) {
		this.mBSSID = bssid;
		this.mSSID = ssid;
		this.mLevel = level;
		this.mFreqency = frequency;
		this.mCapabilities = capabilities;
		this.mTimestamp = timestamp;
	}

	public NetworkScanRecord(ScanResult result) {
		this(result.BSSID, result.SSID, result.level, result.frequency, result.capabilities, System.currentTimeMillis());
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
	
	public long getTimestamp() {
		return mTimestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.mTimestamp = timestamp;
	}
	
	public List<SecurityType> getSecurityTypes() {
		return SecurityType.parseCapabilities(this.mCapabilities);
	}
	
	public String getFriendlySecurityInfo() {
		NetworkSecurity security = new NetworkSecurity(this.mCapabilities);
		return security.getFriendly();
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s) - Level: %d dBm, Frequency: %d MHz, Capabilities: %s", this.mSSID, this.mBSSID, this.mLevel, this.mFreqency, this.getFriendlySecurityInfo(), this.getFormattedTimestamp());
	}
	
	public String getFormattedTimestamp() {
		if(this.mTimestamp > 0)
			return ((String)DateFormat.format("MMM dd, yyyy h:mmaa", this.mTimestamp));
		
		return "Unknown";
	}

	@Override
	public int compareTo(NetworkScanRecord o) {
		if(!this.mSSID.equals(o.getSSID()))
			return this.mSSID.compareTo(o.getSSID());
		
		return this.mBSSID.compareTo(o.getBSSID());
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof NetworkScanRecord))
			return false;
		
		NetworkScanRecord group = (NetworkScanRecord)o;
		if(group.getBSSID().equals(this.mBSSID) && group.getSSID().equals(this.mSSID))
			return true;
		
		return false;
	}
}