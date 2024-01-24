package com.core.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

/**
 * Created by gan on 2016/9/18.
 */
public class ScreenHelper {

    /*Activity activity;
    int screenWidth;

    int screenHeight;
    boolean isPhone;
    boolean isPortrait = false;

    public ScreenHelper(Activity activity) {
        this.activity = activity;
        init();

    }

    private void init(){
        if (activity == null){
            return;
        }
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        Configuration c = activity.getResources().getConfiguration();

        if (Build.VERSION.SDK_INT >  Build.VERSION_CODES.JELLY_BEAN) {
            screenWidth = (int) ((c.densityDpi / 160.0) * c.screenWidthDp);
            screenHeight = (int) ((c.densityDpi / 160.0) * c.screenHeightDp);
        }else {
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
        }

        if (c.orientation == Configuration.ORIENTATION_PORTRAIT){
            isPortrait = true;
        }

        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        // 屏幕尺寸
        double screenInches = Math.sqrt(x + y);
        // 大于6尺寸则为Pad
        if (screenInches >= 6.0) {
            isPhone = false;
        }else{
            isPhone = true;
        }
    }

    public int[] getAvailableScreen(Context context){
        int width;
        int height;
        //判断是否是手机
        if (isPhone) {
            if (isPortrait) {
                width = screenWidth * 7 / 8;
                height = screenHeight * 4 / 5;
            } else {
                width = screenWidth * 7 / 8;
                height = screenHeight * 7 / 8;
            }
        } else {
            if (isPortrait) {
                width = screenWidth * 2 / 3;
                height = screenHeight * 3 / 5;
            } else {
                width = screenWidth * 3 / 5;
                height = screenHeight * 7 / 8;
            }
        }
        return new int[]{width,height};
    }

    public boolean isTablet() {
        return !isPhone;
    }


    public boolean isPhone() {
        return isPhone;
    }

    public boolean isPortrait() {
        return isPortrait;
    }*/

    private static int[] getScreenWH(Context context) {
        int wh[] = new int[]{0,0};
        if (context == null){
            return wh;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int widthPixel = dm.widthPixels;
        int heightPixel = dm.heightPixels;
        float density = dm.density;
        Configuration c = context.getResources().getConfiguration();
        int screenWidth = 0;
        int screenHeight = 0;
        screenWidth = (int) ((c.densityDpi / 160.0) * c.screenWidthDp);
        screenHeight = (int) ((c.densityDpi / 160.0) * c.screenHeightDp);
        PL.d("widthPixel=%s, heightPixel=%s, screenWidth=%s, screenHeight=%s", widthPixel, heightPixel, screenWidth, screenHeight);
        wh[0] = screenWidth;
        wh[1] = screenHeight;
        return wh;
    }

    public static int getScreenWidth(Context context) {
        return getScreenWH(context)[0];
    }
    public static int getScreenHeight(Context context) {
        return getScreenWH(context)[1];
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /**
     * Return whether screen is portrait.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isPortrait(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }
}
