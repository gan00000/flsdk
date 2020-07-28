package com.flyfun.base.excute;

import android.text.TextUtils;

import com.core.base.request.AbsHttpRequest;
import com.core.base.utils.PL;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class GamaBaseRestRequestTask  extends AbsHttpRequest {

    protected String encode2Utf8(String s) {
        if(TextUtils.isEmpty(s)) {
            PL.e("encode string is empty");
            return "";
        }
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
