package com.mw.sdk.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;

import com.core.base.SBaseWebView;
import com.core.base.js.WebViewJsObj;
import com.mw.sdk.utils.Localization;

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

	}

	public void addMWSDKJavascriptInterface(Dialog dialog){

		if (getContext() instanceof Activity) {
			Activity activity = (Activity) getContext();
			WebViewJsObj webViewJsObj;
			if (dialog == null) {
				webViewJsObj = new WebViewJsObj(activity);
			}else {
				webViewJsObj = new WebViewJsObj(activity, dialog);
			}
			this.addJavascriptInterface(webViewJsObj,"MWSDK");
		}

	}

}
