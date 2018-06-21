package com.gama.thirdlib.facebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.facebook.messenger.ShareToMessengerParamsBuilder;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareLinkContent.Builder;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.GameRequestDialog;
import com.facebook.share.widget.ShareDialog;
import com.gama.thirdlib.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SFacebookProxy {

	private static final String FB_TAG = "PL_LOG_FB";

	private static DefaultAudience defaultAudience = DefaultAudience.FRIENDS;
	private static LoginBehavior loginBehavior = LoginBehavior.NATIVE_WITH_FALLBACK;
//	private static List<String> permissions = Arrays.asList("public_profile", "email", "user_friends");
	private static List<String> permissions = Collections.singletonList("public_profile");
	private static final int REQUEST_TOMESSENGER = 16;
	FbShareCallBack shareCallBack;
	
	//final static List<String> PUBLISH_PERMISSIONS = Arrays.asList("publish_actions");

	
	private static LoginManager loginManager;
	private static CallbackManager callbackManager;
	
//	private boolean canPresentShareDialog;
    private boolean canPresentShareDialogWithPhotos;
	
	public SFacebookProxy(Context context) {
		/*FacebookSdk.sdkInitialize(context.getApplicationContext(), new InitializeCallback() {
			@Override
			public void onInitialized() {

			}
		});*/
		PL.i("the jar version:" + BuildConfig.JAR_VERSION);//打印版本号
	}
	
	public static void initFbSdk(Context context){
		FacebookSdk.sdkInitialize(context.getApplicationContext());
	}
	
	public static void activateApp(Context context){

		if (TextUtils.isEmpty(FbResUtil.findStringByName(context,"facebook_app_id"))) {
			Toast.makeText(context, "fb applicationId is empty", Toast.LENGTH_LONG).show();
		}else{
			activateApp(context, FbResUtil.findStringByName(context,"facebook_app_id"));
		}
		
	}
	
	public static void activateApp(final Context context,final String appId){
//		FacebookSdk.sdkInitialize(context.getApplicationContext(),new InitializeCallback() {
//
//			@Override
//			public void onInitialized() {
//				Log.d(FB_TAG, "InitializeCallback");
//				AppEventsLogger.activateApp(context, appId);
//			}
//		});
		if (context instanceof Activity) {
			AppEventsLogger.activateApp(((Activity)context).getApplication(), appId);
		}else {
			AppEventsLogger.activateApp(context, appId);
		}
	}
	
	public static void trackingEvent(Activity activity,String eventName){
		trackingEvent(activity, eventName,null,null);
	}

	public static void trackingEvent(Activity activity,String eventName,double valueToSum){
		trackingEvent(activity, eventName,valueToSum,null);
	}
	
	public static void trackingEvent(final Activity activity,final String eventName, final Double valueToSum, final Bundle parameters){
		if (activity == null || TextUtils.isEmpty(eventName)) {
			return;
		}
		
		/*FacebookSdk.sdkInitialize(activity.getApplicationContext(), new InitializeCallback() {

			@Override
			public void onInitialized() {
				Log.d(FB_TAG, "InitializeCallback");
				AppEventsLogger logger = AppEventsLogger.newLogger(activity);
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
		});*/
		AppEventsLogger logger = AppEventsLogger.newLogger(activity);
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

	public void fbLogin(final Fragment fragment, final FbLoginCallBack fbLoginCallBack) {
		if (loginManager == null) {
			loginManager = LoginManager.getInstance();
		}
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult result) {
				Log.d(FB_TAG, "onSuccess");
				if(result != null) {
					Log.d(FB_TAG, "login result: " + result.toString());
				}
				if (fbLoginCallBack == null){
					return;
				}
				User user = new User();
				user.setUserId(result.getAccessToken().getUserId());
				FbSp.saveFbId(fragment.getActivity(),user.getUserId());
				requestTokenForBusines(fragment.getActivity(),user, fbLoginCallBack);
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

		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken == null) {
			Log.d(FB_TAG, "accessToken == null");
			loginManager.setDefaultAudience(defaultAudience);
			loginManager.setLoginBehavior(loginBehavior);
			loginManager.logInWithReadPermissions(fragment, permissions);

		} else {
			getMyProfile(fragment.getActivity(), new FbLoginCallBack() {
				@Override
				public void onCancel() {
					Log.d(FB_TAG, "onCancel");
					if (fbLoginCallBack != null) {
						fbLoginCallBack.onCancel();
					}
				}

				@Override
				public void onError(String message) {
					Log.d(FB_TAG, "onError:" + message);
					if (fbLoginCallBack != null) {
						fbLoginCallBack.onError(message);
					}
				}

				@Override
				public void onSuccess(User user) {
					FbSp.saveFbId(fragment.getActivity(),user.getUserId());
					requestTokenForBusines(fragment.getActivity(),user, fbLoginCallBack);
				}
			});
		}

	}


		/**
         * <p>Description: fb 登陆</p>
         * @param activity
         * @param fbLoginCallBack
         * @date 2015年11月20日
         */
	public void fbLogin(final Activity activity, final FbLoginCallBack fbLoginCallBack) {

		if (loginManager == null) {
			loginManager = LoginManager.getInstance();
		}
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult result) {
				Log.d(FB_TAG, "onSuccess");
				if(result != null && result.getAccessToken() != null) {
					PL.d(FB_TAG, "login result: " + result.getAccessToken().getUserId() + "  token: " + result.getAccessToken().getToken());
					PL.d(FB_TAG, "getApplicationId: " + result.getAccessToken().getApplicationId());
				}
				if (fbLoginCallBack == null){
					PL.d(FB_TAG, "fbLoginCallBack null, return");
					return;
				}
				User user = new User();
				user.setUserId(result.getAccessToken().getUserId());
				user.setAccessTokenString(AccessToken.getCurrentAccessToken().getToken());
				user.setFacebookAppId(result.getAccessToken().getApplicationId());
				FbSp.saveFbId(activity,user.getUserId());
//				requestTokenForBusines(activity,user, fbLoginCallBack);
				if(fbLoginCallBack != null) {
					fbLoginCallBack.onSuccess(user);
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

		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		if (accessToken != null && !accessToken.isExpired()) {
			Log.d(FB_TAG, "login result: onSuccess: accessToken != null" );
			String fbThirdId = accessToken.getUserId();
			String appId = accessToken.getApplicationId();
			String token = accessToken.getToken();
			Log.d(FB_TAG, "fbThirdId: " + fbThirdId + "  appId: " + appId + "  token: " + token);
			User user = new User();
			user.setUserId(fbThirdId);
			user.setAccessTokenString(token);
			user.setFacebookAppId(appId);
			if(fbLoginCallBack != null) {
				fbLoginCallBack.onSuccess(user);
			}
		} else {
			Log.d(FB_TAG, "accessToken == null");
			loginManager.setDefaultAudience(defaultAudience);
			loginManager.setLoginBehavior(loginBehavior);
			loginManager.logInWithReadPermissions(activity, permissions);
//			Log.d(FB_TAG, "accessToken == " + accessToken);
//			getMyProfile(activity, new FbLoginCallBack() {
//				@Override
//				public void onCancel() {
//					Log.d(FB_TAG, "onCancel");
//					if (fbLoginCallBack != null) {
//						fbLoginCallBack.onCancel();
//					}
//				}
//
//				@Override
//				public void onError(String message) {
//					Log.d(FB_TAG, "onError:" + message);
//					if (fbLoginCallBack != null) {
//						fbLoginCallBack.onError(message);
//					}
//				}
//
//				@Override
//				public void onSuccess(User user) {
//					FbSp.saveFbId(activity,user.getUserId());
//					requestTokenForBusines(activity,user, fbLoginCallBack);
//				}
//			});
		}

	}

	/**
	 * 获取token_for_business,登陆完成后获取
	 * @param activity
	 * @param user
	 * @param fbLoginCallBack
	 */
	public void requestTokenForBusines(final Activity activity, final User user, final FbLoginCallBack fbLoginCallBack){
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
			loginManager.logOut();
		}else {
			LoginManager.getInstance().logOut();
		}
	}
	
	public void fbShare(Activity activity, final FbShareCallBack fbShareCallBack, String shareCaption, String shareDescrition,
						String shareLinkUrl, String sharePictureUrl) {
		fbShare(activity, fbShareCallBack, shareCaption, shareDescrition, shareLinkUrl, sharePictureUrl, "");
	}
	public void fbShare(Activity activity, final FbShareCallBack fbShareCallBack, String shareCaption, String shareDescrition,
						String shareLinkUrl, String sharePictureUrl, String shareLinkName) {
		
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
		if (!ShareDialog.canShow(ShareLinkContent.class)){
			ToastUtils.toast(activity,"share error");
		}

		ShareDialog shareDialog = new ShareDialog(activity);
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		shareDialog.registerCallback(callbackManager, shareCallback);
		
		/*
		 * Note: If your app share links to the iTunes or Google Play stores, we
		 * do not post any images or descriptions that you specify in the share.
		 * Instead we post some app information we scrape from the app store
		 * directly with the Webcrawler. This may not include images.
		 */
		Builder builder = new Builder().setContentTitle(shareCaption).setContentDescription(
				shareDescrition);
		if (!TextUtils.isEmpty(shareLinkUrl)) {
			builder.setContentUrl(Uri.parse(shareLinkUrl));
		}
		if (!TextUtils.isEmpty(sharePictureUrl)) {
			builder.setImageUrl(Uri.parse(sharePictureUrl));
		}
		ShareLinkContent linkContent = builder.build();

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
		if (canPresentShareDialogWithPhotos) {
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
/*	public void shareVideohh(){
		Bundle params = new Bundle();
		params.putString("source", "{video-data}");
		 make the API call 
		new GraphRequest(
		    AccessToken.getCurrentAccessToken(),
		    "/{page-id}/videos",
		    params,
		    HttpMethod.POST,
		    new GraphRequest.Callback() {
		        public void onCompleted(GraphResponse response) {
		             handle the result 
		        }
		    }
		).executeAsync();
	}
	
	public void shareVideo(Activity activity){
		if (!AccessToken.getCurrentAccessToken().getPermissions().contains(PUBLISH_PERMISSIONS)) {
			Log.d(FB_TAG, "not publish_actions");
		}
		FacebookCallback<Sharer.Result> shareCallback = new FacebookCallback<Sharer.Result>() {
			@Override
			public void onCancel() {
				Log.d(FB_TAG, "onCancel");
			}

			@Override
			public void onError(FacebookException error) {
				Log.d(FB_TAG, "onError");
				error.printStackTrace();
			}

			@Override
			public void onSuccess(Sharer.Result result) {
				Log.d(FB_TAG, "onSuccess");
				if (result.getPostId() != null) {
					String id = result.getPostId();
				}
				
			}

		};
		
		ShareDialog shareDialog = new ShareDialog(activity);
		if (callbackManager == null) {
			callbackManager = CallbackManager.Factory.create();
		}
		shareDialog.registerCallback(callbackManager, shareCallback);
			
		File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/mmmmmm.mp4");
		if (f.exists() && f.isFile()) {
			if (ShareDialog.canShow(ShareVideoContent.class)) {
				
				Uri videoUrl = Uri.fromFile(f);
				ShareVideo video = new ShareVideo.Builder()
						.setLocalUrl(videoUrl)
						.build();
				ShareVideoContent content = new ShareVideoContent.Builder()
						.setVideo(video)
						.setContentTitle("Video Title")
						.setContentDescription("Video description")
						.build();
				//ShareDialog.show(activity, content);
				ShareApi.share(content, shareCallback);
			}else{
				Log.d(FB_TAG, "you cannot share videos");
			}
		}
		
	}*/
	
	public void requestInviteFriends(Activity activity,final Bundle b, final RequestFriendsCallBack requestFriendsCallBack){
		
		fbLogin(activity, new FbLoginCallBack() {
			
			
			@Override
			public void onSuccess(User user) {
				
				Bundle bundle = b;
				AccessToken accessToken = AccessToken.getCurrentAccessToken();
				if (bundle == null) {
					bundle = new Bundle();
					bundle.putString("limit", "2000");
					bundle.putString("fields", "name,picture.width(300)");
				}
				if (accessToken != null) {
					new GraphRequest(accessToken, "/me/invitable_friends", bundle, HttpMethod.GET, new GraphRequest.Callback() {
						public void onCompleted(GraphResponse response) {
							Log.d(FB_TAG, "invite:" + response.toString());
							if (requestFriendsCallBack != null && response != null) {
								List<FriendProfile>  friends = JsonUtil.parseInviteFriendsJson(response.getJSONObject());
								requestFriendsCallBack.onSuccess(response.getJSONObject(),friends);
							}
						}
					}).executeAsync();
				}else{
					if (requestFriendsCallBack != null) {
						requestFriendsCallBack.onError();
					}
				}
				
			}
			
			@Override
			public void onError(String message) {
				Log.d(FB_TAG, "onError:" + message);
				if (requestFriendsCallBack != null) {
					requestFriendsCallBack.onError();
				}
				
			}
			
			@Override
			public void onCancel() {
				if (requestFriendsCallBack != null) {
					requestFriendsCallBack.onError();
				}	
			}
		});
		
		
	}
	
	
	public void inviteFriends(Activity activity, List<FriendProfile> friendProfileIdsList, String message, final FbInviteFriendsCallBack fbInviteFriendsCallBack) {
		
		if (friendProfileIdsList != null && !friendProfileIdsList.isEmpty()) {

			List<FriendProfile> invitingList = new ArrayList<>();
			for (FriendProfile friend : friendProfileIdsList){
				if(!TextUtils.isEmpty(friend.getId())){
					invitingList.add(friend);
				}
			}

			inviteFriendinBatches(activity,invitingList,message, fbInviteFriendsCallBack);
		}
	}

	/* 处理分批次邀请的逻辑*/
	private void inviteFriendinBatches(final Activity activity, final List<FriendProfile>
			invitingList, final String message, final FbInviteFriendsCallBack callback) {

		final List<FriendProfile> currentList = new ArrayList<FriendProfile>();
		while (currentList.size() < 40 && invitingList.size() > 0) {
			currentList.add(invitingList.remove(0));
		}

		StringBuilder stringBuilder = new StringBuilder();
		for (FriendProfile friendProfile : currentList) {
			stringBuilder.append(friendProfile.getId()).append(",");
		}

//		final List<String> requestRecipientsId = new ArrayList<String>();

		inviteFriends(activity, stringBuilder.toString(), message, callback);

	}
	
	
	public void inviteFriends(final Activity activity,final String inviteFriendIds,final String message,final FbInviteFriendsCallBack fbInviteFriendsCallBack) {
		if (TextUtils.isEmpty(message)) {
			Toast.makeText(activity, "request message is empty", Toast.LENGTH_SHORT).show();
			return;
		}
		fbLogin(activity, new FbLoginCallBack() {
			
			@Override
			public void onSuccess(User user) {
				GameRequestDialog requestDialog = new GameRequestDialog(activity);
				if (callbackManager == null) {
					callbackManager = CallbackManager.Factory.create();
				}
				requestDialog.registerCallback(callbackManager, new FacebookCallback<GameRequestDialog.Result>() {
					public void onSuccess(GameRequestDialog.Result result) {
						String id = result.getRequestId();
						List<String> requestRecipients = result.getRequestRecipients();
						Log.d(FB_TAG, "inviteFriends:" + id);
						if (fbInviteFriendsCallBack != null) {
							fbInviteFriendsCallBack.onSuccess(id,requestRecipients);
						}
					}

					public void onCancel() {
						Log.d(FB_TAG, "inviteFriends onCancel");
						if (fbInviteFriendsCallBack != null) {
							fbInviteFriendsCallBack.onCancel();
						}
					}

					public void onError(FacebookException error) {
						Log.d(FB_TAG, "inviteFriends onError:" + error.getMessage());
						if (fbInviteFriendsCallBack != null) {
							fbInviteFriendsCallBack.onError(error.getMessage());
						}
					}
				});
				
				 GameRequestContent content = new GameRequestContent.Builder()
			                .setMessage(message)
			                .setTo(inviteFriendIds)
			                
			                //.setActionType(ActionType.SEND)
			                .build();
			     requestDialog.show(content);
				
			}
			
			@Override
			public void onError(String message) {
				if (fbInviteFriendsCallBack != null) {
					fbInviteFriendsCallBack.onError(message);
				}
				
			}
			
			@Override
			public void onCancel() {
				if (fbInviteFriendsCallBack != null) {
					fbInviteFriendsCallBack.onCancel();
				}
			}
		});
		
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
	
	
	public void getMyProfile(Activity activity, final FbLoginCallBack callBack) {
		// me?fields=name,id,first_name,last_name
		Bundle b = new Bundle();
		b.putString("fields", "name,id,picture.width(300)");
		AccessToken accessToken = AccessToken.getCurrentAccessToken();
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
					String picture = "";
					if(userInfo.optJSONObject("picture") != null && userInfo.optJSONObject("picture").optJSONObject("data") != null) {
						picture = userInfo.optJSONObject("picture").optJSONObject("data").optString("url", "");
					}
					User user = new User();
					user.setUserId(id);
					user.setName(name);
					user.setPictureUri(Uri.parse(picture));
					if (callBack != null) {
						callBack.onSuccess(user);
					}
				}
			}
		}).executeAsync();
	}
	
	public void requestMyFriends(Activity activity, final Bundle mBundle, final RequestFriendsCallBack myFiendsCallBack) {

		fbLogin(activity, new FbLoginCallBack() {
			@Override
			public void onCancel() {
				if (myFiendsCallBack != null) {
					myFiendsCallBack.onError();
				}
			}

			@Override
			public void onError(String message) {
				if (myFiendsCallBack != null) {
					myFiendsCallBack.onError();
				}
			}

			@Override
			public void onSuccess(User user) {

				Bundle bundle = mBundle;

				AccessToken accessToken = AccessToken.getCurrentAccessToken();
				if (accessToken != null) {
					if (bundle == null) {
						bundle = new Bundle();
						bundle.putString("fields", "friends.limit(2000){name,picture.width(300)}");
					}
					new GraphRequest(accessToken, "/me", bundle, HttpMethod.GET, new GraphRequest.Callback() {

						@Override
						public void onCompleted(GraphResponse response) {
							if (response == null) {
								if (myFiendsCallBack != null) {
									myFiendsCallBack.onError();
								}
							} else if (response.getError() != null) {
								Log.e(FB_TAG, response.getError().getErrorMessage() + "");
								if (myFiendsCallBack != null) {
									myFiendsCallBack.onError();
								}
							} else {
								Log.d(FB_TAG, "response:" + response.toString());
								if (myFiendsCallBack != null) {
									List<FriendProfile>  friends = JsonUtil.parseMyFriendsJson(response.getJSONObject());
									myFiendsCallBack.onSuccess(response.getJSONObject(), friends);
								}
							}

						}
					}).executeAsync();
				} else {
					if (myFiendsCallBack != null) {
						myFiendsCallBack.onError();
					}
				}
			}
		});

	}

	public void requestMyFriends(Activity activity,final RequestFriendsCallBack fbMyFiendsCallBack){

		/*fbLogin(activity, new FbLoginCallBack() {

			@Override
			public void onSuccess(User user) {

				AccessToken accessToken =  AccessToken.getCurrentAccessToken();

				if (accessToken != null) {
					GraphRequest.newMyFriendsRequest(accessToken, new GraphRequest.GraphJSONArrayCallback() {

						@Override
						public void onCompleted(JSONArray objects, GraphResponse response) {
							Log.d(FB_TAG, "objects:" + objects.toString());
							Log.d(FB_TAG, "response:" + response.toString());
							if (fbMyFiendsCallBack != null) {
								fbMyFiendsCallBack.onSuccess(objects, response.getJSONObject());
							}
						}
					}).executeAsync();
				}else{
					if (fbMyFiendsCallBack != null) {
						fbMyFiendsCallBack.onError();
					}
				}

			}

			@Override
			public void onError(String message) {
				Log.d(FB_TAG, "onError:" + message);
				if (fbMyFiendsCallBack != null) {
					fbMyFiendsCallBack.onError();
				}

			}

			@Override
			public void onCancel() {
				if (fbMyFiendsCallBack != null) {
					fbMyFiendsCallBack.onError();
				}
			}
		});*/

		requestMyFriends(activity,null, fbMyFiendsCallBack);
	}
	
	public void shareToMessenger(Activity activity, String picPath, FbShareCallBack fbShareCallBack) {
		if(TextUtils.isEmpty(picPath)) {
			Log.e(FB_TAG, "shareToMessenger 图片路径为空");
			return;
		}
		File tempFile = new File(picPath);
		Uri picUri = Uri.fromFile(tempFile);
		shareToMessenger(activity, picUri, fbShareCallBack);
	}
	
	public void shareToMessenger(Activity activity, Uri picUri, FbShareCallBack fbShareCallBack) {
		if(picUri == null) {
			Log.e(FB_TAG, "shareToMessenger 图片Uri为空");
			return;
		}
		this.shareCallBack = fbShareCallBack;
		String mimeType = "image/*";
		ShareToMessengerParamsBuilder newBuilder = ShareToMessengerParams.newBuilder(picUri, mimeType);
		ShareToMessengerParams shareToMessengerParams = newBuilder.build();
		
		MessengerUtils.shareToMessenger(activity, REQUEST_TOMESSENGER, shareToMessengerParams);
	}

	
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
		 if (callbackManager != null) {
			callbackManager.onActivityResult(requestCode, resultCode, data);
		}
		 if(requestCode == REQUEST_TOMESSENGER) {
			 Log.d(FB_TAG, "to messenger 回调");
			 if(shareCallBack != null) {
				 shareCallBack.onSuccess();
			 }
		 }
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
		public void onSuccess(User user);
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
		public void onSuccess(JSONObject graphObject, List<FriendProfile> friendProfiles);
	}
	
//	public interface FbMyFriendsCallBack {
//		public void onError();
//		public void onSuccess(JSONArray objects, JSONObject graphObject);
//	}
	public interface FbBusinessIdCallBack {
		public void onError();
		public void onSuccess(String businessId);
	}
	
	
	
	public class User{
		
		String userId;

		/**
		 * @return the userId
		 */
		public String getUserId() {
			return userId;
		}

		/**
		 * @param userId the userId to set
		 */
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
//	    private  String firstName;
//	    private  String middleName;
//	    private  String lastName;
	    private  String name;
//	    private  Uri linkUri;
	    
//	    private String gender;
		private Uri pictureUri;

		private String tokenForBusiness;
		private String accessTokenString;
		private String facebookAppId;


		/**
		 * @return the firstName
		 */
//		public String getFirstName() {
//			return firstName;
//		}

		/**
		 * @param firstName the firstName to set
		 */
//		public void setFirstName(String firstName) {
//			this.firstName = firstName;
//		}

		/**
		 * @return the middleName
		 */
//		public String getMiddleName() {
//			return middleName;
//		}

		/**
		 * @param middleName the middleName to set
		 */
//		public void setMiddleName(String middleName) {
//			this.middleName = middleName;
//		}

		/**
		 * @return the lastName
		 */
//		public String getLastName() {
//			return lastName;
//		}

		/**
		 * @param lastName the lastName to set
		 */
//		public void setLastName(String lastName) {
//			this.lastName = lastName;
//		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the linkUri
		 */
//		public Uri getLinkUri() {
//			return linkUri;
//		}
//
//		/**
//		 * @param linkUri the linkUri to set
//		 */
//		public void setLinkUri(Uri linkUri) {
//			this.linkUri = linkUri;
//		}
//
//
//		public String getGender() {
//			return gender;
//		}
//
//		public void setGender(String gender) {
//			this.gender = gender;
//		}

		public Uri getPictureUri() {
			return pictureUri;
		}

		public void setPictureUri(Uri pictureUri) {
			this.pictureUri = pictureUri;
		}

		public String getAccessTokenString() {
			return accessTokenString;
		}

		public void setAccessTokenString(String accessTokenString) {
			this.accessTokenString = accessTokenString;
		}

		public String getFacebookAppId() {
			return facebookAppId;
		}

		public void setFacebookAppId(String facebookAppId) {
			this.facebookAppId = facebookAppId;
		}

		@Override
		public String toString() {
			return "User{" +
					"userId='" + userId + '\'' +
					", name='" + name + '\'' +
					", pictureUri=" + pictureUri + '\'' +
					", tokenForBusiness='" + tokenForBusiness + '\'' +
					", facebookAppId='" + facebookAppId + '\'' +
					'}';
		}

		public String getTokenForBusiness() {
			return tokenForBusiness;
		}

		public void setTokenForBusiness(String tokenForBusiness) {
			this.tokenForBusiness = tokenForBusiness;
		}
	}

}
