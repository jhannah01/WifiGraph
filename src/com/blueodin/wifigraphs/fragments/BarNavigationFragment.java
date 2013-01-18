package com.blueodin.wifigraphs.fragments;

import java.util.ArrayList;
import java.util.List;

import com.blueodin.wifigraphs.R;
import com.blueodin.wifigraphs.activities.DebugActivity;
import com.blueodin.wifigraphs.activities.GraphActivity;
import com.blueodin.wifigraphs.activities.MainActivity;
import com.blueodin.wifigraphs.activities.OverviewActivity;
import com.blueodin.wifigraphs.activities.SettingsActivity;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BarNavigationFragment extends ListFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		List<String> items = new ArrayList<String>();

		for(CharSequence i : getResources().getTextArray(R.array.nav_items))
			items.add(i.toString());
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, items);
	
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		
		if(item.equals("exit"))
			getActivity().finish();
		
		Intent newIntent = getIntent(item);
		
		if(newIntent == null)
			Toast.makeText(this.getActivity(), "Couldn't find the intent to launch for '" + item + "'.", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this.getActivity(), "Got a request for: " + item, Toast.LENGTH_SHORT).show();
		
		startActivity(newIntent);
		
		/*
		 DetailFrag frag = (DetailFrag) getFragmentManager().findFragmentById(R.id.frag_capt);
		if (frag != null && frag.isInLayout()) {
			frag.setText(getCapt(item));
		}
		*/
	}

	private Intent getIntent(String item) {
		if(item.equals("main_page"))
			return new Intent(this.getActivity(), MainActivity.class);
		
		if(item.equals("overview_page"))
			return new Intent(this.getActivity(), OverviewActivity.class);
			
	    if(item.equals("graphs_page"))
	    	return new Intent(this.getActivity(), GraphActivity.class);
	    	
	    if(item.equals("settings_page"))
	    	return new Intent(this.getActivity(), SettingsActivity.class);
	    	 
	    if(item.equals("debug_page"))
	    	return new Intent(this.getActivity(), DebugActivity.class);
	    	 
	    return null;	
	}

}
