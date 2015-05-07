package com.citrix.recentrics.network;

import android.util.Log;

import com.citrix.recentrics.activity.BaseApplication;
import com.citrix.recentrics.data.ContactInfo;
import com.citrix.recentrics.database.DatabaseManager;
import com.citrix.recentrics.event.ContactInfoUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.ContactModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    private static final int TIME_OUT_IN_MILLIS = 10 * 1000;

    private static GetContactInfoTask instance;
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
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(new RestEndPoint())
                .setClient(new CustomClient())
                .build();
        restServiceApi = restAdapter.create(RestServiceApi.class);
        databaseManager = DatabaseManager.getInstance();
        contactModel = ContactModel.getInstance();
    }

    public void getContactInfoByKeyAndEmail(int key, String email) {
        String path = key + "/" + email;
        restServiceApi.getContactInfoByKeyAndEmail(path, new GetContactInfoCallback());
    }

    public void getContactInfoListByKey(int key) {
        restServiceApi.getContactInfoListByKey(key, new GetContactInfoListCallback());
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
            if (jsonArray == null || jsonArray.size() == 0 || jsonArray.get(0) == null) {
                return;
            }
            ContactInfo contactInfo = new ContactInfo();
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            contactInfo.setEmail(jsonObject.get("email").getAsString());
            contactInfo.setName(jsonObject.get("name").getAsString());
            if (jsonObject.has("title")) {
                contactInfo.setTitle(jsonObject.get("title").getAsString());
            }
            if (jsonObject.has("officePhone")) {
                contactInfo.setOfficePhoneNumber(jsonObject.get("officePhone").getAsString());
            }
            if (jsonObject.has("officeCity")) {
                contactInfo.setOfficeCity(jsonObject.get("officeCity").getAsString());
            }
            if (jsonObject.has("officeCountry")) {
                contactInfo.setOfficeCountry(jsonObject.get("officeCountry").getAsString());
            }
            contactInfo.setInfoComplete(true);
            databaseManager.persistContactInfo(contactInfo);
        }

        @Override
        public void failure(RetrofitError error) {

        }
    }

    private class GetContactInfoListCallback implements Callback<JsonArray> {

        @Override
        public void success(JsonArray jsonArray, Response response) {
            Gson gson = new GsonBuilder().create();
            Type listType = new TypeToken<List<ContactInfo>>() {
            }.getType();
            List<ContactInfo> contactInfoList = gson.fromJson(jsonArray, listType);

            databaseManager.persistContactInfoList(contactInfoList);
            contactModel.updateAllContactInfos(contactInfoList);

            BaseApplication.getBus().post(new ContactInfoUpdatedEvent());
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("findme", "Get error with REST call " + error);
            List<ContactInfo> contactInfoList = databaseManager.getAllContactInfos();
            contactModel.updateAllContactInfos(contactInfoList);

            BaseApplication.getBus().post(new ContactInfoUpdatedEvent());
            BaseApplication.getBus().post(new TimeOutEvent());
        }
    }
}
