package com.core.base.request;

import com.core.base.bean.BaseReqeustBean;

/**
 * Created by gan on 2017/2/11.
 */

public interface ISRqeust {

    <T> void excute(final Class<T> classOfT);

    void excute();

    /**
     * 从各Task对象中获取对应的请求Bean。子线程执行
     */
    BaseReqeustBean createRequestBean();

    /**
     *
     * 主线程执行
     * @param responseModel
     * @param <T>
     */
    <T> void onHttpSucceess(T responseModel);

    void onTimeout(String result);

    void onNoData(String result);

}
