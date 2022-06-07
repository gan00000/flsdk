package com.mw.sdk.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.base.bean.SLoginType;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.login.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.ThirdAccountBindRequestBean;

/**
 * <p>Title: 三方綁定</p>
 *
 * @author HuangShaoGuang
 * @date 2019年11月21日
 */
public class ThirdAccountBindRequestTaskV2 extends BaseLoginRequestTask {

    ThirdAccountBindRequestBean thirdAccountBindRequestBean;

    /**
     *   需要手机验证码的fb綁定
     */
    public ThirdAccountBindRequestTaskV2(Context context,
                                       String name,
                                       String pwd,
                                       String areaCode,
                                       String phone,
                                       String vfcode,
                                       String fbScopeId,
                                       String fbApps,
                                       String fbAccessToken,
                                       String fbTokenBusiness) {
        super(context);

        thirdAccountBindRequestBean = new ThirdAccountBindRequestBean(context);

        sdkBaseRequestBean = thirdAccountBindRequestBean;

        thirdAccountBindRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_FB);
        thirdAccountBindRequestBean.setThirdPlatId(fbScopeId);
        thirdAccountBindRequestBean.setApps(fbApps);
        thirdAccountBindRequestBean.setTokenBusiness(fbTokenBusiness);

        thirdAccountBindRequestBean.setName(name);
        thirdAccountBindRequestBean.setPwd(SStringUtil.toMd5(pwd));
        if (TextUtils.isEmpty(areaCode)){ //areaCode為空值是未通過email獲取驗證碼進行綁定
            thirdAccountBindRequestBean.setEmail(phone);
        }else {
            thirdAccountBindRequestBean.setPhone(phone);
        }
        thirdAccountBindRequestBean.setPhoneAreaCode(areaCode);
        thirdAccountBindRequestBean.setVfCode(vfcode);
        thirdAccountBindRequestBean.setFb_oauthToken(fbAccessToken);

        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);

    }

    /**
     * 需要手机验证码的通用綁定
     */
    public ThirdAccountBindRequestTaskV2(Context context,
                                       String sLoginType,
                                       String name,
                                       String pwd,
                                       String areaCode,
                                       String phone,
                                       String vfcode,
                                       String thirdPlatId) {
        super(context);

        if(TextUtils.isEmpty(thirdPlatId)){
            PL.d("thirdPlatId:" + thirdPlatId);
            return;
        }

        thirdAccountBindRequestBean = new ThirdAccountBindRequestBean(context);

        sdkBaseRequestBean = thirdAccountBindRequestBean;

        thirdAccountBindRequestBean.setRegistPlatform(sLoginType);
        thirdAccountBindRequestBean.setThirdPlatId(thirdPlatId);
        thirdAccountBindRequestBean.setName(name);
        thirdAccountBindRequestBean.setPwd(SStringUtil.toMd5(pwd));
        if (TextUtils.isEmpty(areaCode)){ //areaCode為空值是未通過email獲取驗證碼進行綁定
            thirdAccountBindRequestBean.setEmail(phone);
        }else {
            thirdAccountBindRequestBean.setPhone(phone);
        }
        thirdAccountBindRequestBean.setPhoneAreaCode(areaCode);
        thirdAccountBindRequestBean.setVfCode(vfcode);

        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);
    }

    /**
     * 需要手机验证码的Google綁定
     */
    public ThirdAccountBindRequestTaskV2(Context context,
                                       String name,
                                       String pwd,
                                       String areaCode,
                                       String phone,
                                       String vfcode,
                                       String thirdPlatId,
                                       String googleIdToken,
                                       String googleClientId) {
        super(context);

        if(TextUtils.isEmpty(thirdPlatId)){
            PL.d("thirdPlatId:" + thirdPlatId);
            return;
        }

        thirdAccountBindRequestBean = new ThirdAccountBindRequestBean(context);

        sdkBaseRequestBean = thirdAccountBindRequestBean;

        thirdAccountBindRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
        thirdAccountBindRequestBean.setThirdPlatId(thirdPlatId);
        thirdAccountBindRequestBean.setName(name);
        thirdAccountBindRequestBean.setPwd(SStringUtil.toMd5(pwd));

        if (TextUtils.isEmpty(areaCode)){ //areaCode為空值是未通過email獲取驗證碼進行綁定
            thirdAccountBindRequestBean.setEmail(phone);
        }else {
            thirdAccountBindRequestBean.setPhone(phone);
        }

        thirdAccountBindRequestBean.setPhoneAreaCode(areaCode);
        thirdAccountBindRequestBean.setVfCode(vfcode);
        thirdAccountBindRequestBean.setGoogleIdToken(googleIdToken);
        thirdAccountBindRequestBean.setGoogleClientId(googleClientId);


        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);
    }

    public ThirdAccountBindRequestTaskV2(Context context,
                                         String name,
                                         String pwd,
                                         String thirdPlatId,
                                         String thirdToken) {
        super(context);

        if(TextUtils.isEmpty(thirdPlatId)){
            PL.d("thirdPlatId:" + thirdPlatId);
            return;
        }

        thirdAccountBindRequestBean = new ThirdAccountBindRequestBean(context);

        sdkBaseRequestBean = thirdAccountBindRequestBean;

        thirdAccountBindRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_GOOGLE);
        thirdAccountBindRequestBean.setThirdPlatId(thirdPlatId);
        thirdAccountBindRequestBean.setName(name);
        thirdAccountBindRequestBean.setPwd(SStringUtil.toMd5(pwd));
        thirdAccountBindRequestBean.setLineAccessToken(thirdToken);

        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();


        thirdAccountBindRequestBean.setSignature(SStringUtil.toMd5(
                ResConfig.getAppKey(context)
                + thirdAccountBindRequestBean.getTimestamp()
                +thirdAccountBindRequestBean.getName()
                +thirdAccountBindRequestBean.getGameCode()));

        return thirdAccountBindRequestBean;
    }
}
