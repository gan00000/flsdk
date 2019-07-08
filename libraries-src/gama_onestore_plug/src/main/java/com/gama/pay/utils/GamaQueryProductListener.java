package com.gama.pay.utils;

import com.gama.base.bean.BasePayBean;

import java.util.Map;

public interface GamaQueryProductListener {
    void onQueryResult(Map<String, BasePayBean> details);
}
