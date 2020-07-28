package com.flyfun.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.flyfun.base.bean.SLoginType;
import com.flyfun.data.login.constant.GSRequestMethod;
import com.flyfun.data.login.constant.GamaRequestMethod;
import com.flyfun.data.login.request.ThirdLoginRegRequestBean;

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
                                    String accessTokenString, GSRequestMethod.GSRequestType requestMethod) {
        super(context);

        thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(context);

        sdkBaseRequestBean = thirdLoginRegRequestBean;

        thirdLoginRegRequestBean.setRegistPlatform(SLoginType.LOGIN_TYPE_FB);
        thirdLoginRegRequestBean.setThirdPlatId(fbScopeId);
        thirdLoginRegRequestBean.setApps(fbApps);
        thirdLoginRegRequestBean.setTokenBusiness(fbTokenBusiness);
        thirdLoginRegRequestBean.setFb_oauthToken(accessTokenString);


        if(requestMethod == GSRequestMethod.GSRequestType.GAMESWORD) {
            thirdLoginRegRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_THIRD_LOGIN);
        } else if (requestMethod == GSRequestMethod.GSRequestType.GAMAMOBI) {
            thirdLoginRegRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_THIRD_LOGIN);
        }
    }

    /**
     * 其他第三方登录使用
     * @param context
     */
    public ThirdLoginRegRequestTask(Context context, ThirdLoginRegRequestBean thirdLoginRegRequestBean, GSRequestMethod.GSRequestType requestMethod) {
        super(context);

        this.thirdLoginRegRequestBean = thirdLoginRegRequestBean;

        sdkBaseRequestBean = thirdLoginRegRequestBean;

        if(requestMethod == GSRequestMethod.GSRequestType.GAMESWORD) {
            thirdLoginRegRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_THIRD_LOGIN);
        } else if (requestMethod == GSRequestMethod.GSRequestType.GAMAMOBI) {
            thirdLoginRegRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_THIRD_LOGIN);
        }
    }

    /**
     * 其他第三方登录使用
     * @param context
     * @param thirdPlatId
     * @param registPlatform
     */
    @Deprecated
    public ThirdLoginRegRequestTask(Context context, String thirdPlatId, String registPlatform, GSRequestMethod.GSRequestType requestMethod) {
        super(context);

        thirdLoginRegRequestBean = new ThirdLoginRegRequestBean(context);

        sdkBaseRequestBean = thirdLoginRegRequestBean;

        thirdLoginRegRequestBean.setRegistPlatform(registPlatform);
        thirdLoginRegRequestBean.setThirdPlatId(thirdPlatId);

        if(requestMethod == GSRequestMethod.GSRequestType.GAMESWORD) {
            thirdLoginRegRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_THIRD_LOGIN);
        } else if (requestMethod == GSRequestMethod.GSRequestType.GAMAMOBI) {
            thirdLoginRegRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_THIRD_LOGIN);
        }
    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();


        thirdLoginRegRequestBean.setSignature(SStringUtil.toMd5(thirdLoginRegRequestBean.getAppKey() + thirdLoginRegRequestBean.getTimestamp() +
                thirdLoginRegRequestBean.getThirdPlatId() + thirdLoginRegRequestBean.getGameCode() + thirdLoginRegRequestBean.getRegistPlatform()));

        return thirdLoginRegRequestBean;
    }
}
