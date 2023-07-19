package com.ldy.callback;

import com.ldy.sdk.login.model.response.SLoginResult;
import com.ldy.callback.ISCallBack;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public interface ILoginCallBack extends ISCallBack{

    void onLogin(SLoginResult sLoginResponse);

}
