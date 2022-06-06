package com.mw.sdk.callback;

import android.os.Bundle;

public interface IPayListener {
    void onPayFinish(Bundle bundle);
    void onPaySuccess(String productId,String cpOrderId);
    void onPayFail();
}
