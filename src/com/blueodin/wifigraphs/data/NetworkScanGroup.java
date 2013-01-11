package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.NetworkSecurity;

public class NetworkScanGroup implements Comparable<NetworkScanGroup>, Collection<NetworkScanResult> {
	private String mBSSID;
	private String mSSID;
	private List<NetworkScanResult> mItems;
	
	public NetworkScanGroup(String bssid, String ssid) {
		this.mBSSID = bssid;
		this.mSSID = ssid;
		this.mItems = new ArrayList<NetworkScanResult>();
	}
	
	public NetworkScanGroup(String bssid, String ssid, List<NetworkScanResult> items) {
		this(bssid, ssid);
		this.mItems = items;
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
	
	public List<NetworkScanResult> getRecords() {
		return this.mItems;
	}
	
	public void setRecords(List<NetworkScanResult> items) {
		this.mItems = items;
	}
	
	public NetworkScanResult getLastEntry() {
		long ts = 0;
		NetworkScanResult lastEntry = null;
		
		for(NetworkScanResult entry : mItems) {
			if(entry.getTimestamp() > ts)
				lastEntry = entry;
			ts = entry.getTimestamp();
		}
				
		return lastEntry;
	}

	public String getLastTimestamp() {
		NetworkScanResult lastEntry = getLastEntry();
		if(lastEntry == null)
			return "Unknown";
		
		return lastEntry.getFormattedTimestamp();
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", this.mBSSID, this.mSSID);
	}
	
	public List<NetworkSecurity.SecurityType> getSecurityTypes() {
		if(mItems.size() < 1)
			return (new ArrayList<NetworkSecurity.SecurityType>());
		
		return getLastEntry().getSecurityTypes();
	}
	
	public int getListIcon() {
		List<NetworkSecurity.SecurityType> securityTypes = getSecurityTypes();
		
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
		
		NetworkScanGroup other = (NetworkScanGroup)o;
		if(other.getBSSID().equals(mBSSID) && other.getSSID().equals(mSSID))
			return true;
		
		return false;
	}

	@Override
	public boolean add(NetworkScanResult object) {
		return this.mItems.add(object);
	}

	@Override
	public boolean addAll(Collection<? extends NetworkScanResult> collection) {
		return this.mItems.addAll(collection);
	}

	@Override
	public void clear() {
		this.mItems.clear();
	}

	@Override
	public boolean contains(Object object) {
		return this.mItems.contains(object);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return this.mItems.containsAll(collection);
	}

	@Override
	public boolean isEmpty() {
		return this.mItems.isEmpty();
	}

	@Override
	public Iterator<NetworkScanResult> iterator() {
		return this.mItems.iterator();
	}

	@Override
	public boolean remove(Object object) {
		return this.mItems.remove(object);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return this.mItems.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return this.mItems.retainAll(collection);
	}

	@Override
	public int size() {
		return this.mItems.size();
	}

	@Override
	public Object[] toArray() {
		return this.mItems.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return this.mItems.toArray(array);
	}
}