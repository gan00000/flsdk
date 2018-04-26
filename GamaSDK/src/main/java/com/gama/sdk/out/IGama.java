package com.gama.sdk.out;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.data.login.ILoginCallBack;

/**
 * Created by gan on 2017/2/13.
 */

public interface IGama extends IGameLifeCycle {

    void initSDK(Activity activity);

    void setGameLanguage(Activity activity,SGameLanguage gameLanguage);

    /**
     * 保存角色信息
     */
    void registerRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

    void login(Activity activity,ILoginCallBack iLoginCallBack);

    void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra);

    void share(Activity activity, ISdkCallBack iSdkCallBack, String shareLinkUrl);

    @Deprecated
    void share(Activity activity, ISdkCallBack iSdkCallBack, String title, String message , String shareLinkUrl, String sharePictureUrl);

    void openWebview(Activity activity);

    void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults);

    void onWindowFocusChanged(Activity activity, boolean hasFocus);

    void openWebPage(Activity activity, String url);

    void openPlatform(Activity activity);
}
