package com.game.sdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.core.base.utils.MarketUtil;
import com.core.base.utils.PL;
import com.mw.base.bean.SGameLanguage;
import com.mw.base.bean.SPayType;
import com.mw.base.utils.SdkUtil;
import com.mw.base.utils.SLog;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.out.ICompleteListener;
import com.mw.sdk.out.MWSdkFactory;
import com.mw.sdk.out.IMWSDK;
import com.mw.sdk.demo.R;

public class MainActivity extends Activity {

    protected Button loginButton, webPayButton, googlePayBtn, shareButton, showPlatform, demo_language,
            demo_cs;
    protected IMWSDK mIMWSDK;
    protected String userId;

    /**
     * 同步角色信息(以下均为测试信息)
     */
    String roleId = "20001000402"; //角色id
    String roleName = "貪婪聖殿"; //角色名
    String roleLevel = "106"; //角色等级
    String vipLevel = "5"; //角色vip等级
    String serverCode = "999"; //角色伺服器id
    String serverName = "S1"; //角色伺服器名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLog.enableDebug(true);

        loginButton = findViewById(R.id.demo_login);
        demo_language = findViewById(R.id.demo_language);
        webPayButton = findViewById(R.id.web_pay);
        googlePayBtn = findViewById(R.id.demo_pay_google);
        demo_cs = findViewById(R.id.demo_cs);

        mIMWSDK = MWSdkFactory.create();

        //初始化sdk
        mIMWSDK.initSDK(this, SGameLanguage.zh_TW);

        //在游戏Activity的onCreate生命周期中调用
        mIMWSDK.onCreate(this);

        mIMWSDK.setGameLanguage(MainActivity.this, SGameLanguage.zh_TW);

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
                mIMWSDK.login(MainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
                        if (sLoginResponse != null && sLoginResponse.getData() != null) {
                            String uid = sLoginResponse.getData().getUserId();
                            userId = uid;
                            String accessToken = sLoginResponse.getData().getToken();
                            String timestamp = sLoginResponse.getData().getTimestamp();
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
                                    .setMessage(sLoginResponse.getData().print())
                                    .create();
//                            dialog.show();


                            mIMWSDK.registerRoleInfo(MainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
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

//                com.game.superand.1usd
//                com.game.superand.2usd
                String skuId = "com.game.superand.1usd";
                mIMWSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),skuId, "xxxx","role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {

                    }

                    @Override
                    public void onPayFail() {

                    }

                });


            }
        });

        findViewById(R.id.demo_pay_google2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skuId = "com.game.superand.2usd";
                mIMWSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),skuId, "xxxx", "role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {

                    }

                    @Override
                    public void onPayFail() {

                    }

                });
            }
        });



        webPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skuId = "com.game.superand.2usd";
                mIMWSDK.pay(MainActivity.this, SPayType.WEB, "" + System.currentTimeMillis(),skuId, "xxxx", "role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {
                        PL.i("webPayButton onPaySuccess");
                    }

                    @Override
                    public void onPayFail() {

                    }

                });
            }
        });

        findViewById(R.id.openScore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMWSDK.requestStoreReview(MainActivity.this, new ICompleteListener() {
                    @Override
                    public void onComplete() {
                        //重要提示：如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
                        PL.i("requestReviewFlow onComplete");
                    }
                });
            }
        });
        findViewById(R.id.goDownLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketUtil.openMarket(MainActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        mIMWSDK.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIMWSDK.onActivityResult(this, requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mIMWSDK.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        mIMWSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        mIMWSDK.onDestroy(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PL.i("activity onRequestPermissionsResult");
        mIMWSDK.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mIMWSDK.onWindowFocusChanged(this, hasFocus);
    }

    private boolean isLogin() {
        return !TextUtils.isEmpty(SdkUtil.getUid(this));
    }

}
