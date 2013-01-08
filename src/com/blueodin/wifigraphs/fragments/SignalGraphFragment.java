package com.blueodin.wifigraphs.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;

public class SignalGraphFragment extends GraphFragmentBase {
	public static final String TAB_TAG = "signal_graph";
	public static final String TAB_TITLE = "Signal Graph";
	
	@Override
	public GraphView getGraphView(Context ctx) {
		GraphViewSeries graphData = new GraphViewSeries("AP #1", new GraphViewSeriesStyle(Color.rgb(200, 50, 0), 5), new GraphViewData[] {
            new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d), new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d),
            new GraphViewData(5, 1.8d), new GraphViewData(6, 2.5d), new GraphViewData(7, 3.0d), new GraphViewData(8, 2.8d)
        });

        BarGraphView graphView = new BarGraphView(ctx, getTabTitle()) {
            @SuppressLint("DefaultLocale")
			@Override
            protected String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return String.format("%1$.2f dBm", value);
                } else return super.formatLabel(value, isValueX);
            }
        };

        graphView.addSeries(graphData);
        
        graphView.setViewPort(1, 4);
        graphView.setScalable(true);

        graphView.setBackgroundColor(Color.rgb(25,25,25));
        
		return graphView;
	}

	@Override
	public String getTabTitle() {
		return TAB_TITLE;
	}
	
	@Override
	public String getTabTag() {
		return TAB_TAG;
	}
}
