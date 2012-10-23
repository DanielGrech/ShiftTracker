package com.dgsd.android.ShiftTracker.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.DateUtils;
import android.text.format.Time;
import com.dgsd.android.ShiftTracker.WeekFragment;

import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class WeekPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = WeekPagerAdapter.class.getSimpleName();

    private static int YEAR_IN_WEEKS = 52;

    private Map<Integer, String> posToTitleMap = new HashMap<Integer, String>();
    private Context mContext;

    private int mCenterJulianDay = -1;

    private Time mTime;
    private Formatter mFormatter;
    private StringBuilder mStringBuilder;

    public WeekPagerAdapter(Context context, FragmentManager fm, int weekContainingJulianDay) {
        super(fm);
        mContext = context;
        mTime = new Time();
        mStringBuilder = new StringBuilder();
        mFormatter = new Formatter(mStringBuilder);

        //TODO: This assumes week starts on Monday. Should be configurable
        mCenterJulianDay = adjustJulianDay(0, weekContainingJulianDay);
    }

    @Override
	public CharSequence getPageTitle(final int pos) {
		if(posToTitleMap.containsKey(pos)) {
			return posToTitleMap.get(pos);
		} else {
			String title = getTitleForPosition(pos);
			posToTitleMap.put(pos, title);
			return title;
		}
	}

    @Override
    public int getCount() {
        //This week, plus 1 year either side
        return (YEAR_IN_WEEKS * 2) + 1;
    }

    public int getCenterPosition() {
        return YEAR_IN_WEEKS + 1;
    }

    @Override
    public Fragment getItem(int pos) {
        return WeekFragment.newInstance(getJulianDayForPosition(pos));
    }

    protected String getTitleForPosition(int pos) {
        mTime.setJulianDay(getJulianDayForPosition(pos));
        if(mTime.weekDay != 0) {
            while(mTime.weekDay != 0) {
                mTime.monthDay--;
                mTime.normalize(true);
            }
        }

        long startMillis = mTime.toMillis(true);
        long endMillis = startMillis + (1000 * 60 * 60 * 24 * 7);
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_ALL;
        mStringBuilder.setLength(0);

        return DateUtils.formatDateRange(mContext, mFormatter, startMillis, endMillis, flags, mTime.timezone).toString();
    }

    public int getJulianDayForPosition(int pos) {
        int middle = (getCount() / 2) + 1;
        if(pos > middle) {
            return mCenterJulianDay + (7 * (pos - middle));
        } else if(pos < middle) {
            return mCenterJulianDay - (7 * (middle - pos));
        } else {
            return mCenterJulianDay;
        }
    }

    public int getPositionForJulianDay(int julianDay) {
        //TODO: This assumes week starts on Monday. Should be configurable
        final int jd = adjustJulianDay(0, julianDay);
        if(mCenterJulianDay == jd) {
            return getCenterPosition();
        } else if(mCenterJulianDay < jd) {
            return getCenterPosition() + (jd - mCenterJulianDay);
        } else {
            return getCenterPosition() - (mCenterJulianDay - jd);
        }
    }

    private int adjustJulianDay(int startWeekday, int jd) {
        mTime.setJulianDay(jd);
        if(mTime.weekDay == startWeekday) {
            //Great, no adjustment needed
            return jd;
        } else if(mTime.weekDay > startWeekday) {
            return jd - (mTime.weekDay - startWeekday);
        } else {
            return jd + (startWeekday - mTime.weekDay);
        }
    }

}
