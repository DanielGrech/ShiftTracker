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

public class GoToFragment extends SherlockDialogFragment {
    private static final String KEY_MIN_DATE = "_min";
    private static final String KEY_MAX_DATE = "_max";

    private long mMinDate = Long.MIN_VALUE;
    private long mMaxDate = Long.MAX_VALUE;

    private OnDateSelectedListener mOnDateSelectedListener;

    public static GoToFragment newInstance(long min, long max) {
        GoToFragment frag = new GoToFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_MIN_DATE, min);
        args.putLong(KEY_MAX_DATE, max);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if(args != null) {
            mMinDate = args.getLong(KEY_MIN_DATE, mMinDate);
            mMaxDate = args.getLong(KEY_MAX_DATE, mMaxDate);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Time time = new Time();
        time.setToNow();

        DatePickerDialog dpd = new DatePickerDialog(getActivity(), null, time.year, time.month, time.monthDay);
        dpd.setTitle("Go to date..");

        final DatePicker dp = dpd.getDatePicker();
        dp.setMaxDate(mMaxDate);
        dp.setMinDate(mMinDate);
        if(Api.isMin(Api.HONEYCOMB))
            dp.setCalendarViewShown(false);

        dpd.setButton(DialogInterface.BUTTON_POSITIVE, "Go to date", new DialogInterface.OnClickListener() {
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