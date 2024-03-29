package com.mw.sdk.out;

import android.app.Activity;
import android.app.Application;

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.IGameLifeCycle;
import com.core.base.callback.SFCallBack;
import com.mw.base.bean.SPayType;
import com.mw.sdk.ads.EventConstant;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.callback.FloatCallback;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.model.response.SLoginResponse;

import java.util.Map;

/**
 * Created by gan on 2017/2/13.
 */

public interface IMWSDK extends IGameLifeCycle {

//    @Deprecated
//    void initSDK(Activity activity);

//    void initSDK(Activity activity, SGameLanguage language);

//    void setGameLanguage(Activity activity,SGameLanguage gameLanguage);

    void applicationOnCreate(Application application);

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

    void switchLogin(Activity activity, ILoginCallBack iLoginCallBack);

    void logout(Activity activity, ISdkCallBack iSdkCallBack);

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
    void pay(Activity activity, String cpOrderId, String productId, String extra, String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName, IPayListener listener);

//    void openWebview(Activity activity);

    /**
     * 请使用 void openCs(Activity activity, String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);
     * @param activity
     */
    @Deprecated
    void openCs(Activity activity);
    void openCs(Activity activity, String roleId,String roleName,String roleLevel,String vipLevel,String severCode,String serverName);

    void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults);

    void onWindowFocusChanged(Activity activity, boolean hasFocus);

//    @Deprecated
//    void openWebPage(Activity activity, GamaOpenWebType type, String url);
//
//    void openWebPage(Activity activity, GamaOpenWebType type, String url, ISdkCallBack callBack);

//    void openPlatform(Activity activity);

    /**
     * fb分享
     * @param activity
     * @param hashTag  话题
     * @param msg   引文内容
     * @param shareLinkUrl  分享的链接
     * @param iSdkCallBack  分享回调
     */
    void shareFacebook(Activity activity, String hashTag, String msg, String shareLinkUrl, ISdkCallBack iSdkCallBack);

    void share(Activity activity, String hashTag, String msg, String shareLinkUrl, ISdkCallBack iSdkCallBack);

    /**
     * line分享
     * @param activity
     * @param content 分享的内容
     * @param iSdkCallBack  分享回调
     */
    void shareLine(final Activity activity, final String content, final ISdkCallBack iSdkCallBack);
    /**
     * 检查是否支持分享
     */
    boolean canShareWithType(Activity activity, ThirdPartyType type);

    /**
     * 事件追踪接口
     * @param activity
     * @param eventName 事件名称
     */
    void trackEvent(Activity activity, EventConstant.EventName eventName);

    void trackEvent(Activity activity, String eventName);
    /**
     * 事件上报接口
     * @param activity
     * @param eventName
     * @param map
     */
    void trackEvent(Activity activity, EventConstant.EventName eventName, Map<String, Object> map);

//    void trackCreateRoleEvent(Activity activity, String roleId,String roleName);

    public void requestStoreReview(Activity activity, SFCallBack sfCallBack);

    /**
     * 显示sdk内部绑定手机页面
     * @param activity
     * @param sfCallBack
     */
    public void showBindPhoneView(Activity activity, SFCallBack<BaseResponseModel> sfCallBack);

    /**
     * 显示sdk内部升级账号页面
     * @param activity
     * @param sfCallBack
     */
    public void showUpgradeAccountView(Activity activity, SFCallBack sfCallBack);

    /**
     * 请求获取手机验证码
     * @param activity
     * @param areaCode  手机区号（不带"+"，如中国：86）
     * @param telephone 手机号码
     * @param sfCallBack    回调
     */
    public void requestVfCode(Activity activity, String areaCode, String telephone,SFCallBack<BaseResponseModel> sfCallBack);

    /**
     * 请求绑定手机
     * @param activity
     * @param areaCode 区号
     * @param telephone 手机号码
     * @param vfCode    验证码
     * @param sfCallBack
     */
    public void requestBindPhone(Activity activity, String areaCode, String telephone,String vfCode, SFCallBack<SLoginResponse> sfCallBack);

    /**
     * 请求账号升级
     * @param activity
     * @param account 需要绑定的新账号
     * @param pwd 新账号的密码
     * @param sfCallBack 回调
     */
    public void requestUpgradeAccount(Activity activity, String account, String pwd, SFCallBack<SLoginResponse> sfCallBack);

    public void checkGooglePlayServicesAvailable(Activity activity);

    public void openFbUrl(Activity activity, String url);

    public void checkPreRegData(Activity activity, ISdkCallBack iSdkCallBack);

    public void showTogglePayDialog(Activity activity, PayCreateOrderReqBean payCreateOrderReqBean);

    public void showSocialView(Activity activity);

    public void showActView(Activity activity);
    public boolean isShowAct(Activity activity);

    public void showFloatView(Activity activity, FloatCallback floatCallback);

    public void openUrlByBrowser(Activity activity,String url);
}
