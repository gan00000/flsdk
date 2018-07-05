package com.gama.thirdlib.google;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.core.base.utils.ToastUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.plus.PlusShare;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;

/**
 */
public class SGoogleProxy {
	
	public static final int GOOGLE_SHARE_CODE = 9002;
	public static final int GOOGLE_SERVICES_ERROR_CODE = 21;
	private static final String TAG = "SGoogleProxy";


    //===========================================================================================
    //=====================================Google 分析============================================
    //===========================================================================================

	/*public static class SGoogleAnalytics {
		//新版google分析
		private static Tracker mTracker;

		public synchronized static Tracker initDefaultTracker(Context context, String trackingId) {
			if (mTracker == null) {
				GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
				mTracker = analytics.newTracker(trackingId);
			}
			return mTracker;
		}

		public static void trackEvent(String category, String action, String label) {

			if (null != mTracker) {
				mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
			}
		}

		public static void stopSession(){

		}
	}*/

    //===========================================================================================
    //=====================================Google 分析  end============================================
    //===========================================================================================


	//=====================================Google getAdvertisingId ============================================


	private static String advertisingIdCache = "";


	/**
	 * 获取Google ads id，不能再主线程调用
	 * @param mContext
	 * @return
	 */

	public static String getAdvertisingId(Context mContext) {

		if (!TextUtils.isEmpty(advertisingIdCache)) {
			return advertisingIdCache;
		}

		try {
			AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(mContext);
			if (null != adInfo) {
				advertisingIdCache = adInfo.getId();
			}
//			boolean isLAT = adInfo.isLimitAdTrackingEnabled();
		} catch (IllegalStateException e) {
		} catch (GooglePlayServicesRepairableException e) {
		} catch (IOException e) {
		} catch (GooglePlayServicesNotAvailableException e) {
		}
		return advertisingIdCache;
	}

	//=====================================Google getAdvertisingId end============================================

/*
	public static void setMessageDispather(Context context,Class< ? extends MessageDispatcher> messageDispatherClazz){
		PushSPUtil.saveDispatherClassName(context, messageDispatherClazz.getCanonicalName());
	}

	public static Map onCreateMainActivity(Activity activity){
		return onCreateMainActivity(activity,true);
	}
*/

	public static int isGooglePlayServicesAvailable(Context context){
		return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
	}

	public static boolean showGoogleServicesErrorDialog(Activity activity){
		int googleServicesCode = isGooglePlayServicesAvailable(activity.getApplicationContext());
		if (googleServicesCode == ConnectionResult.SUCCESS){
			return true;
		}
		Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(activity,googleServicesCode,GOOGLE_SERVICES_ERROR_CODE);
		errorDialog.show();
		return false;
	}

	public static boolean isGooglePlayServicesAvailableToast(Context context){
		int googleServicesCode = isGooglePlayServicesAvailable(context);
		if (googleServicesCode == ConnectionResult.SUCCESS){
			return true;
		}
		ToastUtils.toast(context,"Google Services is not available for this device");
		return false;
	}


	public void share(Activity activity,String text,String url){
		// Launch the Google+ share dialog with attribution to your app.
	      Intent shareIntent = new PlusShare.Builder(activity)
	          .setType("text/plain")
	          .setText(text)
	          .setContentUrl(Uri.parse(url))
	          .getIntent();
	      activity.startActivityForResult(shareIntent, GOOGLE_SHARE_CODE);
	}

	public static void firebaseAnalytics(Activity activity, String eventName, Bundle params) {
		FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(activity);
		analytics.logEvent(eventName, params);
	}

/*

	public static class SFirebaseAnalytics {

		private static FirebaseAnalytics mFirebaseAnalytics;

		public static void logEvent(Context context,String eventName){
			if (mFirebaseAnalytics == null){
				mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
			}
			Bundle bundle = new Bundle();

			mFirebaseAnalytics.logEvent(eventName, bundle);

		}

		public static void logEvent(Context context,String eventName,Bundle bundle){
			if (mFirebaseAnalytics == null){
				mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
			}
			mFirebaseAnalytics.logEvent(eventName, bundle);
		}

	}
*/

}
