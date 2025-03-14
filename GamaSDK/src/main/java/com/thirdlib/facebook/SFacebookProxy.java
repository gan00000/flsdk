package com.thirdlib.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.core.base.utils.PL;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.mw.sdk.utils.SdkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SFacebookProxy {

	private static final String FB_TAG = "PL_LOG_FB";
	public static final int Request_Code_Share_Url = 9101;

//	private static DefaultAudience defaultAudience = DefaultAudience.FRIENDS;
//	private static LoginBehavior loginBehavior = LoginBehavior.WEB_ONLY;
	private static List<String> permissions = Arrays.asList("public_profile");
//	private static List<String> permissions = Arrays.asList("public_profile", "email", "user_friends");
//	private static List<String> permissions = Collections.singletonList("public_profile");
	private static final int REQUEST_TOMESSENGER = 16;
	FbShareCallBack shareCallBack;
	
	//final static List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");

	
	private static LoginManager loginManager;
	private static CallbackManager callbackManager;
	
//	private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
	
	private SFacebookProxy(Context context) {
		/*FacebookSdk.sdkInitialize(context.getApplicationContext(), new InitializeCallback() {
			@Override
			public void onInitialized() {

			}
		});*/
	}

	public static SFacebookProxy newObj(Context context){
		if (existFbModule()){
			return new SFacebookProxy(context);
		}
		return null;
	}

	public static boolean existFbModule() {
		try {
			Class<?> clazz = Class.forName("com.facebook.FacebookSdk");
			if (clazz == null){
				return false;
			}
		} catch (ClassNotFoundException e) {
			PL.w("Facebook module not exist.");
			return false;
		}
		return true;
	}

	public static void fetchDeferredAppLinkData(Activity activity) {

		if (!existFbModule()){
			return;
		}

		FacebookSdk.setAutoInitEnabled(true);
		FacebookSdk.fullyInitialize();
		AppLinkData.fetchDeferredAppLinkData(activity, new AppLinkData.CompletionHandler() {
			@Override
			public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {

				// Process app link data
				if (appLinkData != null && appLinkData.getTargetUri() != null){
					PL.i("fb onDeferredAppLinkDataFetched=" + appLinkData.getTargetUri().toString());
					SdkUtil.saveDeepLink(activity, appLinkData.getTargetUri().toString());
				}else {
					PL.i("fb onDeferredAppLinkDataFetched is no data");
					//SdkUtil.saveDeepLink(activity, "");
				}
			}
		});
	}

//	public static void initFbSdk(Context context){
//		FacebookSdk.sdkInitialize(context.getApplicationContext());
//		FacebookSdk.setAutoLogAppEventsEnabled(true);
//		FacebookSdk.setAdvertiserIDCollectionEnabled(true);
//		FacebookSdk.setAutoInitEnabled(true);
//		FacebookSdk.fullyInitialize();
//	}
	
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

		if (!existFbModule()){
			return;
		}

		if (context == null || TextUtils.isEmpty(eventName)) {
			return;
		}

		PL.i("-----track event facebook start name=" + eventName);
		AppEventsLogger logger = AppEventsLogger.newLogger(context);
		if (valueToSum == null && parameters == null){
			logger.logEvent(eventName);
		}else if (valueToSum == null && parameters != null){
			logger.logEvent(eventName, parameters);
		}else if (valueToSum != null && parameters == null){
			logger.logEvent(eventName,valueToSum);
		}else {
			logger.logEvent(eventName,valueToSum,parameters);
		}

	}

	public static void trackingEvent(final Context context,final String eventName, final Double valueToSum, Map<String, Object> parametersMap){
		if (context == null || TextUtils.isEmpty(eventName)) {
			return;
		}

		Bundle parameters = new Bundle();
		if (parametersMap != null && !parametersMap.isEmpty()){
			for (Map.Entry<String, Object> entry : parametersMap.entrySet()) {
				parameters.putString(entry.getKey(), entry.getValue().toString());
			}
		}

		trackingEvent(context, eventName, valueToSum, parameters);

	}

	public static void logPurchase(Context context, BigDecimal purchaseAmount, Map<String, Object> parametersMap) {

		if (context == null || !existFbModule()){
			return;
		}

		AppEventsLogger logger = AppEventsLogger.newLogger(context);
		if (parametersMap == null || parametersMap.isEmpty()) {
			logger.logPurchase(purchaseAmount, Currency.getInstance(Locale.US));
		}
		Bundle parameters = new Bundle();
		for (Map.Entry<String, Object> entry : parametersMap.entrySet()) {
			parameters.putString(entry.getKey(), entry.getValue().toString());
		}
		PL.i("-----track event facebook logPurchase");
		logger.logPurchase(purchaseAmount, Currency.getInstance(Locale.US), parameters);
	}

	public void fbLogin(final Activity activity, final FbLoginCallBack fbLoginCallBack) {
		fbLogin(activity, permissions, fbLoginCallBack);
	}


		/**
         * <p>Description: fb 登陆</p>
         * @param activity
         * @param fbLoginCallBack
         * @date 2015年11月20日
         */
	public void fbLogin(final Activity activity, List<String> permissions, final FbLoginCallBack fbLoginCallBack) {

		if (fbLoginCallBack == null){
			PL.d(FB_TAG, "fbLoginCallBack null, return");
			return;
		}

		if (loginManager == null) {
			loginManager = LoginManager.getInstance();
		}
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		if (AccessToken.getCurrentAccessToken() != null) {
			fbLogout(activity);
		}
		loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult result) {
				Log.d(FB_TAG, "onSuccess");
				if(result != null && result.getAccessToken() != null) {
					PL.d(FB_TAG, "login result: " + result.getAccessToken().getUserId() + "  token: " + result.getAccessToken().getToken());
					PL.d(FB_TAG, "getApplicationId: " + result.getAccessToken().getApplicationId());

					FaceBookUser user = new FaceBookUser();
					user.setUserFbId(result.getAccessToken().getUserId());
					user.setAccessTokenString(result.getAccessToken().getToken());
					user.setFacebookAppId(result.getAccessToken().getApplicationId());
					FbSp.saveFbId(activity,user.getUserFbId());
//				requestTokenForBusines(activity,user, fbLoginCallBack);
					if(fbLoginCallBack != null) {
						fbLoginCallBack.onSuccess(user);
					}
				}else {
					if (fbLoginCallBack != null) {
						fbLoginCallBack.onError("AccessToken token is null");
					}
				}

			}

			@Override
			public void onError(FacebookException error) {
				Log.d(FB_TAG, "onError:" + error.getMessage());
				if (fbLoginCallBack != null) {
					fbLoginCallBack.onError(error.getMessage());
				}
			}

			@Override
			public void onCancel() {
				Log.d(FB_TAG, "onCancel");
				if (fbLoginCallBack != null) {
					fbLoginCallBack.onCancel();
				}
			}
		});

		/*AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null && !accessToken.isExpired()) {
			Log.d(FB_TAG, "login result: onSuccess: accessToken != null" );
			String fbThirdId = accessToken.getUserId();
			String appId = accessToken.getApplicationId();
			String token = accessToken.getToken();
			Log.d(FB_TAG, "fbThirdId: " + fbThirdId + "  appId: " + appId + "  token: " + token);
			FaceBookUser user = new FaceBookUser();
			user.setUserFbId(fbThirdId);
			user.setAccessTokenString(token);
			user.setFacebookAppId(appId);
			if(fbLoginCallBack != null) {
				fbLoginCallBack.onSuccess(user);
			}
		} else {
			Log.d(FB_TAG, "accessToken == null");
//			loginManager.setDefaultAudience(defaultAudience);
//			loginManager.setLoginBehavior(loginBehavior);
			loginManager.logInWithReadPermissions(activity, permissions);
		}*/

		loginManager.logInWithReadPermissions(activity, permissions);
	}

	/**
	 * 获取token_for_business,登陆完成后获取
	 * @param activity
	 * @param user
	 * @param fbLoginCallBack
	 */
	public void requestTokenForBusines(final Activity activity, final FaceBookUser user, final FbLoginCallBack fbLoginCallBack){
		AccessToken accessToken =  AccessToken.getCurrentAccessToken();
		if (accessToken != null) {
			GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.d(FB_TAG, "token_for_business:" + response.toString());
							if (response != null){
								JSONObject resJsonObject = response.getJSONObject();
								if (resJsonObject != null){
									String token_for_business = resJsonObject.optString("token_for_business","");
									FbSp.saveTokenForBusiness(activity,token_for_business);
									if (user != null){
										user.setTokenForBusiness(token_for_business);
									}
								}
							}
							if (fbLoginCallBack != null) {
								fbLoginCallBack.onSuccess(user);//回调登陆成功
							}

						}
                    });

			Bundle parameters = new Bundle();
			parameters.putString("fields", "token_for_business");
			request.setParameters(parameters);
			request.executeAsync();
		}else {
			if (fbLoginCallBack != null) {
				fbLoginCallBack.onSuccess(user);//回调登陆成功
			}
		}
	}


	public void fbLogout(Activity activity){
		if (loginManager != null) {
			PL.d("fb logOut.....");
			loginManager.logOut();
		}
	}
	
//	public void fbShare(Activity activity, final FbShareCallBack fbShareCallBack,
//						String shareLinkUrl, String sharePictureUrl) {
//		fbShare(activity, fbShareCallBack, shareCaption, shareDescrition, shareLinkUrl, sharePictureUrl, "");
//	}
	public void fbShare(Activity activity, String hashTag, String quote, String shareLinkUrl, final FbShareCallBack fbShareCallBack) {

		if (TextUtils.isEmpty(shareLinkUrl)) {
			if (fbShareCallBack != null) {
				fbShareCallBack.onError("shareLinkUrl is enpty");
			}
			return;
		}

		FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
			@Override
			public void onCancel() {
				Log.d(FB_TAG, "onCancel");
				if (fbShareCallBack != null) {
					fbShareCallBack.onCancel();
//					fbShareCallBack.onSuccess();//由于ios不能判断取消为取消的情况，为了保持一致性，这里也回调成功
				}
			}

			@Override
			public void onError(FacebookException error) {
				Log.d(FB_TAG, "onError");
				if (error != null) {
					Log.d(FB_TAG, "onError:" + error.getMessage());
				}
				if (fbShareCallBack != null) {
					fbShareCallBack.onError(error.getMessage());
				}
			}

			@Override
			public void onSuccess(Sharer.Result result) {
				Log.d(FB_TAG, "onSuccess");
				if (result != null) {
					String id = result.getPostId();
					Log.d(FB_TAG, "onSuccess,post id:" + id);
				}
				
				if (fbShareCallBack != null) {
					fbShareCallBack.onSuccess();
				}
				
			}

		};
//		if (!ShareDialog.canShow(ShareLinkContent.class)){
//			ToastUtils.toast(activity,"share error");
//		}

		ShareDialog shareDialog = new ShareDialog(activity);
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		shareDialog.registerCallback(callbackManager, shareCallback, Request_Code_Share_Url);
		
		/*
		 * Note: If your app share links to the iTunes or Google Play stores, we
		 * do not post any images or descriptions that you specify in the share.
		 * Instead we post some app information we scrape from the app store
		 * directly with the Webcrawler. This may not include images.
		 */

		ShareLinkContent linkContent = new ShareLinkContent.Builder()
				.setContentUrl(Uri.parse(shareLinkUrl))
				.setQuote(quote)
				.setShareHashtag((new ShareHashtag.Builder().setHashtag(hashTag)).build())
//				.setRef("记得记得江江达")
//				.setPlaceId()
				.build();
		shareDialog.show(linkContent);

	}
	
	
	
	public void shareLocalPhotos(Activity activity, final FbShareCallBack fbShareCallBack, Bitmap ...images) {
		if (images != null) {
			List<SharePhoto> sharePhotos = new ArrayList<>();
			for (Bitmap bitmap : images) {
				SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).build();
				sharePhotos.add(photo);
			}
			
			SharePhotoContent contents = new SharePhotoContent.Builder().addPhotos(sharePhotos).build();
			shareLocalPhotoImpl(activity, fbShareCallBack, contents);
		}else{
			shareLocalPhotoImpl(activity, fbShareCallBack, null);
		}
	}
	
	public void shareLocalPhoto(Activity activity, final FbShareCallBack fbShareCallBack, Bitmap bitmap) {
		if (bitmap != null) {
			SharePhoto photo = new SharePhoto.Builder().setBitmap(bitmap).build();
			SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
			shareLocalPhotoImpl(activity, fbShareCallBack, content);
		}else{
			shareLocalPhotoImpl(activity, fbShareCallBack, null);
		}
		
	}
	
	public void shareLocalPhoto(Activity activity, final FbShareCallBack fbShareCallBack, String imagePath) {
		Bitmap image = BitmapFactory.decodeFile(imagePath);
		if (image != null) {
			SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
			SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
			shareLocalPhotoImpl(activity, fbShareCallBack, content);
		}else{
			shareLocalPhotoImpl(activity, fbShareCallBack, null);
		}
		
	}
	
	public void shareLocalPhoto(Activity activity, final FbShareCallBack fbShareCallBack, Uri picUri) {
		if (picUri != null) {
			SharePhoto photo = new SharePhoto.Builder().setImageUrl(picUri).build();
			SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
			shareLocalPhotoImpl(activity, fbShareCallBack, content);
		}else{
			shareLocalPhotoImpl(activity, fbShareCallBack, null);
		}
		
	}
	

	private void shareLocalPhotoImpl(Activity activity, final FbShareCallBack fbShareCallBack, SharePhotoContent content) {
	
		if (content == null) {
			if (fbShareCallBack != null) {
				fbShareCallBack.onError("SharePhotoContent is null");
			}
			return;
		}
		
		FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
			@Override
			public void onCancel() {
				Log.d(FB_TAG, "onCancel");
				if (fbShareCallBack != null) {
					fbShareCallBack.onCancel();
					//fbShareCallBack.onSuccess();//由于ios不能判断取消为取消的情况，为了保持一致性，这里也回调成功
				}
			}

			@Override
			public void onError(FacebookException error) {
				Log.d(FB_TAG, "onError");
				if (error != null) {
					Log.d(FB_TAG, "onError:" + error.getMessage());
				}
				if (fbShareCallBack != null) {
					fbShareCallBack.onError(error.getMessage());
				}
			}

			@Override
			public void onSuccess(Sharer.Result result) {
				Log.d(FB_TAG, "onSuccess");
				if (result != null) {
					String id = result.getPostId();
					Log.d(FB_TAG, "postId:" + id);
				}
				
				if (fbShareCallBack != null) {
					fbShareCallBack.onSuccess();
				}
				
			}

		};
		
//		SharePhoto photo = new SharePhoto.Builder().setBitmap(image).build();
		//SharePhoto photo = new SharePhoto.Builder().setImageUrl(imageUrl).build();
	//	SharePhotoContent content = new SharePhotoContent.Builder().addPhoto(photo).build();
		
		ShareDialog shareDialog = new ShareDialog(activity);
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		shareDialog.registerCallback(callbackManager, shareCallback);
		if (shareDialog.canShow(content)) {
			shareDialog.show(content);
		}else if(hasPublishPermission()){
			 ShareApi.share(content, shareCallback);
		}else{
			Toast.makeText(activity, "facebook app is not installed", Toast.LENGTH_SHORT).show();
			if (fbShareCallBack != null) {
				fbShareCallBack.onError("facebook app is not installed and no publish_actions permission");
			}
		}
	}
	
	private boolean hasPublishPermission() {
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		return accessToken != null && accessToken.getPermissions().contains("publish_actions");
	}


	public void requestBusinessId(final Activity activity,final FbBusinessIdCallBack fbBusinessIdCallBack){
		
		AccessToken accessToken =  AccessToken.getCurrentAccessToken();
		Bundle b = new Bundle();
		b.putString("limit", "300");
	//	b.putString("fields", "300");
		if (accessToken != null) {
			new GraphRequest(accessToken, "/me/ids_for_business", b, HttpMethod.GET, new GraphRequest.Callback() {
				public void onCompleted(GraphResponse response) {
					Log.d(FB_TAG, "requestBusinessId:" + response.toString());
					String apps = "";
					try {
						if (response.getError() == null) {
						//	JSONObject bussesIdsGraphObject = response.getJSONObject();
							JSONObject InnerJSONObject = response.getJSONObject();
							JSONArray dataArr = InnerJSONObject.optJSONArray("data");
							if (dataArr != null && dataArr.length() != 0) {
								StringBuilder stringBuilder = new StringBuilder();
								for (int i = 0; i < dataArr.length(); i++) {
									JSONObject mJsonObject = dataArr.optJSONObject(i);
									String scopeId = mJsonObject.optString("id", "");
									if (!TextUtils.isEmpty(scopeId)) {
										String appId = mJsonObject.optJSONObject("app") == null ? "" : mJsonObject.optJSONObject("app").optString("id", "");
										if (!TextUtils.isEmpty(appId)) {
											stringBuilder.append(scopeId + "_" + appId + ",");// 拼接scopeId和appId
										}
									}
								}
								apps = stringBuilder.substring(0, stringBuilder.lastIndexOf(","));
//								activity.getSharedPreferences(FbSp.S_FACEBOOK_FILE, Activity.MODE_PRIVATE).edit().putString(FbSp.S_FB_APP_BUSINESS_IDS, apps).commit();
								FbSp.saveAppsBusinessId(activity,apps);
								if (fbBusinessIdCallBack != null) {
									fbBusinessIdCallBack.onSuccess(apps);
								}
							}
						}else if(fbBusinessIdCallBack != null){
							fbBusinessIdCallBack.onError();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			}).executeAsync();
		}else if(fbBusinessIdCallBack != null){
			fbBusinessIdCallBack.onError();
		}
	}
	
	
	public void getMyProfile(final Activity activity, final FbLoginCallBack callBack) {
		// me?fields=name,id,first_name,last_name
		final Bundle b = new Bundle();
		b.putString("fields", "name,id,gender,birthday,picture.width(300)");
		final AccessToken accessToken = AccessToken.getCurrentAccessToken();
		new GraphRequest(accessToken, "/me", b, HttpMethod.GET, new GraphRequest.Callback() {

			@Override
			public void onCompleted(GraphResponse response) {
				if (response == null) {
					if (callBack != null) {
						callBack.onError("response null");
					}
				} else if (response.getError() != null) {
					if (callBack != null) {
						callBack.onError(response.getError().getErrorMessage() + "");
					}
				} else {
					Log.d(FB_TAG, "获取信息-- 》 " + response.toString());
					JSONObject userInfo = response.getJSONObject();
					if(userInfo == null) {
						if (callBack != null) {
							callBack.onError("userInfo null");
						}
						return;
					}
					String id = userInfo.optString("id", "");
					String name = userInfo.optString("name", "");
					String gender = userInfo.optString("gender", "");
					String birthday = userInfo.optString("birthday", "");
					Uri picUri = null;

					JSONObject picObject = userInfo.optJSONObject("picture");
					if(picObject != null) {
						JSONObject picData = picObject.optJSONObject("data");
						if(picData != null && !TextUtils.isEmpty(picData.optString("url"))) {
							picUri = Uri.parse(picData.optString("url"));
							FbSp.saveFbPicUrl(activity, picData.optString("url"));
						}
					}

					FbSp.saveFbGender(activity, gender);
					FbSp.saveFbBirthday(activity, birthday);
					FbSp.saveFbName(activity, name);

					FaceBookUser user = new FaceBookUser();
					user.setUserFbId(id);
					user.setName(name);
					user.setGender(gender);
					user.setBirthday(birthday);
					user.setPictureUri(picUri);
					user.setFacebookAppId(accessToken.getApplicationId());
					user.setAccessTokenString(accessToken.getToken());
					if (callBack != null) {
						callBack.onSuccess(user);
					}
				}
			}
		}).executeAsync();
	}


	
	public void onActivityResultForLogin(Activity activity, int requestCode, int resultCode, Intent data) {

		if (!existFbModule()){
			return;
		}

		if (!FacebookSdk.isInitialized() || requestCode != CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()){
			return;
		}

		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		callbackManager.onActivityResult(requestCode, resultCode, data);

		 if(requestCode == REQUEST_TOMESSENGER) {
			 Log.d(FB_TAG, "to messenger 回调");
			 if(shareCallBack != null) {
				 shareCallBack.onSuccess();
			 }
		 }
	}

	public void onActivityResultForShare(Activity activity, int requestCode, int resultCode, Intent data) {

		if (!FacebookSdk.isInitialized() || requestCode != Request_Code_Share_Url){
			return;
		}

		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onDestroy(Activity activity) {
		//fbLogout(activity);
	}
	

	public interface FbShareCallBack {
		public void onCancel();
		public void onError(String message);
		public void onSuccess();
	}
	
	public interface FbLoginCallBack {
		public void onCancel();
		public void onError(String message);
		public void onSuccess(FaceBookUser user);
	}
	
	/**
	* <p>Title: FbInviteFriendsCallBack</p>
	* <p>Description: 请求好友的回调接口</p>
	* @author GanYuanrong
	* @date 2015年11月23日
	*/
	public interface FbInviteFriendsCallBack {
		public void onCancel();
		public void onError(String message);
		public void onSuccess(String requestId, List<String> requestRecipients);
	}
	
	/**
	* <p>Title: RequestFriendsCallBack</p>
	* <p>Description: 获取好友的回调接口</p>
	* @author GanYuanrong
	* @date 2015年11月23日
	*/
	public interface RequestFriendsCallBack {
		public void onError();
		public void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles, String next, String previous, int count);
	}
	
//	public interface FbMyFriendsCallBack {
//		public void onError();
//		public void onSuccess(JSONArray objects, JSONObject graphObject);
//	}
	public interface FbBusinessIdCallBack {
		public void onError();
		public void onSuccess(String businessId);
	}
	
	
	
}
