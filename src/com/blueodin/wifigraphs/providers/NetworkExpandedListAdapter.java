package com.blueodin.wifigraphs.providers;

import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.NetworkSecurity;
import com.blueodin.wifigraphs.data.WifiNetworkEntry;
import com.blueodin.wifigraphs.db.RecordsDataSource;
import com.blueodin.wifigraphs.providers.NetworkResultsAdapter;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NetworkExpandedListAdapter extends BaseExpandableListAdapter {
	private final List<ListGroup> mGroups = new ArrayList<ListGroup>();
	private Context mContext;
	private RecordsDataSource mDataSource;
	
	public NetworkExpandedListAdapter(Context context, RecordsDataSource datasource) {
		super();
		this.mContext = context;
		this.mDataSource = datasource;
	}
	
	@Override
	public ListChild getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).getChild();
	}
	
	@Override
	public int getChildrenCount(int groupPosition) {
		if(getGroup(groupPosition) != null)
				return 1;
		
		return 0;
	}
	
	@Override
	public ListGroup getGroup(int groupPosition) {
		return this.mGroups.get(groupPosition);
	}
	
	public ListGroup getGroup(String bssid, String ssid) {
		for (ListGroup group : this.mGroups) {
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
		ListChild child = (ListChild) getChild(groupPosition, childPosition);
		
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.expandlist_child_item, null);
		}
	
		child.drawView(this.mContext, view, parent);
		
		return view;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isLastChild, View view, ViewGroup parent) {
		ListGroup group = (ListGroup) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inf.inflate(R.layout.expandlist_group_item, null);
		}

		group.drawView(this.mContext, view, parent);
		
		return view;
	}
	
	public class ListGroup {
		private String mBSSID;
		private String mSSID;
		private final ListChild mChild;
		private List<WifiNetworkEntry> mResults;
		
		public ListGroup(String bssid, String ssid, List<WifiNetworkEntry> results) {
			this.mBSSID = bssid;
			this.mSSID = ssid;
			this.mResults = results;
			this.mChild = new ListChild(this);
		}
		
		public DataSetObserver notifyDataSetChanged = new DataSetObserver() {
			public void onChanged() {
				mChild.updateGraph();
			}
		};
		
		public String getBSSID() {
			return this.mBSSID;
		}
		
		public String getSSID() {
			return this.mSSID;
		}
		
		public ListChild getChild() {
			return this.mChild;
		}
		
		public boolean addResult(WifiNetworkEntry result) {
			this.mResults.add(result);
			//this.mChild.addGraphData(result, true);
			return true;
		}
		
		public List<WifiNetworkEntry> getResults() {
			return this.mResults;
		}
		
		public WifiNetworkEntry getLastResult(boolean byTimestamp) {
			if(this.mResults.size() < 1)
				return null;
			
			if(!byTimestamp)
				return this.mResults.get(this.mResults.size()-1);
			
			WifiNetworkEntry lastResult = this.mResults.get(0);
			
			for(WifiNetworkEntry result : this.mResults) {
				if(result.getTimestamp() > lastResult.getTimestamp())
					lastResult = result;
			}
			
			return lastResult;
		}
		
		public void drawView(Context context, View view, ViewGroup parent) {
			WifiNetworkEntry lastResult = (WifiNetworkEntry)getLastResult(true);
			
			TextView textViewRecords = (TextView) view.findViewById(R.id.textview_group_records);
			TextView textViewLastSeen = (TextView) view.findViewById(R.id.textview_group_lastseen);
			TextView textViewBSSID = (TextView) view.findViewById(R.id.textview_group_bssid);
			TextView textViewSSID = (TextView) view.findViewById(R.id.textview_group_ssid);
			ImageView imageViewIcon = (ImageView) view.findViewById(R.id.imageview_group_icon);
			
			textViewRecords.setText(String.format("%d", this.mResults.size()));
			textViewBSSID.setText(this.mBSSID);
			textViewSSID.setText(this.mSSID);
			textViewLastSeen.setText(lastResult.getFormattedTimestamp());
			
			Drawable dr = context.getResources().getDrawable(getListIcon());
			//dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
			imageViewIcon.setImageDrawable(dr);
		}
		
		public int getListIcon() {
			List<NetworkSecurity.SecurityType> securityTypes = getLastResult(false).getSecurities();
			
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

		public void updateGraph(WifiNetworkEntry entry) {
			this.mChild.addEntry(entry);
			
		}
	}
	
	public class ListChild {
		private final ListGroup mParent;
		private final GraphViewSeries mGraphSeries;
		private final LineGraphView mGraph;
		
		public ListChild(ListGroup parent) {
			this.mParent = parent;
			this.mGraphSeries = new GraphViewSeries(parent.getSSID(), 
					new GraphViewSeriesStyle(R.color.ics_blue, 1), 
					new GraphViewData[1]);
			this.mGraph = buildGraph(mContext);
		}
		
		public void addEntry(WifiNetworkEntry entry) {
			this.mGraphSeries.appendData(new GraphViewData(entry.getTimestamp(), entry.getLevel()), true);
			
		}

		public ListGroup getParent() {
			return this.mParent;
		}
		
		public void updateGraph() {
			if(this.mGraph == null)
				return;
			
			//this.mGraphSeries.resetData(this.getSignalData(this.mParent.getResults()));
		}
		
		
		
		protected LineGraphView buildGraph(Context context) {
			LineGraphView graphView = new LineGraphView(context, "Historical Graph for " + this.mParent.getSSID()) {
				protected String formatLabel(double value, boolean isValueX) {
	                if (!isValueX) {
	                    return String.format("%.02f dBm", value);
	                } else {
	                	return (String)DateFormat.format("h:mm:ss aa", (long) value);
	                }
	            }
			};
			
			Resources res = context.getResources();
			
			GraphViewStyle graphViewStyle = graphView.getGraphViewStyle();
			graphViewStyle.setGridColor(res.getColor(R.color.bar_background_color));
			graphView.setBackgroundColor(context.getResources().getColor(R.color.primary_bg_color));
			graphView.getGraphViewStyle().setHorizontalLabelsColor(res.getColor(R.color.secondary_text_color));
			graphView.getGraphViewStyle().setVerticalLabelsColor(res.getColor(R.color.alt_text_color));
			graphView.setDrawBackground(true);
			List<WifiNetworkEntry> results = this.mParent.getResults();
			GraphViewData[] graphData = getSignalData(results);
			
	        if(graphData.length < 1)
	        	return graphView;
	        
			
			double size = (5*60)*1000;
			double start = System.currentTimeMillis()-((15*60)*1000);
			
			this.mGraphSeries.resetData(graphData);
	        graphView.addSeries(mGraphSeries);
	        
	        //graphView.setScalable(true);
	        graphView.setViewPort(start, size);
	        return graphView;
		}
		
		public void drawView(Context context, View view, ViewGroup parent) {
			WifiNetworkEntry lastRecord = this.mParent.getLastResult(true);
			
			TextView textViewLevel = (TextView) view.findViewById(R.id.textview_child_level);
			TextView textViewFrequency = (TextView) view.findViewById(R.id.textview_child_frequency);
			TextView textViewCapabilities = (TextView) view.findViewById(R.id.textview_child_capabilities);
			
			textViewLevel.setText(String.format("%d dBm", lastRecord.getLevel()));
			textViewFrequency.setText(String.format("%d MHz", lastRecord.getFrequency()));
			textViewCapabilities.setText(lastRecord.getFriendlySecurityInfo());
			
			final LinearLayout graphHolder = ((LinearLayout)view.findViewById(R.id.layout_child_graph));
			
			graphHolder.addView(this.mGraph);
		}
		
		public GraphViewData[] getSignalData(List<WifiNetworkEntry> results) { 
			int idx;
			
			if(results.size() < 1)
				idx = 1;
			else 
				idx = results.size();
			
			GraphViewData[] signalData = new GraphViewData[idx];
			
			int i=0;
			for(WifiNetworkEntry result : results)
				signalData[i++] = new GraphViewData(result.getTimestamp(), result.getLevel());
			
			return signalData;
		}
		
		public void addGraphData(WifiNetworkEntry result, boolean scrollToEnd) {
			this.mGraphSeries.appendData(new GraphViewData(result.getTimestamp(), result.getLevel()), scrollToEnd);
		}
	}

	public void updateGraph(WifiNetworkEntry entry) {
		ListGroup group = getGroup(entry.getBSSID(), entry.getSSID());
		if(group != null)
			group.updateGraph(entry);
	}
}