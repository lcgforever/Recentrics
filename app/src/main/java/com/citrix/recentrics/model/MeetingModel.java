package com.citrix.recentrics.model;

import com.citrix.recentrics.data.MeetingInfo;

import java.util.ArrayList;
import java.util.List;

public class MeetingModel {

    private static MeetingModel instance;
    private List<MeetingInfo> meetingInfoList;

    public static MeetingModel getInstance() {
        if (instance == null) {
            instance = new MeetingModel();
        }
        return instance;
    }

    private MeetingModel() {
        meetingInfoList = new ArrayList<>();
    }

    public void addOrUpdateMeetingInfo(MeetingInfo meetingInfo) {
        if (!meetingInfoList.contains(meetingInfo)) {
            meetingInfoList.add(meetingInfo);
        }
    }

    public void updateAllMeetingInfos(List<MeetingInfo> meetingInfoList) {
        this.meetingInfoList.clear();
        for (MeetingInfo meetingInfo : meetingInfoList) {
            this.meetingInfoList.add(meetingInfo);
        }
    }

    public List<MeetingInfo> getMeetingInfoList() {
        return meetingInfoList;
    }
}
