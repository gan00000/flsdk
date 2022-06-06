package com.mw.sdk.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.login.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.AccountRegRequestBean;

//1000成功
//1001註冊登入成功
public class AccountRegisterRequestTask extends BaseLoginRequestTask {

    private AccountRegRequestBean regRequestBean;

    public AccountRegisterRequestTask(Context context, String userName, String password, String areaCode, String phone, String vfcode, String email) {
        super(context);

        userName = userName.toLowerCase();
//        password = password.toLowerCase();

        regRequestBean = new AccountRegRequestBean(context);
        sdkBaseRequestBean = regRequestBean;

        regRequestBean.setName(userName);

        password = SStringUtil.toMd5(password);
        regRequestBean.setPwd(password);

        regRequestBean.setVfCode(vfcode);
        regRequestBean.setPhoneAreaCode(areaCode);
        regRequestBean.setPhone(phone);

        regRequestBean.setEmail(email);

        regRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_REGISTER);


    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        regRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + regRequestBean.getTimestamp() +
                regRequestBean.getName() + regRequestBean.getGameCode()));

        return regRequestBean;
    }
}
