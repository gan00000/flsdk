package com.flyfun.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.flyfun.data.login.constant.GSRequestMethod;
import com.flyfun.data.login.constant.GamaRequestMethod;
import com.flyfun.data.login.request.AccountRegRequestBean;

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

        regRequestBean.setRequestMethod(GSRequestMethod.GS_REQUEST_METHOD_REGISTER);


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

        regRequestBean.setRequestMethod(GamaRequestMethod.GAMA_REQUEST_METHOD_REGISTER);


    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        regRequestBean.setSignature(SStringUtil.toMd5(regRequestBean.getAppKey() + regRequestBean.getTimestamp() +
                regRequestBean.getName() + regRequestBean.getPwd() + regRequestBean.getGameCode()));

        return regRequestBean;
    }
}
