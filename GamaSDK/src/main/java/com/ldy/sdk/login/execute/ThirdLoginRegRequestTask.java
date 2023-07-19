package com.ldy.sdk.login.execute;

import android.content.Context;

import com.mybase.bean.BaseReqModel;
import com.mybase.utils.PL;
import com.mybase.utils.SStringUtil;
import com.ldy.base.bean.SLoginType;
import com.ldy.base.cfg.ResConfig;
import com.ldy.sdk.constant.ApiRequestMethod;
import com.ldy.sdk.login.model.request.ThirdLoginRegRequestBean;

/**
 * <p>Title: MacLoginRegRequestTask</p> <p>Description: 新三方登陆&注册接口</p> <p>Company:GanYuanrong</p>
 *
 * @author GanYuanrong
 * @date 2014年9月16日
 */
public class ThirdLoginRegRequestTask extends BaseLoginRequestTask {

    ThirdLoginRegRequestBean thirdLoginRegRequestBean;


    /**
     *  fb登录使用
     * @param context
     * @param fbScopeId
     * @param fbApps
     * @param fbTokenBusiness
     */
    public ThirdLoginRegRequestTask(Context context, String fbScopeId, String fbApps, String fbTokenBusiness,
                                    String accessTokenString) {
        super(context);

        thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(context);

        sdkBaseRequestBean = thirdLoginRegRequestBean;

        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_FB);
        thirdLoginRegRequestBean.setThirdPlatId(fbScopeId);
        thirdLoginRegRequestBean.setApps(fbApps);
        thirdLoginRegRequestBean.setTokenBusiness(fbTokenBusiness);
        thirdLoginRegRequestBean.setFb_oauthToken(accessTokenString);


        thirdLoginRegRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_THIRD_LOGIN);
    }

    /**
     * 其他第三方登录使用
     * @param context
     */
    public ThirdLoginRegRequestTask(Context context, ThirdLoginRegRequestBean thirdLoginRegRequestBean) {
        super(context);

        this.thirdLoginRegRequestBean = thirdLoginRegRequestBean;

        sdkBaseRequestBean = thirdLoginRegRequestBean;

        thirdLoginRegRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_THIRD_LOGIN);
    }


    @Override
    public BaseReqModel createRequestBean() {
        super.createRequestBean();

        String key = ResConfig.getAppKey(context) +
                thirdLoginRegRequestBean.getTimestamp() +
                thirdLoginRegRequestBean.getThirdPlatId() +
                thirdLoginRegRequestBean.getGameCode();
        thirdLoginRegRequestBean.setSignature(SStringUtil.toMd5(key));
        PL.d("request key:" + key);
        return thirdLoginRegRequestBean;
    }
}
