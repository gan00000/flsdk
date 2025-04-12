package com.core.base.request;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.core.base.callback.ISReqCallBack;
import com.core.base.http.HttpRequest;
import com.core.base.http.HttpResponse;
import com.core.base.bean.BaseReqeustBean;
import com.core.base.bean.BaseResponseModel;
import com.core.base.utils.JsonUtil;
import com.core.base.utils.PL;
import com.core.base.utils.SStringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


public abstract class AbsHttpRequest implements ISRqeust {

    private PostType postType = PostType.application_urlencoded;

    private HttpResponse coreHttpResponse;

    private Dialog loadDialog;

    private ISReqCallBack reqCallBack;

    public void setPostType(PostType postType) {
        this.postType = postType;
    }

    public void setReqCallBack(ISReqCallBack reqCallBack) {
        this.reqCallBack = reqCallBack;
    }

    protected boolean isCancel = false; //是否取消请求。

   /* public <T> void excute(final Type mTypeOfT) {

        @SuppressLint("StaticFieldLeak") SRequestAsyncTask asyncTask = new SRequestAsyncTask() {

            T responseModule = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadDialog != null && !loadDialog.isShowing()){
                    loadDialog.show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                if(isCancel) {
                    return null;
                }
                BaseReqeustBean baseReqeustBean = createRequestBean();
                if (baseReqeustBean == null) {
                    return "";
                }
                String rawResponse = doRequest(baseReqeustBean);

                //解析json数据
                if (!TextUtils.isEmpty(rawResponse) && mTypeOfT != null && JsonUtil.isJson(rawResponse)) {
                    Gson gson = new Gson();
                    responseModule = gson.fromJson(rawResponse, mTypeOfT);
                    if (responseModule != null && (responseModule instanceof BaseResponseModel)) {
                        ((BaseResponseModel) responseModule).setRawResponse(rawResponse);
                    }

                }
                return rawResponse;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (loadDialog != null && loadDialog.isShowing()){
                    loadDialog.dismiss();
                }

                if(isCancel) {
                    return;
                }

                if (coreHttpResponse != null) {
                    if (coreHttpResponse.getHttpResponseCode() != HttpURLConnection.HTTP_OK) {
                        onTimeout(coreHttpResponse.getHttpResponseCode() + "");
                        if (reqCallBack != null){
                            reqCallBack.timeout(coreHttpResponse.getHttpResponseCode() + "");
                        }
                    } else if (TextUtils.isEmpty(result)) {
                        onNoData(coreHttpResponse.getRequestCompleteUrl());
                        if (reqCallBack != null){
                            reqCallBack.noData();
                        }
                    } else {
                        onHttpSucceess(responseModule);
                        if (reqCallBack != null){
                            reqCallBack.success(responseModule,result);
                        }
                    }
                }

            }
        };

        asyncTask.asyncExcute();
    }*/

    @Override
    public void excute() {
        excute(BaseResponseModel.class);
    }

    @Override
    public <T> void excute(final Class<T> classOfT) {

        if (loadDialog != null && !loadDialog.isShowing()){
            loadDialog.show();
        }

//        onNext 用于处理 Observable 发出的每个数据项。
//        onError 用于处理 Observable 发出的错误。
//        onComplete 用于表示 Observable 正常结束。
//        在一个正确运行的事件序列中,onCompleted() 和 onError() 有且只有一个，
//        并且是事件序列中的最后一个。需要注意的是，onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
//        onError 和 onComplete 是互斥的，在一次数据流中只会有其中一个方法被调用，用于表示数据流的结束状态
        Observable.just("" + System.currentTimeMillis())
                .map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Throwable {
                PL.i("apply");
                if(isCancel) {
                    return null;
                }
                BaseReqeustBean baseReqeustBean = createRequestBean();
                if (baseReqeustBean == null) {
                    return null;
                }

                String rawResponse = doRequest(baseReqeustBean);
                return rawResponse;
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        PL.i("onSubscribe");
                    }

                    @Override
                    public void onNext(@NonNull String rawResponse) {
                        PL.i("onNext");

                        if (loadDialog != null && loadDialog.isShowing()){
                            loadDialog.dismiss();
                        }
                        if(isCancel) {
                            return;
                        }

                        if (coreHttpResponse != null) {
                            if (coreHttpResponse.getHttpResponseCode() != HttpURLConnection.HTTP_OK) {
                                onTimeout(coreHttpResponse.getHttpResponseCode() + "");
                                if (reqCallBack != null){
                                    reqCallBack.timeout(coreHttpResponse.getHttpResponseCode() + "");
                                }
                            } else if (TextUtils.isEmpty(rawResponse)) {
                                onNoData(coreHttpResponse.getRequestCompleteUrl());
                                if (reqCallBack != null){
                                    reqCallBack.noData();//也是请求成功
                                }
                            } else {

                                //解析json数据
//                                if (!TextUtils.isEmpty(rawResponse) && classOfT != null && JsonUtil.isJson(rawResponse)) {
                                T responseModule = null;
                                if (classOfT != null) {
                                    try {
                                        Gson gson = new Gson();
                                        responseModule = gson.fromJson(rawResponse, classOfT);
                                        if (responseModule != null && (responseModule instanceof BaseResponseModel)) {
                                            ((BaseResponseModel) responseModule).setRawResponse(rawResponse);
                                        }

                                    } catch (JsonSyntaxException e) {
                                        PL.e("fromJson error url = %s", coreHttpResponse.getRequestCompleteUrl());
                                        e.printStackTrace();
                                    }
                                }
                                onHttpSucceess(responseModule);
                                if (reqCallBack != null){
                                    reqCallBack.success(responseModule, rawResponse);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        PL.i("onError");
                        if (loadDialog != null && loadDialog.isShowing()){
                            loadDialog.dismiss();
                        }
                        onNoData("");
                        if (reqCallBack != null){
                            reqCallBack.noData();//发生错误
                        }
                    }

                    @Override
                    public void onComplete() {
                        PL.i("onComplete");//onError()和onComplete()只会回调一个。
                        if (loadDialog != null && loadDialog.isShowing()){
                            loadDialog.dismiss();
                        }
                    }
                });
       /* @SuppressLint("StaticFieldLeak") SRequestAsyncTask asyncTask = new SRequestAsyncTask() {

            T responseModule = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (loadDialog != null && !loadDialog.isShowing()){
                    loadDialog.show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                if(isCancel) {
                    return null;
                }
                BaseReqeustBean baseReqeustBean = createRequestBean();
                if (baseReqeustBean == null) {
                    return "";
                }
                String rawResponse = doRequest(baseReqeustBean);

                //解析json数据
                if (!TextUtils.isEmpty(rawResponse) && classOfT != null && JsonUtil.isJson(rawResponse)) {
                    Gson gson = new Gson();
                    responseModule = gson.fromJson(rawResponse, classOfT);
                    if (responseModule != null && (responseModule instanceof BaseResponseModel)) {
                        ((BaseResponseModel) responseModule).setRawResponse(rawResponse);
                    }

                }
                return rawResponse;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (loadDialog != null && loadDialog.isShowing()){
                    loadDialog.dismiss();
                }
                if(isCancel) {
                    return;
                }

                if (coreHttpResponse != null) {
                    if (coreHttpResponse.getHttpResponseCode() != HttpURLConnection.HTTP_OK) {
                        onTimeout(coreHttpResponse.getHttpResponseCode() + "");
                        if (reqCallBack != null){
                            reqCallBack.timeout(coreHttpResponse.getHttpResponseCode() + "");
                        }
                    } else if (TextUtils.isEmpty(result)) {
                        onNoData(coreHttpResponse.getRequestCompleteUrl());
                        if (reqCallBack != null){
                            reqCallBack.noData();
                        }
                    } else {
                        onHttpSucceess(responseModule);
                        if (reqCallBack != null){
                            reqCallBack.success(responseModule,result);
                        }
                    }
                }

            }
        };

        asyncTask.asyncExcute();*/
    }

    /**
     * <p>Title: doRequest</p> <p>Description: 实际网络请求</p>
     */
    public String doRequest(BaseReqeustBean baseReqeustBean) {
        if (SStringUtil.isNotEmpty(baseReqeustBean.getCompleteUrl())) {

            if (isGetMethod) {
                if (isNeedGetParams){

                    coreHttpResponse = HttpRequest.getReuqest(baseReqeustBean.getCompleteUrl(),baseReqeustBean.fieldValueToMap());
                }else{

                    coreHttpResponse = HttpRequest.getReuqestIn2Url(baseReqeustBean.getCompleteUrl(),baseReqeustBean.getCompleteSpaUrl());
                }

            }else{

                PL.i("postType:" + postType.name());

                if (postType == PostType.application_json){

                    coreHttpResponse = HttpRequest.postJsonObject(baseReqeustBean.getCompleteUrl(), SStringUtil.map2Json(baseReqeustBean.fieldValueToMap()));

                }else {

                    coreHttpResponse = HttpRequest.postIn2Url(baseReqeustBean.getCompleteUrl(), baseReqeustBean.getCompleteSpaUrl(), baseReqeustBean.fieldValueToMap());
                }
            }
            if (coreHttpResponse != null) {
                return coreHttpResponse.getResult();
            }
        }
        return "";
    }

    public void setLoadDialog(Dialog loadDialog) {
        this.loadDialog = loadDialog;
        if(this.loadDialog != null) {
            this.loadDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    isCancel = true;
                    if(reqCallBack != null) {
                        reqCallBack.cancel();
                    }
                }
            });
        }
    }

    private boolean isGetMethod = false;
    private boolean isNeedGetParams = false;


    public void setGetMethod(boolean getMethod, boolean isNeedGetParams) {
        isGetMethod = getMethod;
        this.isNeedGetParams = isNeedGetParams;
    }

    @Override
    public <T> void onHttpSucceess(T responseModel) {

    }

    @Override
    public void onTimeout(String result) {

    }

    @Override
    public void onNoData(String result) {

    }

//    @Override
//    public void cancelTask() {
//        this.isCancel = true;
//    }
}
