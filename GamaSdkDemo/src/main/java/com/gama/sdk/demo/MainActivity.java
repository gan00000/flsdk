package com.gama.sdk.demo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.core.base.utils.PL;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.SLog;
import com.gama.data.login.ILoginCallBack;
import com.gama.data.login.response.SLoginResponse;
import com.gama.pay.gp.GooglePayActivity2;
import com.gama.pay.gp.util.IabHelper;
import com.gama.pay.gp.util.IabResult;
import com.gama.pay.gp.util.Inventory;
import com.gama.pay.gp.util.Purchase;
import com.gama.sdk.callback.IPayListener;
import com.gama.sdk.out.ISdkCallBack;
import com.gama.sdk.out.IGama;
import com.gama.sdk.out.GamaFactory;
import com.google.firebase.FirebaseApiNotAvailableException;

import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, othersPayButton,googlePayBtn,shareButton, showPlatform, crashlytics;
    IabHelper mHelper;
    private IGama iGama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SLog.enableDebug(true);

        loginButton = (Button) findViewById(R.id.demo_login);
        othersPayButton = (Button) findViewById(R.id.demo_pay);
        googlePayBtn = (Button) findViewById(R.id.demo_pay_google);
        shareButton = (Button) findViewById(R.id.demo_share);
        showPlatform = findViewById(R.id.showPlatform);
        crashlytics = findViewById(R.id.Crashlytics);

        iGama = GamaFactory.create();

        iGama.setGameLanguage(this, SGameLanguage.zh_TW);

        //初始化sdk
        iGama.initSDK(this);

        //在游戏Activity的onCreate生命周期中调用
        iGama.onCreate(this);

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
                            /**
                             * 同步角色信息(以下均为测试信息)
                             */
                            String roleId = "123"; //角色id
                            String roleName = "角色名"; //角色名
                            String roleLevel = "10"; //角色等级
                            String vipLevel = "5"; //角色vip等级
                            String serverCode = "666"; //角色伺服器id
                            String serverName = "S1"; //角色伺服器名称
                            iGama.registerRoleInfo(MainActivity.this, roleId, roleName, roleLevel, vipLevel, serverCode, serverName);
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
                            dialog.show();
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
                customize 自定义透传字段（从服务端回调到cp）
                */
                iGama.pay(MainActivity.this, SPayType.OTHERS, "" + System.currentTimeMillis(), "payone", "customize", new IPayListener() {
                    @Override
                    public void onPayFinish(Bundle bundle) {
                        PL.i("OtherPay支付结束");
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
                iGama.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(), "com.ezfy.1usd", "customize", new IPayListener() {
                    @Override
                    public void onPayFinish(Bundle bundle) {
                        PL.i("GooglePay结束");
                        int status = 0;
                        if(bundle != null) {
                            status = bundle.getInt("status");

                        }
                        for (String next : bundle.keySet()) {
                            PL.i(next + " : " + bundle.get(next));
                        }
                    }
                });

            }
        });


        // String shareUrl = "http://ads.starb168.com/ads_scanner?gameCode=mthxtw&adsPlatForm=star_event&advertiser=share";
        // https://dodi.gamamobi.com/share/index.html?gameCode=dodi&userId=1&roleId=123&roleName=erge&serverCode=1000&serverName=testServer&package=com.xxx.xxx&
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
                 */
                iGama.openWebview(MainActivity.this);
            }
        });

        findViewById(R.id.open_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 打开一个指定的url
                 * String url: 打开的url
                 */
                iGama.openWebPage(MainActivity.this, "https://dodi.gamamobi.com/news/index.html");
            }
        });

        showPlatform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iGama.openPlatform(MainActivity.this);
            }
        });

        mHelper = new IabHelper(MainActivity.this.getApplicationContext());
        findViewById(R.id.xiaofei).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mHelper.isSetupDone()) {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            if(result.isSuccess()) {
                                SLog.logD("初始化iabHelper成功，开始消费");
                                consume();
                            } else {
                                SLog.logD("初始化iabHelper失败，消费结束");
                            }
                        }
                    });
                } else {
                    SLog.logD("已经初始化iabHelper，开始消费");
                    consume();
                }
            }
        });

        crashlytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crashlytics.getInstance().crash(); // Force a crash
            }
        });
    }

    private void consume() {
        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    // callbackFail();
                    PL.i("query result:" + result.getMessage());
                    SLog.logD("getQueryInventoryState is null");
                } else {

                    SLog.logD("Query inventory was successful.");
                    List<Purchase> purchaseList = inventory.getAllPurchases();

                    if (null == purchaseList || purchaseList.isEmpty()) {
                        //没有未消费的商品
                        //  callbackFail();
                        SLog.logD("purchases is empty");

                    } else {
                        SLog.logD("purchases size: " + purchaseList.size());

                        if (purchaseList.size() == 1) {
                            SLog.logD("mConsumeFinishedListener. 消费一个");
                            mHelper.consumeAsync(purchaseList.get(0), new IabHelper.OnConsumeFinishedListener() {
                                @Override
                                public void onConsumeFinished(Purchase purchase, IabResult result) {
                                    if (null != purchase) {
                                        SLog.logD("Purchase: " + purchase.toString() + ", result: " + result);
                                    } else {
                                        SLog.logD("Purchase is null");
                                    }
                                    if (result.isSuccess()) {
                                        SLog.logD("Consumption successful.");
                                        if (purchase != null) {
                                            SLog.logD("sku: " + purchase.getSku() + " Consume finished success");
                                        }
                                    } else {
                                        SLog.logD("consumption is not success, yet to be consumed.");
                                    }
                                }
                            });
                        } else if (purchaseList.size() > 1) {
                            SLog.logD("mConsumeMultiFinishedListener.消费多个");
                            mHelper.consumeAsync(purchaseList, new IabHelper.OnConsumeMultiFinishedListener() {
                                @Override
                                public void onConsumeMultiFinished(List<Purchase> purchases, List<IabResult> results) {
                                    SLog.logD("Consume Multiple finished.");
                                    for (int i = 0; i < purchases.size(); i++) {
                                        if (results.get(i).isSuccess()) {
                                            SLog.logD("sku: " + purchases.get(i).getSku() + " Consume finished success");
                                        } else {
                                            SLog.logD("sku: " + purchases.get(i).getSku() + " Consume finished fail");
                                            SLog.logD(purchases.get(i).getSku() + "consumption is not success, yet to be consumed.");
                                        }
                                    }
                                    SLog.logD("End consumption flow.");
                                }
                            });
                        }
                    }
                }
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
