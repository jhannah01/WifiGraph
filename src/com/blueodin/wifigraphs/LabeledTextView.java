package com.blueodin.wifigraphs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LabeledTextView extends LinearLayout {
	private TextView mLabelTextView;
	private TextView mContentTextView;
	private CharSequence mLabelText = "";
	private CharSequence mContentText = "";
	private int mLabelMargin = 0;
	
	public LabeledTextView(Context context) {
		this(context, null);
		//buildLayout(context);
	}
	
	public LabeledTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LabeledTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		readAttributes(context, attrs, defStyle);
		buildLayout(context);
	}
	
	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		mLabelTextView = (TextView)findViewById(R.id.textview_labeled_widget_label);
		mContentTextView = (TextView)findViewById(R.id.textview_labeled_widget_content);
		
		setupView();
	}
	
	protected void buildLayout(Context context) {
		LayoutInflater.from(context).inflate(R.layout.ltv_widget, this);
	}
	
	protected void setupView() {
		setLabelText(mLabelText);
		setContentText(mContentText);
		setLabelMargin(mLabelMargin);
	}
	
	protected void readAttributes(Context context, AttributeSet attrs, int defStyle) {
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.LabeledTextView, defStyle, 0);
		
		mLabelText = attrArray.getText(R.styleable.LabeledTextView_labelText);
		mContentText = attrArray.getText(R.styleable.LabeledTextView_contentText);
		mLabelMargin = attrArray.getDimensionPixelSize(R.styleable.LabeledTextView_labelPadding, 0);
		
		attrArray.recycle();
	}
		
	public void setLabelText(CharSequence text) {
		if(mLabelTextView == null)
			return;
		
		mLabelTextView.setText(text);
	}
	
	public CharSequence getLabelText() {
		return mLabelTextView.getText();
	}
	
	public void setContentText(CharSequence text) {
		if(mContentTextView == null)
			return;
		
		mContentTextView.setText(text);
	}
	
	public CharSequence getContentText() {
		return mContentTextView.getText();
	}
	
	public void setLabelMargin(int margin) {
		if(mLabelTextView == null)
			return;
		
		mLabelTextView.setPadding(0, 0, mLabelMargin, 0);
	}
	
	public int getLabelMargin() {
		return mLabelMargin;
	}
	
	public TextView getLabelTextView() {
		return mLabelTextView;
	}
	
	public TextView getContentTextView() {
		return mContentTextView;
	}
}
