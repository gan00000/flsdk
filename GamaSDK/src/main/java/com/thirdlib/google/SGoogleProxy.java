package com.thirdlib.google;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.Map;

/**
 */
public class SGoogleProxy {
	
	public static final int GOOGLE_SHARE_CODE = 9002;
	public static final int GOOGLE_SERVICES_ERROR_CODE = 21;
	private static final String TAG = "SGoogleProxy";


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


	public static void firebaseAnalytics(Context context, String eventName, Bundle params) {

		if (!existFirebaseModule()) {
			return;
		}

		if (context == null || TextUtils.isEmpty(eventName)) {
			return;
		}

		FirebaseHelper.firebaseAnalytics(context, eventName, params);

	}

	public static void trackRevenueFirebase(Context context, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){
		if (!existFirebaseModule()) {
			return;
		}
		if (context == null || TextUtils.isEmpty(eventName)) {
			return;
		}

		FirebaseHelper.trackRevenueFirebase(context, eventName, usdPrice, currency, uid, roleId, productId, orderId, channel_platform, otherParams);
	}

	public static Bundle trackPayCC(Context context, String eventName, String orderId, String productId, double usdPrice, String uid) {

		if (!existFirebaseModule()) {
			return null;
		}

		return FirebaseHelper.trackPayCC(context, eventName, orderId, productId, usdPrice, uid);
	}

	private static boolean existFirebaseModule() {
		try {
			Class<?> clazz = Class.forName("com.google.firebase.analytics.FirebaseAnalytics");
			if (clazz == null){
				return false;
			}
		} catch (ClassNotFoundException e) {
			PL.w("Firebase module not exist.");
			return false;
		}
		return true;
	}

}
