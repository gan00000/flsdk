package com.thirdlib.facebook;

import android.content.Context;
import android.os.Bundle;

import java.math.BigDecimal;
import java.util.Map;

public class SFacebookProxy {

//	private static final String FB_TAG = "PL_LOG_FB";
//	public static final int Request_Code_Share_Url = 9101;
//
//	private static DefaultAudience defaultAudience = DefaultAudience.FRIENDS;
//	private static LoginBehavior loginBehavior = LoginBehavior.WEB_ONLY;
//	private static List<String> permissions = Arrays.asList("public_profile");
////	private static List<String> permissions = Arrays.asList("public_profile", "email", "user_friends");
////	private static List<String> permissions = Collections.singletonList("public_profile");
//	private static final int REQUEST_TOMESSENGER = 16;
//	FbShareCallBack shareCallBack;
//
//	//final static List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");
//
//
//	private static LoginManager loginManager;
//	private static CallbackManager callbackManager;
//
////	private boolean canPresentShareDialog;
//    private boolean canPresentShareDialogWithPhotos;
	
	public SFacebookProxy(Context context) {
		/*FacebookSdk.sdkInitialize(context.getApplicationContext(), new InitializeCallback() {
			@Override
			public void onInitialized() {

			}
		});*/
	}

	public static void initFbSdk(Context context){
//		FacebookSdk.sdkInitialize(context.getApplicationContext());
//		FacebookSdk.setAutoLogAppEventsEnabled(true);
//		FacebookSdk.setAdvertiserIDCollectionEnabled(true);
//		FacebookSdk.setAutoInitEnabled(true);
//		FacebookSdk.fullyInitialize();
	}
	
//	public static void activateApp(Context context){
//
//		if (TextUtils.isEmpty(FbResUtil.findStringByName(context,"facebook_app_id"))) {
//			Toast.makeText(context, "fb applicationId is empty", Toast.LENGTH_LONG).show();
//		}else{
//			activateApp(context, FbResUtil.findStringByName(context,"facebook_app_id"));
//		}
//
//	}
	
//	public static void activateApp(final Context context,final String appId){
//		if (context instanceof Activity) {
//			AppEventsLogger.activateApp(((Activity)context).getApplication(), appId);
//		}else {
//			AppEventsLogger.activateApp(context, appId);
//		}
//	}
	
//	public static void trackingEvent(Activity activity,String eventName){
//		trackingEvent(activity, eventName,null,null);
//	}
//
//	public static void trackingEvent(Activity activity,String eventName,double valueToSum){
//		trackingEvent(activity, eventName,valueToSum,null);
//	}
	
	public static void trackingEvent(final Context context,final String eventName, final Double valueToSum, final Bundle parameters){

	}

	public static void trackingEvent(final Context context,final String eventName, final Double valueToSum, Map<String, Object> parametersMap){

	}

	public static void logPurchase(Context context, BigDecimal purchaseAmount, Map<String, Object> parametersMap) {

	}



}
