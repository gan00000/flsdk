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

import com.core.base.bean.BaseResponseModel;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.AppUtil;
import com.core.base.utils.MarketUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.core.base.utils.ToastUtils;
import com.mw.base.bean.SPayType;
import com.mw.sdk.act.FloatContentView;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.MenuData;
import com.mw.sdk.callback.FloatCallback;
import com.mw.sdk.utils.SdkUtil;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.demo.R;
import com.mw.sdk.login.ILoginCallBack;
import com.mw.sdk.login.model.response.SLoginResponse;
import com.mw.sdk.out.IMWSDK;
import com.mw.sdk.out.ISdkCallBack;
import com.mw.sdk.out.MWSdkFactory;
import com.mw.sdk.act.FloatingManager;
import com.mw.sdk.widget.SBaseDialog;

import java.util.ArrayList;

public class MainActivity extends Activity {

    protected Button loginButton, webPayButton, googlePayBtn, shareButton, showPlatform, demo_language,
            demo_share;
    protected IMWSDK mIMWSDK;
    protected String userId;
    Activity activity;
    /**
     * 同步角色信息(以下均为测试信息)
     */
    String roleId = "20001000402"; //角色id
    String roleName = "貪婪聖殿"; //角色名
    String roleLevel = "106"; //角色等级
    String vipLevel = "5"; //角色vip等级
    String serverCode = "1"; //角色伺服器id
    String serverName = "S1"; //角色伺服器名称
    String extra = "extra_xxxx";

    SLoginResponse tempSLoginResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        loginButton = findViewById(R.id.demo_login);
        demo_language = findViewById(R.id.demo_language);
        webPayButton = findViewById(R.id.web_pay);
        googlePayBtn = findViewById(R.id.demo_pay_google);
        demo_share = findViewById(R.id.demo_share);

        mIMWSDK = MWSdkFactory.create();

        //在游戏Activity的onCreate生命周期中调用
        mIMWSDK.onCreate(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //登陆接口 ILoginCallBack为登录成功后的回调
                mIMWSDK.login(MainActivity.this, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
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
                String skuId = "com.tgswkr.koda.499";
                mIMWSDK.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(),skuId, extra,roleId,roleName,roleLevel, vipLevel,serverCode, serverName, new IPayListener() {

                    @Override
                    public void onPaySuccess(String productId, String cpOrderId) {
                        ToastUtils.toast(activity,"充值成功>" + skuId);
                    }

                    @Override
                    public void onPayFail() {
                        ToastUtils.toast(activity,"充值失败>" + skuId);
                    }

                });
//                mIMWSDK.checkPreRegData(activity, new ISdkCallBack() {
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
                String skuId = "com_mwyyz_tw_1u";
                mIMWSDK.pay(MainActivity.this, SPayType.HUAWEI, "" + System.currentTimeMillis(),skuId, extra,roleId,roleName,roleLevel, vipLevel, serverCode, serverName, new IPayListener() {

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
                mIMWSDK.pay(MainActivity.this, SPayType.QooApp, "" + System.currentTimeMillis(),skuId, extra,roleId,roleName,roleLevel, vipLevel,serverCode, serverName, new IPayListener() {

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
                String skuId = "com.yuewan.tgvn.99";
                mIMWSDK.pay(MainActivity.this, SPayType.WEB, "" + System.currentTimeMillis(),skuId, extra,roleId,roleName,roleLevel, vipLevel,serverCode, serverName, new IPayListener() {

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
                mIMWSDK.requestStoreReview(MainActivity.this, new SFCallBack() {
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
                mIMWSDK.share(MainActivity.this, "#獲得最多好友推薦萬靈應召而來","集結東西方神話英靈《萬靈召喚師》卡牌RPG，以高養成自由度為核心，無限搭配的陣營組合，掌握六種無法想象的強大力量，召喚師們踏上拯救三界的道路！","https://member.mmhyplayer.com/sdk/share/wlzhs/index.html", new ISdkCallBack() {
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
                mIMWSDK.shareLine(MainActivity.this, "★胡宇威盛裝代言★邀你前往異時空三國世界！重新定義三國名將，在鬼武時空大顯神威！https://member.dustyx.com/sdk/share/gwsg/index.html", new ISdkCallBack() {
                    @Override
                    public void success() {
                        PL.i("share success");
                    }

                    @Override
                    public void failure() {
                        PL.i("share failure");
                    }
                });

//                mIMWSDK.openFbUrl(MainActivity.this,"https://www.facebook.com/groups/431250895792522");

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
                mIMWSDK.switchLogin(activity, new ILoginCallBack() {
                    @Override
                    public void onLogin(SLoginResponse sLoginResponse) {
                        handleLoginResponse(sLoginResponse);
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
                        //todo绑定手机成功

                    }

                    @Override
                    public void fail(BaseResponseModel result, String msg) {
                        //todo绑定手机失败
                    }
                });

            }
        });
        findViewById(R.id.updateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIMWSDK.showUpgradeAccountView(MainActivity.this, new SFCallBack<SLoginResponse>() {
                    @Override
                    public void success(SLoginResponse sLoginResponse, String msg) {
                        //账号升级成功

                    }

                    @Override
                    public void fail(SLoginResponse result, String msg) {

                    }
                });

            }
        });

        findViewById(R.id.open_float_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SStringUtil.isEmpty(SdkUtil.getUid(activity))){
                    ToastUtils.toast(activity,"请先登录");
                    return;
                }
                mIMWSDK.showFloatView(activity, new FloatCallback() {
                    @Override
                    public void switchAccount(String msg) {

                        //研发再次需要进行切换账号，重新回到登录页面
                        //.....研发处理游戏退出，退出后重新调用登录接口......

                        /*mIMWSDK.login(MainActivity.this, new ILoginCallBack() {
                            @Override
                            public void onLogin(SLoginResponse sLoginResponse) {
                                handleLoginResponse(sLoginResponse);
                            }
                        });*/
                    }
                });

            }
        });

//        mIMWSDK.requestVfCode(this, "86", "13622843403", new SFCallBack<BaseResponseModel>() {
//            @Override
//            public void success(BaseResponseModel responseModel, String result) {
//                //获取手机验证码成功
//                //todo
//            }
//
//            @Override
//            public void fail(BaseResponseModel responseModel, String result) {
//                if (responseModel != null){
//                    String errMsg = responseModel.getMessage();//获取验证码 错误信息
//                }
//            }
//        });
//
//        mIMWSDK.requestBindPhone(this, "86", "13622843403", "111111", new SFCallBack<SLoginResponse>() {
//            @Override
//            public void success(SLoginResponse sLoginResponse, String result) {
//                if (sLoginResponse != null) {
//                    String tel = sLoginResponse.getData().getTelephone();//绑定的手机号码
//                }
//            }
//
//            @Override
//            public void fail(SLoginResponse sLoginResponse, String result) {
//                if (sLoginResponse != null) {
//                    String errMsg = sLoginResponse.getMessage();
//                }
//
//            }
//        });
//
//        mIMWSDK.requestUpgradeAccount(this, "xxx", "pwd", new SFCallBack<SLoginResponse>() {
//
//            @Override
//            public void success(SLoginResponse result, String msg) {
//                //账号升级成功
//            }
//
//            @Override
//            public void fail(SLoginResponse result, String msg) {
//                //账号升级绑定失败
//            }
//        });

        findViewById(R.id.cs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIMWSDK.openCs(MainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
            }
        });
        findViewById(R.id.checkGoogleServer).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                mIMWSDK.checkGooglePlayServicesAvailable(MainActivity.this);
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
                sendNotification("tests觉得基督教", "kdkkdk好的哈哈哈哈");
            }
        });
        findViewById(R.id.showTogDialog).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                mIMWSDK.showTogglePayDialog(MainActivity.this, new PayCreateOrderReqBean(MainActivity.this));
            }
        });
        findViewById(R.id.showSocialBanner).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                if(SStringUtil.isEmpty(SdkUtil.getUid(activity))){
                    ToastUtils.toast(activity,"请先登录");
                    return;
                }
                mIMWSDK.showSocialView(MainActivity.this);
            }
        });

        findViewById(R.id.showActExpo).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {

                if(SStringUtil.isEmpty(SdkUtil.getUid(activity))){
                    ToastUtils.toast(activity,"请先登录");
                    return;
                }
                mIMWSDK.showActView(MainActivity.this);

//                ArrayList<ActDataModel.ActData> arrayList = new ArrayList<>();
//                for (int i = 0; i < 10; i++) {
//                    ActDataModel.ActData actData = new ActDataModel.ActData();
//                    actData.setContentUrl("https://www.baidu.com/");
//                    actData.setTitle("test=" + i);
//                    arrayList.add(actData);
//                }
//                SBaseDialog commonDialog = new SBaseDialog(activity, com.mw.sdk.R.style.Sdk_Theme_AppCompat_Dialog_Notitle_Fullscreen);
//                ActExpoView mActExpoView = new ActExpoView(activity, arrayList, commonDialog);
//                mActExpoView.setsBaseDialog(commonDialog);
//                commonDialog.setContentView(mActExpoView);
//                commonDialog.show();

            }
        });
        AppUtil.hideActivityBottomBar(this);
    }

    private void handleLoginResponse(SLoginResponse sLoginResponse) {
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
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


    private void sendNotification(String messageTitle, String messageBody) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager == null) {
            return;
        }

        PendingIntent pendingIntent = null;
        String pkName = getApplicationContext().getPackageName();
        if (!TextUtils.isEmpty(pkName)) {
            Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(pkName);
            if(intent != null) {

// Targeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                        PendingIntent.FLAG_ONE_SHOT);

                pendingIntent = PendingIntent.getActivity(this,
                        0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            }
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int icon = getPushIconId(getApplicationContext());
        if(TextUtils.isEmpty(messageTitle)) {
            messageTitle = getApplicationName(this);
        }

//        小图标：必须提供，通过 setSmallIcon() 进行设置。
//        应用名称：由系统提供。
//        时间戳：由系统提供，但您可以使用 setWhen() 替换它或者使用 setShowWhen(false) 隐藏它。
//        大图标：可选内容（通常仅用于联系人照片，请勿将其用于应用图标），通过 setLargeIcon() 进行设置。
//        标题：可选内容，通过 setContentTitle() 进行设置。
//        文本：可选内容，通过 setContentText() 进行设置

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(messageTitle)
                .setSmallIcon(icon)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        if (pendingIntent != null) {
            notificationBuilder.setContentIntent(pendingIntent);
        }

        if (Build.VERSION.SDK_INT >= 26) {

            // The id of the channel.
            String id = "nw_notify_channel";
            // The user-visible name of the channel.
//            String name = context.getApplicationInfo().name;
            String name = "new message";
            //范围是 IMPORTANCE_NONE(0) 至 IMPORTANCE_HIGH(4)。默认重要性级别为 3：在所有位置显示，发出提示音，但不会对用户产生视觉干扰。
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(mChannel);
            notificationBuilder.setChannelId(id);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

//     * 获取推送icon的id
//     * @return 先读取meta-data配置(com.google.firebase.messaging.default_notification_icon)，然后读取应用icon。


    public static int getPushIconId(Context context) {
        int iconId = 0;
        try {
            ApplicationInfo appInfoappInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle metaData = appInfoappInfo.metaData;
            if(metaData != null) {
                iconId = metaData.getInt("com.google.firebase.messaging.default_notification_icon");
            } else {
                PL.i("当前应用没有在meta-data配置");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(iconId == 0) {
            ApplicationInfo info = context.getApplicationInfo();
            iconId = info.icon;
            PL.i("没有找到推送icon，使用应用icon : " + iconId);
        }
        return iconId;
    }
}
