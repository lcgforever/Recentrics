package com.citrix.recentrics.network;

import com.citrix.recentrics.database.ContactInfo;
import com.citrix.recentrics.database.DatabaseManager;
import com.citrix.recentrics.model.ContactModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GetContactInfoTask {

    private static GetContactInfoTask instance;
    private RestAdapter restAdapter;
    private RestServiceApi restServiceApi;

    public static GetContactInfoTask getInstance() {
        if (instance == null) {
            instance = new GetContactInfoTask();
        }
        return instance;
    }

    private GetContactInfoTask() {
        restAdapter = new RestAdapter.Builder().setEndpoint(new RestEndPoint()).build();
        restServiceApi = restAdapter.create(RestServiceApi.class);
    }

    public void getContactInfoListByKey(int key) {
        restServiceApi.getContactInfoListByKey(key, new GetContactInfoCallback());
    }

    private class GetContactInfoCallback implements Callback<JsonObject> {

        @Override
        public void success(JsonObject jsonObject, Response response) {
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<ContactInfo>>() {
            }.getType();
            List<ContactInfo> contactInfoList = gson.fromJson(jsonObject, listType);

            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.persistList(contactInfoList);
            ContactModel contactModel = ContactModel.getInstance();
            contactModel.updateAllContactInfo(contactInfoList);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    }
}
