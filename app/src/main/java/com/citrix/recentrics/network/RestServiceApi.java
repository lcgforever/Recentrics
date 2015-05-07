package com.citrix.recentrics.network;

import com.google.gson.JsonArray;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface RestServiceApi {

    @GET("/contacts/{userKey}")
    void getContactInfoListByKey(@Path("userKey") int userKey,
                                 Callback<JsonArray> callback);

    @GET("/meetings/{userKey}")
    void getMeetingInfoListByKey(@Path("userKey") int userKey,
                                 Callback<JsonArray> callback);

    @GET("/contact/{userKey}")
    void getContactInfoByKeyAndEmail(@Path(value = "userKey", encode = false) String userKey,
                                     Callback<JsonArray> callback);
}
