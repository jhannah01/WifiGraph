package com.blueodin.wifigraphs;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsActivity extends Activity {
	public static final String KEY_DEFAULT_ACTIVITY = "default_activity";
	public static final String KEY_GRAPH_INTERVAL = "graph_interval";
	public static final String KEY_AUTOSTART_SCANNING = "autostart_scanning";
	public static final String KEY_SCAN_INTERVAL = "scan_interval";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                new GeneralPreferenceFragment()).commit();
	}
	
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.pref_general);
	        addPreferencesFromResource(R.xml.pref_graphing);
	    }
	}
	
	public static class GraphingPreferenceFragment extends PreferenceFragment {
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        addPreferencesFromResource(R.xml.pref_graphing);
	    }
	}
}
