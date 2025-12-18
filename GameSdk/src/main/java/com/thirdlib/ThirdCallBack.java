package com.thirdlib;

public interface ThirdCallBack {

    void success(String thirdId, String mFullName, String mEmail, String idTokenString);
    void failure(String msg);

}
