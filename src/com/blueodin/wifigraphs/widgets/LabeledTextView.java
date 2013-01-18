package com.blueodin.wifigraphs.widgets;

import com.blueodin.wifigraphs.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LabeledTextView extends LinearLayout {
	private Context mContext;
	private TextView mLabelTextView;
	private TextView mContentTextView;
	private CharSequence mLabelText = "";
	private CharSequence mContentText = "";
	private int mLabelMargin = 0;
	private int mContentTextAppearance = R.style.MediumText;
	private int mLabelTextAppearance = R.style.MediumText_Alt;
	
	public LabeledTextView(Context context) {
		this(context, null);
	}
	
	public LabeledTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public LabeledTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		readAttributes(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.ltv_widget, this);
	}
	
	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		mLabelTextView = (TextView)findViewById(R.id.textview_labeled_widget_label);
		mContentTextView = (TextView)findViewById(R.id.textview_labeled_widget_content);
		setupView();
	}
	
	protected void setupView() {
		setLabelText(mLabelText);
		setContentText(mContentText);
		setLabelMargin(mLabelMargin);
		setTextAppearance(this.mContentTextAppearance);
		setLabelTextAppearance(this.mLabelTextAppearance);
	}
	
	protected void readAttributes(Context context, AttributeSet attrs, int defStyle) {
		TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.LabeledTextView, defStyle, 0);

		this.mLabelText = attrArray.getText(R.styleable.LabeledTextView_labelText);
		this.mContentText = attrArray.getText(R.styleable.LabeledTextView_android_text);
		this.mLabelMargin = attrArray.getDimensionPixelSize(R.styleable.LabeledTextView_labelPadding, getResources().getDimensionPixelOffset(R.dimen.label_margin_default));
		this.mContentTextAppearance = attrArray.getResourceId(R.styleable.LabeledTextView_android_textAppearance, this.mContentTextAppearance);
		this.mLabelTextAppearance = attrArray.getResourceId(R.styleable.LabeledTextView_labelStyle, this.mLabelTextAppearance);
		
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
	
	public void setLabelTextAppearance(int style) {
		if(mLabelTextView != null)
			mLabelTextView.setTextAppearance(this.mContext, style);
	}
	
	public void setTextAppearance(int style) {
		if(mContentTextView != null)
			mContentTextView.setTextAppearance(this.mContext, style);		
	}
	
	public void setText(CharSequence text) {
		this.setContentText(text);
	}
	
	public CharSequence getText() {
		return this.getContentText();
	}
}