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

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
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
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.out.MWSdkFactory;
import com.mw.sdk.out.IMWSDK;
import com.mw.sdk.demo.R;

public class MainActivity extends Activity {

    protected Button loginButton, webPayButton, googlePayBtn, shareButton, showPlatform, demo_language,
            demo_share;
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
        demo_share = findViewById(R.id.demo_share);

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
                            //是否绑定手机
                            boolean isBindPhone = sLoginResponse.getData().isBindPhone();
                            //是否绑定账号
                            boolean isBind = sLoginResponse.getData().isBind();
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
                MarketUtil.openMarket(MainActivity.this, MainActivity.this.getPackageName());
            }
        });

        demo_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMWSDK.share(MainActivity.this, "#萬靈召喚師","2022首款卡牌大作【萬靈召喚師】，爆笑來襲！從東方文明到西方文明的羈絆，從神族到魔族的對抗，一段奇妙的神仙冒險之旅就此展開！","https://share.leyouye.com/aedzj/1.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share failure");
                    }
                });
            }
        });
        findViewById(R.id.demo_share2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMWSDK.share(MainActivity.this, "#萬靈召喚師","2022首款卡牌大作【萬靈召喚師】，爆笑來襲！從東方文明到西方文明的羈絆，從神族到魔族的對抗，一段奇妙的神仙冒險之旅就此展開！","https://static-resource.meowplayer.com/share/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share failure");
                    }
                });

//                if (Build.VERSION.SDK_INT >= 33) {
//                    //todo 现在还没13系统设备，无法测试先注释
////                    PermissionUtil.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, RequestCode.RequestCode_Permission_POST_NOTIFICATIONS);
//                    askNotificationPermission();
//                }
            }
        });
        findViewById(R.id.share_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMWSDK.shareLine(MainActivity.this, "2022首款卡牌大作【萬靈召喚師】，爆笑來襲！從東方文明到西方文明的羈絆，從神族到魔族的對抗，一段奇妙的神仙冒險之旅就此展開！https://static-resource.meowplayer.com/share/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share failure");
                    }
                });

//                if (Build.VERSION.SDK_INT >= 33) {
//                    //todo 现在还没13系统设备，无法测试先注释
////                    PermissionUtil.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, RequestCode.RequestCode_Permission_POST_NOTIFICATIONS);
//                    askNotificationPermission();
//                }
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIMWSDK.logout(MainActivity.this, new ISdkCallBack() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void failure() {

                    }
                });
            }
        });

        findViewById(R.id.bindphone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIMWSDK.showBindPhoneView(MainActivity.this, new SFCallBack<BaseResponseModel>() {
                    @Override
                    public void success(BaseResponseModel result, String msg) {

                    }

                    @Override
                    public void fail(BaseResponseModel result, String msg) {

                    }
                });

            }
        });
        findViewById(R.id.updateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIMWSDK.showUpgradeAccountView(MainActivity.this, new SFCallBack() {
                    @Override
                    public void success(Object result, String msg) {

                    }

                    @Override
                    public void fail(Object result, String msg) {

                    }
                });

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

    // Declare the launcher at the top of your Activity/Fragment:
//    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                if (isGranted) {
//                    // FCM SDK (and your app) can post notifications.
//                } else {
//                    // TODO: Inform user that that your app will not show notifications.
//                }
//            });

    // ...
    private void askNotificationPermission() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
//                    PackageManager.PERMISSION_GRANTED) {
//                // FCM SDK (and your app) can post notifications.
//            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
//                // TODO: display an educational UI explaining to the user the features that will be enabled
//                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
//                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
//                //       If the user selects "No thanks," allow the user to continue without notifications.
//                PL.i("shouldShowRequestPermissionRationale");
//            } else {
//                // Directly ask for the permission
////                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//            }
//        }
    }

}
