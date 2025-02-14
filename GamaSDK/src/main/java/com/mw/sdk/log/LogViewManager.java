package com.mw.sdk.log;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ScreenHelper;
import com.mw.sdk.R;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.bean.AccountModel;
import com.mw.sdk.constant.SLoginType;
import com.mw.sdk.login.widget.v2.SdkLogLayout;
import com.mw.sdk.login.widget.v2.SelectPayChannelLayout;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.widget.SBaseDialog;

/**
 *
 * @author itxuxxey
 *
 */
public class LogViewManager {

	private static LogViewManager floatingManager;

	public Activity activity;

	WindowManager mWindowManager = null;
	TextView textView;

	SBaseDialog commonDialog;

	private LogViewManager() {
	}

	// 获得实例
	public static LogViewManager getInstance() {
		if (floatingManager == null) {
			floatingManager = new LogViewManager();
		}
		return floatingManager;
	}

    public void initFloatingView(final Activity activity) {

		//不是特定账号不显示
		AccountModel accountModel = SdkUtil.getLastLoginAccount(activity);
		if (accountModel != null
				&& SLoginType.LOGIN_TYPE_MG.equals(accountModel.getLoginType())
				&& SStringUtil.isNotEmpty(accountModel.getAccount())
				&& accountModel.getAccount().endsWith("@sdkdebug.com")){
			PL.enableLocal = true;
		}else {
			PL.enableLocal = false;
			PL.clearLocalLog(activity);
			return;
		}

        if (mWindowManager != null && textView != null) {
            //没有重新登陆，不用重新生成悬浮，防止造成重复
            PL.i("logview已存在");
            return;
        }

		this.activity = activity;

        mWindowManager = activity.getWindowManager();
		textView = new TextView(activity);
        textView.setText("sdk debug");
        textView.setTextColor(Color.RED);

		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showLog();
			}
		});

//        int screenWidth = activity.getWindow().getDecorView().getWidth();
//        int screenHeight = activity.getWindow().getDecorView().getHeight();

		int y =  10;
		if(ScreenHelper.isPortrait(activity)){
			int navigationBarHeight = getNavigationBarHeight(activity);
			y = navigationBarHeight + 10;
		}

        // 生成悬浮按钮的参数
        WindowManager.LayoutParams mWindowParams = getButtonParams(0,  y);

        mWindowManager.addView(textView, mWindowParams);
    }

	private void showLog() {

		commonDialog = new SBaseDialog(activity, R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
		SdkLogLayout sdkLogLayout = new SdkLogLayout(activity);
		sdkLogLayout.setsBaseDialog(commonDialog);
		commonDialog.setContentView(sdkLogLayout);
		commonDialog.show();

	}


	// 悬浮按钮的属性
	private WindowManager.LayoutParams getButtonParams(int pointX, int pointY) {
		WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
		windowParams.x = pointX;
		windowParams.y = pointY;
//		windowParams.width = (int) (logoSize * 1.3);
//		windowParams.height = (int) (logoSize * 1.3);
		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		windowParams.format = 1;
		windowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
//		windowParams.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 2;
		windowParams.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		return windowParams;
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

}
