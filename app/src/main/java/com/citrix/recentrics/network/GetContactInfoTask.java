package com.citrix.recentrics.network;

import android.util.Log;

import com.citrix.recentrics.activity.BaseApplication;
import com.citrix.recentrics.database.ContactInfo;
import com.citrix.recentrics.database.DatabaseManager;
import com.citrix.recentrics.event.DataUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.ContactModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;

public class GetContactInfoTask {

    private static final int TIME_OUT_IN_MILLIS = 5 * 1000;

    private static GetContactInfoTask instance;
    private RestAdapter restAdapter;
    private RestServiceApi restServiceApi;
    private DatabaseManager databaseManager;
    private ContactModel contactModel;

    public static GetContactInfoTask getInstance() {
        if (instance == null) {
            instance = new GetContactInfoTask();
        }
        return instance;
    }

    private GetContactInfoTask() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(new RestEndPoint())
                .setClient(new CustomClient())
                .build();
        restServiceApi = restAdapter.create(RestServiceApi.class);
        databaseManager = DatabaseManager.getInstance();
        contactModel = ContactModel.getInstance();
    }

    public void getContactInfoListByKey(int key) {
        restServiceApi.getContactInfoListByKey(key, new GetContactInfoCallback());
    }


    private class CustomClient extends UrlConnectionClient {

        @Override
        protected HttpURLConnection openConnection(Request request) throws IOException {
            HttpURLConnection connection = super.openConnection(request);
            connection.setConnectTimeout(TIME_OUT_IN_MILLIS);
            connection.setReadTimeout(TIME_OUT_IN_MILLIS);
            return connection;
        }
    }

    private class GetContactInfoCallback implements Callback<JsonArray> {

        @Override
        public void success(JsonArray jsonArray, Response response) {
            Log.e("findme", "Get contact info successfully!");
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<ContactInfo>>() {
            }.getType();
            List<ContactInfo> contactInfoList = gson.fromJson(jsonArray, listType);

            databaseManager.persistList(contactInfoList);
            contactModel.updateAllContactInfos(contactInfoList);

            BaseApplication.getBus().post(new DataUpdatedEvent());
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("findme", "Get error with REST call " + error);
            List<ContactInfo> contactInfoList = databaseManager.getAllContactInfos();
            contactModel.updateAllContactInfos(contactInfoList);

            BaseApplication.getBus().post(new DataUpdatedEvent());
            BaseApplication.getBus().post(new TimeOutEvent());
        }
    }
}
