package com.developerdreamteam.jia.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(formatter);
    }

    public static LocalDateTime TimeLimit() {
        return LocalDateTime.now().plusHours(24);
    }

}