package com.thirdlib.huawei;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.core.base.utils.PL;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.common.CommonConstant;
import com.thirdlib.line.SLineSignIn;

public class HuaweiSignIn {

	public static final int Huawei_LOGIN_REQUEST_CODE = 8002;

	private Activity activity;
	private Fragment fragment;

	private Dialog mConnectionProgressDialog;

	boolean isCancel = false;



	// 华为帐号登录授权服务，提供静默登录接口silentSignIn，获取前台登录视图getSignInIntent，登出signOut等接口
	private AccountAuthService mAuthService;

	// 华为帐号登录授权参数
	private AccountAuthParams mAuthParam;


	HWSignInCallBack signInCallBack;

	public HuaweiSignIn(Activity activity) {

		if (activity == null) {
			PL.e("SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		initxx();
	}

	public HuaweiSignIn(Activity activity, Dialog dialog) {

		if (activity == null) {
			PL.e("SGoogleSignIn activity must not null");
			return;
		}

		this.activity = activity;
		this.mConnectionProgressDialog = dialog;
		initxx();
	}

	public HuaweiSignIn(FragmentActivity fragmentActivity, Fragment fragment, Dialog dialog) {

		if (fragmentActivity == null) {
			PL.e("SGoogleSignIn fragmentActivity must not null");
			return;
		}
		this.activity = fragmentActivity;
		this.mConnectionProgressDialog = dialog;

		this.fragment = fragment;
		initxx();
	}

	private void initxx(){
		initDialog();
	}

	public void startSignIn(Activity activity, HWSignInCallBack mSignInCallBack){
		silentSignInByHwId(activity, mSignInCallBack);
	}

	//退出华为账号
	public void signOut(Activity activity) {
		if (mAuthService == null){
			return;
		}
		Task<Void> signOutTask = mAuthService.signOut();
		signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				PL.i("huawei signOut Success");
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				PL.i("huawei signOut fail");
			}
		});
	}

	//取消授权登录
	private void cancelAuthorization() {

		if (mAuthService == null){
			return;
		}

		Task<Void> task = mAuthService.cancelAuthorization();
		task.addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				PL.i("cancelAuthorization success");
			}
		});
		task.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				PL.i("cancelAuthorization failure:" + e.getClass().getSimpleName());
			}
		});
	}



	/**
	 * 静默登录，如果设备上的华为帐号系统已经登录，并且用户已经授权过，无需再拉起登录页面和授权页面，
	 * 将直接静默登录成功，在成功监听器中，返回帐号信息;
	 * 如果华为帐号系统帐号未登录或者用户没有授权，静默登录会失败，需要显式拉起前台登录授权视图。
	 */
	private void silentSignInByHwId(Activity activity, HWSignInCallBack mSignInCallBack) {

		this.signInCallBack = mSignInCallBack;

		// 1、配置登录请求参数AccountAuthParams，包括请求用户id(openid、unionid)、email、profile（昵称、头像）等。
		// 2、DEFAULT_AUTH_REQUEST_PARAM默认包含了id和profile（昵称、头像）的请求。
		// 3、如需要请求获取用户邮箱，需要setEmail();
		mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
				.setEmail()
				.createParams();

		// 使用请求参数构造华为帐号登录授权服务AccountAuthService
		mAuthService = AccountAuthManager.getService(activity, mAuthParam);

		// 使用静默登录进行华为帐号登录
		Task<AuthAccount> task = mAuthService.silentSignIn();
		task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
			@Override
			public void onSuccess(AuthAccount authAccount) {
				// 静默登录成功，处理返回的帐号对象AuthAccount，获取帐号信息
				dealWithResultOfSignIn(authAccount);
			}
		});
		task.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(Exception e) {
				// 静默登录失败，使用getSignInIntent()方法进行前台显式登录
				if (e instanceof ApiException) {
					ApiException apiException = (ApiException) e;
					Intent signInIntent = mAuthService.getSignInIntent();
					// 如果应用是全屏显示，即顶部无状态栏的应用，需要在Intent中添加如下参数：
//					signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
					// 具体详情可以参见应用调用登录接口的时候是全屏页面，为什么在拉起登录页面的过程中顶部的状态栏会闪一下？应该如何解决？
					signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
					activity.startActivityForResult(signInIntent, Huawei_LOGIN_REQUEST_CODE);
				}else {
					if (signInCallBack != null) {
						signInCallBack.failure();
					}
				}
			}
		});
	}

	/**
	 * 处理返回的AuthAccount，获取帐号信息
	 *
	 * @param authAccount AuthAccount对象，用于记录帐号信息
	 */
	private void dealWithResultOfSignIn(AuthAccount authAccount) {
		// 获取帐号信息
		PL.i("display name:" + authAccount.getDisplayName());
//		Log.i(TAG, "photo uri string:" + authAccount.getAvatarUriString());
//		Log.i(TAG, "photo uri:" + authAccount.getAvatarUri());
		PL.i("email:" + authAccount.getEmail());
//		Log.i(TAG, "openid:" + authAccount.getOpenId());
		PL.i("unionid:" + authAccount.getUnionId());
		// TODO 获取用户信息后业务逻辑
		isCancel = false;
		if (this.signInCallBack != null){
			this.signInCallBack.success(authAccount.getUnionId(), authAccount.getDisplayName(), authAccount.getEmail(),"");
		}
	}


	public void handleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
		PL.i("handleActivityResult --> " + requestCode + "  --> " +  resultCode);
		if (requestCode != Huawei_LOGIN_REQUEST_CODE) {
			return;
		}
		if (isCancel) {
			return;
		}
		isCancel = false;
		dimissDialog();

		Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
		if (authAccountTask.isSuccessful()) {
			PL.i("onActivitResult authAccountTask.isSuccessful()");
			// 登录成功，获取到登录帐号信息对象authAccount
			AuthAccount authAccount = authAccountTask.getResult();
			dealWithResultOfSignIn(authAccount);
		} else {
			// 登录失败，status code标识了失败的原因，请参见API参考中的错误码了解详细错误原因
			PL.i("sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
			if (signInCallBack != null) {
				signInCallBack.failure();
			}
		}
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
				if (signInCallBack != null) {
					signInCallBack.failure();
				}
			}
		});

	}

	private void dimissDialog() { //关闭loading窗
		if(mConnectionProgressDialog != null) {
			PL.i("dimiss dialog");
			mConnectionProgressDialog.dismiss();
		}
	}


//	============firebase 1=================


	public interface HWSignInCallBack{
		void success(String id, String mFullName, String mEmail, String idTokenString);
		void failure();
	}

}
