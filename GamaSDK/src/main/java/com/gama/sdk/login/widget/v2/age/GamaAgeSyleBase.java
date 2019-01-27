package com.gama.sdk.login.widget.v2.age;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gama.sdk.SBaseDialog;
import com.gama.sdk.login.widget.v2.age.impl.GamaAgeImpl;

public class GamaAgeSyleBase extends SBaseDialog {

    protected GamaAgeImpl presenter;
//    protected GamaCommonViewCallback viewListener;

    public GamaAgeSyleBase(@NonNull Context context, GamaAgeImpl presenter) {
        super(context, com.gama.sdk.R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
//        this.viewListener = callback;
        this.presenter = presenter;
        setFullScreen();
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
    }

    protected void dismissSelf() {
        try {
            this.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
