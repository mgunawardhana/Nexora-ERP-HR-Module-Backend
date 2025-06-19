package com.nexora.backend.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for date and time operations.
 */
public class DateTimeUtils {

    /**
     * Formats a given Date object into a string with the pattern "yyyy-MM-dd HH:mm:ss".
     *
     * @param date the Date object to be formatted
     * @return a formatted date string in the pattern "yyyy-MM-dd HH:mm:ss"
     */
    public static String format(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}