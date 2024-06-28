package com.thirdlib.qooapp;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.core.base.base64.Base64;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.mw.sdk.R;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.gp.PayApi;
import com.mw.sdk.bean.req.PayCreateOrderReqBean;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;
import com.mw.sdk.pay.gp.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.pay.gp.bean.res.GPExchangeRes;
import com.mw.sdk.pay.gp.task.LoadingDialog;
import com.mw.sdk.pay.gp.util.PayHelper;
import com.qooapp.opensdk.QooAppOpenSDK;
import com.qooapp.opensdk.common.PaymentCallback;
import com.qooapp.opensdk.common.QooAppCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class QooappPayImpl {

    public static final int HW_IAP_REQUEST_CODE_ENVREADY = 6660;
    public static final int HW_IAP_REQUEST_CODE_PURCHASE = 6661;

    private Activity mActivity;

    private IPayCallBack iPayCallBack;

    private String productId;
    private String cpOrderId;
    private String extra;
    private String currentOrderId;

//    private String current_inAppPurchaseData;

    private GooglePayCreateOrderIdReqBean createOrderIdReqBean;

    private LoadingDialog loadingDialog;
    private Double skuAmount;

    public void setPayCallBack(IPayCallBack iPayCallBack) {
        this.iPayCallBack = iPayCallBack;
    }

    public QooappPayImpl(Activity mActivity) {
        this.mActivity = mActivity;
        loadingDialog = new LoadingDialog(mActivity);
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        PL.i("QooappPayImpl onActivityResult");
    }

    public void startPay(Activity activity, GooglePayCreateOrderIdReqBean createOrderIdReqBean) {

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
        this.createOrderIdReqBean.setMode(this.mActivity.getString(R.string.channel_platform));

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
                        devJsonObject.put("cpOrderId", createOrderIdReqBean.getCpOrderId());

                        qooappPurchase(mActivity, createOrderIdReqBean.getProductId(), orderId, createOrderIdReqBean.getCpOrderId(), devJsonObject.toString());
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

    private void qooappPurchase(Activity activity, String productId, String orderId, String cpOrderId, String developerPayload) {
        loadingDialog.dismissProgressDialog();
        QooAppOpenSDK.getInstance().purchase(new PaymentCallback() {
            @Override
            public void onComplete(String s) {
                PL.i("QooAppOpenSDK.purchase onComplete:" + s);
                try {
                    JSONObject puchaseData = new JSONObject(s);
                    int code = puchaseData.getInt("code");
                    String message = puchaseData.getString("message");

                    JSONObject dataJson = puchaseData.getJSONObject("data");
                    String signature = puchaseData.getString("signature");
                    String algorithm = puchaseData.getString("algorithm");//有效值是 sha1或sha256

                    if (SStringUtil.isNotEmpty(dataJson.toString()) && SStringUtil.isNotEmpty(signature)) {
                        sendPayment(dataJson.toString(), signature, algorithm, false);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handlePayFail("purchase json data error");

            }

            @Override
            public void onError(String s) {
                PL.i("QooAppOpenSDK.purchase onError:" + s);
                handlePayFail("purchase error");
            }

            @Override
            public void onCancel() {
                PL.i("QooAppOpenSDK.onCancel");
                handlePayFail("");
            }
        }, activity, productId, orderId, developerPayload);
    }

    public void sendPayment(String inAppPurchaseData, String inAppPurchaseDataSignature, String algorithm, boolean reissue){

        if (SStringUtil.isEmpty(inAppPurchaseData)){
            handlePayFail("inAppPurchaseData is empty");
            return;
        }
        if (SStringUtil.isEmpty(inAppPurchaseDataSignature)){
            handlePayFail("inAppPurchaseDataSignature is empty");
            return;
        }
//        boolean checkSign = checkSign(inAppPurchaseData, inAppPurchaseDataSignature, publicKey, algorithm);
//        PL.i("checkSign is " + checkSign);
        //this.current_inAppPurchaseData = inAppPurchaseData;
        if (!reissue){
            loadingDialog.showProgressDialog();
        }
        PayApi.requestSendStone_qooapp(this.mActivity, inAppPurchaseData, inAppPurchaseDataSignature, algorithm, reissue, new SFCallBack<GPExchangeRes>() {
            @Override
            public void success(GPExchangeRes result, String msg) {

                try {//true表示补发
                    if (reissue){
                        JSONArray puchaseDataArr = new JSONArray(inAppPurchaseData);
                        for (int i = 0; i < puchaseDataArr.length(); i++) {
                            JSONObject puchaseData = puchaseDataArr.getJSONObject(i);
                            consumeOwnedPurchase(mActivity, puchaseData);
                        }
                    }else {
                        JSONObject puchaseData = new JSONObject(inAppPurchaseData);
                        consumeOwnedPurchase(mActivity, puchaseData);
                        handlePaySuccess(puchaseData);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(GPExchangeRes result, String msg) {
                //test
                //consumeOwnedPurchase(mActivity, inAppPurchaseDataBean);

                if (reissue){
                    return;
                }
                if (result != null && SStringUtil.isNotEmpty(result.getMessage())) {
                    handlePayFail(result.getMessage());
                }else{
                    handlePayFail("payment error");
                }
            }
        });

    }

    public void getOwnedPurchases(Activity activity){
        //包含內購的遊戲，每次啟動時都應調用此接口檢查是否有未完成發放商品的訂單，並完成正常的 訂單流程。
        QooAppOpenSDK.getInstance().restorePurchases(new QooAppCallback() {
            @Override
            public void onSuccess(String response) {
                PL.i("QooAppOpenSDK.restorePurchases onSuccess:" + response);
                if (SStringUtil.isNotEmpty(response)){

                    try {
                        JSONObject restorePurchasesJsonObject = new JSONObject(response);

                        int code = restorePurchasesJsonObject.getInt("code");
                        String message = restorePurchasesJsonObject.getString("message");
                        //JSONObject dataJson = puchaseData.getJSONObject("data");
                        String signature = restorePurchasesJsonObject.getString("signature");
                        String algorithm = restorePurchasesJsonObject.getString("algorithm");

                        //查询的时候这里是数组，购买的时候不是数组
                        JSONArray dataJsonArray = restorePurchasesJsonObject.getJSONArray("data");
                        PL.i("QooAppOpenSDK.restorePurchases onSuccess dataJsonArray:" + dataJsonArray.length());

                        if (dataJsonArray.length() > 0 && SStringUtil.isNotEmpty(signature)) {
                            sendPayment(dataJsonArray.toString(), signature, algorithm, true);
                        }

//                        for (int i = 0; i < dataJsonArray.length(); i++) {
//
//                            JSONObject puchaseData = dataJsonArray.getJSONObject(i);
//
//                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(String error) {
                PL.i("QooAppOpenSDK.restorePurchases onError:" + error);
            }
        });

    }

    public void consumeOwnedPurchase(Activity activity, JSONObject puchaseData){

        try {

           String purchase_id = puchaseData.getString("purchase_id");
           String token = puchaseData.getString("token");
           String product_id = puchaseData.getString("product_id");

            //如果訂單包含可消耗商品，遊戲在發放商品後必須調用此接口完成消耗已標記訂單完成。
            QooAppOpenSDK.getInstance().consume(new QooAppCallback() {

                @Override
                public void onSuccess(String response) {
                    PL.i("QooAppOpenSDK.consume onSuccess:" + response);
                }

                @Override
                public void onError(String error) {
                    PL.i("QooAppOpenSDK.consume onError:" + error);
                }
            }, purchase_id, token);//必須傳入正確的 purchase_id 和 token


        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    private void handlePaySuccess(JSONObject puchaseData){

        if (loadingDialog != null){
            loadingDialog.dismissProgressDialog();
        }

        if (iPayCallBack != null) {

            Gson gson = new Gson();

            BasePayBean payBean = new BasePayBean();

//            payBean.setTransactionId(hwPurchaseData.getOrderId());
            payBean.setOrderId(this.currentOrderId);
//            payBean.setPackageName(hwPurchaseData.getPackageName());
            payBean.setUsdPrice(this.skuAmount);
            payBean.setCpOrderId(this.cpOrderId);
//            payBean.setProductId(hwPurchaseData.getProductId());
//                    payBean.setmItemType(purchase.getItemType());
//            payBean.setOriginPurchaseData(this.current_inAppPurchaseData);
//            payBean.setPurchaseState(hwPurchaseData.getPurchaseState());
//            payBean.setPurchaseTime(hwPurchaseData.getPurchaseTime());
            //payBean.setSignature(purchase.getSignature());
//            payBean.setDeveloperPayload(hwPurchaseData.getDeveloperPayload());
            //payBean.setmToken(purchase.getPurchaseToken());
//            payBean.setPrice(hwPurchaseData.getPrice());
//            payBean.setCurrency(hwPurchaseData.getCurrency());

            iPayCallBack.success(payBean);
        }
    }

    public static final String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1p0wgJCX9XERkGaU3t7hFvoc9tES+wDcbKu/C8BQojCX43MH7ZWjM4A7zze8sGq/41pV7v30lcf4psQN0YxHDtG7k97byvGVgtdgTVfpRm3i0da5tu1VqQufgsVdmT8vHrCU5oOvI+flVEEBzQrKio2spe+/l1QnfdYmwKf0wZ3/Z+SSc9KnLwimB6dUisrDRXNOsfYU0oT38x1Zt1V+lUuU6nFQjfA7AsaIOX45W7Q47P00zlRrF438KXJu81r8M9rcxFt4/t9lNXCOwI4fn2KzJfsp8gOZonMANyiMRNT1k0gUilGXkzyBH5OHEQidEpwBs0YnJvSPxAfXgh66EQIDAQAB";
    /**
     * 校验签名信息
     *
     * @param content 结果字符串
     * @param sign 签名字符串
     * @param publicKey IAP公钥
     * @param signatureAlgorithm 签名算法字段，可从接口返回数据中获取，例如：OwnedPurchasesResult.getSignatureAlgorithm()
     * @return 是否校验通过
     */
    public static boolean checkSign(String content, String sign, String publicKey, String signatureAlgorithm) {
        if (sign == null) {
            return false;
        }
        if (publicKey == null) {
            return false;
        }

        // 当signatureAlgorithm为空时使用默认签名算法
        if ("sha1".equals(signatureAlgorithm)) {
            signatureAlgorithm = "SHA1withRSA";
        }else {
            signatureAlgorithm = "SHA256WithRSA"; //sha256
        }

        try {
            //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            // 生成"RSA"的KeyFactory对象
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] decodedKey = Base64.decode(publicKey);
            // 生成公钥
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
            java.security.Signature signature = null;
            // 根据SHA256WithRSA算法获取签名对象实例
            signature = java.security.Signature.getInstance(signatureAlgorithm);
            // 初始化验证签名的公钥
            signature.initVerify(pubKey);
            // 把原始报文更新到签名对象中
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            // 将sign解码
            byte[] bsign = Base64.decode(sign);
            // 进行验签
            return signature.verify(bsign);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
