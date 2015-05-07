package com.citrix.recentrics.util;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getMeetingFormattedTime(String time) {
        DateTimeFormatter fromFormat = ISODateTimeFormat.dateTime();
        Date date = fromFormat.parseDateTime(time).toDate();
        SimpleDateFormat toFormat = new SimpleDateFormat("MM/dd/yyyy hh:mma");
        return toFormat.format(date);
    }
}

