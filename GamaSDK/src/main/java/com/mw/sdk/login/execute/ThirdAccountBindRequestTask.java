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
 * @author GanYuanrong
 * @date 2014年9月16日
 */
public class ThirdAccountBindRequestTask extends BaseLoginRequestTask {

    ThirdAccountBindRequestBean thirdAccountBindRequestBean;

    /**
     *   email fb綁定
     */
    public ThirdAccountBindRequestTask(Context context,
                                       String name,
                                       String pwd,
                                       String email,
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
        thirdAccountBindRequestBean.setEmail(email);
        thirdAccountBindRequestBean.setFb_oauthToken(fbAccessToken);
        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);
    }

//    public ThirdAccountBindRequestTask(Context context,
//                                       String name,
//                                       String pwd,
//                                       String email,
//                                       String thirdPlatId,
//                                       GSRequestMethod.GSRequestType requestMethod) {
//        super(context);
//
//        if(TextUtils.isEmpty(thirdPlatId)){
//            PL.d("thirdPlatId:" + thirdPlatId);
//            return;
//        }
//
//        thirdAccountBindRequestBean = new ThirdAccountBindRequestBean(context);
//
//        sdkBaseRequestBean = thirdAccountBindRequestBean;
//
//        thirdAccountBindRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_UNIQUE);
//        thirdAccountBindRequestBean.setThirdPlatId(thirdPlatId);
//        thirdAccountBindRequestBean.setName(name);
//        thirdAccountBindRequestBean.setPwd(SStringUtil.toMd5(pwd));
//        thirdAccountBindRequestBean.setEmail(email);
//
//        if(requestMethod == GSRequestMethod.GSRequestType.GAMESWORD) {
//            thirdAccountBindRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_BIND);
//        } else if (requestMethod == GSRequestMethod.GSRequestType.GAMAMOBI) {
//            thirdAccountBindRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_BIND);
//        }
//    }

    /**
     * email Google綁定
     */
    public ThirdAccountBindRequestTask(Context context,
                                         String name,
                                         String pwd,
                                         String email,
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
        thirdAccountBindRequestBean.setEmail(email);
        thirdAccountBindRequestBean.setGoogleIdToken(googleIdToken);
        thirdAccountBindRequestBean.setGoogleClientId(googleClientId);

        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);
    }

    /**
     * email通用綁定
     */
    public ThirdAccountBindRequestTask(Context context,
                                       String sLoginType,
                                       String name,
                                       String pwd,
                                       String email,
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
        thirdAccountBindRequestBean.setEmail(email);

        thirdAccountBindRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_BIND);
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        String key = ResConfig.getAppKey(context) + thirdAccountBindRequestBean.getTimestamp() +
                thirdAccountBindRequestBean.getName() + thirdAccountBindRequestBean.getPwd() +
                thirdAccountBindRequestBean.getGameCode() + thirdAccountBindRequestBean.getThirdPlatId() + thirdAccountBindRequestBean.getRegistPlatform();
        PL.d("request key:" + key);
        thirdAccountBindRequestBean.setSignature(SStringUtil.toMd5(key));
        return thirdAccountBindRequestBean;
    }
}
