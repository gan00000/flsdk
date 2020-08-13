package com.gama.thirdlib.google;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import static android.app.Activity.RESULT_OK;

public class SGooglePlayGameServices implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks {

	private static final String TAG = "SGooglePlayGameServices";

	public static final int REQUEST_ACHIEVEMENTS = 9003;
	public static final int REQUEST_LEADERBOARD = 9004;

	private Activity activity;


	private GoogleApiClient mGoogleApiClient;

	private GoogleConnectCallBack googleConnectCallBack;

	// Bool to track whether the app is already resolving an error
	private boolean mResolvingError = false;
	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;

	private boolean hasConnnected = false;

	public SGooglePlayGameServices(Activity activity) {

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
	}

	
	private void startSignIn(GoogleConnectCallBack googleConnectCallBack){

		if (activity == null) {
			Log.e(TAG,"SGoogleSignIn fragmentActivity must not null");
			return;
		}
		if (googleConnectCallBack == null) {
			Log.e(TAG,"googleSignInCallBack must not null");
			return;
		}
		this.googleConnectCallBack = googleConnectCallBack;

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and
		// basic profile are included in DEFAULT_SIGN_IN.
		if (mGoogleApiClient == null) {

			// Build a GoogleApiClient with access to SGoogleSignIn.API and the
			// options above.
			mGoogleApiClient = new GoogleApiClient.Builder(activity)
					.addApi(Games.API)
					.addScope(Games.SCOPE_GAMES)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.build();

		}
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}

	}



	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d(TAG, "onConnectionFailed");
		hasConnnected = false;
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		}
		mResolvingError = true;
		if (!BaseGameUtils.resolveConnectionFailure(activity, mGoogleApiClient, result,
				REQUEST_RESOLVE_ERROR, BaseGameUtils.sign_in_other_error)) {
			mResolvingError = false;
		}
	}
	

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d(TAG, "onConnected");

		hasConnnected = true;

		if (googleConnectCallBack != null){
			googleConnectCallBack.connected();
		}

	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d(TAG, "onConnectionSuspended(): attempting to connect");
		mGoogleApiClient.connect();
	}


	public void handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"handleActivityResult --> " + requestCode + "  --> " +  resultCode);
		if(mGoogleApiClient == null){
			return;
		}
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			mResolvingError = false;
			if (resultCode == RESULT_OK) {
				mGoogleApiClient.connect();
			} else {
				BaseGameUtils.showActivityResultError(activity, requestCode, resultCode);
			}
		}
	}

	public interface GoogleConnectCallBack{
		void connected();

	}



	// The rest of this code is all about building the error dialog

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {

		GoogleApiAvailability.getInstance().getErrorDialog(activity, errorCode, REQUEST_RESOLVE_ERROR).show();
	}


	public void unlock(final String achievementID){

		if (!SGoogleProxy.isGooglePlayServicesAvailableToast(activity)){
			return;
		}

		if (TextUtils.isEmpty(achievementID)){
			return;
		}

		if (hasConnnected){
			Games.Achievements.unlock(mGoogleApiClient,achievementID);
		}else {

			startSignIn(new GoogleConnectCallBack() {
				@Override
				public void connected() {
					Games.Achievements.unlock(mGoogleApiClient,achievementID);
				}
			});
		}
	}

	public void displayingAchievements(){
		if (!SGoogleProxy.isGooglePlayServicesAvailableToast(activity)){
			return;
		}
		if (hasConnnected){
			activity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
		}else {

			startSignIn(new GoogleConnectCallBack() {
				@Override
				public void connected() {
					activity.startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
				}
			});
		}

	}

	public void submitScore(final String leaderboardID,final long score){
		if (!SGoogleProxy.isGooglePlayServicesAvailableToast(activity)){
			return;
		}
		if (TextUtils.isEmpty(leaderboardID)){
			return;
		}

		if (hasConnnected){
			Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, score);
		}else {

			startSignIn(new GoogleConnectCallBack() {
				@Override
				public void connected() {
					Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, score);
				}
			});
		}

	}

	public void displayLeaderboard(final String leaderboardID){
		if (!SGoogleProxy.isGooglePlayServicesAvailableToast(activity)){
			return;
		}
		if (TextUtils.isEmpty(leaderboardID)){
			return;
		}
		if (hasConnnected){
			activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboardID), REQUEST_LEADERBOARD);
		}else {

			startSignIn(new GoogleConnectCallBack() {
				@Override
				public void connected() {
					activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, leaderboardID), REQUEST_LEADERBOARD);
				}
			});
		}
	}

}
