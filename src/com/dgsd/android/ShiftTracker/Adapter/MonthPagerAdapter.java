/*
 * Copyright 2013 Daniel Grech
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.dgsd.android.ShiftTracker.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.SparseArray;
import com.dgsd.android.ShiftTracker.Const;
import com.dgsd.android.ShiftTracker.Fragment.MonthFragment;
import com.dgsd.android.ShiftTracker.Fragment.WeekFragment;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;

import java.lang.ref.WeakReference;
import java.util.*;

public class MonthPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = MonthPagerAdapter.class.getSimpleName();

    private Map<Integer, String> posToTitleMap = new HashMap<Integer, String>();
    private Context mContext;

    private static final int COUNT = 25;

    private int mCenterJulianDay = -1;

    private Time mTime;

    private SparseArray<WeakReference<MonthFragment>> mPosToFragRef = new SparseArray<WeakReference<MonthFragment>>(COUNT);

    public MonthPagerAdapter(Context context, FragmentManager fm, int monthContainingJulianDay) {
        super(fm);
        mContext = context;
        mTime = new Time();
        mCenterJulianDay = monthContainingJulianDay;
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
        return COUNT;
    }

    public int getCenterPosition() {
        return COUNT / 2;
    }

    @Override
    public Fragment getItem(int pos) {
        final YearAndMonth ym = getYearAndMonthForPosition(pos);
        final MonthFragment frag = MonthFragment.newInstance(ym.month, ym.year);

        mPosToFragRef.put(pos, new WeakReference<MonthFragment>(frag));
        return frag;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mPosToFragRef.clear();
    }

    protected String getTitleForPosition(int pos) {
        final YearAndMonth ym = getYearAndMonthForPosition(pos);

        mTime.set(1, ym.month, ym.year);
        return mTime.format("%b %Y");
    }

    public void selectJulianDay(int pos, int jd) {
        WeakReference<MonthFragment> fragRes = mPosToFragRef.get(pos);
        MonthFragment frag = fragRes == null ? null : fragRes.get();

        if(frag != null) {
            frag.selectJulianDay(jd);
        }
    }

    public int getPositionForJulianDay(int julianDay) {
        mTime.setJulianDay(mCenterJulianDay);

        Calendar center = Calendar.getInstance();
        center.set(mTime.year, mTime.month, 1);

        mTime.setJulianDay(julianDay);

        Calendar query = Calendar.getInstance();
        query.set(mTime.year, mTime.month, 1);

        int diff = getMonthsDifference(center, query);
        return getCenterPosition() + diff - 1;
    }

    public int getSelectedJulianDay(int pos) {
        int jd = mCenterJulianDay;

        WeakReference<MonthFragment> fragRes = mPosToFragRef.get(pos);
        MonthFragment frag = fragRes.get();

        if(frag != null)
            jd = frag.getSelectedJulianDay();

        return jd;
    }

    private YearAndMonth getYearAndMonthForPosition(int pos) {
        final int middle = getCenterPosition();
        mTime.setJulianDay(mCenterJulianDay);

        YearAndMonth ym = new YearAndMonth(mTime.year, mTime.month);
        if(pos != middle) {
            Calendar cal = Calendar.getInstance();
            cal.set(ym.year, ym.month, 1);
            cal.add(Calendar.MONTH, pos - middle);

            ym.month = cal.get(Calendar.MONTH);
            ym.year = cal.get(Calendar.YEAR);
        }

        return ym;
    }

    public static final int getMonthsDifference(Calendar first, Calendar second) {
        int m1 = first.get(Calendar.YEAR) * 12 + first.get(Calendar.MONTH);
        int m2 = second.get(Calendar.YEAR) * 12 + second.get(Calendar.MONTH);
        return m2 - m1 + 1;
    }

    private static class YearAndMonth {
        public int year;
        public int month;

        public YearAndMonth(int year, int month) {
            this.year = year;
            this.month = month;
        }
    }
}
