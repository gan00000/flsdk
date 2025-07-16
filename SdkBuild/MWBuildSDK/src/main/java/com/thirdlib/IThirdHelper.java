package com.thirdlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public interface IThirdHelper {

    public void onActivityResult(int requestCode, int resultCode, Intent data);
    void init(Context context);
    void startLogin(Activity activity, ThirdCallBack thirdCallBack);
}
