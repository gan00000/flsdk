package com.allextends.af;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.allextends.adjust.AdjustHelper;
import com.allextends.facebook.SFacebookProxy;
import com.appsflyer.AppsFlyerLib;
import com.ldy.sdk.BuildConfig;
import com.ldy.sdk.R;
import com.ldy.sdk.ads.EventConstant;
import com.mybase.utils.PL;

import java.util.Map;

public class AFHelper {

    public static void activateApp(Activity activity){

        try {
            AppsFlyerLib.getInstance().setCollectIMEI(false);
            AppsFlyerLib.getInstance().setCollectAndroidID(false);
            String afDevKey = activity.getResources().getString(R.string.sdk_af_dev_key);//ResConfig.getConfigInAssetsProperties(activity,"sdk_ads_appflyer_dev_key");
            if(!TextUtils.isEmpty(afDevKey)) {
//                AppsFlyerLib.getInstance().startTracking(activity.getApplication(), afDevKey);
                AppsFlyerLib.getInstance().init(afDevKey,null,activity.getApplication());//应用层调用
                AppsFlyerLib.getInstance().start(activity.getApplicationContext());
                if (BuildConfig.DEBUG) {//debug打印日志
                    AppsFlyerLib.getInstance().setDebugLog(true);
                }
            } else {
                PL.e("af dev key empty!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void logEvent(Context context, String name, Map<String, Object> map){

        AdjustHelper.trackEvent(context, name, map);//adjust后来添加，直接添加到这里

        String afDevKey = context.getResources().getString(R.string.sdk_af_dev_key);//ResConfig.getConfigInAssetsProperties(activity,"sdk_ads_appflyer_dev_key");
        if(!TextUtils.isEmpty(afDevKey)) {
            return;
        }
        AppsFlyerLib.getInstance().logEvent(context, name, map);

    }

}
