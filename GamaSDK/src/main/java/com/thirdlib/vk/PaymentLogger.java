package com.thirdlib.vk;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kotlin.jvm.functions.Function0;
import ru.rustore.sdk.billingclient.provider.logger.ExternalPaymentLogger;

public class PaymentLogger implements ExternalPaymentLogger {
 
    private final String tag;
 
    public PaymentLogger(String tag) {
        this.tag = tag;
    }
 
    @Override
    public void d(@Nullable Throwable throwable, @NonNull Function0<String> function0) {
        Log.d(tag, function0.invoke());
    }
 
    @Override
    public void e(@Nullable Throwable throwable, @NonNull Function0<String> function0) {
        Log.e(tag, function0.invoke());
    }
 
    @Override
    public void i(@Nullable Throwable throwable, @NonNull Function0<String> function0) {
        Log.i(tag, function0.invoke());
    }
 
    @Override
    public void v(@Nullable Throwable throwable, @NonNull Function0<String> function0) {
        Log.v(tag, function0.invoke());
    }
 
    @Override
    public void w(@Nullable Throwable throwable, @NonNull Function0<String> function0) {
        Log.w(tag, function0.invoke());
    }
}