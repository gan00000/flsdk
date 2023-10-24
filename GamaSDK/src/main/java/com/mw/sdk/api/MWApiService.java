package com.mw.sdk.api;

import com.core.base.bean.BaseResponseModel;
import com.mw.sdk.bean.res.ActData;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MWApiService {

    @POST("sdk/api/market/switch")
    @FormUrlEncoded
    Observable<BaseResponseModel> marketSwitch(@FieldMap Map<String, String> paramMap);

    //https://cdn-download.tthplay.com/sdk/config/vnmxw/v1/market.json
    @GET("sdk/config/{gameCode}/v1/market.json")
    Observable<List<ActData>> getMarketData(@Path("gameCode") String gameCode, @Query("timestamp") String timestamp);


}
