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
     * 设置角色信息  在游戏获得角色信息的时候调用，每次登陆，切换账号等角色变化时调用
     * @param roleId            角色id  				必传
     * @param roleName          角色名   			必传
     * @param roleLevel         角色等级			没有传空值 ""
     * @param vipLevel          vip等级   			没有传空值 ""
     * @param severCode         角色伺服器id 		必传
     * @param serverName        角色伺服器名称	 	必传
     */
    void registerRoleInfo(Activity activity,String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

    /**
     * 登录
     * @param activity
     * @param iLoginCallBack        登录回调
     */
    void login(Activity activity, ILoginCallBack iLoginCallBack);

    /**
     * @param activity
     * @param payType           SPayType.WEB为平台网页第三方储值，SPayType.GOOGLE为Google储值
     * @param cpOrderId         厂商订单号    必传
     * @param productId         购买的商品id   必传
     * @param extra             预留的穿透值   可选
     * @param roleId            角色id  				必传
     * @param roleName          角色名   			必传
     * @param roleLevel         角色等级			没有传空值 ""
     * @param vipLevel          vip等级   			没有传空值 ""
     * @param severCode         角色伺服器id 		必传
     * @param serverName        角色伺服器名称	 	必传
     * @param listener          充值回调              辅助回调，充值是否成功以服务端回调为准
     */
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
