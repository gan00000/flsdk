package com.starpy.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.starpy.data.login.request.AccountRegRequestBean;

//1000成功
//1001註冊登入成功
public class AccountRegisterRequestTask extends BaseLoginRequestTask {

    private AccountRegRequestBean regRequestBean;

    public AccountRegisterRequestTask(Context context, String userName, String password) {
        super(context);

        userName = userName.toLowerCase();
//        password = password.toLowerCase();

        regRequestBean = new AccountRegRequestBean(context);
        sdkBaseRequestBean = regRequestBean;

        regRequestBean.setName(userName);

        password = SStringUtil.toMd5(password);
        regRequestBean.setPwd(password);

        regRequestBean.setRequestMethod("register");


    }

    public AccountRegisterRequestTask(Context context, String userName, String password,String email) {
        super(context);

        userName = userName.toLowerCase();

        regRequestBean = new AccountRegRequestBean(context);
        sdkBaseRequestBean = regRequestBean;

        regRequestBean.setName(userName);

        password = SStringUtil.toMd5(password);
        regRequestBean.setPwd(password);

        regRequestBean.setEmail(email);

        regRequestBean.setRequestMethod("register");


    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        regRequestBean.setSignature(SStringUtil.toMd5(regRequestBean.getAppKey() + regRequestBean.getTimestamp() +
                regRequestBean.getName() + regRequestBean.getPwd() + regRequestBean.getGameCode()));

        return regRequestBean;
    }
}
