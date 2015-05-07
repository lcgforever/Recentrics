package com.citrix.recentrics.network;

import android.util.Log;

import com.citrix.recentrics.activity.BaseApplication;
import com.citrix.recentrics.activity.MainActivity;
import com.citrix.recentrics.data.ContactInfo;
import com.citrix.recentrics.data.MeetingInfo;
import com.citrix.recentrics.data.MeetingToContact;
import com.citrix.recentrics.database.DatabaseManager;
import com.citrix.recentrics.event.MeetingInfoUpdatedEvent;
import com.citrix.recentrics.event.TimeOutEvent;
import com.citrix.recentrics.model.MeetingModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.client.UrlConnectionClient;

public class GetMeetingInfoTask {

    private static final int TIME_OUT_IN_MILLIS = 10 * 1000;

    private static GetMeetingInfoTask instance;
    private RestServiceApi restServiceApi;
    private DatabaseManager databaseManager;
    private MeetingModel meetingModel;

    public static GetMeetingInfoTask getInstance() {
        if (instance == null) {
            instance = new GetMeetingInfoTask();
        }
        return instance;
    }

    private GetMeetingInfoTask() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(new RestEndPoint())
                .setClient(new CustomClient())
                .build();
        restServiceApi = restAdapter.create(RestServiceApi.class);
        databaseManager = DatabaseManager.getInstance();
        meetingModel = MeetingModel.getInstance();
    }

    public void getMeetingInfoListByKey(int key) {
        restServiceApi.getMeetingInfoListByKey(key, new GetMeetingInfoCallback());
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

    private class GetMeetingInfoCallback implements Callback<JsonArray> {

        @Override
        public void success(JsonArray jsonArray, Response response) {
            List<MeetingInfo> meetingInfoList = new ArrayList<>();
            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                MeetingInfo meetingInfo = new MeetingInfo();
                meetingInfo.setSubject(jsonObject.get("subject").getAsString());
                meetingInfo.setLocation(jsonObject.get("loc").getAsString());
                meetingInfo.setBody(jsonObject.get("body").getAsString());
                meetingInfo.setNumOfAttendees(jsonObject.get("numOfAttendees").getAsInt());

                JsonObject timeObject = jsonObject.get("time").getAsJsonObject();
                meetingInfo.setStartTime(timeObject.get("startTime").getAsString());
                meetingInfo.setEndTime(timeObject.get("endTime").getAsString());

//                JsonObject weatherObject = jsonObject.get("weather").getAsJsonObject();
//                meetingInfo.setWeatherCondition(weatherObject.get("weather").getAsString());
//                meetingInfo.setWeatherTemperature(weatherObject.get("temperature").getAsString());
//                meetingInfo.setWeatherUnit(weatherObject.get("unit").getAsString());
//                meetingInfo.setWeatherHumidity(weatherObject.get("humidity").getAsString());
                meetingInfo.setWeatherCondition("sunny");
                meetingInfo.setWeatherTemperature("80");
                meetingInfo.setWeatherUnit("F");
                meetingInfo.setWeatherHumidity("50");

                JsonObject organizerObject = jsonObject.get("organizer").getAsJsonObject();
                String organizerEmail = organizerObject.get("email").getAsString();
                ContactInfo organizerInfo = databaseManager.getContactInfoByEmail(organizerEmail);
                if (organizerInfo == null) {
                    String organizerName = organizerObject.get("name").getAsString();
                    organizerInfo = new ContactInfo(organizerEmail, organizerName);
                    organizerInfo.setInfoComplete(false);
                    GetContactInfoTask.getInstance().getContactInfoByKeyAndEmail(MainActivity.USER_KEY, organizerEmail);
                }
                meetingInfo.setOrganizer(organizerInfo);

                JsonArray attendeeArray = jsonObject.get("attendees").getAsJsonArray();
                List<MeetingToContact> meetingToContactList = new ArrayList<>();
                List<ContactInfo> attendeeList = new ArrayList<>();
                for (JsonElement attendeeElement : attendeeArray) {
                    JsonObject attendee = attendeeElement.getAsJsonObject();
                    String attendeeEmail = attendee.get("email").getAsString();
                    ContactInfo attendeeInfo = databaseManager.getContactInfoByEmail(attendeeEmail);
                    if (attendeeInfo != null) {
                        MeetingToContact meetingToContact = new MeetingToContact(meetingInfo, attendeeInfo);
                        meetingToContactList.add(meetingToContact);
                    } else {
                        String attendeeName = attendee.get("name").getAsString();
                        attendeeInfo = new ContactInfo(attendeeEmail, attendeeName);
                        attendeeInfo.setInfoComplete(false);
                        GetContactInfoTask.getInstance().getContactInfoByKeyAndEmail(MainActivity.USER_KEY, attendeeEmail);
                    }
                    attendeeList.add(attendeeInfo);
                }

                databaseManager.persistMeetingToContactList(meetingToContactList);
                meetingInfo.setAttendeeList(attendeeList);
                meetingInfoList.add(meetingInfo);
            }

            databaseManager.persistMeetingInfoList(meetingInfoList);
            meetingModel.updateAllMeetingInfos(meetingInfoList);
            BaseApplication.getBus().post(new MeetingInfoUpdatedEvent());
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("findme", "Get error with REST call " + error);
            List<MeetingInfo> meetingInfoList = databaseManager.getAllMeetingInfos();
            meetingModel.updateAllMeetingInfos(meetingInfoList);

            BaseApplication.getBus().post(new MeetingInfoUpdatedEvent());
            BaseApplication.getBus().post(new TimeOutEvent());
        }
    }
}
