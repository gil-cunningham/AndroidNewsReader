package com.example.gilcunningham.androidnewsreader.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by gil.cunningham on 9/9/2016.
 * Utility for formatting a string with one date format to another
 */
public class DateFormatHelper {

    public static String formatDate(String date, String fromFormat, String toFormat)
    {
        return formatDate(date, fromFormat, toFormat, false);
    }

    public static String formatDate(String date, String fromFormat, String toFormat, boolean amPmToLower )
    {
        try {
            SimpleDateFormat inSdf = new SimpleDateFormat(fromFormat);
            Date d = inSdf.parse(date);
            SimpleDateFormat outSdf = new SimpleDateFormat(toFormat);

            if (amPmToLower) {
                return formatAmPmToLower(outSdf.format(d));
            }
            return outSdf.format(d);
        }
        catch (Exception e) {
            return date;
        }
    }

    public static String formatAmPmToLower(String dateTime)
    {
        return dateTime.replace("AM", "am").replace("PM", "pm");
    }
}
