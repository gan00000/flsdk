package com.ldy.sdk.login;

import android.app.Activity;

import com.ldy.callback.IGameLifeCycle;
import com.allextends.facebook.SFacebookProxy;
import com.ldy.callback.ILoginCallBack;

/**
 * Created by gan on 2017/4/12.
 */

public interface ILogin extends IGameLifeCycle {

    public void startLogin(Activity activity, ILoginCallBack iLoginCallBack);
    public void initFacebookPro(Activity activity, SFacebookProxy sFacebookProxy);

    public void signOut(Activity activity);
}
