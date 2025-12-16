package com.mw.sdk.api;

import android.content.Context;

import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.mw.sdk.utils.ResConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    static RetrofitClient mRetrofitClient;

    private Retrofit loginRetrofit;
    private Retrofit payRetrofit;
    private Retrofit cndRetrofit;
    private Retrofit platRetrofit;
    private Retrofit logRetrofit;
    private Retrofit memberRetrofit;

    private Map<String, Retrofit> stringRetrofitMap = new HashMap<>();

    public static RetrofitClient instance(){
        if (mRetrofitClient ==null){
            mRetrofitClient = new RetrofitClient();
        }
        return mRetrofitClient;
    }

    public Retrofit build(Context context, URLType URLType){
        String baseUrl = "";
        if (com.mw.sdk.api.URLType.CDN == URLType){
            if (cndRetrofit != null) {
                return cndRetrofit;
            }
            baseUrl = ResConfig.getCdnPreferredUrl(context);
            cndRetrofit = build(baseUrl);
            return cndRetrofit;
        } else if (com.mw.sdk.api.URLType.LOGIN == URLType) {

            if (loginRetrofit != null) {
                return loginRetrofit;
            }
            baseUrl = ResConfig.getLoginPreferredUrl(context);
            loginRetrofit = build(baseUrl);
            return loginRetrofit;
        }else if (com.mw.sdk.api.URLType.PAY == URLType) {

            if (payRetrofit != null) {
                return payRetrofit;
            }
            baseUrl = ResConfig.getPayPreferredUrl(context);
            payRetrofit = build(baseUrl);
            return payRetrofit;
        }else if (com.mw.sdk.api.URLType.PLAT == URLType) {

            if (platRetrofit != null) {
                return platRetrofit;
            }
            baseUrl = ResConfig.getPlatPreferredUrl(context);
            platRetrofit = build(baseUrl);
            return platRetrofit;
        }else if (com.mw.sdk.api.URLType.LOG == URLType) {

            if (logRetrofit != null) {
                return logRetrofit;
            }
            baseUrl = ResConfig.getLogPreferredUrl(context);
            logRetrofit = build(baseUrl);
            return logRetrofit;
        }else if (com.mw.sdk.api.URLType.MEMBER == URLType) {

            if (memberRetrofit != null) {
                return memberRetrofit;
            }
            baseUrl = ResConfig.getMemberPreferredUrl(context);
            memberRetrofit = build(baseUrl);
            return memberRetrofit;
        }
        return null;
    }

    public Retrofit getRetrofit(String url){

//        if (SStringUtil.isEmpty(url)){
//            PL.e("baseUrl is empty");
//            return null;
//        }

        if (SStringUtil.isNotEmpty(url) && stringRetrofitMap.get(url) != null){
            return stringRetrofitMap.get(url);
        }
        Retrofit retrofit = build(url);
        if (SStringUtil.isNotEmpty(url) && retrofit != null) {
            stringRetrofitMap.put(url, retrofit);
        }
        return retrofit;

    }

    private Retrofit build(String baseUrl){

//        if (SStringUtil.isEmpty(baseUrl)){
//            PL.e("baseUrl is empty");
//            return null;
//        }

        //只要按「ScalarsConverterFactory 在前，GsonConverterFactory 在后」的顺序配置，
        // Retrofit 就能根据接口声明的返回类型自动、正确地选择对应的转换器
        //顺序不要反，不然可能会出现解析错误

        if (SStringUtil.isEmpty(baseUrl)){
            PL.e("baseUrl is empty");
            //return null;
            //.baseUrl(baseUrl)
            return new Retrofit.Builder()
                    .client(getOkHttpClient())
                    //.baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return new Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    // 全局唯一的 OkHttpClient（核心，复用连接池）
    private static OkHttpClient sOkHttpClient;
    // 初始化 OkHttpClient（配置通用网络参数）
    private static OkHttpClient getOkHttpClient() {
        if (sOkHttpClient == null) {
            synchronized (RetrofitClient.class) {
                if (sOkHttpClient == null) {

                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(/*new HttpLoggingInterceptor.Logger() {
                        @Override
                        public void log(@NonNull String s) {
                            PL.d("RetrofitLog=" + s);
                        }
                    }*/);
                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    sOkHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS) // 通用超时
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .addInterceptor(interceptor) // 公共拦截器（如加 Header）
                            .connectionPool(new ConnectionPool(5, 2, TimeUnit.MINUTES)) // 连接池复用
                            .build();
                }
            }
        }
        return sOkHttpClient;
    }

    /*private static class CommonInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("Token", "your_global_token") // 全局 Token
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();
            return chain.proceed(request);
        }
    }*/
}
