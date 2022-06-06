package com.mw.sdk.pay.utils;

import com.mw.sdk.pay.gp.util.SkuDetails;

import java.util.Map;

public interface QueryProductListener {
    void onQueryResult(Map<String, SkuDetails> details);
}
