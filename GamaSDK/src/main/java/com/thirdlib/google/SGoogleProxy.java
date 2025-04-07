package com.thirdlib.google;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.thirdlib.ThirdModuleUtil;

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

		if (!ThirdModuleUtil.existFirebaseModule()) {
			return;
		}

		if (context == null || TextUtils.isEmpty(eventName)) {
			return;
		}

		FirebaseHelper.firebaseAnalytics(context, eventName, params);

	}

	public static void trackRevenueFirebase(Context context, String eventName, double usdPrice, String currency, String uid,String roleId, String productId, String orderId, String channel_platform, Map<String, Object> otherParams){
		if (!ThirdModuleUtil.existFirebaseModule()) {
			return;
		}
		if (context == null || TextUtils.isEmpty(eventName)) {
			return;
		}

		FirebaseHelper.trackRevenueFirebase(context, eventName, usdPrice, currency, uid, roleId, productId, orderId, channel_platform, otherParams);
	}

	public static Bundle trackPayCC(Context context, String eventName, String orderId, String productId, double usdPrice, String uid) {

		if (!ThirdModuleUtil.existFirebaseModule()) {
			return null;
		}

		return FirebaseHelper.trackPayCC(context, eventName, orderId, productId, usdPrice, uid);
	}

	public static void requestStoreReview(Activity activity, SFCallBack sfCallBack){

		if (!ThirdModuleUtil.existFirebaseModule()) {
			return;
		}

		ReviewManager manager = ReviewManagerFactory.create(activity);

//                if (BaseSdkImpl.this.reviewInfo != null){
//
//                    Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
//                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            //https://developer.android.com/guide/playcore/in-app-review/kotlin-java?hl=zh-cn
//                            // 如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
//                            // The flow has finished. The API does not indicate whether the user
//                            // reviewed or not, or even whether the review dialog was shown. Thus, no
//                            // matter the result, we continue our app flow.
//                            if (iCompleteListener != null) {
//                                iCompleteListener.onComplete();
//                            }
//                        }
//                    });
//
//                    return;
//                }

		Task<ReviewInfo> request = manager.requestReviewFlow();
		request.addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				// We can get the ReviewInfo object
				PL.i("task.isSuccessful We can get the ReviewInfo object");
				ReviewInfo reviewInfo = task.getResult();
				Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
				flow.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						//https://developer.android.com/guide/playcore/in-app-review/kotlin-java?hl=zh-cn
						// 如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
						// The flow has finished. The API does not indicate whether the user
						// reviewed or not, or even whether the review dialog was shown. Thus, no
						// matter the result, we continue our app flow.
						if (sfCallBack != null) {
							sfCallBack.success("","");
						}
					}
				});

			} else {
				// There was some problem, log or handle the error code.
				PL.i("requestReviewFlow There was some problem");
//                        int reviewErrorCode = task.getException().getErrorCode();
				if (sfCallBack != null) {
					sfCallBack.success("","");
				}
			}
		});

	}


}
