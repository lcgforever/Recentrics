package com.citrix.recentrics.database;

import android.content.Context;

import com.citrix.recentrics.data.ContactInfo;
import com.citrix.recentrics.data.MeetingInfo;
import com.citrix.recentrics.data.MeetingToContact;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Dao<ContactInfo, String> contactInfoDao;
    private Dao<MeetingInfo, Integer> meetingInfoDao;
    private Dao<MeetingToContact, Integer> meetingToContactDao;

    public static void init(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    private DatabaseManager(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        contactInfoDao = databaseHelper.getContactInfoDao();
        meetingInfoDao = databaseHelper.getMeetingInfoDao();
        meetingToContactDao = databaseHelper.getMeetingToContactDao();
    }

    public ContactInfo getContactInfoByEmail(String email) {
        try {
            return contactInfoDao.queryForId(email);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<ContactInfo> getAllContactInfos() {
        try {
            return contactInfoDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public List<MeetingInfo> getAllMeetingInfos() {
        try {
            List<MeetingInfo> meetingInfoList = meetingInfoDao.queryForAll();
            for (MeetingInfo meetingInfo : meetingInfoList) {
                meetingInfo.setAttendeeList(findContactsForMeeting(meetingInfo));
            }
            return meetingInfoList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<MeetingToContact> getAllMeetingToContacts() {
        try {
            return meetingToContactDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void persistContactInfo(ContactInfo contactInfo) {
        try {
            contactInfoDao.createOrUpdate(contactInfo);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void persistContactInfoList(List<ContactInfo> contactInfoList) {
        try {
            for (ContactInfo contactInfo : contactInfoList) {
                contactInfoDao.createOrUpdate(contactInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void persistMeetingInfoList(List<MeetingInfo> meetingInfoList) {
        try {
            for (MeetingInfo meetingInfo : meetingInfoList) {
                meetingInfoDao.createOrUpdate(meetingInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void persistMeetingToContactList(List<MeetingToContact> meetingToContactList) {
        try {
            for (MeetingToContact meetingToContact : meetingToContactList) {
                meetingToContactDao.createOrUpdate(meetingToContact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private List<ContactInfo> findContactsForMeeting(MeetingInfo meetingInfo) {
        try {
            PreparedQuery<ContactInfo> contactsForMeetingQuery = makeContactsForMeetingQuery();
            contactsForMeetingQuery.setArgumentHolderValue(0, meetingInfo);
            return contactInfoDao.query(contactsForMeetingQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private PreparedQuery<ContactInfo> makeContactsForMeetingQuery() {
        QueryBuilder<MeetingToContact, Integer> meetingToContactQB = meetingToContactDao.queryBuilder();
        meetingToContactQB.selectColumns(MeetingToContact.FIELD_CONTACT_INFO);
        SelectArg meetingSelectArg = new SelectArg();
        try {
            meetingToContactQB.where().eq(MeetingToContact.FIELD_MEETING_INFO, meetingSelectArg);
            QueryBuilder<ContactInfo, String> contactInfoQB = contactInfoDao.queryBuilder();
            contactInfoQB.where().in(ContactInfo.FIELD_EMAIL, meetingToContactQB);
            return contactInfoQB.prepare();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
