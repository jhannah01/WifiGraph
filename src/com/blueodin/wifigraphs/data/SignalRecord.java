package com.blueodin.wifigraphs.data;

public class SignalRecord {
	private int mId;
	private long mTime;
	private int mValue;
	private String mSSID;
	private String mBSSID;
	
	public SignalRecord() {
		this.mId = 0;
		this.mTime = 0;
		this.mValue = 0;
		this.mSSID = "";
	}
	
	public SignalRecord(int id, long time, int value, String ssid, String bssid) {
		this.mId = id;
		this.mTime = time;
		this.mValue = value;
		this.mSSID = ssid;
	}
	
	public void setValues(int id, long time, int value, String ssid, String bssid) {
		this.mId = id;
		this.mTime = time;
		this.mValue = value;
		this.mSSID = ssid;
		this.mBSSID = bssid;
	}
	
	public int getId() {
		return this.mId;
	}
	
	public long getTime() {
		return this.mTime;
	}

	public int getValue() {
		return this.mValue;
	}
	
	public String getSSID() {
		return this.mSSID;
	}
	
	public String getBSSID() {
		return this.mBSSID;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s) @ %d dBm", this.mSSID, this.mBSSID, this.mValue);
	}
}
