//package com.mw.sdk.log;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.res.Resources;
//import android.view.Gravity;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.RelativeLayout;
//
//import com.bumptech.glide.Glide;
//import com.core.base.callback.SFCallBack;
//import com.core.base.utils.ApkInfoUtil;
//import com.core.base.utils.PL;
//import com.core.base.utils.SStringUtil;
//import com.core.base.utils.ScreenHelper;
//import com.mw.sdk.R;
//import com.mw.sdk.api.Request;
//import com.mw.sdk.bean.res.RedDotRes;
//import com.mw.sdk.callback.FloatButtionClickCallback;
//import com.mw.sdk.callback.FloatViewMoveListener;
//import com.mw.sdk.utils.SdkUtil;
//import com.mw.sdk.widget.FloatImageView;
//
//import java.util.Timer;
//
///**
// *
// * @author itxuxxey
// *
// */
//public class LogViewManager {
//
//	private static LogViewManager floatingManager;
//	Timer redPointReqTimer;
//
//	public Activity activity;
//
//	int screenWidth = 0;
//	int screenHeight = 0;
//	private int navigationBarHeight;//竖屏影响Y坐标，横屏影响X坐标
//	private int statusBarHeight;
//
//	WindowManager mWindowManager = null;
//	RelativeLayout floatLayout;
//
//	private boolean floatHiddenState = false;//悬浮按钮是否正在隐藏半边状态
//	private boolean hasEndMove = true;
//
//
//	private LogViewManager() {
//	}
//
//	// 获得实例
//	public static LogViewManager getInstance() {
//		if (floatingManager == null) {
//			floatingManager = new LogViewManager();
//		}
//		return floatingManager;
//	}
//
//
//
//	// 悬浮按钮的属性
//	private WindowManager.LayoutParams getCricleButtonParams(int pointX, int pointY) {
//		WindowManager.LayoutParams windowParams;
//		windowParams = new WindowManager.LayoutParams();
//		windowParams.x = pointX;
//		windowParams.y = pointY;
////		windowParams.width = (int) (logoSize * 1.3);
////		windowParams.height = (int) (logoSize * 1.3);
//		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		windowParams.format = 1;
//		windowParams.gravity = Gravity.LEFT | Gravity.TOP;
////		windowParams.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 2;
//		windowParams.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
//		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
//		return windowParams;
//	}
//
//
//	// windowManager撤销
//	public void windowManagerFinish() {
//		PL.d( "========windowManagerFinish====");
//		try {
//			if (redPointReqTimer != null){
//				redPointReqTimer.cancel();
//				redPointReqTimer = null;
//			}
//			if (mWindowManager != null) {
//				if (floatLayout != null){
//					mWindowManager.removeViewImmediate(floatLayout);
//					floatLayout = null;
//				}
//				mWindowManager = null;
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			mWindowManager = null;
//		}
//	}
//
//
//	private int getNavigationBarHeight(Context context) {
//	    Resources resources = context.getResources();
//	    int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
//	    int height = 0 ;
//		if(resourceId > 0){//过滤没有导航栏的设备
//	    	height = resources.getDimensionPixelSize(resourceId);
//	    }
//		PL.d( "Navi height:" + height);
//	    return height;
//	}
//
//
//	public void initParams() {
//		// 屏幕宽高
//		screenWidth = activity.getWindow().getDecorView().getWidth();//ScreenHelper.getScreenWidth(activity);
//		screenHeight = activity.getWindow().getDecorView().getHeight();//ScreenHelper.getScreenHeight(activity);
//		navigationBarHeight = getNavigationBarHeight(activity);
//		statusBarHeight = 0;//getStatusHeight(activity);
//
//	}
//
//}
