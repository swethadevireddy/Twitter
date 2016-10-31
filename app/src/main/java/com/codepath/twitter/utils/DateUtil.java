package com.codepath.twitter.utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sdevired on 10/28/16.
 * DateUtil for formatting dates
 */
public class DateUtil {

    public static String getRelativeTime(Date createdDate) {
        //convert to date ("created_at": "Wed Mar 03 19:37:35 +0000 2010",)
        String relativeTime = DateUtils.getRelativeTimeSpanString(createdDate.getTime(), System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            Log.d("DEBUG", " relativeTime: " + relativeTime);
            String[] tokens = relativeTime.split("\\s+");
            if (tokens.length > 1) {
                return String.format("%s%s", tokens[0], tokens[1].charAt(0));
            } else {
                return tokens[0];
            }
    }



    public static Date formatTwitterDate(String timeStamp) {
        //convert to date ("created_at": "Wed Mar 03 19:37:35 +0000 2010",)
        //define pattern for twitter created_at
        SimpleDateFormat s = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        Date createdDate = null;
        try {
            createdDate = s.parse(timeStamp);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return createdDate;

    }
}