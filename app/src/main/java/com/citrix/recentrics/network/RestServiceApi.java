package com.citrix.recentrics.network;

import com.google.gson.JsonArray;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface RestServiceApi {

    @GET("/contacts/{userKey}")
    void getContactInfoListByKey(@Path("userKey") int userKey,
                                 Callback<JsonArray> callback);
}
