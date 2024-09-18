package com.thirdlib.ld;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.ld.sdk.LDSdkManager;
import com.ld.sdk.core.bean.LdGamePayInfo;
import com.ld.sdk.internal.LDException;
import com.ld.sdk.internal.LDNotLoginException;
import com.ld.sdk.internal.LDPayCallback;
import com.mw.sdk.R;
import com.mw.sdk.api.PayApi;
import com.mw.sdk.api.task.LoadingDialog;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class LDPayImpl {


    private Activity mActivity;

    private IPayCallBack iPayCallBack;

//    private String productId;
//    private String cpOrderId;
//    private String extra;
//    private String currentOrderId;

//    private String current_inAppPurchaseData;

    private PayCreateOrderReqBean createOrderIdReqBean;

    private LoadingDialog loadingDialog;
//    private Double skuAmount;

    public void setPayCallBack(IPayCallBack iPayCallBack) {
        this.iPayCallBack = iPayCallBack;
    }

    public LDPayImpl(Activity mActivity) {
        this.mActivity = mActivity;
        loadingDialog = new LoadingDialog(mActivity);
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        PL.i("pay onActivityResult");
    }

    public void startPay(Activity activity, PayCreateOrderReqBean createOrderIdReqBean) {

        PL.i("startPay");
        if (SStringUtil.isEmpty(createOrderIdReqBean.getProductId())){
            handlePayFail("productId is empty");
            return;
        }
        this.mActivity = activity;
        this.createOrderIdReqBean = createOrderIdReqBean;
//        this.productId = createOrderIdReqBean.getProductId();
//        this.cpOrderId = createOrderIdReqBean.getCpOrderId();
//        this.extra = createOrderIdReqBean.getExtra();
        loadingDialog.showProgressDialog();

        createOrder();
    }

    private void createOrder(){

        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(this.mActivity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(this.mActivity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);
        this.createOrderIdReqBean.setMode(this.mActivity.getString(R.string.channel_platform));
        PL.i("requestCreateOrder");
        //创单
        PayApi.requestCreateOrder(this.mActivity, this.createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
            @Override
            public void success(GPCreateOrderIdRes result, String msg) {
                try {
                    if (result != null && result.getPayData() != null && SStringUtil.isNotEmpty(result.getPayData().getOrderId()) && result.getPayData().getLocalAmount() != null){
                        String orderId = result.getPayData().getOrderId();
//                        currentOrderId = orderId;
//                        skuAmount = result.getPayData().getAmount();

                        JSONObject devJsonObject = new JSONObject();
                        devJsonObject.put("userId", createOrderIdReqBean.getUserId());
                        devJsonObject.put("orderId", orderId);
                        //devJsonObject.put("cpOrderId", createOrderIdReqBean.getCpOrderId());
                        PL.i("thirdPartyPurchase");
                        thirdPartyPurchase(mActivity,  result, devJsonObject.toString());
                    }else {
                        handlePayFail("create order error");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(GPCreateOrderIdRes createOrderIdRes, String msg) {
                PL.i("requestCreateOrder finish fail");
                //创建订单失败
                if (createOrderIdRes != null && SStringUtil.isNotEmpty(createOrderIdRes.getMessage())) {
                    handlePayFail(createOrderIdRes.getMessage());
                }else{
                    handlePayFail("create order error");
                }
            }
        });

    }

    private void thirdPartyPurchase(Activity activity, GPCreateOrderIdRes result, String developerPayload) {

        if (result == null || result.getPayData() == null){
            return;
        }

        loadingDialog.dismissProgressDialog();

        //注：自从ld_sdk_v2.2.0_0530开始，SDK所有对外的方法内部已经自行切换到了主线程，不再需要游戏方关心主线程调度问题。
        //如果是v2.2.0_0530之前的版本，还是需要游戏方务必将以下方法放到主线程中调用，否则会报以下两种错误之一：
        //1、android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
        //2、java.lang.IllegalArgumentException: You must call this method on the main thread
        LdGamePayInfo ldGamePayInfo = new LdGamePayInfo();
        ldGamePayInfo.tradeName = result.getPayData().getProductName() + "";          //页面展示的商品名称
        ldGamePayInfo.cpOrderId = result.getPayData().getOrderId();          // cp订单id 字符串类型
        ldGamePayInfo.productId = createOrderIdReqBean.getProductId();            //产品id
        ldGamePayInfo.cpUserId = createOrderIdReqBean.getUserId();         //cp用户id
        ldGamePayInfo.currencyType = result.getPayData().getLocalCurrency();    //当前游戏页面显示货币类型例如:TWD,USD,HKD等
        //PL.i("sku amount=" + (long) (skuAmount.doubleValue() * 100));
//        ldGamePayInfo.commodityPrice = (long) (skuAmount.doubleValue() * 100);//当前游戏商品显示的价格，分为单位，long类型，例如如果是游戏显示0.99USD这里需要传99
        BigDecimal bigDecimal = BigDecimal.valueOf(result.getPayData().getLocalAmount()).multiply(BigDecimal.valueOf(100));
        ldGamePayInfo.commodityPrice = bigDecimal.longValue();//(long) (result.getPayData().getLocalAmount() * 100);
//该透传参数需要sdk_v2.2.0版本才支持，请注意更新sdk版本
        ldGamePayInfo.transparentParams = developerPayload;  //透传参数，该字段的值将在服务端SDK的支付成功的回调方法中返回
        LDSdkManager.getInstance().showChargeView(activity, ldGamePayInfo, new LDPayCallback() {
            @Override
            public void onSuccess(String uid, String orderId) {
                //支付成功
                if (loadingDialog != null){
                    loadingDialog.dismissProgressDialog();
                }
            }

            @Override
            public void onError(LDException e) {

                if (e instanceof LDNotLoginException) {
                    //LDNotLoginException表示还没有登录，则可以在这里调用showLoginView方法来先打开登录页面，进行SDK的登录操作
                    handlePayFail("LDNotLoginException");
                }else{
                    //其他错误原因：e.toString()
                    handlePayFail("" + e.toString());
                }
            }

            @Override
            public void onCancel() {
                //取消支付
                handlePayFail("");
            }
        });
    }


    private void handlePayFail(String msg){

        if (loadingDialog != null){
            loadingDialog.dismissProgressDialog();
        }

        if (SStringUtil.isEmpty(msg)){
            if (iPayCallBack != null) {
                iPayCallBack.fail(null);
            }
            return;
        }
        loadingDialog.alert(msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                if (iPayCallBack != null) {
                    iPayCallBack.fail(null);
                }
            }
        });
    }

}
