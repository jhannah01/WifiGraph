package com.blueodin.wifigraphs.activities;

import com.blueodin.wifigraphs.R;
import android.app.Activity;
import android.os.Bundle;

public class OverviewActivity extends Activity { 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview_layout);

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
