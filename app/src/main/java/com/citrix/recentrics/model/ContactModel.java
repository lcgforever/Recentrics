package com.citrix.recentrics.model;

import com.citrix.recentrics.data.ContactInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactModel {

    private static ContactModel instance;
    private Map<String, ContactInfo> contactInfoMap;

    public static ContactModel getInstance() {
        if (instance == null) {
            instance = new ContactModel();
        }
        return instance;
    }

    private ContactModel() {
        contactInfoMap = new HashMap<>();
    }

    public void addOrUpdateContactInfo(ContactInfo contactInfo) {
        contactInfoMap.put(contactInfo.getEmail(), contactInfo);
    }

    public void updateAllContactInfos(List<ContactInfo> contactInfoList) {
        for (ContactInfo contactInfo : contactInfoList) {
            contactInfoMap.put(contactInfo.getEmail(), contactInfo);
        }
    }

    public List<ContactInfo> getContactInfoList() {
        List<ContactInfo> contactInfoList = new ArrayList<>();
        for (ContactInfo contactInfo : contactInfoMap.values()) {
            contactInfoList.add(contactInfo);
        }
        return contactInfoList;
    }

    public void removeContactInfo(ContactInfo contactInfo) {
        contactInfoMap.remove(contactInfo.getEmail());
    }
}
