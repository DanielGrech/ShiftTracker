package com.dgsd.android.shifttracker.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.dgsd.android.shifttracker.fragment.WeekFragment;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.model.TimePeriod;

import java.util.Date;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class WeekPagerAdapter extends BrowsablePagerAdapter {

    private static final int WEEKS_IN_YEAR = 52;
    private static final int DAYS_IN_WEEK = 7;

    private int centerJulianDay = -1;

    private final int weekStartDay;

    private final Time time;

    private final Formatter formatter;

    private final StringBuilder stringBuilder;

    private final Context context;

    public WeekPagerAdapter(Context context, FragmentManager fm,
                            int weekStartDay, int weekContainingJulianDay) {
        super(fm);
        this.context = context;
        this.time = new Time();
        this.weekStartDay = weekStartDay;
        this.stringBuilder = new StringBuilder();
        this.formatter = new Formatter(stringBuilder);

        centerJulianDay = adjustJulianDay(weekStartDay, weekContainingJulianDay);
    }

    @Override
    protected String getTitleForPosition(int pos) {
        final TimePeriod timePeriod = getTimePeriodForPosition(pos);
        stringBuilder.setLength(0);

        return DateUtils.formatDateRange(
                context,
                formatter,
                timePeriod.startMillis(),
                timePeriod.endMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_ALL,
                time.timezone
        ).toString();
    }

    @Override
    public Date getSelectedDateForItem(int position) {
        if (position == getStartingPosition()) {
            return new Date();
        } else {
            return new Date(getTimePeriodForPosition(position).startMillis());
        }
    }

    @Override
    public int getPositionForDate(Date date) {
        final long millis = TimeUtils.toTime(date).toMillis(false);

        for (int i = 0, count = getCount(); i < count; i++) {
            if (getTimePeriodForPosition(i).contains(millis)) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Fragment getItem(int position) {
        return WeekFragment.newInstance(getTimePeriodForPosition(position));
    }

    @Override
    public int getCount() {
        //This week, plus 1 year either side
        return 1 + (2 * WEEKS_IN_YEAR);
    }

    @Override
    public int getStartingPosition() {
        return WEEKS_IN_YEAR + 1;
    }

    public int getJulianDayForPosition(int pos) {
        int middle = (getCount() / 2) + 1;
        if (pos > middle) {
            return centerJulianDay + (DAYS_IN_WEEK * (pos - middle));
        } else if (pos < middle) {
            return centerJulianDay - (DAYS_IN_WEEK * (middle - pos));
        } else {
            return centerJulianDay;
        }
    }

    private int adjustJulianDay(int startWeekday, int jd) {
        time.setJulianDay(jd);

        int adjustedWd = convertTimeWeekDay(time.weekDay);
        if (adjustedWd == startWeekday) {
            //Great, no adjustment needed
            return jd;
        } else {
            while (adjustedWd != startWeekday) {
                time.setJulianDay(jd--);
                adjustedWd = convertTimeWeekDay(time.weekDay);
            }
            return TimeUtils.getJulianDay(time);
        }
    }

    private TimePeriod getTimePeriodForPosition(int pos) {
        time.setJulianDay(getJulianDayForPosition(pos));
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
                .endMillis(startMillis + TimeUnit.DAYS.toMillis(DAYS_IN_WEEK))
                .create();
    }

    /**
     * {@link Time} class has weekdays beginning with {@link Time#SUNDAY} == 0.
     *
     * Need to adjust for our input, which interprets 0 == MONDAY
     */
    private static final int convertTimeWeekDay(int weekDay) {
        int proposedWeekDay = weekDay - 1;
        if (proposedWeekDay < 0) {
            proposedWeekDay = 6;// Sunday
        }

        return proposedWeekDay;
    }
}
