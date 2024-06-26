package com.mw.sdk.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.ResConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    static RetrofitClient mRetrofitClient;

    private Retrofit loginRetrofit;
    private Retrofit payRetrofit;
    private Retrofit cndRetrofit;
    private Retrofit platRetrofit;
    private Retrofit logRetrofit;
    private Retrofit memberRetrofit;

    public static RetrofitClient instance(){
        if (mRetrofitClient ==null){
            mRetrofitClient = new RetrofitClient();
        }
        return mRetrofitClient;
    }

    public Retrofit build(Context context, URLType URLType){
        String baseUrl = "";
        if (URLType.CDN == URLType){
            if (cndRetrofit != null) {
                return cndRetrofit;
            }
            baseUrl = ResConfig.getCdnPreferredUrl(context);
            cndRetrofit = build(baseUrl);
            return cndRetrofit;
        } else if (URLType.LOGIN == URLType) {

            if (loginRetrofit != null) {
                return loginRetrofit;
            }
            baseUrl = ResConfig.getLoginPreferredUrl(context);
            loginRetrofit = build(baseUrl);
            return loginRetrofit;
        }else if (URLType.PAY == URLType) {

            if (payRetrofit != null) {
                return payRetrofit;
            }
            baseUrl = ResConfig.getPayPreferredUrl(context);
            payRetrofit = build(baseUrl);
            return payRetrofit;
        }else if (URLType.PLAT == URLType) {

            if (platRetrofit != null) {
                return platRetrofit;
            }
            baseUrl = ResConfig.getPlatPreferredUrl(context);
            platRetrofit = build(baseUrl);
            return platRetrofit;
        }else if (URLType.LOG == URLType) {

            if (logRetrofit != null) {
                return logRetrofit;
            }
            baseUrl = ResConfig.getLogPreferredUrl(context);
            logRetrofit = build(baseUrl);
            return logRetrofit;
        }else if (URLType.MEMBER == URLType) {

            if (memberRetrofit != null) {
                return memberRetrofit;
            }
            baseUrl = ResConfig.getMemberPreferredUrl(context);
            memberRetrofit = build(baseUrl);
            return memberRetrofit;
        }
        return null;
    }

    private Retrofit build(String baseUrl){

        if (SStringUtil.isEmpty(baseUrl)){
            PL.e("baseUrl is empty");
            return null;
        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(/*new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String s) {
                PL.d("RetrofitLog=" + s);
            }
        }*/);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

//    private void createRetrofit() {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

//        retrofit = new Retrofit.Builder().baseUrl(HOST_NAME).client(okHttpClient)
//                .addConverterFactory(LoganSquareConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
//    }
}
