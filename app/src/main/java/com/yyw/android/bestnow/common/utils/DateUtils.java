package com.yyw.android.bestnow.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yangyongwen on 16/12/2.
 */

public class DateUtils {
    public static final SimpleDateFormat FORMAT_DAY = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat FORMAT_DAY_HOUR_MIN_SEC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
    }

    public static String toDisplayFormat(long time) {
        int timeInSec = (int) (time / 1000);
        int sec = timeInSec % 60;
        int timeInMin = timeInSec / 60;
        int min = timeInMin % 60;
        int hour = timeInMin / 60;
        String text = "";
        if (hour == 0) {
            if (min == 0) {
                text = sec + "秒";
            } else {
                text = text + min + "分钟" + sec + "秒";
            }
        } else {
            text = text + hour + "小时" + min + "分钟";
        }
        return text;
    }

    public static String formatTime(long time) {
        return FORMAT_DAY_HOUR_MIN_SEC.format(time);
    }

    public static int daysBetween(Date date1, Date date2) {
        long time1 = dayStartTime(date1);
        long time2 = dayStartTime(date2);
        return (int) ((time2 - time1) / (1000 * 60 * 60 * 24));
    }

    private static long dayStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static Date offsetDays(Date date, int daysOffset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, daysOffset);
        return new Date(calendar.getTimeInMillis());
    }

    public static int getHour(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }


}
