package com.cqwu.jwy.mulberrydoc.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtil
{
    private DateUtil()
    {
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static Date nowDatetime()
    {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String formatDatetime(Date date)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        LocalDateTime dateTime = date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
        return dateTime.format(formatter);
    }

    public static Long timestamp(Date date)
    {
        return date.getTime();
    }

    public static Long timestamp(LocalDateTime dateTime)
    {
        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
