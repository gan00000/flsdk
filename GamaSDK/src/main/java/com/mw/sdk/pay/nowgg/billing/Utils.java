package com.mw.sdk.pay.nowgg.billing;

import gg.now.billingclient.api.BillingClient;

public class Utils {
    public static String getResponseString(@BillingClient.BillingResponse int response) {
        switch (response) {
            case BillingClient.BillingResponse.ACTIVE_SUBSCRIPTION_CANNOT_BE_PURCHASED_AGAIN:
                return "ACTIVE_SUBSCRIPTION_CANNOT_BE_PURCHASED_AGAIN";
            case BillingClient.BillingResponse.BILLING_UNAVAILABLE:
                return "BILLING_UNAVAILABLE";
            case BillingClient.BillingResponse.DEVELOPER_ERROR:
                return "DEVELOPER_ERROR";
            case BillingClient.BillingResponse.ERROR:
                return "ERROR";
            case BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED:
                return "FEATURE_NOT_SUPPORTED";
            case BillingClient.BillingResponse.INTERNAL_ERROR:
                return "INTERNAL_ERROR";
            case BillingClient.BillingResponse.INVALID_PAYMENT_ID:
                return "INVALID_PAYMENT_ID";
            case BillingClient.BillingResponse.INVALID_PURCHASE_TOKEN:
                return "INVALID_PURCHASE_TOKEN";
            case BillingClient.BillingResponse.ITEM_ALREADY_OWNED:
                return "ITEM_ALREADY_OWNED";
            case BillingClient.BillingResponse.ITEM_NOT_OWNED:
                return "ITEM_NOT_OWNED";
            case BillingClient.BillingResponse.ITEM_UNAVAILABLE:
                return "ITEM_UNAVAILABLE";
            case BillingClient.BillingResponse.NO_VALID_SUBSCRIPTION_FOUND:
                return "NO_VALID_SUBSCRIPTION_FOUND";
            case BillingClient.BillingResponse.OK:
                return "OK";
            case BillingClient.BillingResponse.PAYMENT_CANCELLED:
                return "PAYMENT_CANCELLED";
            case BillingClient.BillingResponse.PAYMENT_FAILED:
                return "PAYMENT_FAILED";
            case BillingClient.BillingResponse.PAYMENT_TIMEOUT:
                return "PAYMENT_TIMEOUT";
            case BillingClient.BillingResponse.PRODUCT_ALREADY_ACKNOWLEDGED:
                return "PRODUCT_ALREADY_ACKNOWLEDGED";
            case BillingClient.BillingResponse.PRODUCT_ALREADY_CONSUMED:
                return "PRODUCT_ALREADY_CONSUMED";
            case BillingClient.BillingResponse.PRODUCT_NOT_CONSUMED:
                return "PRODUCT_NOT_CONSUMED";
            case BillingClient.BillingResponse.PRODUCT_NOT_FOUND_ON_STUDIO:
                return "PRODUCT_NOT_FOUND_ON_STUDIO";
            case BillingClient.BillingResponse.SERVICE_DISCONNECTED:
                return "SERVICE_DISCONNECTED";
            case BillingClient.BillingResponse.SERVICE_TIMEOUT:
                return "SERVICE_TIMEOUT";
            case BillingClient.BillingResponse.SERVICE_UNAVAILABLE:
                return "SERVICE_UNAVAILABLE";
            case BillingClient.BillingResponse.USER_CANCELED:
                return "USER_CANCELED";
        }
        return "UNKNOWN_ERROR";
    }

}
