package com.androidcollider.vysotsky.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Пархоменко on 31.12.2014.
 */
public class NumberConverter {


    public static Long dateToLongConverter(String dateStr){
        if (!dateStr.equals("null")) {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = null;
            try {
                d = f.parse(dateStr);
                long milliseconds = d.getTime();
                return milliseconds;
            } catch (ParseException e) {
                e.printStackTrace();
                return new Long(0);
            }
        } else {
            return new Long(0);
        }

    }

    public static String longToDateConverter(long dateLong){
        Date date = new Date(dateLong);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = f.format(date);
        return dateStr;
    }

}