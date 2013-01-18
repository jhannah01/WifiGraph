package com.blueodin.wifigraphs.fragments;

import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.data.WifiNetworkEntry;
import com.blueodin.wifigraphs.db.DBHelper;
import com.blueodin.wifigraphs.db.RecordsDataSource;
import com.blueodin.wifigraphs.providers.NetworkResultsAdapter;
import com.blueodin.wifigraphs.providers.ResultsProvider;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class NetworkListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	private RecordsDataSource mDataSource;
	private NetworkResultsAdapter mListAdapter;

	@Override
	public void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		
		String bssid = this.mListAdapter.getItem(position);
		
		WifiNetworkEntry lastResult = this.mListAdapter.getLastEntryForItem(bssid);
		List<WifiNetworkEntry> selectedResults = this.mListAdapter.getEntriesForItem(bssid);
		
		getFragmentManager().beginTransaction()
			.replace(R.id.overview_detail_fragment, NetworkGraphFragment.newInstance(lastResult.getSSID(), lastResult.toString(), selectedResults))
			.commit();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		List<WifiNetworkEntry> results = new ArrayList<WifiNetworkEntry>();
		
		super.onCreate(savedInstanceState);
		
		this.mDataSource = new RecordsDataSource(this.getActivity());
		this.mDataSource.open();
		
		results.addAll(mDataSource.getAllRecords());
		
		this.mListAdapter = new NetworkResultsAdapter(this.getActivity(), this.mDataSource);
		this.setListAdapter(mListAdapter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(this.mDataSource.isOpen())
			this.mDataSource.close();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { DBHelper.COLUMN_ID, DBHelper.COLUMN_BSSID, DBHelper.COLUMN_SSID,
	    		DBHelper.COLUMN_LEVEL, DBHelper.COLUMN_FREQUENCY, DBHelper.COLUMN_CAPABILITIES,
	    		DBHelper.COLUMN_TIMESTAMP };
		
	    return (new CursorLoader(this.getActivity(), ResultsProvider.CONTENT_URI, projection, null, null, null));
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.mListAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub
		
	}
}