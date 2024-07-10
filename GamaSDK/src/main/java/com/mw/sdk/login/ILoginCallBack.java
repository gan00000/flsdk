package com.mw.sdk.login;

import com.core.base.callback.ISCallBack;
import com.mw.sdk.login.model.response.SLoginResponse;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public interface ILoginCallBack extends ISCallBack{

    void onLogin(SLoginResponse sLoginResponse);
    void onLogout(String msg);
}
