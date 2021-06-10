package Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat readableFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public static String getDatabaseFormatTime() {
        return databaseFormat.format(Calendar.getInstance().getTime());
    }

    public static String getReadableTime() {
        return readableFormat.format(Calendar.getInstance().getTime());
    }

    public static String getReadableTime(Date date) {
        return readableFormat.format(date.getTime());
    }

    public static Date getDate(String d) {
        Date date = null;
        try {
            date = dateFormat.parse(d);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return date;
    }

    public static Date getToday() {
        Date date = Calendar.getInstance().getTime();
        date.setHours(0);
        date.setSeconds(0);
        date.setMinutes(0);
        return date;
    }

    public static void standardDate(Date date) {
        date.setHours(0);
        date.setSeconds(0);
        date.setMinutes(0);
    }
    public static Date getDateWithAddTime(long time) {
        Date date = new Date(getTimeStamp() + time);
        return date;
    }

    public static Long getTimeStamp() {
        return Calendar.getInstance().getTime().getTime();
    }
}
