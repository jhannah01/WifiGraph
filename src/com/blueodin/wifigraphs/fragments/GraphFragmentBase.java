package com.blueodin.wifigraphs.fragments;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueodin.wifigraphs.R;

public abstract class GraphFragmentBase extends Fragment implements IGraphFragment {
	protected final boolean mUseViewGroup = true;

	@Override
	public ActionBar.Tab getActionBarTab(ActionBar actionBar) {
		return (actionBar.newTab()
					.setText(getTabTitle())
					.setTabListener(new TabListener<GraphFragmentBase>(getTag(), this)));
	}
	
	public static class TabListener<T extends GraphFragmentBase> implements ActionBar.TabListener {
		protected GraphFragmentBase mFragment;
	    protected String mTag = "";
	    
	    public TabListener(GraphFragmentBase fragment) {
	    	this.mFragment = fragment;
	    }
	    public TabListener(String tag, GraphFragmentBase fragment) {
	    	this.mFragment = fragment;
	    	this.mTag = tag;
	    }
	    
	    @Override
	    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    		ft.add(R.id.container, mFragment, mTag);
	    }

	    @Override
	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(mFragment);
	    }

	    @Override
	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	    
	    }
	}
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView;
		
		if(!this.mUseViewGroup)
			rootView = this.getGraphView(getActivity());
		else {
			rootView = (View)inflater.inflate(R.layout.graph_fragment, container, false);
		
			((ViewGroup)rootView.findViewById(R.id.graph_fragment_holder))
				.addView(this.getGraphView(this.getActivity()));
		}
		return rootView;
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
