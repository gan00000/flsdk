package com.gama.pay.utils;

import com.gama.pay.gp.util.SkuDetails;

import java.util.Map;

public interface QueryProductListener {
    void onQueryResult(Map<String, SkuDetails> details);
}
