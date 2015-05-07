package com.citrix.recentrics.data;

import com.j256.ormlite.field.DatabaseField;

public class MeetingToContact {

    public static final String FIELD_ID = "field_id";
    public final static String FIELD_MEETING_INFO = "field_meeting_info";
    public final static String FIELD_CONTACT_INFO = "field_contact_info";

    @DatabaseField(generatedId = true, canBeNull = false, columnName = FIELD_ID)
    private int id;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FIELD_MEETING_INFO)
    private MeetingInfo meetingInfo;

    @DatabaseField(foreign = true, canBeNull = false, columnName = FIELD_CONTACT_INFO)
    private ContactInfo contactInfo;

    public MeetingToContact() {

    }

    public MeetingToContact(MeetingInfo meetingInfo, ContactInfo contactInfo) {
        this.meetingInfo = meetingInfo;
        this.contactInfo = contactInfo;
    }

    public MeetingInfo getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(MeetingInfo meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }
}
