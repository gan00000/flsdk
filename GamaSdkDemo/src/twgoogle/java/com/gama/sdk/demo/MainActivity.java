package com.gama.sdk.demo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.core.base.utils.GamaTimeUtil;
import com.core.base.utils.PL;
import com.core.base.utils.ToastUtils;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.SLog;
import com.gama.pay.gp.util.IabHelper;
import com.gama.pay.gp.util.IabResult;
import com.gama.pay.gp.util.Inventory;
import com.gama.pay.gp.util.Purchase;
import com.gama.pay.gp.util.SkuDetails;
import com.gama.pay.utils.GamaQueryProductListener;
import com.gama.sdk.callback.IPayListener;
import com.gama.thirdlib.twitter.GamaTwitterLogin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseMainActivity {

    private IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SLog.enableDebug(true);

        //初始化sdk
        iGama.initSDK(this, SGameLanguage.zh_TW );

        googlePayBtn.setVisibility(View.VISIBLE);
        googlePayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iGama.pay(MainActivity.this, SPayType.GOOGLE, "" + System.currentTimeMillis(), getResources().getString(R.string.test_sku), "customize", new IPayListener() {
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
                iGama.openPlatform(MainActivity.this);
                GamaTimeUtil.getBeiJingTime(MainActivity.this);
                GamaTimeUtil.getDisplayTime(MainActivity.this);
            }
        });

        mHelper = new IabHelper(MainActivity.this.getApplicationContext());

        xiaofei.setVisibility(View.VISIBLE);
        xiaofei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHelper.isSetupDone()) {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            if (result.isSuccess()) {
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

        PurchasesHistory.setVisibility(View.VISIBLE);
        PurchasesHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHelper.isSetupDone()) {
                    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                        @Override
                        public void onIabSetupFinished(IabResult result) {
                            if (result.isSuccess()) {
                                SLog.logD("初始化iabHelper成功，开始查历史记录");
                                mHelper.queryPurchasesHistory();
                            } else {
                                SLog.logD("初始化iabHelper失败，查历史记录结束");
                            }
                        }
                    });
                } else {
                    SLog.logD("已经初始化iabHelper，开始查历史记录");
                    mHelper.queryPurchasesHistory();
                }
            }
        });

        chaxun.setVisibility(View.VISIBLE);
        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                querySku();
            }
        });
    }

    private void querySku() {
//        final List<String> list = Arrays.asList(BuildConfig.PRODUCE_LIST);
//        list.add(getResources().getString(R.string.test_sku));
//        list.add("com.sd.zshk");
//        list.add("com.sd.xcpb");
//        list.add("com.sd.1usd");
//        list.add("com.sd.5usd");
//        list.add("com.sd.10usd'");
//        list.add("com.sd.20usd");
//        list.add("com.sd.50usd");
//        list.add("com.sd.100usd");
//        list.add("com.sd.cz");
//        list.add("com.sd.tzlb");
//        list.add("com.sd.sslb");
        List<String> list = Arrays.asList("com.ezfy.1usd", "com.sd.qmlb.5usd","com.sd.qmlb.25usd","com.sd.qmlb.50usd","com.sd.ztlb.5usd","com.sd.ztlb.25usd","com.sd.ztlb.50usd",
                "com.sd.sjlb.5usd","com.sd.sjlb.25usd","com.sd.sjlb.50usd","com.sd.xslb.5usd","com.sd.xslb.25usd","com.sd.xslb.50usd","com.sd.chlb.5usd",
                "com.sd.chlb.25usd","com.sd.chlb.50usd","com.sd.zshk","com.sd.xcpb","com.sd.1usd","com.sd.5usd","com.sd.10usd", "com.ezfy.100usd");  //20
//                ,"com.sd.20usd"  //21
//                , "com.sd.50usd","com.sd.100usd","com.sd.cz","com.sd.tzlb","com.sd.sslb");
        iGama.gamaQueryProductDetail(this, SPayType.GOOGLE, list, new GamaQueryProductListener() {
            @Override
            public void onQueryResult(Map<String, SkuDetails> details) {
                String detail = "";
                if(details != null && details.size() > 0) {
                    detail += "查询总数： " + details.size() + "\n\n";
                    for (Map.Entry<String, SkuDetails> entry : details.entrySet()) {
                        SkuDetails skuDetails = entry.getValue();
                        String title = skuDetails.getTitle();//名称
                        String sku = skuDetails.getSku();//产品ID
                        String type = skuDetails.getType();//类型
                        String priceCurrencyCode = skuDetails.getPrice_currency_code();//价格编号
                        String price = skuDetails.getPrice();//价格
                        String description = skuDetails.getDescription();//商品描述

                        detail += "title: " + title
                                + "\n sku: " + sku
                                + "\n type: " + type
                                + "\n priceCurrencyCode: " + priceCurrencyCode
                                + "\n price: " + price
                                + "\n description: " + description
                                + "\n\n";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("商品信息")
                            .setMessage(detail);
                    builder.create().show();
                } else {
                    ToastUtils.toast(MainActivity.this, "没有查到商品信息");
                }
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
