package com.core.base.callback;

/**
 * Created by gan on 2017/2/11.
 */
public interface SFCallBack<T> {

    void success(T result,String msg);
    void fail(T result,String msg);
}
