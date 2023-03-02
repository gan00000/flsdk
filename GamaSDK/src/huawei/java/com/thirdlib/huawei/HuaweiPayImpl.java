package com.thirdlib.huawei;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;

import com.core.base.base64.Base64;
import com.core.base.callback.SFCallBack;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.iap.Iap;
import com.huawei.hms.iap.IapApiException;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseReq;
import com.huawei.hms.iap.entity.ConsumeOwnedPurchaseResult;
import com.huawei.hms.iap.entity.InAppPurchaseData;
import com.huawei.hms.iap.entity.IsEnvReadyResult;
import com.huawei.hms.iap.entity.OrderStatusCode;
import com.huawei.hms.iap.entity.OwnedPurchasesReq;
import com.huawei.hms.iap.entity.OwnedPurchasesResult;
import com.huawei.hms.iap.entity.ProductInfo;
import com.huawei.hms.iap.entity.ProductInfoReq;
import com.huawei.hms.iap.entity.ProductInfoResult;
import com.huawei.hms.iap.entity.PurchaseIntentReq;
import com.huawei.hms.iap.entity.PurchaseIntentResult;
import com.huawei.hms.iap.entity.PurchaseResultInfo;
import com.huawei.hms.iap.util.IapClientHelper;
import com.huawei.hms.support.api.client.Status;
import com.mw.sdk.constant.ApiRequestMethod;
import com.mw.sdk.pay.IPayCallBack;
import com.mw.sdk.pay.gp.PayApi;
import com.mw.sdk.pay.gp.bean.req.GooglePayCreateOrderIdReqBean;
import com.mw.sdk.pay.gp.bean.res.BasePayBean;
import com.mw.sdk.pay.gp.bean.res.GPCreateOrderIdRes;
import com.mw.sdk.pay.gp.bean.res.GPExchangeRes;
import com.mw.sdk.pay.gp.task.LoadingDialog;
import com.mw.sdk.pay.gp.util.PayHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class HuaweiPayImpl {

    public static final int HW_IAP_REQUEST_CODE_ENVREADY = 6660;
    public static final int HW_IAP_REQUEST_CODE_PURCHASE = 6661;

    private Activity mActivity;

    private IPayCallBack iPayCallBack;

    private String productId;
    private String cpOrderId;
    private String extra;
    private String currentOrderId;

    private String current_inAppPurchaseData;

    private ProductInfo currentProductInfo;
    private GooglePayCreateOrderIdReqBean createOrderIdReqBean;

    private LoadingDialog loadingDialog;
    private Double skuAmount;

    public void setPayCallBack(IPayCallBack iPayCallBack) {
        this.iPayCallBack = iPayCallBack;
    }

    public HuaweiPayImpl(Activity mActivity) {
        this.mActivity = mActivity;
        loadingDialog = new LoadingDialog(mActivity);
    }


    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

        PL.i("HuaweiPayImpl onActivityResult");
        if (requestCode == HW_IAP_REQUEST_CODE_ENVREADY) {
            if (data != null) {
                // 使用parseRespCodeFromIntent方法获取接口请求结果
                int returnCode = IapClientHelper.parseRespCodeFromIntent(data);
                // 使用parseCarrierIdFromIntent方法获取接口返回的运营商ID
                String carrierId = IapClientHelper.parseCarrierIdFromIntent(data);

                obtainProductInfo(activity, productId);
            }
        }else if (requestCode == HW_IAP_REQUEST_CODE_PURCHASE){

            if (data == null) {
                PL.i("onActivityResult data is null");
                return;
            }
            // 调用parsePurchaseResultInfoFromIntent方法解析支付结果数据
            PurchaseResultInfo purchaseResultInfo = Iap.getIapClient(activity).parsePurchaseResultInfoFromIntent(data);
            switch(purchaseResultInfo.getReturnCode()) {
                case OrderStatusCode.ORDER_STATE_CANCEL:
                    // 用户取消
                    PL.i("onActivityResult purchase 用户取消");
                    handlePayFail("");
                    break;
                case OrderStatusCode.ORDER_STATE_FAILED:
                case OrderStatusCode.ORDER_STATE_DEFAULT_CODE:
                case OrderStatusCode.ORDER_PRODUCT_OWNED:
                    // 检查是否存在未发货商品
                    PL.i("onActivityResult purchase 检查是否存在未发货商品");
                    getOwnedPurchases(activity);
                    break;
                case OrderStatusCode.ORDER_STATE_SUCCESS:
                    // 支付成功
                    PL.i("onActivityResult purchase 支付成功");
                    String inAppPurchaseData = purchaseResultInfo.getInAppPurchaseData();
                    String inAppPurchaseDataSignature = purchaseResultInfo.getInAppDataSignature();
                    // 使用您应用的IAP公钥验证签名
                    // 若验签成功，必须校验InAppPurchaseData中的productId、price、currency等信息的一致性，验证一致后发货
                    // 若用户购买商品为消耗型商品，您需要在发货成功后调用consumeOwnedPurchase接口进行消耗

                    sendPayment(inAppPurchaseData, inAppPurchaseDataSignature, "no");
                    break;
                default:
                    break;
            }

        }
    }

    public void startPay(Activity activity, GooglePayCreateOrderIdReqBean createOrderIdReqBean) {

        if (SStringUtil.isEmpty(productId)){
            handlePayFail("productId is empty");
            return;
        }
        this.productId = createOrderIdReqBean.getProductId();
        this.cpOrderId = createOrderIdReqBean.getCpOrderId();
        this.extra = createOrderIdReqBean.getExtra();
        loadingDialog.showProgressDialog();
        checkEnvReady(activity);
    }

    private void checkEnvReady(Activity activity) {
        // 获取调用接口的Activity对象
        Task<IsEnvReadyResult> task = Iap.getIapClient(activity).isEnvReady();
        task.addOnSuccessListener(new OnSuccessListener<IsEnvReadyResult>() {
            @Override
            public void onSuccess(IsEnvReadyResult result) {
                // 返回的运营商ID
                int returnCode = result.getReturnCode();
                PL.i("isEnvReady onSuccess returnCode:" + returnCode);
                PL.i("isEnvReady onSuccess status:" + result.getStatus().toString());
                obtainProductInfo(activity, productId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                PL.i("isEnvReady onFailure");
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    PL.i("isEnvReady onFailure status=" + status.toString());
                    if (status.getStatusCode() == OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                        // 未登录帐号
                        if (status.hasResolution()) {
                            try {
                                // 6666是您自定义的常量
                                // 启动IAP返回的登录页面
                                PL.i("isEnvReady onFailure startResolutionForResult");
                                status.startResolutionForResult(activity, HW_IAP_REQUEST_CODE_ENVREADY);
                            } catch (IntentSender.SendIntentException exp) {
                                PL.i("isEnvReady startResolutionForResult exception");
                                handlePayFail("startResolutionForResult exception");
                            }
                        }else {
                            PL.i("isEnvReady status has no resolution");
                            handlePayFail("status has no resolution");
                        }

                    } else if (status.getStatusCode() == OrderStatusCode.ORDER_ACCOUNT_AREA_NOT_SUPPORTED) {
                        // 用户当前登录的华为帐号所在的服务地不在华为IAP支持结算的国家/地区中
                        PL.i("isEnvReady onFailure 用户当前登录的华为帐号所在的服务地不在华为IAP支持结算的国家/地区中");
                        handlePayFail(status.getStatusCode() + ":" + status.getErrorString());
                    }else {
                        handlePayFail(status.getStatusCode() + ":" + status.getErrorString());
                    }

                } else {
                    // 其他外部错误
                    handlePayFail("An unknown error occurred, please try again");
                }
            }
        });
    }


    public void obtainProductInfo(Activity activity, String productId) {

        if (SStringUtil.isEmpty(productId)){
            handlePayFail("productId is empty");
            return;
        }

        List<String> productIdList = new ArrayList<>();
        // 查询的商品必须是您在AppGallery Connect网站配置的商品
        productIdList.add(productId);
        ProductInfoReq req = new ProductInfoReq();
        // priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
        req.setPriceType(0);
        req.setProductIds(productIdList);
        PL.i("obtainProductInfo start");
        // 调用obtainProductInfo接口获取AppGallery Connect网站配置的商品的详情信息
        Task<ProductInfoResult> task = Iap.getIapClient(activity).obtainProductInfo(req);
        task.addOnSuccessListener(new OnSuccessListener<ProductInfoResult>() {
            @Override
            public void onSuccess(ProductInfoResult result) {
                PL.i("obtainProductInfo onSuccess");
                // 获取接口请求成功时返回的商品详情信息
                List<ProductInfo> productList = result.getProductInfoList();
                if (productList != null){

                    for (int i = 0; i <productList.size(); i++) {
                        ProductInfo productInfo = productList.get(i);
                        if (productInfo.getProductId().equals(productId)){
                            currentProductInfo = productInfo;
                            break;
                        }
                    }

                    if (currentProductInfo != null){
                        createOrder();
                        return;
                    }
                }

                handlePayFail("Product Info Empty");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                PL.i("obtainProductInfo onFailure");
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    //int returnCode = apiException.getStatusCode();
                    handlePayFail(status.getStatusCode() + ":" + status.getErrorString());

                } else {
                    // 其他外部错误
                    handlePayFail("An unknown error occurred, please try again");
                }
            }
        });
    }

    private void createOrder(){

        //由GooglePayActivity2传入
        this.createOrderIdReqBean = new GooglePayCreateOrderIdReqBean(this.mActivity);
        //设置储值主域名
        this.createOrderIdReqBean.setRequestUrl(PayHelper.getPreferredUrl(this.mActivity));
        //设置储值备用域名
        this.createOrderIdReqBean.setRequestSpaUrl(PayHelper.getSpareUrl(this.mActivity));
        //设置储值接口名
        this.createOrderIdReqBean.setRequestMethod(ApiRequestMethod.API_ORDER_CREATE);
        this.createOrderIdReqBean.setMode("huawei");
        this.createOrderIdReqBean.setProductId(this.productId);
        this.createOrderIdReqBean.setCpOrderId(this.cpOrderId);
        this.createOrderIdReqBean.setExtra(this.extra);

        PayApi.requestCreateOrder(this.mActivity, this.createOrderIdReqBean, new SFCallBack<GPCreateOrderIdRes>() {
            @Override
            public void success(GPCreateOrderIdRes result, String msg) {

                if (result != null && result.getPayData() != null && SStringUtil.isNotEmpty(result.getPayData().getOrderId())){
                    skuAmount = result.getPayData().getAmount();
                    createPurchaseIntentToPay(mActivity, result.getPayData().getOrderId(), createOrderIdReqBean.getProductId());
                }else {
                    handlePayFail("create order error");
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

    public void createPurchaseIntentToPay(Activity activity, String orderId, String productId){

        this.currentOrderId = orderId;
        // 构造一个PurchaseIntentReq对象
        PurchaseIntentReq req = new PurchaseIntentReq();
        // 通过createPurchaseIntent接口购买的商品必须是您在AppGallery Connect网站配置的商品
        req.setProductId(productId);
        // priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
        req.setPriceType(0);
        try {
            JSONObject devJsonObject = new JSONObject();
            devJsonObject.put("userId", this.createOrderIdReqBean.getUserId());
            devJsonObject.put("orderId", orderId);
            devJsonObject.put("cpOrderId", this.createOrderIdReqBean.getCpOrderId());
            req.setDeveloperPayload(devJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PL.i("createPurchaseIntent start");
        // 调用createPurchaseIntent接口创建托管商品订单
        Task<PurchaseIntentResult> task = Iap.getIapClient(activity).createPurchaseIntent(req);
        task.addOnSuccessListener(new OnSuccessListener<PurchaseIntentResult>() {
            @Override
            public void onSuccess(PurchaseIntentResult result) {
                PL.i("createPurchaseIntent onSuccess");
                // 获取创建订单的结果
                Status status = result.getStatus();
                if (status.hasResolution()) {
                    try {
                        // 6666是您自定义的常量
                        // 启动IAP返回的收银台页面
                        PL.i("startResolutionForResult PurchaseIntentReq");
                        status.startResolutionForResult(activity, HW_IAP_REQUEST_CODE_PURCHASE);
                    } catch (IntentSender.SendIntentException exp) {

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                PL.i("createPurchaseIntent onFailure");
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    int returnCode = apiException.getStatusCode();

                    handlePayFail(status.getStatusCode() + ":" + status.getErrorString());

                } else {
                    // 其他外部错误
                    handlePayFail("An unknown error occurred, please try again");
                }
            }
        });

    }

    public void sendPayment(String inAppPurchaseData, String inAppPurchaseDataSignature,String reissue){

        if (SStringUtil.isEmpty(inAppPurchaseData)){
            handlePayFail("inAppPurchaseData is empty");
            return;
        }
        if (SStringUtil.isEmpty(inAppPurchaseDataSignature)){
            handlePayFail("inAppPurchaseDataSignature is empty");
            return;
        }
        //boolean checkSign = checkSign(inAppPurchaseData, inAppPurchaseDataSignature, publicKey, "SHA256WithRSA");
        //PL.i("checkSign is " + checkSign);
//        try {
//            JSONObject jsonObject = new JSONObject(inAppPurchaseData);
//            String developerPayload = jsonObject.getString("developerPayload");
//            JSONObject jsonObjectDevPayload = new JSONObject(developerPayload);
//            String orderId = jsonObjectDevPayload.getString("orderId");
//            PL.i("orderId=" + orderId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        this.current_inAppPurchaseData = inAppPurchaseData;
        PayApi.requestSendStone_huawei(this.mActivity, inAppPurchaseData, inAppPurchaseDataSignature, reissue, new SFCallBack<GPExchangeRes>() {
            @Override
            public void success(GPExchangeRes result, String msg) {

                try {
                    InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
                    int purchaseState = inAppPurchaseDataBean.getPurchaseState();
                    consumeOwnedPurchase(mActivity, inAppPurchaseDataBean);
                    handlePaySuccess();
                } catch (JSONException e) {

                }
            }

            @Override
            public void fail(GPExchangeRes result, String msg) {
                if (result != null && SStringUtil.isNotEmpty(result.getMessage())) {
                    handlePayFail(result.getMessage());
                }else{
                    handlePayFail("payment error");
                }
            }
        });

    }

    public void getOwnedPurchases(Activity activity){
        // 构造一个OwnedPurchasesReq对象
        OwnedPurchasesReq ownedPurchasesReq = new OwnedPurchasesReq();
        // priceType: 0：消耗型商品; 1：非消耗型商品; 2：订阅型商品
        ownedPurchasesReq.setPriceType(0);
        // 调用obtainOwnedPurchases接口获取所有已购但未发货的消耗型商品的购买信息
        Task<OwnedPurchasesResult> task = Iap.getIapClient(activity).obtainOwnedPurchases(ownedPurchasesReq);
        task.addOnSuccessListener(new OnSuccessListener<OwnedPurchasesResult>() {
            @Override
            public void onSuccess(OwnedPurchasesResult result) {

                PL.i("getOwnedPurchases onSuccess");
                // 获取接口请求成功的结果
                if (result != null && result.getInAppPurchaseDataList() != null) {
                    for (int i = 0; i < result.getInAppPurchaseDataList().size(); i++) {
                        String inAppPurchaseData = result.getInAppPurchaseDataList().get(i);
                        String inAppSignature = result.getInAppSignature().get(i);
                        // 使用应用的IAP公钥验证inAppPurchaseData的签名数据
                        // 如果验签成功，必须校验InAppPurchaseData中的productId、price、currency等信息的一致性
                        // 验证一致后，确认每个商品的购买状态。确认商品已支付后，检查此前是否已发过货，未发货则进行发货操作。发货成功后执行消耗操作

                        sendPayment(inAppPurchaseData, inAppSignature, "yes");

//                        try {
//                            InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
//                            int purchaseState = inAppPurchaseDataBean.getPurchaseState();
//                            consumeOwnedPurchase(mActivity, inAppPurchaseDataBean);
//                        } catch (JSONException e) {
//
//                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                PL.i("getOwnedPurchases onFailure");

                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    int returnCode = apiException.getStatusCode();
                } else {
                    // 其他外部错误
                }

                handlePayFail("getOwnedPurchases onFailure");
            }
        });
    }

    public void consumeOwnedPurchase(Activity activity, InAppPurchaseData inAppPurchaseDataBean){

        if (inAppPurchaseDataBean == null){
            PL.i("consumeOwnedPurchase inAppPurchaseDataBean is null");
            return;
        }
        // 构造ConsumeOwnedPurchaseReq对象
        ConsumeOwnedPurchaseReq req = new ConsumeOwnedPurchaseReq();
        String purchaseToken = inAppPurchaseDataBean.getPurchaseToken();
//        try {
//            // purchaseToken需从购买信息InAppPurchaseData中获取
//            InAppPurchaseData inAppPurchaseDataBean = new InAppPurchaseData(inAppPurchaseData);
//            purchaseToken = inAppPurchaseDataBean.getPurchaseToken();
//        } catch (JSONException e) {
//        }
        if (SStringUtil.isEmpty(purchaseToken)){
            PL.i("consumeOwnedPurchase purchaseToken isEmpty");
            return;
        }
        req.setPurchaseToken(purchaseToken);
        // 消耗型商品发货成功后，需调用consumeOwnedPurchase接口进行消耗
        Task<ConsumeOwnedPurchaseResult> task = Iap.getIapClient(activity).consumeOwnedPurchase(req);
        task.addOnSuccessListener(new OnSuccessListener<ConsumeOwnedPurchaseResult>() {
            @Override
            public void onSuccess(ConsumeOwnedPurchaseResult result) {
                // 获取接口请求结果
                PL.i("consumeOwnedPurchase onSuccess");
                //handlePaySuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                PL.i("consumeOwnedPurchase onFailure");
                //handlePayFail("finish");
                if (e instanceof IapApiException) {
                    IapApiException apiException = (IapApiException) e;
                    Status status = apiException.getStatus();
                    int statusCode = apiException.getStatusCode();
                    PL.i("consumeOwnedPurchase onFailur statusCode=" + statusCode);

                } else {
                    // 其他外部错误
                }
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

    private void handlePaySuccess(){
        if (loadingDialog != null){
            loadingDialog.dismissProgressDialog();
        }

        if (iPayCallBack != null) {

            Gson gson = new Gson();

            BasePayBean payBean = new BasePayBean();

            try {

                if (this.current_inAppPurchaseData != null) {
                    HWPurchaseData hwPurchaseData = gson.fromJson(this.current_inAppPurchaseData, HWPurchaseData.class);

                    if (hwPurchaseData != null){

                        payBean.setTransactionId(hwPurchaseData.getOrderId());
                        payBean.setOrderId(this.currentOrderId);
                        payBean.setPackageName(hwPurchaseData.getPackageName());
                        payBean.setUsdPrice(this.skuAmount);
                        payBean.setProductId(hwPurchaseData.getProductId());
//                    payBean.setmItemType(purchase.getItemType());
                        payBean.setOriginPurchaseData(this.current_inAppPurchaseData);
                        payBean.setPurchaseState(hwPurchaseData.getPurchaseState());
                        payBean.setPurchaseTime(hwPurchaseData.getPurchaseTime());
                        //payBean.setSignature(purchase.getSignature());
                        payBean.setDeveloperPayload(hwPurchaseData.getDeveloperPayload());
                        //payBean.setmToken(purchase.getPurchaseToken());
                        payBean.setPrice(hwPurchaseData.getPrice());
                        payBean.setCurrency(hwPurchaseData.getCurrency());

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            iPayCallBack.success(payBean);
        }
    }

    public static final String publicKey = "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAnQtpQslB03181lk0aDzjqkyfOSvu4fe31XSzsNYn63McP6A1jKFLtWHt8L8wdK9EBTOzXcMGX4QlM0WdscuK5g4Dmz8ivNH4vFF1cX5mx1Z9JoVhfVR5jEjzLxOBzoLnNLFk1DW45H6u7F7+5JMDyxp87DwbelaCq9DLn0269w2gbI0npheERwwj8CmDxCeO5JQTiaTOGWM9u/akIw+D99cX/YpNo4TiNdGAaESR0d5YMqOH536LexZvshvSeOCIJaNc0soINHJaffaB1amR4QFyKY5ISQQDTdtW4RfR5snHM1cq6hxnOT4ze8JtBzqE9Q7dUGFd9mbqWguYm8vpeKyjBXhf6AKzEFILrU+Yyrg312hoSKwWFTAjSyCw8mH2ktPvk9oObcQpelhMNXZMmCPlT5eijQkgRqwMeHxXJQNA930yDahMGdI+taMO6TtOyCxSMRA54IOSSO7s1Fl8Pu0cDn2WJrOHSECX0L47Up/P93II2N3zNfGNKs+zNidfAgMBAAE=";
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
        if (signatureAlgorithm == null || signatureAlgorithm.length() == 0) {
            signatureAlgorithm = "SHA256WithRSA";
            System.out.println("doCheck, algorithm: SHA256WithRSA");
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
