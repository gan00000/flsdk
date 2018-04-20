package com.gama.sdk.demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.core.base.utils.PL;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.data.login.ILoginCallBack;
import com.gama.data.login.response.SLoginResponse;
import com.gama.sdk.out.ISdkCallBack;
import com.gama.sdk.out.IGama;
import com.gama.sdk.out.GamaFactory;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, othersPayButton,googlePayBtn,shareButton, showPlatform;


    private IGama iGama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton = (Button) findViewById(R.id.demo_login);
        othersPayButton = (Button) findViewById(R.id.demo_pay);
        googlePayBtn = (Button) findViewById(R.id.demo_pay_google);
        shareButton = (Button) findViewById(R.id.demo_share);
        showPlatform = findViewById(R.id.showPlatform);

        iGama = GamaFactory.create();

        iGama.setGameLanguage(this, SGameLanguage.zh_TW);

        //初始化sdk
        iGama.initSDK(this);

        //在游戏Activity的onCreate生命周期中调用
        iGama.onCreate(this);

        /**
         * 在游戏获得角色信息的时候调用
         * roleId 角色id
         * roleName  角色名
         * rolelevel 角色等级
         * severCode 角色伺服器id
         * serverName 角色伺服器名称
         */
        iGama.registerRoleInfo(this, "roleid_1", "roleName", "rolelevel", "1", "serverName");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //登陆接口 ILoginCallBack为登录成功后的回调
                iGama.login(MainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
                        if (sLoginResponse != null){
                            String uid = sLoginResponse.getUserId();
                            String accessToken = sLoginResponse.getAccessToken();
                            String timestamp = sLoginResponse.getTimestamp();
                            PL.i("uid:" + uid);
                        }
                    }
                });

            }
        });


        othersPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /*
                充值接口
                SPayType SPayType.OTHERS为第三方储值，SPayType.GOOGLE为Google储值
                cpOrderId cp订单号，请保持每次的值都是不会重复的
                productId 充值的商品id
                roleLevel 觉得等级
                customize 自定义透传字段（从服务端回调到cp）
                */
                iGama.pay(MainActivity.this, SPayType.OTHERS, "" + System.currentTimeMillis(), "payone", "roleLevel", "customize");


            }
        });

        googlePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                充值接口
                SPayType SPayType.OTHERS为第三方储值，SPayType.GOOGLE为Google储值
                cpOrderId cp订单号，请保持每次的值都是不会重复的
                productId 充值的商品id
                roleLevel 觉得等级
                customize 自定义透传字段（从服务端回调到cp）
                */
                iGama.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(), "com.dodi.payone", "roleLevel", "customize");

            }
        });




        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //下面的参数请按照实际传值
                String shareUrl = "http://www.gamamobi.com/";
                //分享回调
                ISdkCallBack iSdkCallBack = new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share  success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share  failure");
                    }
                };

                iGama.share(MainActivity.this,iSdkCallBack,shareUrl);

//                iGama.share(MainActivity.this,iSdkCallBack,"", "", shareUrl, "");

            }
        });

        findViewById(R.id.open_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 打开一个活动页面接口
                 * level：游戏等级
                 * vipLevel：vip等级，没有就写""
                 */
//                iGama.openWebview(MainActivity.this,"roleLevel","10");
                iGama.openWebPage(MainActivity.this, "https://www.baidu.com/?tn=47018152_dg");
            }
        });

        showPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.openPlatform(MainActivity.this, "10", "2");
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        iGama.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        iGama.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        iGama.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        iGama.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        iGama.onDestroy(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PL.i("activity onRequestPermissionsResult");
        iGama.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        iGama.onWindowFocusChanged(this,hasFocus);
    }
}
