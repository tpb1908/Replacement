package com.tpb.timetable.Utils;

import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by theo on 21/04/16.
 */
public class FormattingUtils {
    
    private FormattingUtils() {}

    public static int hmToInt(int hour, int minute) {
        return (hour * 100) + minute;
    }

    /**
     * Formats a numeric time in the format HHMM
     * @param time The time to format
     * @param separator The separator to place between hours and minutes
     * @return The formatted time string
     */
    public static String format(int time, String separator) {
        if(time < 0 || time > 2400) {
            throw new IllegalArgumentException("Time must be between 0 and 2400. " + time + " is invalid");
        }
        if (time < 1000) {
            if(time == 0 || time == 2400) {
                return "0:00";
            } else {
                return (Integer.toString(time).substring(0, 1) + separator + Integer.toString(time).substring(1));
            }
        } else {
            return (Integer.toString(time).substring(0, 2) + separator + Integer.toString(time).substring(2));
        }
    }

    /**
     * Forwards to format(int time, String separator) with separator of ':'
     * @param time The time to format
     * @return The formatted time string
     */
    public static String format(int time) {
        return format(time, ":");
    }

    /**
     * Forwards to format(int time, String separator) by converting hour and minute to four digit value
     * @param hour The hour of the time, 0 to 23
     * @param minute Minute of the time, 0 to 59
     * @param separator The separator string to place between the hour and minute
     * @return The formatted time string
     */
    /**
     * @param hour The hour of the time
     * @param minute The minute of the time
     * @param separator The string to place between the hour and minute
     * @return A formatted string of the time
     */
    public static String format(int hour, int minute, String separator) {
        return format((hour*100)+minute, separator);
    }

    /**
     * @param hour The hour of the time
     * @param minute The minute of the time
     * @return A formatted string of the time hour:minute
     */
    public static String format(int hour, int minute) {
        return format(hmToInt(hour, minute));
    }

    /**
     * @param time An integer time in the format hhmm
     * @return The hour of the time, as a string
     */
    public static String getHour(int time) {
        if(time < 1000) {
            return Integer.toString(time).substring(0, 1);
        } else {
            return Integer.toString(time).substring(0, 2);
        }
    }

    /**
     * @param time An integer time in the format hhmm
     * @return The minute of the time, as a string
     */
    public static String getMinute(int time) {
        if(time < 1000) {
            return Integer.toString(time).substring(1);
        } else {
            return Integer.toString(time).substring(2);
        }
    }

    public static float getPercentageComplete(int time, int start, int end) {
        final int timeHour = (int) Math.floor((float) time/100);
        final int startHour = (int) Math.floor((float)start/100);
        final int endHour = (int) Math.floor((float)end/100);
        final int timeMinute = time - (timeHour*100);
        final int startMinute = start - (startHour*100);
        final int endMinute = end - (endHour*100);

        return ((float) ((timeHour*60+timeMinute)-(startHour*60+startMinute))) /
                ((float)((endHour*60+endMinute)-(startHour*60+startMinute)));

    }

    /**
     * @param day The day, of a week, month, year, etc
     * @return The correct suffix for the value
     */
    public static String getSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String dateToString(Date d) {
        final Calendar  cal = Calendar.getInstance();
        cal.setTime(d);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        String format = DateFormat.format("EEEE", d) + " " +
                DateFormat.format("MMMM",d) + " " +
                day + getSuffix(day);
        if(d.getYear() != new Date().getYear()) {
            format += " " + (1900+d.getYear());
        }
        return format;
    }

    /**
     * Calculates how many lines are needed to fit a given string into a text
     * -view of a given width
     * @param textView A reference to the text view being fitted
     * @param text The text to be fitted into the textview
     * @return The number of lines need to fit the text in the textview
     */
    public static int numLinesForTextView(TextView textView, String text) {
        final String[] lines = text.split("\n");
        int numLines = lines.length-1;
        for(String s : lines) {
            if(textView.getPaint().breakText(s,  0, s.length(),
                    true, textView.getWidth(), null) > s.length()) {
                numLines ++;
            }
        }
        return numLines;
    }


}
