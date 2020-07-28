package com.flyfun.sdk;

import android.os.Bundle;

import com.core.base.SBaseActivity;
import com.flyfun.base.utils.Localization;

/**
 * Created by gan on 2017/2/7.
 */

public class SBaseSdkActivity extends SBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Localization.updateSGameLanguage(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}
