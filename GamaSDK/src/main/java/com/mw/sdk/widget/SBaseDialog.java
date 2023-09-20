package com.mw.sdk.widget;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.core.base.utils.AppUtil;
import com.core.base.utils.PL;
import com.mw.sdk.utils.Localization;

/**
 * Created by gan on 2017/3/31.
 */

public class SBaseDialog extends Dialog {

    private static String TAG = SBaseDialog.class.getSimpleName();
    private InputMethodManager inputMethodManager;

    public SBaseDialog(@NonNull Context context) {
        super(context);

        initContentLayout(context);
    }

    public SBaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initContentLayout(context);
    }

    protected SBaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initContentLayout(context);
    }

    private void initContentLayout(Context context){
        //获得dialog的window窗口
        Window window = this.getWindow();
        if(window == null) {
            return;
        }

        //Window开启硬件加速方式,防止 游戏设置android:hardwareAccelerated="false"会造成输入框看不到输入中的内容(原因未知)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        window.getDecorView().setPadding(0, 0, 0, 0);
//
//        //获得window窗口的属性
//        android.view.WindowManager.LayoutParams lp = window.getAttributes();
//
//        //设置窗口宽度为充满全屏
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        //设置窗口高度为包裹内容
////        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//
//        //将设置好的属性set回去
//        window.setAttributes(lp);

        setCanceledOnTouchOutside(true);

        Localization.updateSGameLanguage(context);//设置应用内语言
        AppUtil.hideDialogBottomBar(this);
    }

    protected void setFullScreen(){

        //获得dialog的window窗口
        Window window = this.getWindow();

        window.getDecorView().setPadding(0, 0, 0, 0);

        //获得window窗口的属性
        android.view.WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        //将设置好的属性set回去
        window.setAttributes(lp);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
//            AppUtil.hideDialogBottomBar(this);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        PL.i(TAG, "Dialog dismiss");
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus() != null) {
                if (this.getCurrentFocus().getWindowToken() != null) {//点击页面其他地方输入法隐藏
                    inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }
}