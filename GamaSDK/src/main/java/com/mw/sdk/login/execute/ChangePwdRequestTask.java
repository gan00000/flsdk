package com.mw.sdk.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.login.constant.ApiRequestMethod;
import com.mw.sdk.login.model.request.ChangePwdRequestBean;

//1000成功
//1001註冊登入成功
public class ChangePwdRequestTask extends BaseLoginRequestTask {

    private ChangePwdRequestBean pwdRequestBean;

    public ChangePwdRequestTask(Context context, String userName, String password, String newPwd) {
        super(context);

        userName = userName.toLowerCase();

        pwdRequestBean = new ChangePwdRequestBean(context);
        sdkBaseRequestBean = pwdRequestBean;

        pwdRequestBean.setName(userName);

        pwdRequestBean.setPwd(SStringUtil.toMd5(password.trim()));
        pwdRequestBean.setNewPwd(SStringUtil.toMd5(newPwd.trim()));

        pwdRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_CHANGE_PASSWORD);

    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        pwdRequestBean.setSignature(SStringUtil.toMd5(pwdRequestBean.getAppKey() + pwdRequestBean.getTimestamp() +
                pwdRequestBean.getName() + pwdRequestBean.getPwd() +
                pwdRequestBean.getNewPwd() + pwdRequestBean.getGameCode()));

        return pwdRequestBean;
    }
}
