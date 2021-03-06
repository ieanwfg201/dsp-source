package com.kritter.kritterui.api.utils.TimeManipulation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeManipulator {
    public static String convertDate(long timeinmilis, String format, String tz){
        if(timeinmilis == 0 || format == null || tz == null ){
            return null;
        }
        
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        DateFormat dfm = new SimpleDateFormat(format);
        return dfm.format(new Date(timeinmilis));
    }
    public static String convertDate(Date date, String format, String tz){
        if(date == null || format == null || tz == null ){
            return null;
        }
        
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        DateFormat dfm = new SimpleDateFormat(format);
        return dfm.format(date);
    }

}
