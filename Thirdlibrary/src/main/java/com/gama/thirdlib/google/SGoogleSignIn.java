package com.gama.thirdlib.google;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.gama.base.utils.StarPyUtil;

import static android.app.Activity.RESULT_OK;

public class SGoogleSignIn implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {
	
	private static final String TAG = "SGoogleSignIn";
	
	public static final int RC_SIGN_IN = 9001;

	public static final int GOOGLE_SHARE_CODE = 9002;
	
	private Activity activity;
	
	private Dialog mConnectionProgressDialog;
	
	private GoogleApiClient mGoogleApiClient;
	
	GoogleSignInCallBack googleSignInCallBack;
	
	boolean isCancel = false;

	private Fragment fragment;

	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";

	private String clientId = "";

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public SGoogleSignIn(Activity activity) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}
		
		this.activity = activity;
	}

	public SGoogleSignIn(Activity activity, Dialog dialog) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		this.mConnectionProgressDialog = dialog;
//		if (activity instanceof FragmentActivity){
//			this.fragmentActivity = (FragmentActivity)activity;
//		}
	}

	public SGoogleSignIn(FragmentActivity fragmentActivity, Fragment fragment, Dialog dialog) {

		if (fragmentActivity == null) {
			Log.e(TAG,"SGoogleSignIn fragmentActivity must not null");
			return;
		}
		this.activity = fragmentActivity;
		this.mConnectionProgressDialog = dialog;

		this.fragment = fragment;
	}

	
	public void startSignIn(GoogleSignInCallBack googleSignInCallBack){
		if (!SGoogleProxy.isGooglePlayServicesAvailableToast(activity)){
			return;
		}
		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn fragmentActivity must not null");
			return;
		}

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and
		// basic profile are included in DEFAULT_SIGN_IN.
		if (mGoogleApiClient == null) {
			GoogleSignInOptions gso;
			if (TextUtils.isEmpty(clientId)){
				gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestId().build();
			}else {
				gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestId().requestIdToken(clientId).build();
			}

			// Build a GoogleApiClient with access to SGoogleSignIn.API and the
			// options above.

			mGoogleApiClient = new GoogleApiClient.Builder(activity)
					//.enableAutoManage(fragmentActivity, this)
					.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

		}
		if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
			mGoogleApiClient.connect();
		}else {
			signIn();
		}
		this.googleSignInCallBack = googleSignInCallBack;
		isCancel = false;
		Log.d(TAG,"Start Google SignIn ");
		if (mConnectionProgressDialog != null) {
			initDialog();
			mConnectionProgressDialog.show();
		}

	}
	
	public void handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"handleActivityResult --> " + requestCode + "  --> " +  resultCode);
		if (isCancel || mGoogleApiClient == null) {
			return;
		}
		dimissDialog();
		 // Result returned from launching the Intent from
	    //   GoogleSignInApi.getSignInIntent(...);
	    if (requestCode == RC_SIGN_IN) {
	        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
	        if (result != null && result.isSuccess()) {
	            GoogleSignInAccount acct = result.getSignInAccount();
	            // Get account information
	            String mFullName = acct.getDisplayName();
	            String mEmail = acct.getEmail();
	            String id = acct.getId();
	            Log.d(TAG, "mFullName：" + mFullName + ",mEmail:" + mEmail + ",id:" + id);

				String idToken = acct.getIdToken();
				Log.d(TAG, "idToken：" + idToken);
				if (idToken == null){
					idToken = "";
				}
				StarPyUtil.saveGoogleIdToken(context,idToken);
				if (googleSignInCallBack != null) {
	            	googleSignInCallBack.success(id, mFullName, mEmail, idToken);
				}
			} else {
				Log.d(TAG, "失败");
				if (googleSignInCallBack != null) {
					googleSignInCallBack.failure();
				}
			}
	    }else if(requestCode == GOOGLE_SHARE_CODE){
	    	Log.d(TAG, "分享完成");
	    }else if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				// Make sure the app is not already connected or attempting to connect
				if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
					mGoogleApiClient.connect();
				}
			}else{
				if (googleSignInCallBack != null) {
					googleSignInCallBack.failure();
				}
			}
		}
	}
	
	public void handleActivityDestroy(Context context) {
		signOut();
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
				if (googleSignInCallBack != null) {
					googleSignInCallBack.failure();
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
	
	
	
	 // [START signIn]
	private void signIn() {

		if (mGoogleApiClient != null && activity != null) {

			Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
			if (this.fragment != null){

				if (signInIntent != null) {
					this.fragment.startActivityForResult(signInIntent, RC_SIGN_IN);
				}

			}else {

				if (signInIntent != null) {
					activity.startActivityForResult(signInIntent, RC_SIGN_IN);
				}
			}

		}
	}
   // [END signIn]

   // [START signOut]
	public void signOut() {

		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			Log.d(TAG, "signOut");
			try {
				Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        mGoogleApiClient.disconnect();
                        // [END_EXCLUDE]
                    }
                });
			} catch (Exception e) {
				mGoogleApiClient.disconnect();
			}
		} else {
			Log.d(TAG, "mGoogleApiClient not connected");
		}
		
	}
   // [END signOut]

   // [START revokeAccess]
	public void revokeAccess() {
		Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(Status status) {
				// [START_EXCLUDE]

				// [END_EXCLUDE]
			}
		});
	}
   // [END revokeAccess]


	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onConnectionFailed");
		if (isCancel) {
			return;
		}
		dimissDialog();
		/*if (!TextUtils.isEmpty(result.getErrorMessage())) {
			Toast.makeText(fragmentActivity, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
		}
		if (googleSignInCallBack != null) {
			googleSignInCallBack.failure();
		}*/


		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				mGoogleApiClient.connect();
			}
		} else {
			// Show dialog using GoogleApiAvailability.getErrorDialog()
			if (!TextUtils.isEmpty(result.getErrorMessage())) {
				Toast.makeText(activity, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
	}
	

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d(TAG, "onConnected");

		signIn();

	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended");
	}


	public interface GoogleSignInCallBack{
		void success(String id, String mFullName, String mEmail, String idTokenString);
		void failure();
	}



	// The rest of this code is all about building the error dialog

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {

		GoogleApiAvailability.getInstance().getErrorDialog(activity, errorCode, REQUEST_RESOLVE_ERROR).show();
	}

	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingError = false;
		if (googleSignInCallBack != null) {
			googleSignInCallBack.failure();
		}
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		private SGoogleSignIn mSGoogleSignIn;

		public void setmSGoogleSignIn(SGoogleSignIn mSGoogleSignIn) {
			this.mSGoogleSignIn = mSGoogleSignIn;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (mSGoogleSignIn != null) {
				mSGoogleSignIn.onDialogDismissed();
			}
		}
	}
}
