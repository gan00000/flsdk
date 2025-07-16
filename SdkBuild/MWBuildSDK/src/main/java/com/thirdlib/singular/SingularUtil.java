package com.thirdlib.singular;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.singular.sdk.Singular;
import com.singular.sdk.SingularConfig;
import com.thirdlib.ThirdModuleUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SingularUtil {

    private static String getSingularSdkKey(Context context){
        return context.getString(R.string.mw_singular_key);
    }
    private static String getSingularSdkSecret(Context context){
        return context.getString(R.string.mw_singular_secret);
    }

    /*放置：将此方法添加到MainActivity 中，而不是Application 类中，以确保正确的会话跟踪。
    错误处理： try-catch 块可记录初始化错误（如无效凭据），而不会导致应用程序崩溃。
    深度链接（可选）：要处理深层链接（例如，启动应用的 URL），请参阅我们的《支持深层链接》文章，了解扩展设置。
    支持 META 安装 Referrer Attribution：添加config.withFacebookAppId("FacebookAppID") 配置选项，启用"元安装推荐人 "归属。
    专业提示：如果您的应用程序支持多个入口点（如深度链接），请确保在每个相关活动的onCreate() 中调用initSingularSDK() ，以保证行为一致。*/
    public static void initSingularSDK(Activity activity) {

        if (!ThirdModuleUtil.existSingularModule()){
            return;
        }

        String sdkKey = getSingularSdkKey(activity);
        String secret = getSingularSdkSecret(activity);
        PL.d("-----track even Singular sdkKey=" + sdkKey + ", secret=" + secret);
        if (SStringUtil.isNotEmpty(sdkKey) && SStringUtil.isNotEmpty(secret)){

            // Configure Singular with SDK key and secret
            SingularConfig config = new SingularConfig(getSingularSdkKey(activity), getSingularSdkSecret(activity));

            try {
                Singular.init(activity.getApplicationContext(), config);
                PL.d("SingularConfig SDK initialized successfully");
            } catch (Exception e) {
                PL.d("SingularConfig SDK initialization failed: " + e.getMessage());
            }

        }
    }

    public static void setCustomUserId(Activity activity, String userId) {

        if (!ThirdModuleUtil.existSingularModule()){
            return;
        }
        if (TextUtils.isEmpty(userId)){
            return;
        }
        if (SStringUtil.isEmpty(getSingularSdkKey(activity)) || SStringUtil.isEmpty(getSingularSdkSecret(activity))){
            return;
        }
        Singular.setCustomUserId(userId);
    }

    public static void logEvent(Context context, String eventName, Map<String, Object> attributes){
        comTrackEvent(context, eventName, "", 0, attributes);
    }

    public static void logRevenue(Context context, double amount, Map<String, Object> attributes){
        comTrackEvent(context, "", "USD", amount, attributes);
    }

    public static void logCustomRevenue(Context context, String eventName, double amount, Map<String, Object> attributes){
        comTrackEvent(context, eventName, "USD", amount, attributes);
    }

    private static void comTrackEvent(Context context, String eventName, String currency, double amount, Map<String, Object> attributes){

        if (context == null){
            return;
        }

        if (!ThirdModuleUtil.existSingularModule()){
            return;
        }
        if (SStringUtil.isEmpty(getSingularSdkKey(context)) || SStringUtil.isEmpty(getSingularSdkSecret(context))){
            return;
        }

        if (attributes == null){
            attributes = new HashMap<>();
        }
        if (SStringUtil.isEmpty(currency)){//普通事件上报
            JSONObject att = JsonUtil.map2Json(attributes);
            PL.i("-----track event singular start name=" + eventName);
            Singular.eventJSON(eventName, att);
            PL.i("-----track event singular onSuccess eventName=" + eventName);
        }else {//收益上报

            if (SStringUtil.isEmpty(eventName)){//使用默认收益事件名
                PL.i("-----track event revenue singular start");
                Singular.revenue(currency, amount, attributes);
                PL.i("-----track event revenue singular onSuccess");
            }else {
                PL.i("-----track event customRevenue singular start name=" + eventName);
                Singular.customRevenue(eventName, currency, amount, attributes);
                PL.i("-----track event customRevenue singular onSuccess eventName=" + eventName);
            }

        }

    }

}
