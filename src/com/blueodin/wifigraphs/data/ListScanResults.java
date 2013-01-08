package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;

import com.blueodin.wifigraphs.data.ScanResultManager.ScanResultEntry;
import com.blueodin.wifigraphs.data.ScanResultManager.ScanResultGroup;

public class ListScanResults extends ArrayList<ScanResultEntry> {
	private static final long serialVersionUID = 6951083493526177881L;
	
	public void add(ScanResult result) {
		this.add(new ScanResultEntry(result));
	}
	
	public List<ScanResultEntry> getByGroup(ScanResultGroup group) {
		List<ScanResultEntry> results = new ArrayList<ScanResultEntry>();
		for(ScanResultEntry entry : this) {
			if(group.matchesEntry(entry))
				results.add(entry);
		}
		
		return results;
	}
}
