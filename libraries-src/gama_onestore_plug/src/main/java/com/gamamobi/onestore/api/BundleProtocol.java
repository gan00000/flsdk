//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.gamamobi.onestore.api;

import android.content.Intent;
import android.os.Bundle;

import com.gamamobi.onestore.api.PurchaseClient.IapException;
import com.gamamobi.onestore.api.PurchaseClient.NeedUpdateException;
import com.gamamobi.onestore.api.PurchaseClient.SecurityException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class BundleProtocol {
    static final String RESPONSE_CODE = "responseCode";
    static final String PURCHASE_DATA_ORDER_ID = "orderId";
    static final String PURCHASE_DATA_PACKAGE_NAME = "packageName";
    static final String PURCHASE_DATA_PRODUCT_ID = "productId";
    static final String PURCHASE_DATA_PURCHASE_TIME = "purchaseTime";
    static final String PURCHASE_DATA_PURCHASE_ID = "purchaseId";
    static final String PURCHASE_DATA_PAYLOAD = "developerPayload";
    static final String PURCHASE_DATA_PURCHASE_STATE = "purchaseState";
    static final String PURCHASE_DATA_RECURRING_STATE = "recurringState";
    static final String BUY_INTENT = "purchaseIntent";
    static final String PURCHASE_DATA = "purchaseData";
    static final String PURCHASE_DATA_SIGNATURE = "purchaseSignature";
    static final String BILLING_KEY = "billingKey";
    static final String PUT_DATA_GAME_ID = "gameUserId";
    static final String PUT_DATA_PROMOTION_APPLICABLE = "promotionApplicable";
    static final String LOGIN_INTENT = "loginIntent";
    static final String PRODUCT_DETAIL_LIST = "productDetailList";
    static final String PRODUCT_ID_LIST = "productIdList";
    static final String PRODUCT_DETAIL_PRODUCT_ID = "productId";
    static final String PRODUCT_DETAIL_TYPE = "type";
    static final String PRODUCT_DETAIL_PRICE = "price";
    static final String PRODUCT_DETAIL_TITLE = "title";
    static final String IN_APP_PURCHASE_DETAIL_LIST = "purchaseDetailList";
    static final String IN_APP_CONTINUATION_KEY = "continuationKey";
    static final String IN_APP_PURCHASE_SIGNATURE_LIST = "purchaseSignatureList";

    private BundleProtocol() {
        throw new IllegalAccessError("Utility class");
    }

    static class PurchaseResult {
        private int responseCode;
        private String purchaseData;
        private String signature;
        private String billingKey;

        public PurchaseResult(Intent data) throws IapException, SecurityException, NeedUpdateException {
            if (data == null) {
                throw new IapException(IapResult.IAP_ERROR_DATA_PARSING);
            } else {
                this.responseCode = data.getIntExtra("responseCode", -1);
                this.purchaseData = data.getStringExtra("purchaseData");
                this.signature = data.getStringExtra("purchaseSignature");
                if (IapResult.RESULT_SECURITY_ERROR.equalCode(this.responseCode)) {
                    throw new SecurityException();
                } else if (IapResult.RESULT_NEED_UPDATE.equalCode(this.responseCode)) {
                    throw new NeedUpdateException();
                } else if (!IapResult.RESULT_OK.equalCode(this.responseCode)) {
                    throw new IapException(this.responseCode);
                }
            }
        }

        public boolean isSuccess() {
            return IapResult.RESULT_OK.equalCode(this.responseCode);
        }

        public PurchaseData toPurchaseData() throws JSONException {
            JSONObject purchaseObject = new JSONObject(this.purchaseData);
            return PurchaseData.builder().orderId(purchaseObject.optString("orderId")).packageName(purchaseObject.optString("packageName")).productId(purchaseObject.optString("productId")).purchaseTime(purchaseObject.optLong("purchaseTime")).purchaseId(purchaseObject.optString("purchaseId")).developerPayload(purchaseObject.optString("developerPayload")).signature(this.signature).purchaseData(this.purchaseData).build();
        }

        public int getResponseCode() {
            return this.responseCode;
        }

        public String getPurchaseData() {
            return this.purchaseData;
        }

        public String getSignature() {
            return this.signature;
        }
    }

    static class PurchasedItemListResponse {
        private final Bundle mBundle;
        private List<String> mPurchaseDataList;
        private List<String> mSignatureList;
        private String mPublicKeyBase64;

        PurchasedItemListResponse(Bundle bundle, String publicKeyBase64) throws IapException, SecurityException, NeedUpdateException {
            if (bundle == null) {
                throw new IapException(IapResult.IAP_ERROR_DATA_PARSING);
            } else {
                int responseCode = bundle.getInt("responseCode");
                if (IapResult.RESULT_SECURITY_ERROR.equalCode(responseCode)) {
                    throw new SecurityException();
                } else if (IapResult.RESULT_NEED_UPDATE.equalCode(responseCode)) {
                    throw new NeedUpdateException();
                } else if (!IapResult.RESULT_OK.equalCode(responseCode)) {
                    throw new IapException(responseCode);
                } else {
                    this.mBundle = bundle;
                    this.mPublicKeyBase64 = publicKeyBase64;
                    this.mPurchaseDataList = bundle.getStringArrayList("purchaseDetailList");
                    this.mSignatureList = bundle.getStringArrayList("purchaseSignatureList");
                }
            }
        }

        int size() {
            return this.mPurchaseDataList.size();
        }

        String getContinuationKey() {
            return this.mBundle.getString("continuationKey");
        }

        PurchaseData getSinglePurchaseData(int idx) throws IapException {
            String signature = (String)this.mSignatureList.get(idx);
            String singlePurchaseData = (String)this.mPurchaseDataList.get(idx);

            try {
                if (Security.verifyPurchase(this.mPublicKeyBase64, singlePurchaseData, signature)) {
                    JSONObject object = new JSONObject(singlePurchaseData);
                    return PurchaseData.builder().orderId(object.optString("orderId")).packageName(object.optString("packageName")).productId(object.optString("productId")).purchaseTime(object.optLong("purchaseTime")).purchaseState(object.optInt("purchaseState")).recurringState(object.optInt("recurringState")).purchaseId(object.optString("purchaseId")).developerPayload(object.optString("developerPayload")).signature(signature).purchaseData(singlePurchaseData).build();
                } else {
                    throw new IapException(IapResult.IAP_ERROR_SIGNATURE_VERIFICATION);
                }
            } catch (JSONException var5) {
                throw new IapException(IapResult.IAP_ERROR_DATA_PARSING);
            }
        }
    }

    static class ProductDetailResponse {
        final List<ProductDetail> productDetails = new ArrayList();

        ProductDetailResponse(Bundle bundle) throws IapException, SecurityException, NeedUpdateException {
            if (bundle == null) {
                throw new IapException(IapResult.IAP_ERROR_DATA_PARSING);
            } else {
                int responseCode = bundle.getInt("responseCode");
                if (IapResult.RESULT_SECURITY_ERROR.equalCode(responseCode)) {
                    throw new SecurityException();
                } else if (IapResult.RESULT_NEED_UPDATE.equalCode(responseCode)) {
                    throw new NeedUpdateException();
                } else if (!IapResult.RESULT_OK.equalCode(responseCode)) {
                    throw new IapException(responseCode);
                } else {
                    List<String> productDetailJsons = bundle.getStringArrayList("productDetailList");
                    if (productDetailJsons != null) {
                        try {
                            Iterator var4 = productDetailJsons.iterator();

                            while(var4.hasNext()) {
                                String productDetailJson = (String)var4.next();
                                JSONObject object = new JSONObject(productDetailJson);
                                ProductDetail product = ProductDetail.builder().productId(object.optString("productId")).type(object.optString("type")).price(object.optString("price")).title(object.optString("title")).build();
                                this.productDetails.add(product);
                            }
                        } catch (JSONException var8) {
                            throw new IapException(IapResult.IAP_ERROR_DATA_PARSING);
                        }
                    }

                }
            }
        }

        public List<ProductDetail> getProductData() {
            return this.productDetails;
        }
    }
}
