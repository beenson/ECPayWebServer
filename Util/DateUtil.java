package Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
    private static final SimpleDateFormat databaseFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final SimpleDateFormat readableFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String getDatabaseFormatTime() {
        return databaseFormat.format(Calendar.getInstance().getTime());
    }

    public static  String getReadableTime() {
        return readableFormat.format(Calendar.getInstance().getTime());
    }
}
