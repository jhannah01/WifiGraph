package com.blueodin.wifigraphs.providers;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.WifiNetworkEntry;
import com.blueodin.wifigraphs.db.RecordsDataSource;
import com.blueodin.wifigraphs.widgets.LabeledTextView;

public class NetworkResultsAdapter extends SimpleAdapter {
	protected final Context mContext;
	protected RecordsDataSource mDataSource;
	
	public NetworkResultsAdapter(Context context, RecordsDataSource dataSource) {
		this(context, dataSource.getNetworkBSSIDs());
		this.mDataSource = dataSource;
	}
	
	public NetworkResultsAdapter(Context context, List<String> list) {
		this(context, getArrayFromStringList(list));
	}
	
	public NetworkResultsAdapter(Context context, String[] entries) {
		super(context, R.layout.network_row, entries);
		this.mContext = context;
		this.mDataSource = new RecordsDataSource(context);
	}
	
	public List<WifiNetworkEntry> getEntriesForItem(int position) {
		return getEntriesForItem(getItem(position));
	}
	
	public List<WifiNetworkEntry> getEntriesForItem(String bssid) {
		return this.mDataSource.getRecordsFor(bssid);
	}
	
	public WifiNetworkEntry getLastEntryForItem(int position) {
		return getLastEntryForItem(getItem(position));
	}
	
	public WifiNetworkEntry getLastEntryForItem(String bssid) {
		return this.mDataSource.getLastRecordFor(bssid);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		WifiNetworkEntry currentResult = getLastEntryForItem(getItem(position)); 
				
		View rowView = ((LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
	    	.inflate(R.layout.network_row, parent, false);
	    
	    ((TextView)rowView.findViewById(R.id.tv_row_bssid))
	    		.setText(currentResult.getBSSID());
	    
	    ((TextView)rowView.findViewById(R.id.tv_row_ssid))    
	    		.setText(currentResult.getSSID());
	    
		((LabeledTextView)rowView.findViewById(R.id.tv_row_timestamp))
				.setText(currentResult.getFormattedTimestamp());
	    
		((LabeledTextView)rowView.findViewById(R.id.tv_row_level))
				.setText(String.format("%d dBm", currentResult.getLevel()));

		((LabeledTextView)rowView.findViewById(R.id.tv_row_frequency))
				.setText(String.format("%d MHz", currentResult.getFrequency()));
		
		((LabeledTextView)rowView.findViewById(R.id.tv_row_security))
				.setText(currentResult.getFriendlySecurityInfo());
		
		((ImageView)rowView.findViewById(R.id.iv_row_icon))
				.setImageDrawable(this.mContext.getResources().getDrawable(currentResult.getIcon()));
		
	    return rowView;
	}

	public static String[] getArrayFromStringList(List<String> list) {
		String[] results = new String[list.size()];
		return list.toArray(results);
	}
}
