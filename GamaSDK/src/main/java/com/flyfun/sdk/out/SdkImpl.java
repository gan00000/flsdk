package com.flyfun.sdk.out;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.core.base.utils.PL;
import com.flyfun.base.bean.SGameLanguage;
import com.flyfun.base.bean.SPayType;
import com.flyfun.base.cfg.ResConfig;
import com.flyfun.pay.gp.GooglePayActivity2;
import com.flyfun.pay.gp.GooglePayHelper;
import com.flyfun.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.flyfun.pay.gp.bean.req.WebPayReqBean;
import com.flyfun.pay.gp.util.PayHelper;
import com.flyfun.pay.utils.QueryProductListener;
import com.flyfun.sdk.SWebViewDialog;
import com.flyfun.sdk.constant.GsSdkImplConstant;
import com.flyfun.base.utils.SLog;
import com.gama.sdk.R;
import com.flyfun.sdk.ads.StarEventLogger;

import java.util.List;

public class SdkImpl extends BaseSdkImpl {
    private static final String TAG = SdkImpl.class.getSimpleName();

    SdkImpl() {
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
    public void onResume(final Activity activity) {
        super.onResume(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //平台
//                PlatformManager.getInstance().resume(activity);

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
                    return;
                }
                //平台
//                PlatformManager.getInstance().onActivityResult(activity, requestCode, resultCode, data);
            }
        });

    }

    @Override
    public void onPause(final Activity activity) {
        super.onPause(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //平台
//                PlatformManager.getInstance().pauseAndStop(activity);
            }
        });
    }

    @Override
    public void onStop(final Activity activity) {
        super.onStop(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //平台
//                PlatformManager.getInstance().pauseAndStop(activity);
                //
                GooglePayHelper.getInstance().setForeground(false);
            }
        });
    }

    @Override
    public void onDestroy(final Activity activity) {
        super.onDestroy(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //平台
//                PlatformManager.getInstance().destory(activity);
                //退出游戏时停止定时查单
                GooglePayHelper.getInstance().stopQueryTask();

            }
        });
    }

    @Override
    protected void startPay(final Activity activity, final SPayType payType, final String cpOrderId, final String productId, final String extra) {
        super.startPay(activity, payType, cpOrderId, productId, extra);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (payType == SPayType.GOOGLE) {
                    googlePay(activity, cpOrderId, productId, extra);
                } else if(payType == SPayType.OTHERS) {
                    a(activity, cpOrderId, extra);
                } else {//默认Google储值
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
                if (iPayListener != null) {
                    iPayListener.onPayFinish(new Bundle());
                } else {
                    PL.i(TAG, "a null occour");
                }
            }
        });
        otherPayWebViewDialog.show();
    }

    @Override
    public void openPlatform(final Activity activity) {
        super.openPlatform(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PL.i("IGama openPlatform");
//                        if (GamaUtil.isLogin(activity)) {
//                            PlatformData platformData = new PlatformData();
//                            platformData.setAppKey(ResConfig.getAppKey(activity));
//                            platformData.setGameCode(ResConfig.getGameCode(activity));
//                            platformData.setLanguage(SGameLanguage.zh_TW.getLanguage()); //设置语言
//                            platformData.setLoginTimestamp(GamaUtil.getSdkTimestamp(activity)); //登录完成timestamp
//                            platformData.setLoginToken(GamaUtil.getSdkAccessToken(activity));//登录完成accessToken
//                            platformData.setRoleId(GamaUtil.getRoleId(activity));
//                            platformData.setRoleLevel(GamaUtil.getRoleLevel(activity));
//                            platformData.setRoleName(GamaUtil.getRoleName(activity));
//                            platformData.setUserId(GamaUtil.getUid(activity));
//                            platformData.setUname(GamaUtil.getAccount(activity));//账号名,第三方没有传空值
//                            platformData.setServerCode(GamaUtil.getServerCode(activity));
//                            platformData.setServerName(GamaUtil.getServerName(activity));
//                            platformData.setLoginType(GamaUtil.getPreviousLoginType(activity)); //登录方式，fb登录 “fb”,Google登录 "google", 免注册登录 "mac"
//
//                            PlatformManager.getInstance().startPlatform(activity, platformData);
//                        } else {
//                            ToastUtils.toast(activity, "please login game first");
//                        }

                    }
                });
            }
        });

    }

    @Override
    public void queryProductDetail(final Activity activity, final SPayType payType, final List<String> skus, final QueryProductListener listener) {
        super.queryProductDetail(activity, payType, skus, listener);
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
