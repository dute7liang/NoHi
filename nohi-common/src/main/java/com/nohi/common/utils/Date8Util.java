package com.nohi.common.utils;

import java.time.*;
import java.util.Date;

public class Date8Util {

    /**
     * 时间戳转时间 、 毫秒
     * @param timestamp 13位时间戳 毫秒级别
     */
    public LocalDateTime parseTimeOfMillis(long timestamp){
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 时间戳转时间 、 秒
     * @param timestamp 10位时间戳 秒级别
     */
    public LocalDateTime parseTimeOfSecond(long timestamp){
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8));
    }


    private static final ZoneOffset DEFAULT_ZONE = ZoneOffset.of("+8");

    /**
     * localDateTime转date
     * @param localDateTime
     * @return
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime){
        if(localDateTime == null){
            return null;
        }
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }


    /**
     * 获取秒级的时间戳
     * @param localDateTime 时间
     * @return 返回10位时间戳
     */
    public static Long timestampSecond(LocalDateTime localDateTime){
        return timestampSecond(localDateTime,DEFAULT_ZONE);
    }

    /**
     * 获取秒级的时间戳
     * @param localDateTime 时间
     * @param zoneOffset 时区
     * @return 返回10位时间戳
     */
    public static Long timestampSecond(LocalDateTime localDateTime,ZoneOffset zoneOffset){
        if(localDateTime == null){
            return null;
        }
        return localDateTime.toInstant(zoneOffset).getEpochSecond();
    }


    /**
     * 获取毫秒级的时间戳
     * @param localDateTime 时间
     * @return 返回13位时间戳
     */
    public static Long timestampMillis(LocalDateTime localDateTime){
        return timestampMillis(localDateTime,DEFAULT_ZONE);
    }

    /**
     * 获取毫秒级的时间戳
     * @param localDateTime 时间
     * @param zoneOffset 时区
     * @return 返回13位时间戳
     */
    public static Long timestampMillis(LocalDateTime localDateTime,ZoneOffset zoneOffset){
        if(localDateTime == null){
            return null;
        }
        return localDateTime.toInstant(zoneOffset).toEpochMilli();
    }
}
