package Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static final SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat readableFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String getDatabaseFormatTime() {
        return databaseFormat.format(Calendar.getInstance().getTime());
    }

    public static String getReadableTime() {
        return readableFormat.format(Calendar.getInstance().getTime());
    }

    public static Date getDateWithAddTime(long time) {
        Date date = new Date(getTimeStamp() + time);
        return date;
    }

    public static Long getTimeStamp() {
        return Calendar.getInstance().getTime().getTime();
    }
}
