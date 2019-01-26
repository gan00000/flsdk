package com.gama.base.bean.restful;

import android.content.Context;

import com.gama.base.bean.SSdkBaseRequestBean;

public class GamaRestfulRequestBean extends SSdkBaseRequestBean {
    private String logingServerSignature;
    private String json;
    private String age;

    public GamaRestfulRequestBean(Context context) {
        super(context);
    }

    public String getLogingServerSignature() {
        return logingServerSignature;
    }

    public void setLogingServerSignature(String logingServerSignature) {
        this.logingServerSignature = logingServerSignature;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

}
