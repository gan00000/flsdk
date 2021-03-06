package com.gama.sdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.core.base.utils.PL;
import com.flyfun.base.bean.SGameLanguage;
import com.flyfun.base.bean.SPayType;
import com.flyfun.base.utils.GamaUtil;
import com.flyfun.base.utils.SLog;
import com.flyfun.data.login.ILoginCallBack;
import com.flyfun.data.login.response.SLoginResponse;
import com.flyfun.sdk.callback.IPayListener;
import com.flyfun.sdk.out.FlSdkFactory;
import com.flyfun.sdk.out.IFLSDK;

public class MainActivity extends Activity {

    protected Button loginButton, othersPayButton, googlePayBtn, shareButton, showPlatform, demo_language,
            demo_cs;
    protected IFLSDK mIFLSDK;
    protected String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLog.enableDebug(true);

        loginButton = findViewById(R.id.demo_login);
        demo_language = findViewById(R.id.demo_language);
        othersPayButton = findViewById(R.id.demo_pay);
        googlePayBtn = findViewById(R.id.demo_pay_google);
        demo_cs = findViewById(R.id.demo_cs);

        mIFLSDK = FlSdkFactory.create();

        //初始化sdk
        mIFLSDK.initSDK(this, SGameLanguage.zh_TW);

        //在游戏Activity的onCreate生命周期中调用
        mIFLSDK.onCreate(this);

        mIFLSDK.setGameLanguage(MainActivity.this, SGameLanguage.zh_TW);

//        demo_language.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
//                        .setItems(new String[]{"繁中", "日语", "韩语"}, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                SGameLanguage language = null;
//                                switch (which) {
//                                    case 0:
//                                        language = SGameLanguage.zh_TW;
//                                        break;
//                                    case 1:
//                                        language = SGameLanguage.ja_JP;
//                                        break;
//                                    case 2:
//                                        language = SGameLanguage.ko_KR;
//                                        break;
//                                }
//                                IFLSDK.setGameLanguage(MainActivity.this, language);
//                            }
//                        })
//                        .setTitle("选择语言");
//                builder.create().show();
//            }
//        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //登陆接口 ILoginCallBack为登录成功后的回调
                mIFLSDK.login(MainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
                        if (sLoginResponse != null) {
                            String uid = sLoginResponse.getUserId();
                            userId = uid;
                            String accessToken = sLoginResponse.getAccessToken();
                            String timestamp = sLoginResponse.getTimestamp();
                            //todo 进行验证

                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= 21) {
                                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog);
                            } else if (Build.VERSION.SDK_INT >= 14) {
                                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Dialog);
                            } else {
                                builder = new AlertDialog.Builder(MainActivity.this);
                            }
                            AlertDialog dialog = builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false)
                                    .setMessage(sLoginResponse.msg())
                                    .create();
                            dialog.show();

                            /**
                             * 同步角色信息(以下均为测试信息)
                             */
                            String roleId = "20001000402"; //角色id
                            String roleName = "貪婪聖殿"; //角色名
                            String roleLevel = "106"; //角色等级
                            String vipLevel = "5"; //角色vip等级
                            String serverCode = "1000"; //角色伺服器id
                            String serverName = "S1"; //角色伺服器名称
                            mIFLSDK.registerRoleInfo(MainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
                        } else {
                            PL.i("从登录界面返回");
                            AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= 21) {
                                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog);
                            } else if (Build.VERSION.SDK_INT >= 14) {
                                builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Holo_Dialog);
                            } else {
                                builder = new AlertDialog.Builder(MainActivity.this);
                            }
                            AlertDialog dialog = builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            }).setPositiveButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loginButton.performClick();
                                }
                            }).setCancelable(false)
                                    .setMessage("是否退出遊戲")
                                    .create();
//                            dialog.show();
                        }
                    }
                });

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
                customize 自定义透传字段（从服务端回调到cp）
                */
                mIFLSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),"com.sku1", "xxx", new IPayListener() {
                    @Override
                    public void onPayFinish(Bundle bundle) {
                        PL.i("支付结束");
                    }
                });


            }
        });


        demo_cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 打开客服页面
                 */
                mIFLSDK.openCs(MainActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        mIFLSDK.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIFLSDK.onActivityResult(this, requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mIFLSDK.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        mIFLSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        mIFLSDK.onDestroy(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PL.i("activity onRequestPermissionsResult");
        mIFLSDK.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mIFLSDK.onWindowFocusChanged(this, hasFocus);
    }

    private boolean isLogin() {
        return !TextUtils.isEmpty(GamaUtil.getUid(this));
    }

}
