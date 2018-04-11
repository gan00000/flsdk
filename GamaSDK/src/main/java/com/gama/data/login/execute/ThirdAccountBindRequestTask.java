package com.gama.data.login.execute;

import android.content.Context;
import android.text.TextUtils;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.gama.base.bean.SLoginType;
import com.gama.data.login.request.ThirdAccountBindRequestBean;

/**
 * <p>Title: 三方綁定</p>
 *
 * @author GanYuanrong
 * @date 2014年9月16日
 */
public class ThirdAccountBindRequestTask extends BaseLoginRequestTask {

    ThirdAccountBindRequestBean thirdAccountBindRequestBean;

    /**
     *   fb綁定
     * @param context
     * @param name
     * @param pwd
     * @param email
     * @param fbScopeId
     * @param fbApps
     * @param fbTokenBusiness
     */
    public ThirdAccountBindRequestTask(Context context, String name, String pwd, String email, String fbScopeId, String fbApps, String fbTokenBusiness) {
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

        thirdAccountBindRequestBean.setRequestMethod("bind_thirdParty");


    }

    /**
     * 免註冊綁定
     * @param context
     * @param name
     * @param pwd
     * @param email
     */
    public ThirdAccountBindRequestTask(Context context, String name, String pwd, String email, String sLoginType, String thirdPlatId) {
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

        thirdAccountBindRequestBean.setRequestMethod("bind_thirdParty");


    }

    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();


        thirdAccountBindRequestBean.setSignature(SStringUtil.toMd5(thirdAccountBindRequestBean.getAppKey() + thirdAccountBindRequestBean.getTimestamp() +
                        thirdAccountBindRequestBean.getName() + thirdAccountBindRequestBean.getPwd() +
                thirdAccountBindRequestBean.getGameCode() + thirdAccountBindRequestBean.getThirdPlatId() + thirdAccountBindRequestBean.getRegistPlatform()));

        return thirdAccountBindRequestBean;
    }
}
