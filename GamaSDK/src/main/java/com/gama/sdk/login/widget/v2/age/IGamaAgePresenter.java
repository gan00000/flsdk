package com.gama.sdk.login.widget.v2.age;

import android.content.Context;

import com.gama.sdk.SBaseDialog;

public interface IGamaAgePresenter {

    void sendAgeRequest(Context context, SBaseDialog dialog);

    void requestAgeLimit(Context context);

    void goAgeStyleOne(Context context);

    void goAgeStyleTwo(Context context);

    void goAgeStyleThree(Context context);

}
