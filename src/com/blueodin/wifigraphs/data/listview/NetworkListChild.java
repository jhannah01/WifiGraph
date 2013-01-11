package com.blueodin.wifigraphs.data.listview;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.NetworkScanResult;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class NetworkListChild {
	private final static int GRAPH_ID = 500;
	private final NetworkListGroup mParent;
	private final GraphViewSeries mGraphSeries;
	private LineGraphView mGraph = null;
	
	public NetworkListChild(NetworkListGroup parent) {
		this.mParent = parent;
		this.mGraphSeries = new GraphViewSeries(parent.getSSID(), 
				new GraphViewSeriesStyle(R.color.ics_blue, 1), 
				new GraphViewData[1]);
	}
	
	public NetworkListGroup getParent() {
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
		List<NetworkScanResult> results = this.mParent.getResults();
		GraphViewData[] graphData = getSignalData(results);
		
        if(graphData.length < 1)
        	return graphView;
        
		
		double size = (5*60)*1000;
		double start = System.currentTimeMillis()-((15*60)*1000);
		
		this.mGraphSeries.resetData(graphData);
        graphView.addSeries(mGraphSeries);
        
        //graphView.setScalable(true);
        graphView.setViewPort(start, size);
        graphView.setId(GRAPH_ID);
        return graphView;
	}
	
	public void drawView(Context context, View view, ViewGroup parent) {
		NetworkScanResult lastRecord = this.mParent.getLastResult(true);
		
		TextView textViewLevel = (TextView) view.findViewById(R.id.textview_child_level);
		TextView textViewFrequency = (TextView) view.findViewById(R.id.textview_child_frequency);
		TextView textViewCapabilities = (TextView) view.findViewById(R.id.textview_child_capabilities);
		
		textViewLevel.setText(String.format("%d dBm", lastRecord.getLevel()));
		textViewFrequency.setText(String.format("%d MHz", lastRecord.getFreqency()));
		textViewCapabilities.setText(lastRecord.getFriendlySecurityInfo());
		
		final LinearLayout graphHolder = ((LinearLayout)view.findViewById(R.id.layout_child_graph));
		
		if(this.mGraph == null)
			this.mGraph = this.buildGraph(context);
		
		graphHolder.removeAllViews();
		graphHolder.addView(this.mGraph);
	}
	
	public GraphViewData[] getSignalData(List<NetworkScanResult> results) { 
		int idx;
		
		if(results.size() < 1)
			idx = 1;
		else 
			idx = results.size();
		
		GraphViewData[] signalData = new GraphViewData[idx];
		
		int i=0;
		for(NetworkScanResult result : results)
			signalData[i++] = new GraphViewData(result.getTimestamp(), result.getLevel());
		
		return signalData;
	}
	
	public void addGraphData(NetworkScanResult result, boolean scrollToEnd) {
		this.mGraphSeries.appendData(new GraphViewData(result.getTimestamp(), result.getLevel()), scrollToEnd);
	}
}