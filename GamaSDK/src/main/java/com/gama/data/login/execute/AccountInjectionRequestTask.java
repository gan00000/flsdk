package com.gama.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.gama.data.login.request.AccountInjectionRequestBean;

//1000成功
//1001註冊登入成功
public class AccountInjectionRequestTask extends BaseLoginRequestTask {

    private AccountInjectionRequestBean requestBean;

    public AccountInjectionRequestTask(Context context, String userName, String password,String uid) {
        super(context);

        userName = userName.trim().toLowerCase();
        password = password.trim();

        requestBean = new AccountInjectionRequestBean(context);
        sdkBaseRequestBean = requestBean;

        requestBean.setName(userName);

        password = SStringUtil.toMd5(password);
        requestBean.setPwd(password);
        requestBean.setUserId(uid);

        requestBean.setRequestMethod("dynamic_injection");


    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        requestBean.setSignature(SStringUtil.toMd5(requestBean.getAppKey() + requestBean.getTimestamp() +
                requestBean.getName() + requestBean.getPwd() + requestBean.getGameCode()));

        return requestBean;
    }
}
