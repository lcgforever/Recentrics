package com.citrix.recentrics.data;

import com.j256.ormlite.field.DatabaseField;

import java.util.List;

public class MeetingInfo {

    public static final String FIELD_ID = "field_id";
    public static final String FIELD_SUBJECT = "field_subject";
    public static final String FIELD_START_TIME = "field_start_time";
    public static final String FIELD_END_TIME = "field_end_time";
    public static final String FIELD_LOCATION = "field_location";
    public static final String FIELD_BODY = "field_body";
    public static final String FIELD_ORGANIZER = "field_organizer";
    public static final String FIELD_NUM_OF_ATTENDEES = "field_num_of_attendees";
    public static final String FIELD_WEATHER_TEMPERATURE = "field_weather_temperature";
    public static final String FIELD_WEATHER_UNIT = "field_weather_unit";
    public static final String FIELD_WEATHER_CONDITION = "field_weather_condition";
    public static final String FIELD_WEATHER_HUMIDITY = "field_weather_humidity";

    @DatabaseField(generatedId = true, canBeNull = false, columnName = FIELD_ID)
    private int id;

    @DatabaseField(canBeNull = false, columnName = FIELD_SUBJECT)
    private String subject;

    @DatabaseField(canBeNull = false, columnName = FIELD_START_TIME)
    private String startTime;

    @DatabaseField(canBeNull = false, columnName = FIELD_END_TIME)
    private String endTime;

    @DatabaseField(canBeNull = false, columnName = FIELD_LOCATION)
    private String location;

    @DatabaseField(canBeNull = false, columnName = FIELD_BODY)
    private String body;

    @DatabaseField(canBeNull = true, columnName = FIELD_WEATHER_TEMPERATURE)
    private String weatherTemperature;

    @DatabaseField(canBeNull = true, columnName = FIELD_WEATHER_UNIT)
    private String weatherUnit;

    @DatabaseField(canBeNull = true, columnName = FIELD_WEATHER_CONDITION)
    private String weatherCondition;

    @DatabaseField(canBeNull = true, columnName = FIELD_WEATHER_HUMIDITY)
    private String weatherHumidity;

    @DatabaseField(foreign = true, canBeNull = true, columnName = FIELD_ORGANIZER)
    private ContactInfo organizer;

    @DatabaseField(canBeNull = true, columnName = FIELD_NUM_OF_ATTENDEES)
    private int numOfAttendees;

    @DatabaseField(persisted = false)
    private List<ContactInfo> attendeeList;

    // Used for database construction
    public MeetingInfo() {

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getWeatherTemperature() {
        return weatherTemperature;
    }

    public void setWeatherTemperature(String weatherTemperature) {
        this.weatherTemperature = weatherTemperature;
    }

    public String getWeatherUnit() {
        return weatherUnit;
    }

    public void setWeatherUnit(String weatherUnit) {
        this.weatherUnit = weatherUnit;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getWeatherHumidity() {
        return weatherHumidity;
    }

    public void setWeatherHumidity(String weatherHumidity) {
        this.weatherHumidity = weatherHumidity;
    }

    public ContactInfo getOrganizer() {
        return organizer;
    }

    public void setOrganizer(ContactInfo organizer) {
        this.organizer = organizer;
    }

    public int getNumOfAttendees() {
        return numOfAttendees;
    }

    public void setNumOfAttendees(int numOfAttendees) {
        this.numOfAttendees = numOfAttendees;
    }

    public List<ContactInfo> getAttendeeList() {
        return attendeeList;
    }

    public void setAttendeeList(List<ContactInfo> attendeeList) {
        this.attendeeList = attendeeList;
    }
}
