package com.citrix.recentrics.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.citrix.recentrics.data.ContactInfo;
import com.citrix.recentrics.data.MeetingInfo;
import com.citrix.recentrics.data.MeetingToContact;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "recentrics.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<ContactInfo, String> contactInfoDao;
    private Dao<MeetingInfo, Integer> meetingInfoDao;
    private Dao<MeetingToContact, Integer> meetingToContactDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contactInfoDao = null;
        meetingInfoDao = null;
        meetingToContactDao = null;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create the tables that will store
     * your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ContactInfo.class);
            TableUtils.createTable(connectionSource, MeetingInfo.class);
            TableUtils.createTable(connectionSource, MeetingToContact.class);
        } catch (SQLException ex) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust the various data to match the
     * new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ContactInfo.class, true);
            TableUtils.createTable(connectionSource, ContactInfo.class);
        } catch (SQLException ex) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop database", ex);
            throw new RuntimeException(ex);
        }
    }

    public Dao<ContactInfo, String> getContactInfoDao() {
        if (contactInfoDao == null) {
            try {
                contactInfoDao = getDao(ContactInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return contactInfoDao;
    }

    public Dao<MeetingInfo, Integer> getMeetingInfoDao() {
        if (meetingInfoDao == null) {
            try {
                meetingInfoDao = getDao(MeetingInfo.class);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return meetingInfoDao;
    }

    public Dao<MeetingToContact, Integer> getMeetingToContactDao() {
        if (meetingToContactDao == null) {
            try {
                meetingToContactDao = getDao(MeetingToContact.class);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return meetingToContactDao;
    }
}
