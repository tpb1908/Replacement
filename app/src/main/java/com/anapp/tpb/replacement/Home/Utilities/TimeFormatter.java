package com.anapp.tpb.replacement.Home.Utilities;

/**
 * Created by theo on 21/04/16.
 */
public class TimeFormatter {
    
    private TimeFormatter() {}
    
    public static String format(int time, String separator) {
        if (time < 1000) {
            return (Integer.toString(time).substring(0, 1) + separator + Integer.toString(time).substring(1));
        } else {
            return (Integer.toString(time).substring(0, 2) + separator + Integer.toString(time).substring(2));
        }
    }

    public static String format(int time) {
        return format(time, ":");
    }

    public static String format(int hour, int minute, String separator) {
        return format((hour*100)+minute, separator);
    }

    public static String format(int hour, int minute) {
        return format((hour*100)+minute);
    }

    public static String getHour(int time) {
        if(time < 1000) {
            return Integer.toString(time).substring(0, 1);
        } else {
            return Integer.toString(time).substring(0, 2);
        }
    }

    public static String getMinute(int time) {
        if(time < 1000) {
            return Integer.toString(time).substring(1);
        } else {
            return Integer.toString(time).substring(2);
        }
    }


    
}
