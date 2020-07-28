package com.flyfun.data.login;

import com.core.base.callback.ISCallBack;
import com.flyfun.data.login.response.SLoginResponse;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public interface ILoginCallBack extends ISCallBack{

    void onLogin(SLoginResponse sLoginResponse);

}
