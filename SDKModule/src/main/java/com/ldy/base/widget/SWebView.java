package com.ldy.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.mybase.SBaseWebView;
import com.ldy.base.utils.Localization;

public class SWebView extends SBaseWebView {

	private static final String AndroidNativeJs = "AndroidNativeJs";

	public SWebView(Context context) {
		super(context);
		init();
	}

	@SuppressLint("NewApi")
	public SWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init();
	}

	public SWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		Localization.updateSGameLanguage(getContext());
	}

}
