package com.gama.sdk.out;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.utils.PL;
import com.gama.base.bean.SGameLanguage;
import com.gama.base.bean.SPayType;
import com.gama.base.utils.GamaUtil;
import com.gama.base.utils.Localization;
import com.gama.sdk.ads.StarEventLogger;
import com.gamamobi.cafe.GamaCafeHelper;
import com.gamamobi.onestore.pay.OneStoreActivity;
import com.gamamobi.onestore.pay.bean.req.OneStoreCreateOrderIdReqBean;

public class GamaImpl extends BaseGamaImpl {
    private static final String TAG = GamaImpl.class.getSimpleName();

    public GamaImpl() {
        super();
    }

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        if (requestCode == OneStoreActivity.ONESTOREPAYREQEUSTCODE && resultCode == OneStoreActivity.ONESTOREPAYRESULTCODE) {
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
                PL.i(TAG, "支付回调");
                iPayListener.onPayFinish(bundle);
            } else {
                PL.i(TAG, "支付回调为空");
            }
            return;
        }
    }

    @Override
    public void onPause(Activity activity) {
        super.onPause(activity);
    }

    @Override
    public void onStop(Activity activity) {
        super.onStop(activity);
        if (Localization.getSGameLanguage(activity) == SGameLanguage.ko_KR) {
            GamaCafeHelper.stopCafe(activity);
        }
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
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
                if (payType == SPayType.ONESTORE) {
                    oneStorePay(activity, cpOrderId, productId, extra);
                } else {
                    PL.i("不支持當前類型： " + payType.name());
                }
            }
        });
    }

    private void oneStorePay(Activity activity, String cpOrderId, String productId, String extra) {
        OneStoreCreateOrderIdReqBean oneStoreCreateOrderIdReqBean = new OneStoreCreateOrderIdReqBean(activity);
        oneStoreCreateOrderIdReqBean.setCpOrderId(cpOrderId);
        oneStoreCreateOrderIdReqBean.setProductId(productId);
        oneStoreCreateOrderIdReqBean.setExtra(extra);

        Intent i = new Intent(activity, OneStoreActivity.class);
        i.putExtra(OneStoreActivity.OneStorePayReqBean_Extra_Key, oneStoreCreateOrderIdReqBean);
        activity.startActivityForResult(i, OneStoreActivity.ONESTOREPAYREQEUSTCODE);
    }
}
