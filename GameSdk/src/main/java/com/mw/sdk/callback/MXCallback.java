package com.mw.sdk.callback;

import com.mw.sdk.bean.MXData;

public interface MXCallback {

    void success(MXData mxData);
    void fail(MXData mxData);
    void cancel(MXData mxData);

}
