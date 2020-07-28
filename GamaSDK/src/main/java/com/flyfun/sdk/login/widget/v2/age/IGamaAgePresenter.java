package com.flyfun.sdk.login.widget.v2.age;

import android.content.Context;

import com.flyfun.sdk.SBaseDialog;

public interface IGamaAgePresenter {

    /**
     * 上报年龄
     */
    void sendAgeRequest(Context context, SBaseDialog dialog);

    /**
     * 检测购买上限
     */
    void requestAgeLimit(Context context);

    void goAgeStyleOne(Context context);

    void goAgeStyleTwo(Context context);

    void goAgeStyleThree(Context context);

}
