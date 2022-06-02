package com.flyfun.thirdlib.line;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.core.base.utils.ResUtil;
import com.core.base.utils.SStringUtil;
import com.flyfun.thirdlib.google.SGoogleProxy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;

import java.util.Arrays;

public class SLineSignIn {

	private static final String TAG = "SLineSignIn";

	public static final int LINE_LOGIN_REQUEST_CODE = 8001;

	private Activity activity;

	private Dialog mConnectionProgressDialog;

	boolean isCancel = false;

	private Fragment fragment;


	// [START declare_auth]
//	private FirebaseAuth mAuth;
	// [END declare_auth]

	LineSignInCallBack signInCallBack;
	private GoogleSignInClient mGoogleSignInClient;

	private String default_web_client_id = "";

	public void setClientId(String clientId) {
		this.default_web_client_id = clientId;
	}

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
	}


	public void startSignIn(String channelId, LineSignInCallBack googleSignInCallBack){

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
			Log.e("ERROR", e.toString());
		}

	}

	public void handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"handleActivityResult --> " + requestCode + "  --> " +  resultCode);
		if (isCancel) {
			return;
		}
		dimissDialog();

		if (requestCode == LINE_LOGIN_REQUEST_CODE) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				// Google Sign In was successful, authenticate with Firebase
				GoogleSignInAccount account = task.getResult(ApiException.class);
				Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

				String mFullName = account.getDisplayName();
				String mEmail = account.getEmail();
				String id = account.getId();
				Log.d(TAG, "mFullName：" + mFullName + ",mEmail:" + mEmail + ",id:" + id);
				String idToken = account.getIdToken();
				Log.d(TAG, "idToken：" + idToken);

				//firebaseAuthWithGoogle(account.getIdToken());

				if (signInCallBack != null) {
					signInCallBack.success(id,mFullName,mEmail,idToken);
				}

			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				// [START_EXCLUDE]

				if (signInCallBack != null) {
					signInCallBack.failure();
				}

				// [END_EXCLUDE]
			}
		}

	}

	/*private void initDialog() {  //初始化loading窗

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
				if (googleSignInCallBack != null) {
					googleSignInCallBack.failure();
				}
			}
		});

	}*/

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
