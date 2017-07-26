package com.ckt.criminal_note.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by D22434 on 2017/7/24.
 */

public class TimeUtil {

    public static String TimeFormat(Date date) {
//        SimpleDateFormat dateFormat =
//                new SimpleDateFormat("yyyy年MM月dd日EE");

//        Locale locale = new Locale("en","US");
        DateFormat fullDateFormat = DateFormat.getDateInstance(DateFormat.FULL);

        return fullDateFormat.format(date);
    }
}
