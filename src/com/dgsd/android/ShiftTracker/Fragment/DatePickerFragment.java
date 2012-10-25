package com.dgsd.android.ShiftTracker.Fragment;

import android.R;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.DatePicker;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.dgsd.android.ShiftTracker.Util.Api;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;

public class DatePickerFragment extends SherlockDialogFragment {
    private static final String KEY_DATE = "_date";
    private static final String KEY_MIN_DATE = "_min";
    private static final String KEY_MAX_DATE = "_max";
    private static final String KEY_TITLE = "_title";
    private static final String KEY_POS_BTN = "_pos_btn";

    private int mDate = -1;
    private long mMinDate = Long.MIN_VALUE;
    private long mMaxDate = Long.MAX_VALUE;
    private String mTitle;
    private String mPositiveBtnText;

    private OnDateSelectedListener mOnDateSelectedListener;

    public static DatePickerFragment newInstance(String title, String postiveButton, long min, long max) {
        return newInstance(title, postiveButton, min, max, -1);
    }

    public static DatePickerFragment newInstance(String title, String postiveButton, long min, long max, int date) {
        DatePickerFragment frag = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_DATE, date);
        args.putLong(KEY_MIN_DATE, min);
        args.putLong(KEY_MAX_DATE, max);
        args.putString(KEY_TITLE, title);
        args.putString(KEY_POS_BTN, postiveButton);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if(args != null) {
            mDate = args.getInt(KEY_DATE, mDate);
            mMinDate = args.getLong(KEY_MIN_DATE, mMinDate);
            mMaxDate = args.getLong(KEY_MAX_DATE, mMaxDate);
            mTitle = args.getString(KEY_TITLE);
            mPositiveBtnText = args.getString(KEY_POS_BTN);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Time time = new Time();

        if(mDate == -1)
            time.setToNow();
        else
            time.setJulianDay(mDate);

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), null, time.year, time.month, time.monthDay);
        dpd.setTitle(mTitle);

        final DatePicker dp = dpd.getDatePicker();
        dp.setMaxDate(mMaxDate);
        dp.setMinDate(mMinDate);
        if(Api.isMin(Api.HONEYCOMB))
            dp.setCalendarViewShown(false);

        dpd.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mOnDateSelectedListener != null) {
                    time.year = dp.getYear();
                    time.month = dp.getMonth();
                    time.monthDay = dp.getDayOfMonth();
                    time.normalize(true);

                    mOnDateSelectedListener.onDateSelected(TimeUtils.getJulianDay(time));
                }

                dismiss();
            }
        });

        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return dpd;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.mOnDateSelectedListener = listener;
    }

    public static interface OnDateSelectedListener {
        public void onDateSelected(int julianDay);
    }

}