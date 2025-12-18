package com.game.sdk.demo;

import android.app.Application;

import com.mw.sdk.out.MWSdkFactory;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        MWSdkFactory.create().applicationOnCreate(this);
    }

  }