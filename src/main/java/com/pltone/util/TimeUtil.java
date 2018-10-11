package com.pltone.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具类
 *
 * @author chenlong
 * @version 1.0 2018-10-10
 */
public class TimeUtil {
    /**
     * 获取指定时间对应的毫秒数
     *
     * @param time "HH:mm:ss"
     * @return 指定时间对应的毫秒数
     */
    public static long getTimeMillis(String time) {
        try {
            DateFormat datetimeFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = datetimeFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    /**
     * 获取指定时间对应的毫秒数
     *
     * @param hour   时
     * @param minute 分
     * @param second 秒
     * @return 指定时间对应的毫秒数
     */
    public static long getTimeMillis(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取指定时间对应的毫秒数，JDK8新的日期时间API
     *
     * @param hour   时
     * @param minute 分
     * @param second 秒
     * @return 指定时间对应的毫秒数
     */
    public static long getTimeMillisByJdk8(int hour, int minute, int second) {
        return getTimeMillisByJdk8(LocalTime.of(hour, minute, second));
    }

    /**
     * 获取指定时间对应的毫秒数，JDK8新的日期时间API
     *
     * @param localTime {@link LocalTime} 指定的时间
     * @return 指定时间对应的毫秒数
     */
    public static long getTimeMillisByJdk8(LocalTime localTime) {
        return LocalDateTime.of(LocalDate.now(), localTime).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * 获取当天0点的毫秒值
     *
     * @return 当天0点的毫秒值
     */
    public static long getZeroMillisByJdk8() {
        return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

}
