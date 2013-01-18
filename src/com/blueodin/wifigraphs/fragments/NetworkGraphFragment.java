package com.blueodin.wifigraphs.fragments;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.WifiNetworkEntry;
import com.blueodin.wifigraphs.widgets.LabeledTextView;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class NetworkGraphFragment extends Fragment {
	private static final String ARG_HEADER = "arg_header";
	private static final String ARG_OVERVIEW = "arg_overview";
	private static final String ARG_ENTRIES = "arg_entries";
	
	private String mHeader;
	private String mOverview;
	private List<WifiNetworkEntry> mEntries;
	
	public static NetworkGraphFragment newInstance(String title, String overview, List<WifiNetworkEntry> entries) {
		NetworkGraphFragment f = new NetworkGraphFragment();
		Bundle args = new Bundle();
		args.putString(ARG_HEADER, title);
		args.putString(ARG_OVERVIEW, overview);
		args.putParcelableArrayList(ARG_ENTRIES, (ArrayList<? extends Parcelable>) entries);
		f.setArguments(args);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String timeFrameText = "N/A";
		
		Bundle args = this.getArguments();
		this.mHeader = args.getString(ARG_HEADER, "Unknown");
		this.mOverview = args.getString(ARG_OVERVIEW, "Unable to find details for this item");
		this.mEntries = args.getParcelableArrayList(ARG_ENTRIES);
		
		if((this.mEntries == null) || (this.mEntries.size() < 1)) {
			this.mEntries = new ArrayList<WifiNetworkEntry>();
			
		}
		
		View rootView = (View) inflater.inflate(R.layout.fragment_graph_detail, container, false);

		((LabeledTextView)rootView.findViewById(R.id.ltv_fragment_graph_header))
			.setText(this.mHeader);
		
		((LabeledTextView)rootView.findViewById(R.id.ltv_fragment_graph_overview))
			.setText(this.mOverview);
		
		((LabeledTextView)rootView.findViewById(R.id.ltv_fragment_graph_records))
			.setText(Integer.toString(this.mEntries.size()));
		
		if(this.mEntries.size() > 1) {
			Date startDate = new Date((long)this.mEntries.get(0).getTimestamp()), endDate = new Date((long)this.mEntries.get(this.mEntries.size()-1).getTimestamp());
        	DateFormat dateFormatter = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.SHORT);
        	timeFrameText = String.format("From %s to %s", dateFormatter.format(startDate), dateFormatter.format(endDate));
        }
		
		((LabeledTextView)rootView.findViewById(R.id.ltv_fragment_graph_timeframe))
			.setText(timeFrameText);
		
		((LinearLayout)rootView.findViewById(R.id.layout_fragment_graph_holder))
			.addView(buildGraph(getActivity(), this.mHeader));
		
		return rootView;
	}
	
    public GraphViewData[] getSignalData(List<WifiNetworkEntry> entries) {
		if(entries.size() < 1)
			return (new GraphViewData[]{});
		
		GraphViewData[] signalData = new GraphViewData[entries.size()];
		
		int i = 0;
		for(WifiNetworkEntry entry : entries)
			signalData[i++] = new GraphViewData(entry.getTimestamp(), entry.getLevel());
		
		return signalData;
	}
    
    protected GraphView buildGraph(Context context, String title) {
    	return buildBarGraph(context, title);
    }
    
    protected BarGraphView buildBarGraph(Context context, String title) {
		BarGraphView graphView = new BarGraphView(context, null) {
			protected String formatLabel(double value, boolean isValueX) {
                if (!isValueX)
                    return String.format("%.02f dBm", value);
                else 
                	return (String) DateUtils.getRelativeTimeSpanString((long)value, System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS);
            }
		};
		
		Resources res = context.getResources();
		
		GraphViewStyle graphViewStyle = graphView.getGraphViewStyle();
		graphViewStyle.setGridColor(res.getColor(R.color.bar_background_color));
		//graphView.setBackgroundColor(context.getResources().getColor(R.color.primary_bg_color));
		graphView.getGraphViewStyle().setHorizontalLabelsColor(res.getColor(R.color.secondary_text_color));
		graphView.getGraphViewStyle().setVerticalLabelsColor(res.getColor(R.color.alt_text_color));
		//graphView.setDrawBackground(true);
		
		graphView.addSeries(new GraphViewSeries(getSignalData(this.mEntries)));
		
		graphView.setScrollable(false);
		graphView.setScalable(true);
		
		double size = MSValues.ONE_DAY.toLong()*4;
		graphView.setViewPort(System.currentTimeMillis() - size, size);

		return graphView;
	}
    
    public enum MSValues {
		ONE_SECOND,
		ONE_MINUTE,
		ONE_HOUR,
		ONE_DAY,
		ONE_WEEK,
		ONE_YEAR;
		
		public long toLong() {
			switch(this) {
			case ONE_DAY:
				return (24*60*60*1000);

			case ONE_HOUR:
				return (60*60*1000);

			case ONE_MINUTE:
				return (60*1000);

			case ONE_SECOND:
				return (1*1000);

			case ONE_WEEK:
				return (7*24*60*60*1000);

			case ONE_YEAR:
				return (356*24*60*60*1000);

			}
			
			return 0;
		}
		
		@Override
		public String toString() {
			switch(this) {
			case ONE_DAY:
				return "One Day";

			case ONE_HOUR:
				return "One Hour";

			case ONE_MINUTE:
				return "One Minute";

			case ONE_SECOND:
				return "One Second";

			case ONE_WEEK:
				return "One Week";

			case ONE_YEAR:
				return "One Year";

			}
			
			return "None";
		}
	}
}
