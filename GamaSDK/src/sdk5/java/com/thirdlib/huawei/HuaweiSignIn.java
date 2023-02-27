package com.thirdlib.huawei;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class HuaweiSignIn {


	HWSignInCallBack signInCallBack;

	public HuaweiSignIn(Activity activity) {
	}

	public HuaweiSignIn(Activity activity, Dialog dialog) {

	}

	public HuaweiSignIn(FragmentActivity fragmentActivity, Fragment fragment, Dialog dialog) {

	}


	public void startSignIn(Activity activity, HWSignInCallBack mSignInCallBack){
	}


	public void handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {

	}



//	============firebase 1=================


	public interface HWSignInCallBack{
		void success(String id, String mFullName, String mEmail, String idTokenString);
		void failure();
	}

}
