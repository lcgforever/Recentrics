package com.citrix.recentrics.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "contacts")
public class ContactInfo {

    public static final String FIELD_EMAIL = "field_email";
    public static final String FIELD_NAME = "field_name";
    public static final String FIELD_TITLE = "field_title";
    public static final String FIELD_OFFICE_PHONE_NUMBER = "field_office_phone_number";
    public static final String FIELD_OFFICE_CITY = "field_office_city";
    public static final String FIELD_OFFICE_COUNTRY = "field_office_country";
    public static final String FIELD_NUMBER_OF_EMAILS = "field_number_of_emails";
    public static final String FIELD_LATEST_EMAIL_TIME = "field_latest_email_time";
    public static final String FIELD_LATEST_EMAIL_CONTENT = "field_latest_email_content";

    @DatabaseField(id = true, canBeNull = false, columnName = FIELD_EMAIL)
    private String email;
    @DatabaseField(canBeNull = false, columnName = FIELD_NAME)
    private String name;
    @DatabaseField(canBeNull = true, columnName = FIELD_TITLE)
    private String title;
    @DatabaseField(canBeNull = true, columnName = FIELD_OFFICE_PHONE_NUMBER)
    private String officePhoneNumber;
    @DatabaseField(canBeNull = true, columnName = FIELD_OFFICE_CITY)
    private String officeCity;
    @DatabaseField(canBeNull = true, columnName = FIELD_OFFICE_COUNTRY)
    private String officeCountry;
    @DatabaseField(canBeNull = true, columnName = FIELD_NUMBER_OF_EMAILS)
    private String numberOfEmails;
    @DatabaseField(canBeNull = true, columnName = FIELD_LATEST_EMAIL_TIME)
    private String latestEmailTime;
    @DatabaseField(canBeNull = true, columnName = FIELD_LATEST_EMAIL_CONTENT)
    private String latestEmailContent;

    // Used for database construction
    public ContactInfo() {

    }

    public ContactInfo(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOfficePhoneNumber() {
        return officePhoneNumber;
    }

    public void setOfficePhoneNumber(String officePhoneNumber) {
        this.officePhoneNumber = officePhoneNumber;
    }

    public String getOfficeCity() {
        return officeCity;
    }

    public void setOfficeCity(String officeCity) {
        this.officeCity = officeCity;
    }

    public String getOfficeCountry() {
        return officeCountry;
    }

    public void setOfficeCountry(String officeCountry) {
        this.officeCountry = officeCountry;
    }

    public String getNumberOfEmails() {
        return numberOfEmails;
    }

    public void setNumberOfEmails(String numberOfEmails) {
        this.numberOfEmails = numberOfEmails;
    }

    public String getLatestEmailTime() {
        return latestEmailTime;
    }

    public void setLatestEmailTime(String latestEmailTime) {
        this.latestEmailTime = latestEmailTime;
    }

    public String getLatestEmailContent() {
        return latestEmailContent;
    }

    public void setLatestEmailContent(String latestEmailContent) {
        this.latestEmailContent = latestEmailContent;
    }
}
