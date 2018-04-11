package com.gama.data.login;

import com.core.base.callback.ISCallBack;
import com.gama.data.login.response.SLoginResponse;

/**
 * Created by GanYuanrong on 2017/2/13.
 */

public interface ILoginCallBack extends ISCallBack{

    void onLogin(SLoginResponse sLoginResponse);

}
