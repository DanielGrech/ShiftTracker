package com.dgsd.android.shifttracker.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;
import android.util.SparseArray;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public abstract class BrowsablePagerAdapter extends FragmentStatePagerAdapter {

    private final SparseArray<String> posToTitleArray;

    private Field fragmentsField;

    protected abstract String getTitleForPosition(int position);

    public abstract Date getSelectedDateForItem(int position);

    public abstract int getPositionForDate(Date date);

    public abstract int getStartingPosition();

    public BrowsablePagerAdapter(FragmentManager fm) {
        super(fm);
        posToTitleArray = new SparseArray<>(getCount());
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        String title = posToTitleArray.get(position);
        if (TextUtils.isEmpty(title)) {
            title = getTitleForPosition(position);
            posToTitleArray.put(position, title);
        }

        return title;
    }

    protected Fragment getFragmentAt(int position) {
        try {
            if (fragmentsField == null) {
                fragmentsField = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
                fragmentsField.setAccessible(true);
            }

            final Object variable = fragmentsField.get(this);
            if (variable != null && List.class.isInstance(variable)) {
                final List<?> list = (List<?>) variable;
                if (position < list.size()) {
                    final Object obj = list.get(position);
                    if (obj instanceof Fragment) {
                        return (Fragment) obj;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Error getting fragments from manager");
        }

        return null;
    }
}
