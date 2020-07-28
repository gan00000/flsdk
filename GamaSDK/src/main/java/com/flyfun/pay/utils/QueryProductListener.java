package com.flyfun.pay.utils;

import com.flyfun.pay.gp.util.SkuDetails;

import java.util.Map;

public interface QueryProductListener {
    void onQueryResult(Map<String, SkuDetails> details);
}
