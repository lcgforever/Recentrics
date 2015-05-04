package com.citrix.recentrics.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {

    private static DatabaseManager instance;
    private Dao<ContactInfo, String> contactInfoDao;

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
    }

    public List<ContactInfo> getAllContactInfos() {
        try {
            List<ContactInfo> contactInfos = contactInfoDao.queryForAll();
            return contactInfos;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public ContactInfo getContactInfoById(String id) {
        try {
            ContactInfo contactInfo = contactInfoDao.queryForId(id);
            return contactInfo;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void persist(ContactInfo contactInfo) {
        try {
            contactInfoDao.createOrUpdate(contactInfo);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void persistList(List<ContactInfo> contactInfoList) {
        try {
            for (ContactInfo contactInfo : contactInfoList) {
                contactInfoDao.createOrUpdate(contactInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
