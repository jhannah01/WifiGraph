package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.net.wifi.ScanResult;
import android.text.format.DateFormat;

public class ScanResultManager {
	private HashMap<ScanResultGroup, List<ScanResultEntry>> mScanResultsHashMap;
	private List<ScanResultGroup> mResultGroups;
		
	public static class ScanResultEntry {
		private String mSSID;
		private String mBSSID;
		private int mLevel = 0;
		private int mFrequency = 0;
		private String mCapabilities = "";
		private long mTimestamp = 0;
		
		public ScanResultEntry(ScanResult result) {
			this.mBSSID = result.BSSID; 
			this.mSSID = result.SSID;
			this.mLevel = result.level;
			this.mFrequency = result.frequency;
			this.mCapabilities = result.capabilities;
			this.mTimestamp = System.currentTimeMillis();
		}
		
		public String getBSSID() {
			return this.mBSSID;
		}
		
		public String getSSID() {
			return this.mSSID;
		}
		
		public int getLevel() {
			return this.mLevel;
		}
		
		public int getFrequency() {
			return this.mFrequency;
		}
		
		public String getCapabilities() {
			return this.mCapabilities;
		}
		
		public long getTimestamp() {
			return this.mTimestamp;
		}

		public String getFormattedTimestamp() {
			if(this.mTimestamp > 0)
				return ((String)DateFormat.format("MMM dd, yyyy h:mmaa", this.mTimestamp));
			
			return "Unknown";
		}


		public void setLevel(int level) {
			this.mLevel = level;
		}
		
		public void setFreqency(int frequency) {
			this.mFrequency = frequency;
		}
		
		public void setCapabilities(String capabilities) {
			this.mCapabilities = capabilities;
		}
		
		public void setTimestamp(long timestamp) {
			this.mTimestamp = timestamp;
		}
		
		@Override
		public String toString() {
			return String.format("%s [BSSID: %s, Signal: %d dBm, Frequency: %d MHz, Capabilities: %s, Timestamp: %s]", this.mSSID, this.mBSSID, this.mLevel, this.mFrequency, this.mCapabilities, getFormattedTimestamp());
		}
	}
	
	public static class ScanResultGroup implements Comparator<ScanResultGroup> {
		private String mBSSID;
		private String mSSID;
		
		public ScanResultGroup(String bssid, String ssid) {
			this.mBSSID = bssid; 
			this.mSSID = ssid;
		}
		
		public String getBSSID() {
			return this.mBSSID;
		}
		
		public String getSSID() {
			return this.mSSID;
		}
		
		@Override
		public String toString() {
			return String.format("% [SSID: %s]", this.mBSSID, this.mSSID); 
		}

		@Override
		public int compare(ScanResultGroup lhs, ScanResultGroup rhs) {
			if(!lhs.getBSSID().equals(rhs.getBSSID()))
				return -1;
			if(!lhs.getSSID().equals(rhs.getSSID()))
				return 1;
								
			return 0;
		}
		
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof ScanResultGroup))
				return super.equals(o);
			
			ScanResultGroup otherGroup = (ScanResultGroup)o;
			if (otherGroup.getBSSID().equals(this.mBSSID) && otherGroup.getSSID().equals(this.mSSID))
				return true;
				
			return false; 
		}
		
		public boolean matchesEntry(ScanResultEntry entry) {
			if(this.mBSSID.equals(entry.getBSSID()) && this.mSSID.equals(entry.getSSID()))
				return true;
			
			return false;
		}
	}
	
	public ScanResultManager() {
		this.mResultGroups = new ArrayList<ScanResultGroup>();
		this.mScanResultsHashMap = new HashMap<ScanResultGroup, List<ScanResultEntry>>();
	}
	
	public void addScanResult(ScanResult result) {
		ScanResultGroup group = new ScanResultGroup(result.BSSID, result.SSID);
		List<ScanResultEntry> entries;
		
		if(!mScanResultsHashMap.containsKey(group))
			entries = mScanResultsHashMap.put(group, new ArrayList<ScanResultEntry>());
		else		
			entries = mScanResultsHashMap.get(group);
		
		entries.add(new ScanResultEntry(result));
	}
	
	public List<ScanResultEntry> getByGroup(String bssid, String ssid) {
		return mScanResultsHashMap.get(new ScanResultGroup(bssid, ssid));
	}
	
	public List<ScanResultEntry> getAllEntries() {
		List<ScanResultEntry> results = new ArrayList<ScanResultManager.ScanResultEntry>();
		
		for(List<ScanResultEntry> entries : mScanResultsHashMap.values())
			results.addAll(entries);

		return results;
	}
	
	public List<ScanResultGroup> getGroups() {
		return mResultGroups;
	}
}
