package com.citrix.recentrics.network;

import retrofit.Endpoint;

public class RestEndPoint implements Endpoint {

    private static final String END_POINT_NAME = "LIVE";

    @Override
    public String getUrl() {
        return "http://10.39.62.168:8080";
    }

    @Override
    public String getName() {
        return END_POINT_NAME;
    }
}
