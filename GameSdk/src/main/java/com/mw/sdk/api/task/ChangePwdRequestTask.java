package com.mw.sdk.api.task;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.ResConfig;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.bean.req.ChangePwdRequestBean;

//1000成功
//5001註冊登入成功
public class ChangePwdRequestTask extends BaseLoginRequestTask {

    private ChangePwdRequestBean pwdRequestBean;

    public ChangePwdRequestTask(Context context, String userName, String password, String newPwd) {
        super(context);

        userName = userName.toLowerCase();

        pwdRequestBean = new ChangePwdRequestBean(context);
        sdkBaseRequestBean = pwdRequestBean;

        pwdRequestBean.setName(userName);

//        pwdRequestBean.setPwd(SStringUtil.toMd5(password.trim()));
        pwdRequestBean.setOldPwd(SStringUtil.toMd5(password.trim()));
        pwdRequestBean.setNewPwd(SStringUtil.toMd5(newPwd.trim()));

        pwdRequestBean.setRequestMethod(ApiRequestMethod.GS_REQUEST_METHOD_CHANGE_PASSWORD);

    }


    @Override
    public BaseReqeustBean createRequestBean() {
        super.createRequestBean();

        pwdRequestBean.setSignature(SStringUtil.toMd5(ResConfig.getAppKey(context) + pwdRequestBean.getTimestamp() +
                pwdRequestBean.getName() + pwdRequestBean.getGameCode()));

        return pwdRequestBean;
    }
}
