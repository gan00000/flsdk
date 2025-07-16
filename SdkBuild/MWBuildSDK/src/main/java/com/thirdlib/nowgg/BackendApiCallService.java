package com.thirdlib.nowgg;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BackendApiCallService {
        @POST("/accounts/oauth2/v1/verify-token")
        Call< TokenVerifyResponse > verifyIdToken(@Body TokenVerifyRequest authRequest);
    }