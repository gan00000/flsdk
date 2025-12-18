package com.core.base.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求的基础设置
 * Created by gan on 2016/11/24.
 */

public abstract class AbsReqeustBean implements Serializable{

    //自己设置的参数，这个参数有值的话，后面的子类不在遍历字段，直接取这里的值
    private Map<String, String> requestParamsMap;

    public Map<String, String> getRequestParamsMap() {
        return requestParamsMap;
    }

    public void setRequestParamsMap(Map<String, String> requestParamsMap) {
        this.requestParamsMap = requestParamsMap;
    }

    /**
     * 请求的主域名
     */
    private String requestUrl = "";
    /**
     * 请求的备用域名
     */
    private String requestSpaUrl = "";
    /**
     * 请求的接口名
     */
    private String requestMethod = "";
    /**
     * 主域名的完整请求链接
     */
    private String completeUrl = "";
    /**
     * 备用域名的完整请求链接
     */
    private String completeSpaUrl = "";

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }



    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getCompleteUrl() {
        if (TextUtils.isEmpty(completeUrl) && !TextUtils.isEmpty(requestUrl) && !TextUtils.isEmpty(requestMethod)){
            completeUrl = requestUrl + requestMethod;
        }
        return completeUrl;
    }

    public void setCompleteUrl(String completeUrl) {
        this.completeUrl = completeUrl;
    }

    public String getCompleteSpaUrl() {

        if (TextUtils.isEmpty(completeSpaUrl) && !TextUtils.isEmpty(requestSpaUrl) && !TextUtils.isEmpty(requestMethod)){
            completeSpaUrl = requestSpaUrl + requestMethod;
        }
        return completeSpaUrl;
    }

    public void setCompleteSpaUrl(String completeSpaUrl) {
        this.completeSpaUrl = completeSpaUrl;
    }

    public String getRequestSpaUrl() {
        return requestSpaUrl;
    }

    public void setRequestSpaUrl(String requestSpaUrl) {
        this.requestSpaUrl = requestSpaUrl;
    }


}
