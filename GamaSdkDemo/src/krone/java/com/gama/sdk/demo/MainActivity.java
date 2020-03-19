package com.gama.sdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.PL;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.SLog;
import com.gama.sdk.callback.IPayListener;
import com.gama.thirdlib.twitter.GamaTwitterLogin;

public class MainActivity extends BaseMainActivity {

    private GamaTwitterLogin gamaTwitterLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SLog.enableDebug(true);

        //初始化sdk
        iGama.initSDK(this, SGameLanguage.ko_KR);
        //在游戏Activity的onCreate生命周期中调用
        iGama.onCreate(this);

        demo_pay_one.setVisibility(View.VISIBLE);
        demo_pay_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGama.pay(MainActivity.this, SPayType.ONESTORE, "" + System.currentTimeMillis(), getResources().getString(R.string.test_sku), "customize", new IPayListener() {
                    @Override
                    public void onPayFinish(Bundle bundle) {
                        PL.i("OneStore Pay结束");
                        int status = 0;
                        if (bundle != null) {
                            status = bundle.getInt("status");

                            for (String next : bundle.keySet()) {
                                PL.i(next + " : " + bundle.get(next));
                            }
                        }
                    }
                });
            }
        });

        showPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.gamaOpenCafeHome(MainActivity.this);
                GamaTimeUtil.getBeiJingTime(MainActivity.this);
                GamaTimeUtil.getDisplayTime(MainActivity.this);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private boolean isLogin() {
        return !TextUtils.isEmpty(GamaUtil.getUid(this));
    }
}
