package com.mw.sdk.login;

import android.app.Activity;

import com.core.base.callback.IGameLifeCycle;
import com.thirdlib.facebook.SFacebookProxy;

/**
 * Created by gan on 2017/4/12.
 */

public interface ILogin extends IGameLifeCycle {

    public void startLogin(Activity activity, ILoginCallBack iLoginCallBack);
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy);

    public void signOut(Activity activity);
}
