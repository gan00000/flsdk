package com.mw.sdk.act;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.core.base.utils.PL;
import com.core.base.utils.ScreenHelper;
import com.mw.sdk.R;
import com.mw.sdk.callback.FloatViewMoveListener;
import com.mw.sdk.widget.FloatImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * @author itxuxxey
 *
 */
public class FloatingManager {

	private static FloatingManager wm;
	private Button redPointButton;
	Timer redPointReqTimer;

	public Activity activity;

	int screenWidth = 0;
	int screenHeight = 0;
	private int navigationBarHeight;//竖屏影响Y坐标，横屏影响X坐标
	private int statusBarHeight;

	WindowManager mWindowManager = null;
	RelativeLayout floatLayout;
	private FloatImageView floatImageView;
	WindowManager.LayoutParams mWindowParamsForFloatBtn = null;

	RelativeLayout.LayoutParams floatImageViewLayoutParams;

	private boolean floatHiddenState = false;//悬浮按钮是否正在隐藏半边状态
	private boolean hasEndMove = true;
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

	private FloatingManager() {
	}

	// 获得实例
	public static FloatingManager getInstance() {
		if (wm == null) {
			wm = new FloatingManager();
		}
		return wm;
	}
	// 展示
	public void initFloatingView(final Activity ctx, int pointX, int pointY) {

		this.activity = ctx;
		if (mWindowManager != null) {
			return;
		}

		mWindowManager = ctx.getWindowManager();

		initParams();

		floatLayout = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.sdk_float_item, null);
		floatImageView = floatLayout.findViewById(R.id.id_iv_sdk_float_item);
		floatImageViewLayoutParams = (RelativeLayout.LayoutParams) floatImageView.getLayoutParams();

		floatImageView.setClickable(true);
		floatImageView.setSceenWidth(screenWidth);
		floatImageView.setSceenHeight(screenHeight);

		// 生成悬浮按钮的参数
		mWindowParamsForFloatBtn = getCricleButtonParams(0, screenHeight/3);

		mWindowManager.addView(floatLayout, mWindowParamsForFloatBtn);

		getNotchInfo();
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


		if (redPointReqTimer != null){
			redPointReqTimer.cancel();
			redPointReqTimer = null;
		}
		redPointReqTimer = new Timer();

		redPointReqTimer.schedule(new TimerTask() {
			@Override
			public void run() {

				try {
					requestRedPoint();
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}, 20 * 1000, 10 * 60 * 1000);

	}

	private void getNotchInfo() {
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
	}

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

	private void initInNotch() {

//		int initX = 0;
//		if (mWindowParamsForFloatBtn.x >= 0){
//			initX = mWindowParamsForFloatBtn.x;
//		}
//		if (xNotchScreenInfo == null){
//			return;
//		}
//		for (int i = 0; i < xNotchScreenInfo.notchRects.size(); i++) {
//			Rect notchRect = xNotchScreenInfo.notchRects.get(i);
//			if (mWindowParamsForFloatBtn.y + floatBtnLayoutParams.height * 0.7 >= notchRect.top &&
//					mWindowParamsForFloatBtn.y + floatBtnLayoutParams.height * 0.3 <= notchRect.bottom &&
//					initX >= notchRect.left &&
//					initX <= notchRect.right){
//
//				mWindowParamsForFloatBtn.x = mWindowParamsForFloatBtn.x + notchRect.width();
//				return;
//			}
//		}
	}

	private void updateWindowParamsInNotchNotInit() {

//		NotchScreenManager.getInstance().getNotchInfo(activity, new INotchScreen.NotchScreenCallback() {
//			@Override
//			public void onResult(INotchScreen.NotchScreenInfo notchScreenInfo) {
//				xNotchScreenInfo = notchScreenInfo;
//			}
//		});
//		if (xNotchScreenInfo == null){
//			return;
//		}
//		for (int i = 0; i < xNotchScreenInfo.notchRects.size(); i++) {
//			Rect notchRect = xNotchScreenInfo.notchRects.get(i);
//			if (mWindowParamsForFloatBtn.y + floatBtnLayoutParams.height * 0.7 >= notchRect.top &&
//					mWindowParamsForFloatBtn.y + floatBtnLayoutParams.height * 0.3 <= notchRect.bottom &&
//					mWindowParamsForFloatBtn.x >= notchRect.left &&
//					mWindowParamsForFloatBtn.x <= notchRect.right){
//
//				mWindowParamsForFloatBtn.x = mWindowParamsForFloatBtn.x + notchRect.width();
//				return;
//			}
//		}
	}

	public void setFloatBtnVisible() {
//		floatBtnLayoutParams.width = (int) (logoSize * 1.3);
//		floatBtnLayoutParams.height = (int) (logoSize * 1.3);
//		floatBtn.setLayoutParams(floatBtnLayoutParams);
//		floatBtn.setImageResource(R.drawable.efun_function_logo_icon);
//		floatImageView.allowMove(false);
//		floatImageView.setAlpha(0.6f);

//		setMenuLayoutVisible();
	}

	public void setFloatBtnHide() {
//		floatBtnLayoutParams.width = (int) (logoSize * 0.75);
//		floatBtnLayoutParams.height = (int) (logoSize * 0.75);
//		floatBtn.setLayoutParams(floatBtnLayoutParams);
//		mWindowManager.updateViewLayout(floatLayout, mWindowParamsForFloatBtn);
//		floatBtn.setImageResource(R.drawable.float_hide_left_icon);
//		floatImageView.allowMove(true);
//		floatImageView.setAlpha(1.0f);
//
//		setMenuLayoutHide();
	}


	private void setMenuLayoutVisible() {
//		floatBtnLayoutParams.width = (int) (logoSize * 1.3);
//		floatBtnLayoutParams.height = (int) (logoSize * 1.3);
//		floatBtn.setLayoutParams(floatBtnLayoutParams);
//		floatBtn.setImageResource(R.drawable.efun_function_logo_icon);
//		floatBtn.allowMove(false);
//		floatBtn.setAlpha(0.7f);
//		mWindowManager.addView(menuViewLayout, getWindowMangerParamsForMenuLayout());
//		menuViewLayout.setVisibility(View.VISIBLE);
//		menuViewLayoutIsAddToWm = true;
	}

	private void setMenuLayoutHide() {
//		floatBtnLayoutParams.width = (int) (logoSize * 0.75);
//		floatBtnLayoutParams.height = (int) (logoSize * 0.75);
//		floatBtn.setLayoutParams(floatBtnLayoutParams);
//		mWindowManager.updateViewLayout(floatLayout, mWindowParamsForFloatBtn);
//		floatBtn.setImageResource(R.drawable.efun_function_logo_icon_click);
//		floatBtn.allowMove(true);
//		floatBtn.setAlpha(1.0f);
//

//		menuViewLayout.setVisibility(View.GONE);

//		if (mWindowManager != null && menuViewLayoutIsAddToWm) {
//			mWindowManager.removeViewImmediate(menuViewLayout);
//		}
//		menuViewLayoutIsAddToWm = false;
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
				
//				if (floatLayout != null){
//
//					mWindowManager.removeViewImmediate(floatLayout);
//				}

				setMenuLayoutHide();

//				if (menuItemBeans != null){
//					menuItemBeans.clear();
//					menuItemBeans = null;
//				}
			}
			
			if (wm != null) {
				wm = null;
			}
			
		} catch (Exception e) {

		}
	}


	// 设置stop 且设置标记isPopShowAgain为true
	public void windowManagerStop() {

	}

	public void windowManagerRestart(Context context) {
//		if(floatCallback != null){
//			floatCallback.callBack();
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
	private void addGoLeftAnimation(Context context){
//		Animation animation = AnimationUtils.loadAnimation(context, ResUtil.findAnimIdByName(context, "efun_pd_exit_go"));
//		animation.setAnimationListener(new GoAnimationListener());
//		floatBtn.startAnimation(animation);
	}
	
	private void addBackLeftAnimation(Context context){
//		Animation animation = AnimationUtils.loadAnimation(context, ResUtil.findAnimIdByName(context, "efun_pd_exit_back"));
//		animation.setAnimationListener(new BackAnimationListener());
//		floatBtn.startAnimation(animation);
	}
	
	
	
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
		
	}


	public void initParams() {
		// 屏幕宽高
		screenWidth = activity.getWindow().getDecorView().getWidth();//ScreenHelper.getScreenWidth(activity);
		screenHeight = activity.getWindow().getDecorView().getHeight();//ScreenHelper.getScreenHeight(activity);
		navigationBarHeight = getNavigationBarHeight(activity);
		statusBarHeight = 0;//getStatusHeight(activity);

	}


	public void requestRedPoint() throws Exception {

//		PlatBaseRequest platBaseRequest = new PlatBaseRequest(activity);
//		String mUrl = PlatformManager.getInstance().getUrlData().getPlatUrl();
//		mUrl = UrlUtils.checkUrl(mUrl);
//		mUrl = mUrl + "getNewRedDot.app";
//		String result = HttpRequest.post(mUrl, platBaseRequest.fieldValueToMap());
//		if (SStringUtil.isNotEmpty(result)){
//
//			JSONObject mJsonObject = new JSONObject(result);
//			String  code = mJsonObject.optString("code");
//			if ("1000".equals(code)){
//				setRedPoitiVsibility(View.VISIBLE);
//			}else {
//				setRedPoitiVsibility(View.GONE);
//			}
//
//		}
	}

}
