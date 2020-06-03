package com.gama.sdk.out;

import android.app.Activity;
import android.os.Bundle;

import com.core.base.callback.IGameLifeCycle;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.data.login.ILoginCallBack;
import com.gama.pay.utils.GamaQueryProductListener;
import com.gama.sdk.ads.GamaAdsConstant;
import com.gama.sdk.callback.IPayListener;
import com.gama.sdk.social.callback.FetchFriendsCallback;
import com.gama.sdk.social.callback.InviteFriendsCallback;
import com.gama.sdk.social.callback.UserProfileCallback;
import com.gama.thirdlib.facebook.FriendProfile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by gan on 2017/2/13.
 */

public interface IGama extends IGameLifeCycle {

    @Deprecated
    void initSDK(Activity activity);

    void initSDK(Activity activity, SGameLanguage language);

    void setGameLanguage(Activity activity,SGameLanguage gameLanguage);

    /**
     * 保存角色信息
     */
    void registerRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

    void login(Activity activity,ILoginCallBack iLoginCallBack);

    void pay(Activity activity, SPayType payType, String cpOrderId, String productId, String extra, IPayListener listener);

    void openWebview(Activity activity);

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
    void gamaGetUserProfile(Activity activity, UserProfileCallback callBack);

    /**
     * 获取好友
     */
    void gamaFetchFriends(Activity activity, GamaThirdPartyType type, Bundle bundle, String paging, int limit, FetchFriendsCallback callback);

    /**
     * 分享
     */
    void gamaShare(Activity activity, GamaThirdPartyType type, String message, String linkUrl, String picPath, ISdkCallBack callback);

//    void gamaSentMessageToSpecifiedFriends(Activity activity, GamaThirdPartyType type, Uri uri, ISdkCallBack callback);

    /**
     * 发消息至游戏中心
     */
    void gamaInviteFriends(Activity activity, GamaThirdPartyType type, List<FriendProfile> invitingList, String msg, String title, InviteFriendsCallback callback);

    /**
     * 检查是否支持分享
     */
    boolean gamaShouldShareWithType(Activity activity, GamaThirdPartyType type);

    /**
     * 事件上报接口
     * @param activity
     * @param eventName
     * @param map
     */
    void gamaTrack(Activity activity, String eventName, Map<String, Object> map, Set<GamaAdsConstant.GamaEventReportChannel> mediaSet);


    void gamaQueryProductDetail(Activity activity, SPayType payType, List<String> skus, GamaQueryProductListener listener);

    void gamaOpenCafeHome(Activity activity);

    /**
     * 处理一些特殊功能的接口
     */
    void openFunction(Activity activity, GsFunctionType type, ISdkCallBack callBack);
}
