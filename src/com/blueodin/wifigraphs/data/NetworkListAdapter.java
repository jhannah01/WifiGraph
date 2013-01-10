package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.webkit.WebView.FindListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NetworkListAdapter extends BaseExpandableListAdapter {	
	protected final Context mContext;
	private List<NetworkListGroup> mGroups = new ArrayList<NetworkListGroup>();
	private List<NetworkScanRecord> mRecords = new ArrayList<NetworkScanRecord>();
	
	public class NetworkListGroup {
		private String mBSSID;
		private String mSSID;
		private List<NetworkScanRecord> mResults = new ArrayList<NetworkScanRecord>();
		private NetworkListChild mChild;
		
		public NetworkListGroup(String bssid, String ssid) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
			this.mChild = new NetworkListChild(bssid, ssid, this);
		}
		
		public String getBSSID() {
			return mBSSID;
		}
		
		public String getSSID() {
			return mSSID;
		}
		
		public NetworkListChild getChild() {
			return mChild;
		}
		
		public void addRecord(NetworkScanRecord record) {
			this.mResults.add(record);
		}
		
		public List<NetworkScanRecord> getResults() {
			return this.mResults;
		}
		
		public void drawView(View view, ViewGroup parent) {
			
		}
		
		public int getListIcon() {
			List<NetworkSecurity.SecurityType> securityTypes = getLastUpdatedRecord(this).getSecurityTypes();
			
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
	
	public class NetworkListChild {
		private String mBSSID;
		private String mSSID;
		private NetworkListGroup mParent;
		private final LineGraphView mGraph;
		private final GraphViewSeries mGraphSeries;
		
		public NetworkListChild(String bssid, String ssid, NetworkListGroup parent) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
			this.mParent = parent;
			this.mGraphSeries = new GraphViewSeries(getSSID(), new GraphViewSeriesStyle(Color.rgb(200, 50, 0), 5), getSignalData(parent.getResults()));
			
			this.mGraph = new LineGraphView(mContext, "Historical Graph for " + ssid) {
				protected String formatLabel(double value, boolean isValueX) {
	                if (!isValueX) {
	                    return String.format("%f dBm", value);
	                } else {
	                	return (String)DateFormat.format("h:mm:ss", (long) value);
	                }
	            }
			};
			mGraph.setBackgroundColor(Color.rgb(25,25,25));
			mGraph.addSeries(mGraphSeries);
		}
		
		public String getBSSID() {
			return mBSSID;
		}
		
		public String getSSID() {
			return mSSID;
		}
		
		public NetworkListGroup getParent() {
			return mParent;
		}
		
		public void drawView(int groupPosition, View view) {
			NetworkScanRecord lastRecord = getLastUpdatedRecord(this.getParent());
			
			TextView textViewLevel = (TextView) view.findViewById(R.id.textview_child_level);
			textViewLevel.setText(String.format("%d dBm", lastRecord.getLevel()));
			
			TextView textViewFrequency = (TextView) view.findViewById(R.id.textview_child_frequency);
			textViewFrequency.setText(String.format("%d MHz", lastRecord.getFreqency()));
			
			TextView textViewCapabilities = (TextView) view.findViewById(R.id.textview_child_capabilities);
			textViewCapabilities.setText(lastRecord.getFriendlySecurityInfo());
			
			TextView textViewRecords = (TextView) view.findViewById(R.id.textview_child_records);
			textViewRecords.setText(String.format("%d", this.getParent().getResults().size()));
			
			LinearLayout graphHolder = ((LinearLayout)view.findViewById(R.id.layout_child_graph));
			graphHolder.removeAllViews();
			graphHolder.addView(mGraph);
		}
		
		public GraphViewData[] getSignalData(List<NetworkScanRecord> records) {
			GraphViewData[] signalData = new GraphViewData[records.size()];
			int i=0;
			for(NetworkScanRecord record : records) {
				signalData[i] = new GraphViewData(record.getTimestamp(), record.getLevel());
				i++;
			}
			
			return signalData;
		}
		
		public void updateGraph(List<NetworkScanRecord> results) {			
			GraphViewData[] signalData = getSignalData(results);
			this.mGraphSeries.resetData(signalData);
			
			double start, size = 120*1000;
			
			if(signalData.length < 9)
	        	start = signalData[0].valueX;
	        else
	        	start = signalData[signalData.length-9].valueX;
	        
	        mGraph.setViewPort(start, size);
		}
	}
	
	public NetworkListAdapter(Context context) {
		super();
		mContext = context;
	}
	
	public NetworkListAdapter(Context context, List<NetworkScanRecord> records) {
		this(context);
		
		for(NetworkScanRecord record : records)
			addRecord(record);
	}
	
	public void addRecord(NetworkScanRecord record) {
		String bssid = record.getBSSID(), ssid = record.getSSID();
		NetworkListGroup group = getGroup(bssid, ssid);
		
		this.mRecords.add(record);
		
		if(group == null) {
			group = new NetworkListGroup(bssid, ssid);
			this.mGroups.add(group);
		}
		
		group.addRecord(record);
		notifyDataSetChanged();
	}
	
	public List<NetworkScanRecord> getRecords(NetworkListGroup group) {
		return getRecords(group.getBSSID(), group.getSSID());
	}
	
	public List<NetworkScanRecord> getRecords(String bssid, String ssid) {
		List<NetworkScanRecord> results = new ArrayList<NetworkScanRecord>();
		
		for(NetworkScanRecord record : this.mRecords) {
			if((record.getBSSID() == bssid) && (record.getSSID() == ssid))
				results.add(record);
		}
		
		return results;
	}
	
	@Override
	public NetworkListChild getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).mChild;
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		if(getGroup(groupPosition) != null)
				return 1;
		
		return 0;
	}
	
	@Override
	public NetworkListGroup getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}
	
	public NetworkListGroup getGroup(String bssid, String ssid) {
		for (NetworkListGroup group : mGroups) {
			if(group.getBSSID().equals(bssid) && group.getSSID().equals(ssid))
				return group;
		}
		
		return null;
	}
	
	@Override
	public int getGroupCount() {
		return mGroups.size();
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
	
		child.drawView(groupPosition, view);
		child.updateGraph(child.getParent().getResults());
		
		return view;
	}
	
	public NetworkScanRecord getLastUpdatedRecord(NetworkListGroup group) {
		return getLastUpdatedRecord(group.getBSSID(), group.getSSID());
	}
	
	public NetworkScanRecord getLastUpdatedRecord(String bssid, String ssid) {
		if(this.mRecords.size() < 1)
			return null;
		
		return this.mRecords.get(this.mRecords.size()-1);
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		NetworkListGroup group = (NetworkListGroup) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}

		TextView textViewLastSeen = (TextView) view.findViewById(R.id.textview_group_lastseen);
		TextView textViewBSSID = (TextView) view.findViewById(R.id.textview_group_bssid);
		TextView textViewSSID = (TextView) view.findViewById(R.id.textview_group_ssid);
		
		ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageview_group_icon);
		
		textViewBSSID.setText(group.getBSSID());
		textViewSSID.setText(group.getSSID());
		textViewLastSeen.setText(getLastUpdatedRecord(group).getFormattedTimestamp());
		
		Drawable dr = mContext.getResources().getDrawable(group.getListIcon());
		dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
		imageViewIcon.setImageDrawable(dr);
		
		group.drawView(view, parent);
		
		return view;
	}
}