package com.dgsd.android.shifttracker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;

import com.dgsd.android.shifttracker.fragment.MonthFragment;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.shifttracker.model.MonthYear;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("deprecation")
public class MonthPagerAdapter extends BrowsablePagerAdapter {

    private static final int PAGE_COUNT = 25;

    private final Time time;

    public MonthPagerAdapter(FragmentManager fm) {
        super(fm);
        time = new Time();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public int getStartingPosition() {
        return PAGE_COUNT / 2;
    }

    @Override
    public Fragment getItem(final int position) {
        return MonthFragment.newInstance(getMonthYear(position));
    }

    @Override
    public Date getSelectedDateForItem(int position) {
        final MonthFragment frag = getFragmentAt(position, MonthFragment.class);
        return frag == null ? null : frag.getPresenter().getSelectedDate();
    }

    @Override
    public int getPositionForDate(Date date) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);

        for (int i = 0, count = getCount(); i < count; i++) {
            final MonthYear monthYear = getMonthYear(i);
            if (monthYear.year() == year && monthYear.month() == month) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected String getTitleForPosition(final int position) {
        final MonthYear ym = getMonthYear(position);

        time.set(1, ym.month(), ym.year());
        return time.format("%b %Y");
    }

    @Override
    public String getStatisticsSummary(int position) {
        final MonthFragment frag = getFragmentAt(position, MonthFragment.class);
        if (frag == null) {
            return "";
        } else {
            return frag.getPresenter().getStatisticsSummary();
        }
    }

    private MonthYear getMonthYear(final int position) {
        final int centerPosition = PAGE_COUNT / 2;

        int difference = centerPosition - position;

        time.setToNow();
        time.monthDay = 1;
        time.month -= difference;
        time.normalize(false);

        return MonthYear.builder().month(time.month).year(time.year).create();
    }
}
