package com.gama.sdk.out;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.utils.PL;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.SLog;
import com.gama.pay.gp.GooglePayActivity2;
import com.gama.pay.gp.GooglePayHelper;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.utils.GamaQueryProductListener;
import com.gama.sdk.ads.StarEventLogger;
import com.gamamobi.cafe.GamaCafeHelper;

import java.util.List;

public class GamaImpl extends BaseGamaImpl {
    private static final String TAG = GamaImpl.class.getSimpleName();

    GamaImpl() {
        super();
    }

    @Override
    public void initSDK(final Activity activity, SGameLanguage gameLanguage) {
        super.initSDK(activity, gameLanguage);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //进行启动的查单
                SLog.logD(TAG, "Start launch query.");
                GooglePayHelper.getInstance().queryConsumablePurchase(activity);
            }
        });
    }

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //平台
                GooglePayHelper.getInstance().setForeground(true);
            }
        });
    }

    @Override
    public void onActivityResult(final Activity activity, final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (requestCode == GooglePayActivity2.GooglePayReqeustCode && resultCode == GooglePayActivity2.GooglePayResultCode) {
                    Bundle bundle = null;
                    if (data != null) {
                        bundle = data.getExtras();
                    }
                    if (bundle == null) {
                        bundle = new Bundle();
                    } else {
                        StarEventLogger.trackinPayEvent(activity, bundle);
                    }

                    if (iPayListener != null) { //支付刷新的回调
                        PL.i(TAG, "GooglePay支付回调");
                        iPayListener.onPayFinish(bundle);
                    } else {
                        PL.i(TAG, "GooglePay支付回调为空");
                    }
                }
            }
        });
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
    }

    @Override
    public void onStop(final Activity activity) {
        super.onStop(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                GamaCafeHelper.stopCafe(activity);

                GooglePayHelper.getInstance().setForeground(false);
            }
        });
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //退出游戏时停止定时查单
                GooglePayHelper.getInstance().stopQueryTask();

            }
        });
    }

    @Override
    public void gamaOpenCafeHome(final Activity activity) {
        super.gamaOpenCafeHome(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PL.i("IGama gamaOpenCafeHome");
                GamaCafeHelper.initCafe(activity);
                GamaCafeHelper.showCafe(activity);
            }
        });
    }

    @Override
    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra) {
        super.startPay(activity, payType, cpOrderId, productId, extra);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (payType == SPayType.GOOGLE) {//第三方储值
                    googlePay(activity, cpOrderId, productId, extra);
                } else {
                    PL.i("不支持當前類型： " + payType.name());
                }
            }
        });
    }

    private void googlePay(Activity activity, String cpOrderId, String productId, String extra) {
        GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(activity);
        googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
        googlePayCreateOrderIdReqBean.setProductId(productId);
        googlePayCreateOrderIdReqBean.setExtra(extra);

        Intent i = new Intent(activity, GooglePayActivity2.class);
        i.putExtra(GooglePayActivity2.GooglePayReqBean_Extra_Key, googlePayCreateOrderIdReqBean);
        activity.startActivityForResult(i, GooglePayActivity2.GooglePayReqeustCode);
    }

    @Override
    public void gamaQueryProductDetail(final Activity activity, final SPayType payType, final List<String> skus, final GamaQueryProductListener listener) {
        super.gamaQueryProductDetail(activity, payType, skus, listener);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (payType == SPayType.GOOGLE) {
                    GooglePayHelper.getInstance().queryProductDetail(activity, skus, listener);
                }
            }
        });
    }
}
