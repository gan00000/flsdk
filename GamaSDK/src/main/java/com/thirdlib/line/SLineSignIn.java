package com.thirdlib.line;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;

import java.util.Arrays;

public class SLineSignIn {

	private static final String TAG = "SLineSignIn";

	public static final int LINE_LOGIN_REQUEST_CODE = 8001;

	private Activity activity;

	private Dialog mConnectionProgressDialog;

	boolean isCancel = false;

	private Fragment fragment;

	LineSignInCallBack signInCallBack;

	public SLineSignIn(Activity activity) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		initxx();
	}

	public SLineSignIn(Activity activity, Dialog dialog) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		this.mConnectionProgressDialog = dialog;
		initxx();
	}

	public SLineSignIn(FragmentActivity fragmentActivity, Fragment fragment, Dialog dialog) {

		if (fragmentActivity == null) {
			Log.e(TAG,"SGoogleSignIn fragmentActivity must not null");
			return;
		}
		this.activity = fragmentActivity;
		this.mConnectionProgressDialog = dialog;

		this.fragment = fragment;
		initxx();
	}

	private void initxx(){
		initDialog();
	}


	public void startSignIn(String channelId, LineSignInCallBack googleSignInCallBack){
		if (SStringUtil.isEmpty(channelId)){
			ToastUtils.toast(this.activity, "channelId is empty");
			return;
		}
		this.signInCallBack = googleSignInCallBack;
		try{
			// App-to-app login
			Intent loginIntent = LineLoginApi.getLoginIntent(
					this.activity,
					channelId,
					new LineAuthenticationParams.Builder()
							.scopes(Arrays.asList(Scope.PROFILE))
							// .nonce("<a randomly-generated string>") // nonce can be used to improve security
							.build());
			this.activity.startActivityForResult(loginIntent, LINE_LOGIN_REQUEST_CODE);

		}
		catch(Exception e) {
			Log.e(TAG, e.toString());
		}

	}

	public void handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"handleActivityResult --> " + requestCode + "  --> " +  resultCode);
		if (requestCode != LINE_LOGIN_REQUEST_CODE) {
			return;
		}
		if (isCancel) {
			return;
		}
		isCancel = false;
		dimissDialog();

		LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);

		switch (result.getResponseCode()) {

			case SUCCESS:
				// Login successful
				String accessToken = result.getLineCredential().getAccessToken().getTokenString();

				String mFullName = result.getLineProfile().getDisplayName();
				String status_message = result.getLineProfile().getStatusMessage();
//				String picture_url = result.getLineProfile().getPictureUrl().toString();//可能为null
				String id = result.getLineProfile().getUserId();
				Log.d(TAG, "mFullName：" + mFullName + ",status_message:" + status_message + ",id:" + id);

				if (signInCallBack != null) {
					signInCallBack.success(id,mFullName,"",accessToken);
				}

				break;

			case CANCEL:
				// Login canceled by user
				Log.e(TAG, "LINE Login Canceled by user.");
				if (signInCallBack != null) {
					signInCallBack.failure();
				}
				break;

			default:
				// Login canceled due to other error
				Log.e(TAG, "Login FAILED!");
				Log.e(TAG, result.getErrorData().toString());
				if (signInCallBack != null) {
					signInCallBack.failure();
				}
		}

	}

	private void initDialog() {  //初始化loading窗

		if (mConnectionProgressDialog == null) {
			mConnectionProgressDialog = new ProgressDialog(activity);
			((ProgressDialog)mConnectionProgressDialog).setMessage("Loading...");
		}
		mConnectionProgressDialog.setCancelable(true);
		mConnectionProgressDialog.setCanceledOnTouchOutside(false);
		mConnectionProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				isCancel = true;
				if (signInCallBack != null) {
					signInCallBack.failure();
				}
			}
		});

	}

	private void dimissDialog() { //关闭loading窗
		if(mConnectionProgressDialog != null) {
			Log.d(TAG,"dimiss dialog");
			mConnectionProgressDialog.dismiss();
		}
	}


//	============firebase 1=================


	public interface LineSignInCallBack{
		void success(String id, String mFullName, String mEmail, String idTokenString);
		void failure();
	}

}
