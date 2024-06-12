package com.mw.sdk.api;

import com.mw.sdk.bean.res.ActDataModel;
import com.mw.sdk.bean.res.GPExchangeRes;
import com.mw.sdk.bean.res.RedDotRes;
import com.mw.sdk.bean.res.ToggleResult;
import com.mw.sdk.login.model.response.SLoginResponse;

import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MWApiService {

    @GET("sdk/config/{gameCode}/v1/version.json")
    Observable<ResponseBody> getBaseConfig(@Path("gameCode")String gameCode, @Query("v") String timeStamp);

    @GET("sdk/config/areaCode/areaInfo.json")
    Observable<ResponseBody> getPhoneInfo(@Query("v") String timeStamp);

    @POST("sdk/api/market/switch")
    @FormUrlEncoded
    Observable<ToggleResult> marketSwitch(@FieldMap Map<String, String> paramMap);

    //https://cdn-download.tthplay.com/sdk/config/vnmxw/v1/market.json
//    @GET("sdk/config/{gameCode}/v1/market.json")
//    Observable<List<ActData>> getMarketData(@Path("gameCode") String gameCode, @Query("timestamp") String timestamp);

    @POST("sdk/api/market/articles")
    @FormUrlEncoded
    Observable<ActDataModel> getMarketData(@FieldMap Map<String, String> paramMap);

//    https://cdn-download.kodaduck.com/sdk/config/tgfm/v1/floatButton.json
//    @GET("sdk/config/{gameCode}/v1/floatButton.json")
//    Observable<ResponseBody> getFloatConfigData(@Path("gameCode") String gameCode);

//    https://platform.kodaduck.com/sdk/api/floatBtn/initMenu
//    @POST("sdk/api/floatBtn/initMenu")
//    @FormUrlEncoded
//    Observable<ResponseBody> getFloatMenus(@FieldMap Map<String, String> paramMap);

    @POST("sdk/api/floatButton/v2/initMenu")
    @FormUrlEncoded
    Observable<ResponseBody> getFloatMenus_V2(@FieldMap Map<String, String> paramMap);

//    @POST("sdk/api/floatBtn/changePassword")
    @POST("api/pwd/changePassword")
    @FormUrlEncoded
    Observable<SLoginResponse> changePassword(@FieldMap Map<String, String> paramMap);

    @POST("api/cancel/account")
    @FormUrlEncoded
    Observable<SLoginResponse> deleteAccout(@FieldMap Map<String, String> paramMap);


//    https://platform.kodaduck.com/sdk/api/floatBtn/getFloatBtnRedDot  获取红点
//    https://platform.kodaduck.com/sdk/api/floatBtn/deleteFloatBtnRedDot  删除红点
    @POST("sdk/api/floatBtn/getFloatBtnRedDot")
    @FormUrlEncoded
    Observable<RedDotRes> getFloatBtnRedDot(@FieldMap Map<String, String> paramMap);


    @POST("sdk/api/floatBtn/deleteFloatBtnRedDot")
    @FormUrlEncoded
    Observable<RedDotRes> deleteFloatBtnRedDot(@FieldMap Map<String, String> paramMap);

    @POST("sdk/pre/event")
    @FormUrlEncoded
    Observable<ResponseBody> send_event_pre(@FieldMap Map<String, String> paramMap);
    @POST("api/google/payment")
    @FormUrlEncoded
    Observable<GPExchangeRes> googlePayment(@FieldMap Map<String, String> paramMap);
}
