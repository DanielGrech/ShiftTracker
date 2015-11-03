package com.dgsd.android.shifttracker.util;

import android.text.format.Time;

import com.dgsd.shifttracker.model.TimePeriod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class TimeUtils {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy");

    private static final DateFormat ABBREVIATED_DATE_FORMAT = new SimpleDateFormat("d MMM");

    public static final long WEEK_IN_MILLIS
            = TimeUnit.DAYS.toMillis(7) - TimeUnit.SECONDS.toMillis(1);

    private TimeUtils() {
        // No Instances
    }

    public static int getMonthDay(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static String formatAsDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    public static String formatAsAbbreviatedDate(Date date) {
        return ABBREVIATED_DATE_FORMAT.format(date);
    }

    public static Time toTime(Date date) {
        final Time time = new Time();
        time.set(date.getTime());

        return time;
    }

    public static Time toTime(long millis) {
        final Time time = new Time();
        time.set(millis);
        return time;
    }

    public static Time toTime(int hour, int minute) {
        final Time newStartTime = new Time();
        newStartTime.setToNow();
        newStartTime.hour = hour;
        newStartTime.minute = minute;
        newStartTime.second = 0;
        return newStartTime;
    }

    public static long toDateTime(Date date, Time time) {
        final Time dateAsTime = toTime(date);

        final Time retval = new Time();
        retval.set(0, time.minute, time.hour,
                dateAsTime.monthDay, dateAsTime.month, dateAsTime.year);
        retval.normalize(false);
        return retval.toMillis(false);
    }

    public static Time millisSinceMidnightToTime(long millisSinceMidnight) {
        final Time time = new Time();
        time.setToNow();

        final int hour = (int) TimeUnit.MILLISECONDS.toHours(millisSinceMidnight);
        final int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(millisSinceMidnight) % TimeUnit.HOURS.toMinutes(1));

        time.hour = hour;
        time.minute = minutes;
        time.second = 0;

        time.normalize(false);

        return time;
    }

    public static long getMillisSinceMidnight(int hour, int minute) {
        return TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
    }

    public static int getJulianDay(Time t) {
        if (t == null) {
            return 0;
        } else {
            return Time.getJulianDay(t.toMillis(true), t.gmtoff);
        }
    }

    public static int getJulianDay(long millis) {
        return getJulianDay(toTime(millis));
    }

    public static String formatDuration(long millis) {
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        String text = null;
        if (minutes < 60) {
            if (minutes > 0 && minutes < 10)
                text = "0" + minutes;
            else
                text = "" + minutes;

            return text + "m";
        }

        text = (int) Math.floor(minutes / 60) + "h";

        final long mins = minutes % 60;

        if (mins > 0 && mins < 10)
            text += " 0" + mins + "m";
        else if (mins >= 10)
            text += " " + mins + "m";

        return text;
    }

    public static int daysBetween(Date start, Date end) {
        final long previousMillisDiff = end.getTime() - start.getTime();
        return (int) TimeUnit.MILLISECONDS.toDays(previousMillisDiff);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        final Time time1 = toTime(date1);
        final Time time2 = toTime(date2);

        return (time1.monthDay == time2.monthDay) &&
                (time1.month == time2.month) &&
                (time1.year == time2.year);
    }

    /**
     * {@link Time} class has weekdays beginning with {@link Time#SUNDAY} == 0.
     *
     * Need to adjust for our input, which interprets 0 == MONDAY
     */
    public static int convertTimeWeekDay(int weekDay) {
        int proposedWeekDay = weekDay - 1;
        if (proposedWeekDay < 0) {
            proposedWeekDay = 6;// Sunday
        }

        return proposedWeekDay;
    }

    public static TimePeriod getWeekTimePeriod(int julianDay, int weekStartDay) {
        final Time time = new Time();
        time.setJulianDay(julianDay);

        int adjustedWd = convertTimeWeekDay(time.weekDay);
        if (adjustedWd != weekStartDay) {
            while (adjustedWd != weekStartDay) {
                time.monthDay--;
                time.normalize(true);

                adjustedWd = convertTimeWeekDay(time.weekDay);
            }
        }

        final long startMillis = time.toMillis(true);
        return TimePeriod.builder()
                .startMillis(startMillis)
                .endMillis(startMillis + TimeUtils.WEEK_IN_MILLIS)
                .create();
    }

}