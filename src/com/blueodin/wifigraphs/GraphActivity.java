package com.blueodin.wifigraphs;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.blueodin.wifigraphs.fragments.*;

public class GraphActivity extends FragmentActivity {
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private SignalGraphFragment mSignalGraphFragment;
	private HistoricalGraphFragment mHistoricalGraphFragment;
	private ChannelGraphFragment mChannelGraphFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graph_activity);

		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayHomeAsUpEnabled(true);
		
		mSignalGraphFragment = new SignalGraphFragment();
		actionBar.addTab(mSignalGraphFragment.getActionBarTab(actionBar));
		
		mHistoricalGraphFragment = new HistoricalGraphFragment();
		actionBar.addTab(mHistoricalGraphFragment.getActionBarTab(actionBar));
		
		mChannelGraphFragment = new ChannelGraphFragment();
		actionBar.addTab(mChannelGraphFragment.getActionBarTab(actionBar));
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph_activity, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/*
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, show the tab contents in the
		// container view.
		
		Fragment fragment = new GraphFragmentBase();
		Bundle args = new Bundle();
		args.putInt(GraphFragmentBase.ARG_SECTION_NUMBER, tab.getPosition() + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	*/
}
