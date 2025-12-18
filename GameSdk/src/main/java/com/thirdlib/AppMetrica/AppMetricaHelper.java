package com.thirdlib.AppMetrica;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.core.base.utils.PL;
import com.mw.sdk.R;
import com.thirdlib.ThirdModuleUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import io.appmetrica.analytics.AppMetrica;
import io.appmetrica.analytics.AppMetricaConfig;
import io.appmetrica.analytics.Revenue;

public class AppMetricaHelper {

    public static String getApiKey(Context context){
        return context.getResources().getString(R.string.mw_appmetrica_api_key);
    }
    public static void applicationOnCreate(Application application){

        if (!ThirdModuleUtil.existAppMetricaModule()){
            return;
        }

        String api_key = getApiKey(application.getApplicationContext());
        PL.d("mw_appmetrica_api_key=" + api_key);
        if (TextUtils.isEmpty(api_key)){
            PL.d("mw_appmetrica_api_key empty");
            return;
        }
        // Creating an extended library configuration.
        AppMetricaConfig config = AppMetricaConfig.newConfigBuilder(api_key)
                // Setting up the configuration. For example, to enable logging.
                .withLogs()
                .build();
        // Initializing the AppMetrica SDK.
        AppMetrica.activate(application, config);
        // Automatic tracking of user activity.
        AppMetrica.enableActivityAutoTracking(application);
    }

    public static void logEvent(Context context, String eventName, Map<String, Object> map){
        if (!ThirdModuleUtil.existAppMetricaModule()){
            return;
        }
        if (TextUtils.isEmpty(getApiKey(context))){
            return;
        }

        if (map == null){
            map = new HashMap<>();
        }
        AppMetrica.reportEvent(eventName, map);
    }

    public static void logRevenue(Context context, String eventName, long priceMicros, String currencyCode, String productId, String orderId){

        if (!ThirdModuleUtil.existAppMetricaModule()){
            return;
        }
        if (TextUtils.isEmpty(getApiKey(context))){
            return;
        }

        Currency mCurrency = null;
        try {
            mCurrency = Currency.getInstance(currencyCode);
        } catch (Exception e) {

        }
        if (mCurrency == null){
            mCurrency = Currency.getInstance("USD");
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("OrderID", orderId);
        } catch (JSONException e) {
        }
        // Creating the Revenue instance.99000000
        Revenue revenue = Revenue.newBuilder(priceMicros, mCurrency)
                .withProductID(productId)
                .withQuantity(1)
                // Passing the OrderID parameter in the .withPayload(String payload) method to group purchases.
                .withPayload(payload.toString())
                .build();
        // Sending the Revenue instance using reporter.
//        AppMetrica.getReporter(getApplicationContext(), "Testing API key").reportRevenue(revenue);

        AppMetrica.reportRevenue(revenue);
    }
}
