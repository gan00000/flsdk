package com.ldy.callback;

/**
 * Created by gan on 2017/1/7.
 */
public interface SavePhotoCallback extends ISCallBack {

    void onSaveSuccess(String path);
    void onSaveFailure();
}
