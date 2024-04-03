package com.thirdlib.lunqi;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.api.PayApi;
import com.mw.sdk.api.task.LoadingDialog;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.constant.ChannelPlatform;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.utils.PayHelper;
import com.mw.sdk.utils.SdkUtil;
import com.xlsdk.mediator.XLSDK;
import com.xlsdk.mediator.sdk.XLGameRoleInfo;
import com.xlsdk.mediator.sdk.XLOrderInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class LunqiPayImpl {


    private Activity mActivity;

    private IPayCallBack iPayCallBack;

    private String productId;
    private String cpOrderId;
    private String extra;
    private String currentOrderId;

//    private String current_inAppPurchaseData;

    private PayCreateOrderReqBean createOrderIdReqBean;

    private LoadingDialog loadingDialog;
    private Double skuAmount;

    public void setPayCallBack(IPayCallBack iPayCallBack) {
        this.iPayCallBack = iPayCallBack;
    }

    public LunqiPayImpl(Activity mActivity) {
        this.mActivity = mActivity;
        loadingDialog = new LoadingDialog(mActivity);
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        PL.i("LunqiPayImpl onActivityResult");
    }

    public void startPay(Activity activity, PayCreateOrderReqBean createOrderIdReqBean) {

        PL.i("LunqiPayImpl startPay");
        if (SStringUtil.isEmpty(createOrderIdReqBean.getProductId())){
            handlePayFail("productId is empty");
            return;
        }
        this.mActivity = activity;
        this.createOrderIdReqBean = createOrderIdReqBean;
        this.productId = createOrderIdReqBean.getProductId();
        this.cpOrderId = createOrderIdReqBean.getCpOrderId();
        this.extra = createOrderIdReqBean.getExtra();
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
        this.createOrderIdReqBean.setMode(ChannelPlatform.LUNQI.getChannel_platform());
        PL.i("LunqiPayImpl requestCreateOrder");
        //创单
        PayApi.requestCreateOrder(this.mActivity, this.createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
            @Override
            public void success(GPCreateOrderIdRes result, String msg) {
                try {
                    if (result != null && result.getPayData() != null && SStringUtil.isNotEmpty(result.getPayData().getOrderId())){
                        String orderId = result.getPayData().getOrderId();
                        currentOrderId = orderId;
                        skuAmount = result.getPayData().getAmount();

                        JSONObject devJsonObject = new JSONObject();
                        devJsonObject.put("userId", createOrderIdReqBean.getUserId());
                        devJsonObject.put("orderId", orderId);
                        //devJsonObject.put("cpOrderId", createOrderIdReqBean.getCpOrderId());
                        PL.i("LunqiPayImpl lunqiPurchase");
                        lunqiPurchase(mActivity, createOrderIdReqBean.getProductId(), orderId, createOrderIdReqBean.getCpOrderId(), devJsonObject.toString(),
                                result.getPayData().getProductName(), result.getPayData().getAmount());
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

    private void lunqiPurchase(Activity activity, String productId, String orderId, String cpOrderId, String developerPayload, String productName, double price) {
        loadingDialog.dismissProgressDialog();

        XLOrderInfo orderInfo = new XLOrderInfo();
        orderInfo.setGoodsID(productId);//商品id，必填
        orderInfo.setGoodsName(productName);//商品名称，必填
        orderInfo.setGoodsDesc(productName);//商品描述，必填
        orderInfo.setPrice(price);//单个商品价格，单位：分 有就传,没有填0
        try {
            JSONObject extraJsonObject = new JSONObject();
            extraJsonObject.put("userId", SdkUtil.getUid(activity));
            extraJsonObject.put("orderId", orderId);
            orderInfo.setExtrasParams(extraJsonObject.toString());//扩展参数，必填
        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderInfo.setCpOrderID(orderId);//产品订单号，有就传,没有填"0"
        XLGameRoleInfo gameRoleInfo = new XLGameRoleInfo();

        gameRoleInfo.setServerID(this.createOrderIdReqBean.getServerCode());//服务器id，必填
        gameRoleInfo.setServerName(this.createOrderIdReqBean.getServerName());//服务器名称，必填
        gameRoleInfo.setGameRoleID(this.createOrderIdReqBean.getRoleId());//角色id，必填
        gameRoleInfo.setGameRoleName(this.createOrderIdReqBean.getRoleName());//角色名称，必填

        gameRoleInfo.setGameUserLevel(this.createOrderIdReqBean.getRoleLevel());//游角色等级，必填
        gameRoleInfo.setVipLevel(this.createOrderIdReqBean.getRoleVipLevel());//vip等级，有就传,没有填"0"
        gameRoleInfo.setPartyName("无");//公会名称，必填，可填"无"
        XLSDK.getInstance().pay(orderInfo,gameRoleInfo);

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
