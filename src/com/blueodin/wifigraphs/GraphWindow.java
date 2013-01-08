package com.blueodin.wifigraphs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public class GraphWindow extends StandOutWindow {
    @Override
    public String getAppName() {
        return getString(R.string.app_name);
    }

    @Override
    public int getAppIcon() {
        return R.drawable.ic_launcher;
    }

    @Override
    public StandOutLayoutParams getParams(int id, Window window) {
        return new StandOutLayoutParams(id, 800, 800, 
        		StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER, 800, 600);
    }

    @Override
    public int getFlags(int id) {
        return super.getFlags(id) | StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE | StandOutFlags.FLAG_DECORATION_SYSTEM;
    }

    @Override
    public void createAndAttachView(int id, FrameLayout frame) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.graph_window, frame, true);

        LinearLayout graphLayout = (LinearLayout)frame.findViewById(R.id.graph_layout);

        // init example series data
        GraphViewSeries graphDataOne = new GraphViewSeries("AP #1", new GraphViewSeriesStyle(Color.rgb(200, 50, 0), 5), new GraphViewData[] {
            new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d), new GraphViewData(3, 2.5d), new GraphViewData(4, 1.0d),
            new GraphViewData(5, 1.8d), new GraphViewData(6, 2.5d), new GraphViewData(7, 3.0d), new GraphViewData(8, 2.8d)
        });

        GraphViewSeries graphDataTwo = new GraphViewSeries("AP #2", new GraphViewSeriesStyle(Color.rgb(90, 250, 0), 5), new GraphViewData[]{
                new GraphViewData(1, 4.0d), new GraphViewData(2, 4.5d), new GraphViewData(3, 3.0d), new GraphViewData(4, 2.1d),
                new GraphViewData(5, 6.0d), new GraphViewData(6, 4.0d), new GraphViewData(7, 2.2d), new GraphViewData(8, 3.2d)
        });

        LineGraphView graphView = new LineGraphView(this, "Wifi Graph") {
            @SuppressLint("DefaultLocale")
			@Override
            protected String formatLabel(double value, boolean isValueX) {
                if (!isValueX) {
                    return String.format("%1$.2f dBm", value);
                } else return super.formatLabel(value, isValueX);
            }
        };

        graphView.addSeries(graphDataOne);
        graphView.addSeries(graphDataTwo);

        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);

        graphView.setViewPort(1, 4);
        graphView.setScalable(true);

        graphView.setBackgroundColor(Color.rgb(25,25,25));

        graphLayout.addView(graphView);
    }
}
