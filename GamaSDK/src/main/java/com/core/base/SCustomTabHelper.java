package com.core.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;

import com.core.base.utils.PL;

import java.util.Collections;

public class SCustomTabHelper {
//    private static boolean isLaunch = false;
//    private static SFCallBack<String> iSdkCallBack;

    private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";


    /**
     * 用CustomTab打开URL
     * @param callBack 用CustomTab打开URL的结果回调：failure() 表示当前环境不支持使用CustomTab打开；success() 表示打开了CustomTab并且从CustomTab返回
     */
    public static boolean startCustomTab(Activity activity, String url) {

        try {
            String packageName = getPackage(activity);
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            if (!TextUtils.isEmpty(packageName)) {
                customTabsIntent.intent.setPackage(packageName);
            }
            customTabsIntent.launchUrl(activity, Uri.parse(url));
            PL.d("startCustomTab sueecee url=" + url);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            PL.e("poen customtab fail");

        }
        return false;
    }

//    public static void onResume() {
//        if(isLaunch) {
//            isLaunch = false;
//            if(iSdkCallBack != null) {
//                iSdkCallBack.success();
//            }
//        }
//    }

    public static String getPackage(Context context) { //核心作用是筛选出设备上最适合处理 Custom Tabs 功能的浏览器包名
        String packageName = CustomTabsClient.getPackageName(
                context,
                Collections.emptyList()
        );
        PL.i("customTab get packageName=" + packageName);
        return packageName;
    }


    /**
     * 检查是否安装 Chrome 浏览器
     */
    public static boolean isChromeInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.android.chrome", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
