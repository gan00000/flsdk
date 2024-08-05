package com.thirdlib.af;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.BuildConfig;
import com.mw.sdk.R;
import com.mw.sdk.utils.ResConfig;

import java.util.Map;

public class AFHelper {

    public static void applicationOnCreate(Application application){
        PL.i("AFHelper applicationOnCreate");
        AppsFlyerLib.getInstance().setCollectIMEI(false);
        AppsFlyerLib.getInstance().setCollectAndroidID(false);
        String afDevKey = ResConfig.getAfDevKey(application.getApplicationContext());
        if(!TextUtils.isEmpty(afDevKey)) {
//           AppsFlyerLib.getInstance().startTracking(activity.getApplication(), afDevKey);
            AppsFlyerLib.getInstance().init(afDevKey,null, application.getApplicationContext());//应用层调用


            //是否发布欧洲，欧洲特殊处理 https://support.appsflyer.com/hc/zh-cn/articles/22310731147153-%E5%AF%B9Google%E6%AC%A7%E7%9B%9F%E6%84%8F%E8%A7%81%E5%BE%81%E6%B1%82%E6%96%B0%E6%94%BF%E7%9A%84%E6%94%AF%E6%8C%81
            if (SStringUtil.isNotEmpty(application.getApplicationContext().getString(R.string.sdk_release_european))) {
                AppsFlyerLib.getInstance().enableTCFDataCollection(true);
            }

            if (BuildConfig.DEBUG) {//debug打印日志
                AppsFlyerLib.getInstance().setDebugLog(true);
            }
        } else {
            PL.e("af dev key empty!");
        }

    }

    public static void activityOnCreate(Activity activity){

        String afDevKey = ResConfig.getAfDevKey(activity);
        if(!TextUtils.isEmpty(afDevKey)) {

            AppsFlyerLib.getInstance().start(activity.getApplicationContext());

        } else {
            PL.e("af dev key empty!");
        }
    }

    public static void logEvent(Context context, String eventName, Map<String, Object> map){

        if (context == null || SStringUtil.isEmpty(eventName)){
            return;
        }
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
