package com.blueodin.wifigraphs;

import android.app.ActionBar;
import android.content.Context;
import com.jjoe64.graphview.GraphView;

public interface WifiGraphInterface {
	public final String TAB_TAG = "graph_fragment";
	public final String TAB_TITLE = "Wifi Graph";
	
	public String getTabTitle();
	public String getTabTag();
	
	public GraphView getGraphView(Context ctx);
	public ActionBar.Tab getActionBarTab(ActionBar actionBar);
}
