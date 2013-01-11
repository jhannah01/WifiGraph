package com.blueodin.wifigraphs.data;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.listview.NetworkListChild;
import com.blueodin.wifigraphs.data.listview.NetworkListGroup;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;

public class NetworkListAdapter extends BaseExpandableListAdapter {	
	protected final Context mContext;
	private final List<NetworkListGroup> mGroups = new ArrayList<NetworkListGroup>();
	
	public NetworkListAdapter(Context context) {
		super();
		this.mContext = context;
	}
	
	public NetworkListAdapter(Context context, List<NetworkScanResult> results) {
		this(context);
		addResults(results);
	}
	
	public boolean addResult(NetworkScanResult result) {
		String bssid = result.getBSSID();
		String ssid = result.getSSID();
		
		NetworkListGroup group = getGroup(bssid, ssid);
		
		if(group == null) {
			group = new NetworkListGroup(bssid, ssid);
			//registerDataSetObserver(group.notifyDataSetChanged);
			this.mGroups.add(group);
			notifyDataSetChanged();
		}
		
		group = this.mGroups.get(this.mGroups.indexOf(group));
		group.addResult(result);
		
		return true;
	}
	
	public void addResults(List<NetworkScanResult> results) {
		for(NetworkScanResult result : results)
			addResult(result);
	}
	
	public List<NetworkScanResult> getResults(String bssid, String ssid) {
		NetworkListGroup group = getGroup(bssid, ssid);
		
		if(group == null)
			return new ArrayList<NetworkScanResult>();
		
		return group.getResults();
	}
	
	@Override
	public NetworkListChild getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).getChild();
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		if(getGroup(groupPosition) != null)
				return 1;
		
		return 0;
	}
	
	@Override
	public NetworkListGroup getGroup(int groupPosition) {
		return this.mGroups.get(groupPosition);
	}
	
	public NetworkListGroup getGroup(String bssid, String ssid) {
		for (NetworkListGroup group : this.mGroups) {
			if(group.getBSSID().equals(bssid) && group.getSSID().equals(ssid))
				return group;
		}
		
		return null;
	}
	
	@Override
	public int getGroupCount() {
		return this.mGroups.size();
	}
	
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}
	
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkListChild child = (NetworkListChild) getChild(groupPosition, childPosition);
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.expandlist_child_item, null);
		}
	
		child.drawView(this.mContext, view, parent);
		
		return view;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkListGroup group = (NetworkListGroup) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}

		group.drawView(this.mContext, view, parent);
		
		return view;
	}
}