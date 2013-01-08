package com.blueodin.wifigraphs.fragments;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class ChannelGraphFragment extends GraphFragmentBase {
	public static final String TAB_TAG = "channel_graph";
	public static final String TAB_TITLE = "Channel Graph";

	@Override
	public GraphView getGraphView(Context ctx) {
		BarGraphView graphView = new BarGraphView(ctx, getTabTitle());
		ArrayList<GraphViewData> graphData = new ArrayList<GraphViewData>();
		Random rnd = new Random(System.currentTimeMillis());
		
		for(int i=1 ; i <= 12 ; i++)
			graphData.add(new GraphViewData(i, rnd.nextInt(15)));
		
		GraphViewSeries graphSeries = new GraphViewSeries("Channels", new GraphViewSeriesStyle(Color.GREEN, 1), (GraphViewData[]) graphData.toArray(new GraphViewData[graphData.size()]));
		graphView.addSeries(graphSeries);
        
		graphView.setViewPort(1, 6);
		graphView.setScalable(true);
		
		graphView.setBackgroundColor(Color.rgb(25,25,25));
		
		return graphView;
	}
	
	@Override
	public String getTabTitle() {
		return TAB_TITLE;
	}
	
	public String getTabTag() {
		return TAB_TAG;
	}
}
