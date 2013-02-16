package com.dgsd.android.ShiftTracker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created: 16/02/13 10:36 PM
 */
public class MonthFragment extends SherlockFragment {
    public static final String TAG = MonthFragment.class.getSimpleName();

    private static final String KEY_MONTH = "_month";
    private static final String KEY_YEAR = "_year";

    private int mMonth;
    private int mYear;

    private CalendarPickerView mCalendar;

    public static MonthFragment newInstance(int month, int year) {
        MonthFragment frag = new MonthFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_MONTH, month);
        args.putInt(KEY_YEAR, year);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            mMonth = args.getInt(KEY_MONTH);
            mYear = args.getInt(KEY_YEAR);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_month, container, false);

        mCalendar = (CalendarPickerView) v.findViewById(R.id.calendar);

        Calendar min = Calendar.getInstance();
        min.set(mYear, mMonth, 1);

        Calendar selected = Calendar.getInstance();
        selected.set(mYear, mMonth, 1);

        Calendar max = Calendar.getInstance();
        max.set(mYear, mMonth, 1);
        max.add(Calendar.MONTH, 1);

        System.err.print("================== " + min.getTime() + " --- " + max.getTime());

        mCalendar.init(selected.getTime(), min.getTime(), max.getTime());

        return v;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public int getSelectedJulianDay() {
        return TimeUtils.getJulianDay(mCalendar.getSelectedDate().getTime());
    }
}
