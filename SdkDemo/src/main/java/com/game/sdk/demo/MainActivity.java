package com.game.sdk.demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.ldy.pub.DYSdk;
import com.ldy.pub.IDYSDK;
import com.ldy.sdk.login.model.response.SLoginResult;
import com.mybase.bean.BaseResultModel;
import com.ldy.callback.SFCallBack;
import com.mybase.utils.MarketUtil;
import com.mybase.utils.PL;
import com.mybase.utils.TimeUtil;
import com.mybase.utils.ToastUtils;
import com.ldy.base.bean.SPayType;
import com.ldy.base.utils.SLog;
import com.ldy.base.utils.SdkUtil;
import com.ldy.callback.IPayListener;
import com.ldy.sdk.demo.R;
import com.ldy.callback.ILoginCallBack;
import com.ldy.callback.ISdkCallBack;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    protected Button loginButton, webPayButton, googlePayBtn, shareButton, showPlatform, demo_language,
            demo_share;
    protected IDYSDK mIDYSDK;
    protected String userId;
    Activity activity;
    /**
     * 同步角色信息(以下均为测试信息)
     */
    String roleId = "20001000402"; //角色id
    String roleName = "你好"; //角色名
    String roleLevel = "106"; //角色等级
    String vipLevel = "5"; //角色vip等级
    String serverCode = "1"; //角色伺服器id
    String serverName = "S1"; //角色伺服器名称

    SLoginResult tempSLoginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        SLog.enableDebug(true);

        loginButton = findViewById(R.id.demo_login);
        demo_language = findViewById(R.id.demo_language);
        webPayButton = findViewById(R.id.web_pay);
        googlePayBtn = findViewById(R.id.demo_pay_google);
        demo_share = findViewById(R.id.demo_share);

        mIDYSDK = DYSdk.getInstance();

        //在游戏Activity的onCreate生命周期中调用
        mIDYSDK.onCreate(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //登陆接口 ILoginCallBack为登录成功后的回调
                mIDYSDK.login(MainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResult sLoginResponse) {
                        handleLoginResponse(sLoginResponse);
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
//                String skuId = "com.miaoou.6jin";
                String skuId = "com_xinhai_chmxt_3card";
                mIDYSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),skuId, "xxxx","role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {
                        ToastUtils.toast(activity,"充值成功>" + skuId);
                    }

                    @Override
                    public void onPayFail() {
                        ToastUtils.toast(activity,"充值失败>" + skuId);
                    }

                });
//                mIDYSDK.checkPreRegData(activity, new ISdkCallBack() {
//                    @Override
//                    public void success() {
////                        PL.d("checkPreRegData success");
//                        //需要发预注册奖励
//                    }
//
//                    @Override
//                    public void failure() {
////                        PL.d("checkPreRegData failure");
//                    }
//                });

            }
        });

        findViewById(R.id.demo_pay_hw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skuId = "com.miaoou.6jin";
                mIDYSDK.pay(MainActivity.this, SPayType.HUAWEI, "" + System.currentTimeMillis(),skuId, "xxxx", "role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {
                        ToastUtils.toast(activity,"充值成功>" + skuId);
                    }

                    @Override
                    public void onPayFail() {
                        ToastUtils.toast(activity,"充值失败>" + skuId);
                    }

                });
            }
        });

        findViewById(R.id.web_pay_qooapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skuId = "com.miaoou.6jin";
                mIDYSDK.pay(MainActivity.this, SPayType.QooApp, "" + System.currentTimeMillis(),skuId, "xxxx", "role_id_1","role_name","role_level","vipLevel",serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {
                        ToastUtils.toast(activity,"充值成功>" + skuId);
                    }

                    @Override
                    public void onPayFail() {
                        ToastUtils.toast(activity,"充值失败>" + skuId);
                    }

                });
            }
        });


        webPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String skuId = "com_xinhai_chmxt_1usd";
                mIDYSDK.pay(MainActivity.this, SPayType.WEB, "" + System.currentTimeMillis(),skuId, "xxxx", "role_id_1","role_name","role_level","vipLevel","1001", serverName, new IPayListener() {

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

                //重要提示：如果在应用内评价流程中出现错误，请勿通知用户或更改应用的正常用户流。调用 onComplete 后，继续执行应用的正常用户流。
                mIDYSDK.requestStoreReview(MainActivity.this, new SFCallBack() {
                    @Override
                    public void success(Object result, String msg) {
                        //评价成功
                    }

                    @Override
                    public void fail(Object result, String msg) {
                        //评价失败
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
                mIDYSDK.share(MainActivity.this, "#獲得最多好友推薦萬靈應召而來","集結東西方神話英靈《萬靈召喚師》卡牌RPG，以高養成自由度為核心，無限搭配的陣營組合，掌握六種無法想象的強大力量，召喚師們踏上拯救三界的道路！","https://member.mmhyplayer.com/sdk/share/wlzhs/index.html", new ISdkCallBack() {
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

        findViewById(R.id.share_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIDYSDK.shareLine(MainActivity.this, "★胡宇威盛裝代言★邀你前往異時空三國世界！重新定義三國名將，在鬼武時空大顯神威！https://member.dustyx.com/sdk/share/gwsg/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share failure");
                    }
                });

//                mIDYSDK.openFbUrl(MainActivity.this,"https://www.facebook.com/groups/431250895792522");

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
                mIDYSDK.switchLogin(activity, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResult sLoginResponse) {
                        handleLoginResponse(sLoginResponse);
                    }
                });
            }
        });

        findViewById(R.id.bindphone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIDYSDK.showBindPhoneView(MainActivity.this, new SFCallBack<BaseResultModel>() {
                    @Override
                    public void success(BaseResultModel result, String msg) {
                        //todo绑定手机成功

                    }

                    @Override
                    public void fail(BaseResultModel result, String msg) {
                        //todo绑定手机失败
                    }
                });

            }
        });
        findViewById(R.id.updateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIDYSDK.showUpgradeAccountView(MainActivity.this, new SFCallBack<SLoginResult>() {
                    @Override
                    public void success(SLoginResult sLoginResponse, String msg) {
                        //账号升级成功

                    }

                    @Override
                    public void fail(SLoginResult result, String msg) {

                    }
                });

            }
        });

//        mIDYSDK.requestVfCode(this, "86", "13622843403", new SFCallBack<BaseResultModel>() {
//            @Override
//            public void success(BaseResultModel responseModel, String result) {
//                //获取手机验证码成功
//                //todo
//            }
//
//            @Override
//            public void fail(BaseResultModel responseModel, String result) {
//                if (responseModel != null){
//                    String errMsg = responseModel.getMessage();//获取验证码 错误信息
//                }
//            }
//        });
//
//        mIDYSDK.requestBindPhone(this, "86", "13622843403", "111111", new SFCallBack<SLoginResult>() {
//            @Override
//            public void success(SLoginResult sLoginResponse, String result) {
//                if (sLoginResponse != null) {
//                    String tel = sLoginResponse.getData().getTelephone();//绑定的手机号码
//                }
//            }
//
//            @Override
//            public void fail(SLoginResult sLoginResponse, String result) {
//                if (sLoginResponse != null) {
//                    String errMsg = sLoginResponse.getMessage();
//                }
//
//            }
//        });
//
//        mIDYSDK.requestUpgradeAccount(this, "xxx", "pwd", new SFCallBack<SLoginResult>() {
//
//            @Override
//            public void success(SLoginResult result, String msg) {
//                //账号升级成功
//            }
//
//            @Override
//            public void fail(SLoginResult result, String msg) {
//                //账号升级绑定失败
//            }
//        });

        findViewById(R.id.cs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIDYSDK.openCs(MainActivity.this);
            }
        });
        findViewById(R.id.checkGoogleServer).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                mIDYSDK.checkGooglePlayServicesAvailable(MainActivity.this);
                ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                if (mActivityManager.isBackgroundRestricted()){
                    ToastUtils.toast(activity,"isBackgroundRestricted true");
                }else {
                    ToastUtils.toast(activity,"isBackgroundRestricted false");
                }
            }
        });

        findViewById(R.id.sendNotify).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {

                Date ydate = TimeUtil.getYesterday(Long.parseLong(tempSLoginResponse.getData().getTimestamp()));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                PL.i(sdf.format(ydate));
            }
        });

    }

    private void handleLoginResponse(SLoginResult sLoginResponse) {
        if (sLoginResponse != null && sLoginResponse.getData() != null) {
            tempSLoginResponse = sLoginResponse;
            String uid = sLoginResponse.getData().getUserId();
            userId = uid;
            String sign = sLoginResponse.getData().getSign();
            String timestamp = sLoginResponse.getData().getTimestamp();

            //检验
//            我们这边现在登录验证没有提供接口，贵方通过加密规则检验
//                    sign = md5(key+ gameCode + uid + timestamp);
//            sign 、uid、timestamp  sdk登录返回
//            gameCode Android为 tgvn . ios为tgvnios

            //是否绑定手机
            boolean isBindPhone = sLoginResponse.getData().isBindPhone();
            //绑定的手机号码
            String telephone = sLoginResponse.getData().getTelephone();
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


            mIDYSDK.setRoleInfo(MainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
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

    @Override
    protected void onResume() {
        super.onResume();
        PL.i("activity onResume");
        mIDYSDK.onResume(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mIDYSDK.onActivityResult(this, requestCode, resultCode, data);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mIDYSDK.onPause(this);
        PL.i("activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PL.i("activity onStop");
        mIDYSDK.onStop(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PL.i("activity onDestroy");
        mIDYSDK.onDestroy(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PL.i("activity onRequestPermissionsResult");
        mIDYSDK.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mIDYSDK.onWindowFocusChanged(this, hasFocus);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PL.i("activity onBackPressed");
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
