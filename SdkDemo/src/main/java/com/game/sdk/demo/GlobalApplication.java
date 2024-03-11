package com.game.sdk.demo;

import android.app.Application;

import com.ldy.pub.DYSdk;

public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DYSdk.getInstance().applicationOnCreate(this);

    }
}