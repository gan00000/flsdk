package com.thirdlib.google;


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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SGoogleSignIn {

	private static final String TAG = "SGoogleSignIn";

	public static final int RC_SIGN_IN = 9001;

	public static final int GOOGLE_SHARE_CODE = 9002;

	private Activity activity;

	private Dialog mConnectionProgressDialog;

	boolean isCancel = false;

	private Fragment fragment;


	// [START declare_auth]
//	private FirebaseAuth mAuth;
	// [END declare_auth]

	GoogleSignInCallBack googleSignInCallBack;
	private GoogleSignInClient mGoogleSignInClient;

	private String default_web_client_id = "";

	public void setClientId(String clientId) {
		this.default_web_client_id = clientId;
	}

	public SGoogleSignIn(Activity activity) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		initxx();
	}

	public SGoogleSignIn(Activity activity, Dialog dialog) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		this.mConnectionProgressDialog = dialog;
		initxx();
	}

	public SGoogleSignIn(FragmentActivity fragmentActivity, Fragment fragment, Dialog dialog) {

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
		// [START config_signin]
		// Configure Google Sign In
		if (SStringUtil.isEmpty(default_web_client_id)) {
			default_web_client_id = ResUtil.findStringByName(activity,"default_web_client_id");
		}
		GoogleSignInOptions gso;
		if (SStringUtil.isNotEmpty(default_web_client_id)){
			gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken(default_web_client_id)
					.requestEmail()
					.build();
			// [END config_signin]

		}else{

			Log.e(TAG,"default_web_client_id为空,firebase配置不正确");
			gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestId().build();

		}
		mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

		// [START initialize_auth]
		// Initialize Firebase Auth
//		mAuth = FirebaseAuth.getInstance();
		// [END initialize_auth]
	}


	public void startSignIn(GoogleSignInCallBack googleSignInCallBack){

		this.googleSignInCallBack = googleSignInCallBack;
		if (!SGoogleProxy.isGooglePlayServicesAvailableToast(activity)){
			return;
		}
		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn fragmentActivity must not null");
			return;
		}

		Log.d(TAG,"Start Google SignIn ");
		if (mConnectionProgressDialog != null) {
			initDialog();
			mConnectionProgressDialog.show();
		}
		signInFirebase();
		isCancel = false;

	}

	public void handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"handleActivityResult --> " + requestCode + "  --> " +  resultCode);
		if (isCancel) {
			return;
		}
		dimissDialog();
		// Result returned from launching the Intent from
		//   GoogleSignInApi.getSignInIntent(...);
	   /* if (requestCode == RC_SIGN_IN) {
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
				GamaUtil.saveGoogleIdToken(context,idToken);
				if (googleSignInCallBack != null) {
	            	googleSignInCallBack.success(id, mFullName, mEmail, idToken);
				}
			} else {
				Log.d(TAG, "失败");
				if(result != null) {
					Log.d(TAG, result.getStatus() + "");
				}
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
		}*/

		if (requestCode == RC_SIGN_IN) {
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

				if (googleSignInCallBack != null) {
					googleSignInCallBack.success(id,mFullName,mEmail,idToken);
				}

			} catch (ApiException e) {
				// Google Sign In failed, update UI appropriately
				Log.w(TAG, "Google sign in failed", e);
				// [START_EXCLUDE]

				if (googleSignInCallBack != null) {
					googleSignInCallBack.failure();
				}

				// [END_EXCLUDE]
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


//	============firebase 1=================

	// [START auth_with_google]
	/*private void firebaseAuthWithGoogle(String idToken) {
		// [START_EXCLUDE silent]
//		showProgressBar();
		// [END_EXCLUDE]
		AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							if (googleSignInCallBack != null) {
								googleSignInCallBack.success(user.getUid(),user.getDisplayName(),user.getEmail(),"");
							}
						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());
							if (googleSignInCallBack != null) {
								googleSignInCallBack.failure();
							}
						}

						// [START_EXCLUDE]
//						hideProgressBar();
						// [END_EXCLUDE]
					}
				});
	}*/
	// [END auth_with_google]

	// [START signin]
	private void signInFirebase() {
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		activity.startActivityForResult(signInIntent, RC_SIGN_IN);
	}
	// [END signin]

	private void signOut() {
		// Firebase sign out
//		mAuth.signOut();

		// Google sign out
		mGoogleSignInClient.signOut().addOnCompleteListener(activity,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
					}
				});
	}

	private void revokeAccess() {
		// Firebase sign out
//		mAuth.signOut();

		// Google revoke access
		mGoogleSignInClient.revokeAccess().addOnCompleteListener(activity,
				new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
					}
				});
	}


//	============firebase 1=================


	public interface GoogleSignInCallBack{
		void success(String id, String mFullName, String mEmail, String idTokenString);
		void failure();
	}


	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		if (googleSignInCallBack != null) {
			googleSignInCallBack.failure();
		}
	}

	/* A fragment to display an error dialog */
	/*public static class ErrorDialogFragment extends DialogFragment {
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
	}*/
}
