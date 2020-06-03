package com.gama.base.bean.unify;

import android.content.Context;

import com.gama.base.bean.SSdkBaseRequestBean;

public class UnifiedSwitchRequestBean extends SSdkBaseRequestBean {
    private String type;

    public UnifiedSwitchRequestBean(Context context) {
        super(context);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
