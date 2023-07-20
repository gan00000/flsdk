package com.ldy.callback;

public interface IPayListener {
//    void onPayFinish(Bundle bundle);
    void onPaySuccess(String productId,String cpOrderId);
    void onPayFail();
}
