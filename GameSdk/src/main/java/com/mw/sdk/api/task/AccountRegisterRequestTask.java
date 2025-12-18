package com.mw.sdk.api.task;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.bean.req.AccountRegRequestBean;

import java.util.Map;

//1000成功
//5001註冊登入成功
public class AccountRegisterRequestTask extends BaseLoginRequestTask {

    private AccountRegRequestBean regRequestBean;

    public AccountRegisterRequestTask(Context context, String userName, String password, String areaCode, String phone, String vfcode, String email, Map<String, String> others) {
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

        if (others != null && !others.isEmpty()){
            String referCode = others.get("referCode");
            if (SStringUtil.isNotEmpty(referCode)){
                regRequestBean.setReferCode(referCode);
            }
        }

    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        regRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + regRequestBean.getTimestamp() +
                regRequestBean.getName() + regRequestBean.getGameCode()));

        return regRequestBean;
    }
}
