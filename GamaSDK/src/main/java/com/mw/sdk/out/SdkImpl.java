package com.mw.sdk.out;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.utils.PL;
import com.gama.pay.IPay;
import com.gama.pay.IPayCallBack;
import com.gama.pay.IPayFactory;
import com.gama.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.gama.pay.gp.bean.req.WebPayReqBean;
import com.gama.pay.gp.bean.res.BasePayBean;
import com.gama.pay.gp.util.PayHelper;
import com.mw.base.bean.SPayType;
import com.mw.base.cfg.ResConfig;
import com.mw.sdk.R;
import com.mw.sdk.SWebViewDialog;
import com.mw.sdk.ads.SdkEventLogger;
import com.mw.sdk.callback.IPayListener;
import com.mw.sdk.constant.GsSdkImplConstant;

public class SdkImpl extends BaseSdkImpl {
    private static final String TAG = SdkImpl.class.getSimpleName();

    SdkImpl() {
        super();
    }

    private IPay iPay;

    @Override
    public void onCreate(Activity activity) {
        super.onCreate(activity);
        iPay = IPayFactory.create(IPayFactory.PAY_GOOGLE);
        iPay.onCreate(activity);
    }

    @Override
    public void onResume(Activity activity) {
        super.onResume(activity);
        iPay.onResume(activity);
    }

    @Override
    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        iPay.onDestroy(activity);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        super.onActivityResult(activity, requestCode, resultCode, data);
        iPay.onActivityResult(activity,requestCode,resultCode,data);
    }



    @Override
    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra, IPayListener listener) {
        super.startPay(activity, payType, cpOrderId, productId, extra,listener);
        if (payType == SPayType.GOOGLE) {
//            googlePay(activity, cpOrderId, productId, extra);
            GooglePayCreateOrderIdReqBean googlePayCreateOrderIdReqBean = new GooglePayCreateOrderIdReqBean(activity);
            googlePayCreateOrderIdReqBean.setCpOrderId(cpOrderId);
            googlePayCreateOrderIdReqBean.setProductId(productId);
            googlePayCreateOrderIdReqBean.setExtra(extra);
//
//        Intent i = new Intent(activity, GooglePayActivity2.class);
//        i.putExtra(GooglePayActivity2.GooglePayReqBean_Extra_Key, googlePayCreateOrderIdReqBean);
//        activity.startActivityForResult(i, GooglePayActivity2.GooglePayReqeustCode);


            //设置Google储值的回调
            iPay.setIPayCallBack(new IPayCallBack() {
                @Override
                public void success(BasePayBean basePayBean) {
                    SdkEventLogger.trackinPayEvent(activity, basePayBean);
                }

                @Override
                public void fail(BasePayBean basePayBean) {

                }
            });

            iPay.startPay(activity,googlePayCreateOrderIdReqBean);

        } else if(payType == SPayType.OTHERS) {
//            a(activity, cpOrderId, extra);
        } else {//默认Google储值
            PL.i("不支持當前類型： " + payType.name());
        }
    }


    private void a(Activity activity, String cpOrderId, String extra) {
        WebPayReqBean webPayReqBean = PayHelper.buildWebPayBean(activity, cpOrderId, extra);

        String payThirdUrl = ResConfig.getPlatPreferredUrl(activity) + GsSdkImplConstant.GS_THIRD_METHOD_APP;
//        if (GamaUtil.getSdkCfg(activity) != null) {
//            payThirdUrl = GamaUtil.getSdkCfg(activity).getS_Third_PayUrl();
//        }
//        if (TextUtils.isEmpty(payThirdUrl)) {
//            if(GamaUtil.isInterfaceSurfixWithApp(activity)) {
//                payThirdUrl = ResConfig.getPayPreferredUrl(activity) + GsSdkImplConstant.GS_THIRD_METHOD_APP;
//            } else {
//                payThirdUrl = ResConfig.getPlatPreferredUrl(activity) + GsSdkImplConstant.GS_THIRD_METHOD_APP;
//            }
//        }
        webPayReqBean.setCompleteUrl(payThirdUrl);

        String webUrl = webPayReqBean.createPreRequestUrl();

        otherPayWebViewDialog = new SWebViewDialog(activity, R.style.Gama_Theme_AppCompat_Dialog_Notitle_Fullscreen);
        otherPayWebViewDialog.setWebUrl(webUrl);
        otherPayWebViewDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                if (iPayListener != null) {
//                    iPayListener.onPayFinish(new Bundle());
//                } else {
//                    PL.i(TAG, "a null occour");
//                }
            }
        });
        otherPayWebViewDialog.show();
    }

    @Override
    public void openPlatform(final Activity activity) {
        super.openPlatform(activity);

    }

}
