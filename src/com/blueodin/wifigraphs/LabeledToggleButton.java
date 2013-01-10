package com.blueodin.wifigraphs;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class LabeledToggleButton extends LinearLayout {
	private TextView mLabelTextView;
	private ToggleButton mToggleButton;
	private CharSequence mLabelText = "";
	private CharSequence mButtonText = "";
	private CharSequence mButtonTextOn = "";
	private CharSequence mButtonTextOff = "";
	private int mButtonPadding = 0;
	
	public LabeledToggleButton(Context context) {
		this(context, null);
	}
	
	public LabeledToggleButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LabeledToggleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.ltb_widget, this);
		
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.LabeledToggleButton, defStyle, 0);
		mLabelText = attrArray.getText(R.styleable.LabeledToggleButton_buttonLabelText);
		mButtonText = attrArray.getText(R.styleable.LabeledToggleButton_buttonText);
		mButtonTextOn = attrArray.getText(R.styleable.LabeledToggleButton_buttonTextOn);
		mButtonTextOff = attrArray.getText(R.styleable.LabeledToggleButton_buttonTextOff);
		mButtonPadding = attrArray.getDimensionPixelSize(R.styleable.LabeledToggleButton_buttonLabelPadding, 0);
		attrArray.recycle();
	}
	
	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		
		mLabelTextView = (TextView)findViewById(R.id.ltb_label);
		mToggleButton = (ToggleButton)findViewById(R.id.ltb_button);
		
		setLabelText(mLabelText);
		setButtonPadding(mButtonPadding);
		
		if(mButtonText.length() > 0)
			setButtonText(mButtonText);
		
		if(mButtonTextOn.length() > 0)
			setButtonTextOn(mButtonTextOn);
		
		if(mButtonTextOff.length() > 0)
			setButtonTextOff(mButtonTextOff);
	}
	
	public void setLabelText(CharSequence text) {
		mLabelTextView.setText(text);
	}
	
	public CharSequence getLabelText() {
		return mLabelTextView.getText();
	}
	
	public void setButtonText(CharSequence text) {
		mToggleButton.setText(text);
	}
	
	public CharSequence getButtonText() {
		return mToggleButton.getText();
	}
	
	public void setButtonTextOn(CharSequence text) {
		mToggleButton.setTextOn(text);
	}
	
	public CharSequence getButtonTextOn() {
		return mToggleButton.getTextOn();
	}
	
	public void setButtonTextOff(CharSequence text) {
		mToggleButton.setTextOff(text);
	}
	
	public CharSequence getButtonTextOff() {
		return mToggleButton.getTextOff();
	}
	
	public void setButtonPadding(int margin) {
		mToggleButton.setPadding(mButtonPadding, 0, 0, 0);
	}
	
	public int getButtonPadding() {
		return mToggleButton.getPaddingLeft();
	}
	
	public TextView getLabelTextView() {
		return mLabelTextView;
	}
	
	public ToggleButton getButton() {
		return mToggleButton;
	}
}
