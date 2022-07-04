package com.mw.sdk.out;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SPayType;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.callback.IPayListener;

import java.util.Map;
import java.util.Set;

/**
 * Created by gan on 2017/2/13.
 */

public interface IMWSDK extends IGameLifeCycle {

//    @Deprecated
//    void initSDK(Activity activity);

    void initSDK(Activity activity, SGameLanguage language);

    void setGameLanguage(Activity activity,SGameLanguage gameLanguage);

    /**
     * 保存角色信息
     */
    void registerRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

    void login(Activity activity, ILoginCallBack iLoginCallBack);

    void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName, IPayListener listener);

//    void openWebview(Activity activity);

    void openCs(Activity activity);

    void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults);

    void onWindowFocusChanged(Activity activity, boolean hasFocus);

//    @Deprecated
//    void openWebPage(Activity activity, GamaOpenWebType type, String url);
//
//    void openWebPage(Activity activity, GamaOpenWebType type, String url, ISdkCallBack callBack);

//    void openPlatform(Activity activity);

    /**
     * 检查是否支持分享
     */
//    boolean gamaShouldShareWithType(Activity activity, ThirdPartyType type);

    /**
     * 事件上报接口
     * @param activity
     * @param eventName
     * @param map
     */
    void trackEvent(Activity activity, EventConstant.EventName eventName, Map<String, Object> map);

//    void trackCreateRoleEvent(Activity activity, String roleId,String roleName);

}
