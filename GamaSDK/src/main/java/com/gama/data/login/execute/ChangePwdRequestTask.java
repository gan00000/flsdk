package com.gama.data.login.execute;

import android.content.Context;

import com.core.base.bean.BaseReqeustBean;
import com.core.base.utils.SStringUtil;
import com.gama.data.login.request.ChangePwdRequestBean;

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

        pwdRequestBean.setRequestMethod("changePwd");


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
