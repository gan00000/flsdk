package com.mw.sdk.out;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SPayType;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.ads.SdkAdsConstant;
import com.mw.sdk.callback.IPayListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gan on 2017/2/13.
 */

public interface IFLSDK extends IGameLifeCycle {

    @Deprecated
    void initSDK(Activity activity);

    void initSDK(Activity activity, SGameLanguage language);

    void setGameLanguage(Activity activity,SGameLanguage gameLanguage);

    /**
     * 保存角色信息
     */
    void registerRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

    void login(Activity activity, ILoginCallBack iLoginCallBack);

    void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, IPayListener listener);

//    void openWebview(Activity activity);

    void openCs(Activity activity);

    void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults);

    void onWindowFocusChanged(Activity activity, boolean hasFocus);

    @Deprecated
    void openWebPage(Activity activity, GamaOpenWebType type, String url);

    void openWebPage(Activity activity, GamaOpenWebType type, String url, ISdkCallBack callBack);

    void openPlatform(Activity activity);

    /**
     * 获取用户资料
     */
//    void gamaGetUserProfile(Activity activity, UserProfileCallback callBack);

    /**
     * 获取好友
     */
//    void gamaFetchFriends(Activity activity, ThirdPartyType type, Bundle bundle, String paging, int limit, FetchFriendsCallback callback);

    /**
     * 分享
     */
    void share(Activity activity, ThirdPartyType type, String message, String linkUrl, String picPath, ISdkCallBack callback);

//    void gamaSentMessageToSpecifiedFriends(Activity activity, GamaThirdPartyType type, Uri uri, ISdkCallBack callback);

    /**
     * 发消息至游戏中心
     */
//    void gamaInviteFriends(Activity activity, ThirdPartyType type, List<FriendProfile> invitingList, String msg, String title, InviteFriendsCallback callback);

    /**
     * 检查是否支持分享
     */
    boolean gamaShouldShareWithType(Activity activity, ThirdPartyType type);

    /**
     * 事件上报接口
     * @param activity
     * @param eventName
     * @param map
     */
    void trackEvent(Activity activity, String eventName, Map<String, Object> map, Set<SdkAdsConstant.EventReportChannel> mediaSet);

    void trackCreateRoleEvent(Activity activity, String roleId,String roleName);

    /**
     * 处理一些特殊功能的接口
     */
//    void openFunction(Activity activity, GsFunctionType type, ISdkCallBack callBack);
}
