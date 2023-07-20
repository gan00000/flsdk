package com.ldy.sdk.login.execute;

import android.content.Context;

import com.mybase.bean.BaseReqModel;
import com.mybase.utils.SStringUtil;
import com.ldy.base.cfg.ResConfig;
import com.ldy.sdk.constant.ApiRequestMethod;
import com.ldy.sdk.login.model.request.AccountRegRequestBean;

//1000成功
//5001註冊登入成功
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
    public BaseReqModel createRequestBean() {
        super.createRequestBean();

        regRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + regRequestBean.getTimestamp() +
                regRequestBean.getName() + regRequestBean.getGameCode()));

        return regRequestBean;
    }
}
