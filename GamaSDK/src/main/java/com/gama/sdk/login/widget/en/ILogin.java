package com.gama.sdk.login.widget.en;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.gama.data.login.ILoginCallBack;
import com.gama.thirdlib.facebook.SFacebookProxy;

/**
 * Created by gan on 2017/4/12.
 */

public interface ILogin extends IGameLifeCycle {

    public void startLogin(Activity activity, ILoginCallBack iLoginCallBack);
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy);
}
