package com.thirdlib.af;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.core.base.utils.PL;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.utils.ResConfig;

import java.util.Map;

public class AFHelper {

    public static void afInit(Activity activity){

        AppsFlyerLib.getInstance().setCollectIMEI(false);
        AppsFlyerLib.getInstance().setCollectAndroidID(false);
        String afDevKey = ResConfig.getAfDevKey(activity);
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
    }

    public static void logEvent(Context context, String eventName, Map<String, Object> map){

        String afDevKey = ResConfig.getAfDevKey(context);
        if(TextUtils.isEmpty(afDevKey)) {
            return;
        }
        PL.i("af logEvent start name=" + eventName);
        AppsFlyerLib.getInstance().logEvent(context.getApplicationContext(), eventName, map, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                PL.i("af logEvent onSuccess");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                PL.i("af logEvent onError:" + s);
            }
        });

    }
}
