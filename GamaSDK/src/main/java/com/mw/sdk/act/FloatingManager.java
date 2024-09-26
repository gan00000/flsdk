package com.mw.sdk.act;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.ApkInfoUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ScreenHelper;
import com.mw.sdk.R;
import com.mw.sdk.api.Request;
import com.mw.sdk.bean.res.RedDotRes;
import com.mw.sdk.callback.FloatButtionClickCallback;
import com.mw.sdk.callback.FloatViewMoveListener;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.widget.FloatImageView;

import java.util.Timer;

/**
 * 
 * @author itxuxxey
 *
 */
public class FloatingManager {

	private static FloatingManager floatingManager;
	Timer redPointReqTimer;

	public Activity activity;

	int screenWidth = 0;
	int screenHeight = 0;
	private int navigationBarHeight;//竖屏影响Y坐标，横屏影响X坐标
	private int statusBarHeight;

	private String oldLoginTimestamp;

	WindowManager mWindowManager = null;
	RelativeLayout floatLayout;
	private FloatImageView floatImageView;
	private View floatReddotView;
	WindowManager.LayoutParams mWindowParamsForFloatBtn = null;

	RelativeLayout.LayoutParams floatImageViewLayoutParams;

	private boolean floatHiddenState = false;//悬浮按钮是否正在隐藏半边状态
	private boolean hasEndMove = true;

	private RedDotRes redDotRes;
	public RedDotRes getRedDotRes() {
		return redDotRes;
	}

	public void setRedDotRes(RedDotRes redDotRes) {
		this.redDotRes = redDotRes;
	}

	private Runnable suoxiaoRunnable = new Runnable() {
		@Override
		public void run() {
			if (hasEndMove && floatLayout != null && mWindowManager != null && floatImageViewLayoutParams!= null){
				if (mWindowParamsForFloatBtn.x == 0){
					mWindowParamsForFloatBtn.x = -floatImageViewLayoutParams.width / 3 * 2;
				}else {
					if (ScreenHelper.isPortrait(activity)){
						mWindowParamsForFloatBtn.x = screenWidth - floatImageViewLayoutParams.width / 3;
					}else {
						mWindowParamsForFloatBtn.x = screenWidth - floatImageViewLayoutParams.width / 3;
					}
				}
				mWindowManager.updateViewLayout(floatLayout, mWindowParamsForFloatBtn);
				floatHiddenState = false;
			}
		}
	};
	private boolean isFullWindows = false;
	private boolean isHasNavigationBar = false;

	private FloatButtionClickCallback floatButtionClickCallback;

	private FloatingManager() {
	}

	// 获得实例
	public static FloatingManager getInstance() {
		if (floatingManager == null) {
			floatingManager = new FloatingManager();
		}
		return floatingManager;
	}
	// 展示
	public void initFloatingView(final Activity ctx, String iconUrl, FloatButtionClickCallback floatButtionClickCallback) {

		String loginTimestamp = SdkUtil.getSdkTimestamp(ctx);
		if (mWindowManager != null && SStringUtil.isNotEmpty(oldLoginTimestamp) && loginTimestamp.equals(oldLoginTimestamp)){
			//没有重新登陆，不用重新生成悬浮，防止造成重复
			PL.i("没有重新登陆，不用重新生成悬浮，防止造成重复");
			return;
		}
		oldLoginTimestamp = loginTimestamp;
		windowManagerFinish();

		this.activity = ctx;
		this.floatButtionClickCallback = floatButtionClickCallback;
		mWindowManager = ctx.getWindowManager();

		initParams();

		floatLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.sdk_float_item, null);
		floatImageView = floatLayout.findViewById(R.id.id_iv_sdk_float_item);
		floatReddotView = floatLayout.findViewById(R.id.id_v_float_reddot);
		floatImageViewLayoutParams = (RelativeLayout.LayoutParams) floatImageView.getLayoutParams();

		floatImageView.setClickable(true);
		floatImageView.setSceenWidth(screenWidth);
		floatImageView.setSceenHeight(screenHeight);
		Glide.with(floatImageView)
				.load(iconUrl + "?" + System.currentTimeMillis())
				.centerCrop()
				.placeholder(ApkInfoUtil.getAppIcon(activity))
				.into(floatImageView);

		// 生成悬浮按钮的参数
		mWindowParamsForFloatBtn = getCricleButtonParams(0, screenHeight/3);

		mWindowManager.addView(floatLayout, mWindowParamsForFloatBtn);

//		getNotchInfo();
		/*floatLayout.setChangedListener(new EEEBaseRelativeLayout.ConfigurationChangedListener() {
			@Override
			public void onConfigurationChanged(Configuration newConfig) {

				PL.d("floatLayout.setChangedListener");
				floatLayout.postDelayed(new Runnable() {
					@Override
					public void run() {

						initParams();

						floatBtn.setSceenWidth(screenWidth);
						floatBtn.setSceenHeight(screenHeight);

						mWindowManager.updateViewLayout(floatLayout, getCricleButtonParams(- floatBtn_width / 2, screenHeight / 2));
						getNotchInfo();
						if (menuViewLayout != null && menuViewLayout.getVisibility() == View.VISIBLE) {
							mWindowManager.updateViewLayout(menuViewLayout, getWindowMangerParamsForMenuLayout());
						}
					}
				}, 1000 * 1);

			}
		});*/

		addListener();
		floatLayout.postDelayed(suoxiaoRunnable, 2000);


//		if (redPointReqTimer != null){
//			redPointReqTimer.cancel();
//			redPointReqTimer = null;
//		}
//		redPointReqTimer = new Timer();
//
//		redPointReqTimer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//
//				try {
//					requestRedPoint();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//
//			}
//		}, 20 * 1000, 10 * 60 * 1000);

		try {
			requestRedPoint(ctx);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	private void getNotchInfo() {
//		NotchScreenManager.getInstance().getNotchInfo(this.activity, new INotchScreen.NotchScreenCallback() {
//			@Override
//			public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
//				xNotchScreenInfo = notchScreenInfo;
//				if (xNotchScreenInfo != null && xNotchScreenInfo.hasNotch && !screenHelper.isPortrait()){
//
//					initInNotch();
//					if (floatLayout != null && mWindowParamsForFloatBtn != null) {
//						mWindowManager.updateViewLayout(floatLayout, mWindowParamsForFloatBtn);
//					}
//				}
//			}
//		});
//	}

	// 设置悬浮按钮的监听
	private void addListener() {

		floatImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				PL.d("floatBtn.setOnClickListener");
				if (floatHiddenState){
					if (mWindowParamsForFloatBtn.x < 0){
						mWindowParamsForFloatBtn.x = 0;
					}else {
						mWindowParamsForFloatBtn.x = screenWidth - floatImageViewLayoutParams.width;
					}
					mWindowManager.updateViewLayout(floatLayout, mWindowParamsForFloatBtn);
					floatHiddenState = true;
				}else {//点击按钮
					PL.d("floatBtn click show content view");
					if (floatButtionClickCallback != null){
						floatButtionClickCallback.show("");
					}
				}
			}
		});

		floatImageView.setFloatViewMoveListener(new FloatViewMoveListener() {

			@Override
			public void move(FloatImageView view, int x, int y, boolean isEndMove) {

				PL.d("isEndMove = " + isEndMove + ",imageview end move x=:" + x + "  y=" + y);
				floatHiddenState = false;
				if (isEndMove) {//结束

					hasEndMove = true;
					if (ScreenHelper.isPortrait(activity)) {//竖屏
						if (x == 0){
							mWindowParamsForFloatBtn.x = 0;
						}else{
							mWindowParamsForFloatBtn.x = x;
						}
					}else{
						if (x == 0){
							mWindowParamsForFloatBtn.x = 0;
						}else{
							mWindowParamsForFloatBtn.x = screenWidth - floatImageViewLayoutParams.width;
						}

					}
				} else {//移动中

					floatLayout.removeCallbacks(suoxiaoRunnable);

					if (hasEndMove) {
						//
					}

					hasEndMove = false;
					mWindowParamsForFloatBtn.x = mWindowParamsForFloatBtn.x + x;
				}

				mWindowParamsForFloatBtn.y = mWindowParamsForFloatBtn.y + y;

				if (ScreenHelper.isPortrait(activity)){//竖屏

					if (mWindowParamsForFloatBtn.x >= screenWidth - floatImageViewLayoutParams.width) {
						mWindowParamsForFloatBtn.x = screenWidth - floatImageViewLayoutParams.width;
					}

					if (mWindowParamsForFloatBtn.x < 0) {//限制滑出屏幕
						mWindowParamsForFloatBtn.x = 0;
					}

					if (mWindowParamsForFloatBtn.y >= (screenHeight - floatImageViewLayoutParams.height)) {//竖屏y不包含导航栏
						mWindowParamsForFloatBtn.y = screenHeight - floatImageViewLayoutParams.height;
						PL.d("V Y max");
					}
					if(mWindowParamsForFloatBtn.y <= statusBarHeight){
						mWindowParamsForFloatBtn.y = statusBarHeight;
					}

				}else {//横屏

					if (mWindowParamsForFloatBtn.x >= screenWidth - floatImageViewLayoutParams.width) {
						mWindowParamsForFloatBtn.x = screenWidth - floatImageViewLayoutParams.width;
					}

					if (mWindowParamsForFloatBtn.x < 0) {//限制滑出屏幕
						mWindowParamsForFloatBtn.x = 0;
					}

					if(mWindowParamsForFloatBtn.y >= screenHeight - floatImageViewLayoutParams.height){
						mWindowParamsForFloatBtn.y = screenHeight - floatImageViewLayoutParams.height;
						PL.d("H Y max");
					}

					if(mWindowParamsForFloatBtn.y <= statusBarHeight){
						mWindowParamsForFloatBtn.y = statusBarHeight;
					}

				}

				PL.d("mWindowParamsForFloatBtn:x=" + mWindowParamsForFloatBtn.x + "-- mWindowParamsForFloatBtn:y=" + mWindowParamsForFloatBtn.y);
				PL.d("screenWidth=" + screenWidth + "-- screenHeight=" + screenHeight + "-- navigationBarHeight=" + navigationBarHeight + "--statusBarHeight=" + statusBarHeight);
				mWindowManager.updateViewLayout(floatLayout, mWindowParamsForFloatBtn);

				if (isEndMove){//2s不动的话缩小
					floatLayout.postDelayed(suoxiaoRunnable, 2000);
				}

			}

		});
	}


	// 悬浮按钮的属性
	private WindowManager.LayoutParams getCricleButtonParams(int pointX, int pointY) {
		WindowManager.LayoutParams windowParams;
		windowParams = new WindowManager.LayoutParams();
		windowParams.x = pointX;
		windowParams.y = pointY;
//		windowParams.width = (int) (logoSize * 1.3);
//		windowParams.height = (int) (logoSize * 1.3);
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.format = 1;
		windowParams.gravity = Gravity.LEFT | Gravity.TOP;
//		windowParams.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 2;
		windowParams.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		return windowParams;
	}


	// windowManager撤销
	public void windowManagerFinish() {
		PL.d( "========windowManagerFinish====");
		try {
			if (redPointReqTimer != null){
				redPointReqTimer.cancel();
				redPointReqTimer = null;
			}
			if (mWindowManager != null) {
				if (floatLayout != null){
					mWindowManager.removeViewImmediate(floatLayout);
					floatLayout = null;
				}
				mWindowManager = null;
			}
			
//			if (floatingManager != null) {
//				floatingManager = null;
//			}

			oldLoginTimestamp = null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
//			if (floatingManager != null) {
//				floatingManager = null;
//			}
			mWindowManager = null;
		}
	}


	// 设置stop 且设置标记isPopShowAgain为true
	public void windowManagerStop() {

	}

	public void windowManagerRestart(Context context) {
//		if(floatButtionClickCallback != null){
//			floatButtionClickCallback.callBack();
//		}
//		if (floatLayout != null)
//			if (floatLayout.getVisibility() == View.GONE) {
//				floatLayout.setVisibility(View.VISIBLE);
//				if (!floatItemInShow) {
//					if (null != menuItemBeans && menuItemBeans.size() > 0)
//						setFloatBtnVisible();
//				}
//			}
	}

	private int getNavigationBarHeight(Context context) {
	    Resources resources = context.getResources();
	    int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
	    int height = 0 ;
		if(resourceId > 0){//过滤没有导航栏的设备
	    	height = resources.getDimensionPixelSize(resourceId);
	    }
		PL.d( "Navi height:" + height);
	    return height;
	}
	
	/**
	 * 向左的动画	（左边）
	 * @param context
	 */
//	private void addGoLeftAnimation(Context context){
//		Animation animation = AnimationUtils.loadAnimation(context, ResUtil.findAnimIdByName(context, "efun_pd_exit_go"));
//		animation.setAnimationListener(new GoAnimationListener());
//		floatBtn.startAnimation(animation);
//	}
	
//	private void addBackLeftAnimation(Context context){
//		Animation animation = AnimationUtils.loadAnimation(context, ResUtil.findAnimIdByName(context, "efun_pd_exit_back"));
//		animation.setAnimationListener(new BackAnimationListener());
//		floatBtn.startAnimation(animation);
//	}
	
	
	/*
	private class GoAnimationListener implements AnimationListener{
		
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private class BackAnimationListener implements AnimationListener{

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
	}*/


	public void initParams() {
		// 屏幕宽高
		screenWidth = activity.getWindow().getDecorView().getWidth();//ScreenHelper.getScreenWidth(activity);
		screenHeight = activity.getWindow().getDecorView().getHeight();//ScreenHelper.getScreenHeight(activity);
		navigationBarHeight = getNavigationBarHeight(activity);
		statusBarHeight = 0;//getStatusHeight(activity);

	}

	public void updateReddot(boolean show){
		if (show){
			floatReddotView.setVisibility(View.VISIBLE);
		}else {
			if (floatReddotView.getVisibility() == View.GONE){
				return;
			}
			floatReddotView.setVisibility(View.GONE);
			deleteRedPoint(activity.getApplicationContext());
		}
	}


	public void requestRedPoint(Context context) throws Exception {

		Request.getFloatBtnRedDot(context, new SFCallBack<RedDotRes>() {
			@Override
			public void success(RedDotRes redDotRes, String msg) {

				if (redDotRes != null && redDotRes.getData() != null && redDotRes.getData().isCs() && floatReddotView != null){
					updateReddot(true);
					FloatingManager.this.redDotRes = redDotRes;
				}
			}

			@Override
			public void fail(RedDotRes result, String msg) {

			}
		});
	}

	public void deleteRedPoint(Context context) {

		Request.deleteFloatBtnRedDot(context, new SFCallBack<RedDotRes>() {
			@Override
			public void success(RedDotRes redDotRes, String msg) {

			}

			@Override
			public void fail(RedDotRes result, String msg) {

			}
		});
	}
}