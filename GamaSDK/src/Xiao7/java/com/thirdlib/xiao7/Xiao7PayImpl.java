package com.thirdlib.xiao7;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.R;
import com.mw.sdk.api.PayApi;
import com.mw.sdk.api.task.LoadingDialog;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.smwl.smsdk.abstrat.SMPayListener;
import com.smwl.smsdk.app.SMPlatformManager;
import com.smwl.smsdk.bean.PayInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;

public class Xiao7PayImpl {


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

    public Xiao7PayImpl(Activity mActivity) {
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

        /**
         * 支付参数示例
         * PayInfo数据来源：游戏的客户端从游戏的服务端获取数据，然后调用sdk的支付接口
         * 1.确保每个支付参数前后没有空格
         * 2.game_sign：根据两边服务端约定的规则产生的，详见服务端的对接文档
         *      下面通过StrUtilsSDK.getSParamSort(map)方法得到的game_sign，仅仅只是
         *      让Demo的支付接口可以跑通的临时方法，游戏方不可调用此方法产生game_sign字段
         * 3.notify_id：回调通知的id，如果只有一个支付回调地址可以将这里设置成-1，但是不允许设置成0
         *      注意：如果以前有接过小7的sdk（sdk版本<=1.0.7）游戏方，要在小7的开放平台上获取回调支付
         *      地址所对应的回调id，赋值给notify_id 即：notify_id=回调id（目的是为了兼容以前低的版本
         *      如果此处给 -1，服务端会默认回调以前低版本sdk的对调地址）
         * 4.测试账号需要找小7测试沟通添加
         */
        final PayInfo mPayInfo = new PayInfo();
        mPayInfo.setExtends_info_data(developerPayload);
        mPayInfo.setGame_level(this.createOrderIdReqBean.getRoleLevel());
        mPayInfo.setGame_role_id(this.createOrderIdReqBean.getRoleId());
        mPayInfo.setGame_role_name(this.createOrderIdReqBean.getRoleName());
        mPayInfo.setGame_area(this.createOrderIdReqBean.getServerName());

        // 登录成功后，服务端会返回游戏guid
        mPayInfo.setGame_guid("24166839");
        mPayInfo.setGame_orderid(result.getPayData().getOrderId());
//        String trim = priceEt.getText().toString().trim();
//        if (StrUtilsSDK.isExitEmptyParameter(trim)) {
//            Toast.makeText(DemoMainActivity.this, "请先填写价格", Toast.LENGTH_SHORT).show();
//        }
        // 商品价格，需保留小数点后两位
        mPayInfo.setGame_price(String.format(Locale.ENGLISH, "%.2f", result.getPayData().getAmount().floatValue()));
        mPayInfo.setNotify_id("-1");
        mPayInfo.setSubject("大元宝");
        // 游戏方不可调用此方法产生game_sign字段，这个方法只是让Demo的支付接口可以跑通的临时方法
        // 游戏里面game_sign参数是根据两边服务端约定的规则产生的，由游戏的服务端返回给客户端，详见服务端的对接文档
        mPayInfo.setGame_sign(getGameSign(mPayInfo));
        // 对参数做判空操作，如果有参数为空请不要传进sdk中!!!

        // 支付接口使用示例
        // 保证支付接口的调用一定在主线程中
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SMPlatformManager.getInstance().pay(activity, mPayInfo, new SMPayListener() {
                    @Override
                    public void onPaySuccess(Object obj) {
                        Log.i("X7SDKDemo", "支付成功");
                    }

                    @Override
                    public void onPayFailed(Object obj) {
                        Log.i("X7SDKDemo", "支付失败:" + obj);
                    }

                    @Override
                    public void onPayCancell(Object obj) {
                        Log.i("X7SDKDemo", "支付取消：" + obj);
                    }
                });
            }
        });




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
