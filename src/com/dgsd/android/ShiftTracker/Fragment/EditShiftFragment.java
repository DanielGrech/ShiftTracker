package com.dgsd.android.ShiftTracker.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.dgsd.android.ShiftTracker.Data.DbField;
import com.dgsd.android.ShiftTracker.Data.Provider;
import com.dgsd.android.ShiftTracker.Model.Shift;
import com.dgsd.android.ShiftTracker.R;
import com.dgsd.android.ShiftTracker.Util.Prefs;
import com.dgsd.android.ShiftTracker.Util.TimeUtils;
import com.dgsd.android.ShiftTracker.View.StatefulEditText;

public class EditShiftFragment extends SherlockFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener,
        DatePickerFragment.OnDateSelectedListener,
        TimePickerFragment.OnTimeSelectedListener{
    private static final String KEY_SHIFT = "_shift";
    private static final String KEY_JULIAN_DAY = "_julian_day";

    private static final int LOADER_ID_SHIFT = 0x01;

    private Shift mInitialShift;
    private int mInitialJulianDay;
    private boolean mHasLoadedShift = false;

    private StatefulEditText mName;
    private StatefulEditText mNotes;
    private StatefulEditText mPayRate;
    private StatefulEditText mUnpaidBreak;
    private TextView mDate;
    private TextView mStartTime;
    private TextView mEndTime;

    private DatePickerFragment mDateDialog;
    private TimePickerFragment mTimeDialog;

    private LastTimeSelected mLastTimeSelected;

    private static enum LastTimeSelected {START, END};

    public static EditShiftFragment newInstance(int julianDay) {
        EditShiftFragment frag = new EditShiftFragment();

        Bundle args = new Bundle();
        args.putInt(KEY_JULIAN_DAY, julianDay);
        frag.setArguments(args);

        return frag;
    }

    public static EditShiftFragment newInstance(Shift shift) {
        EditShiftFragment frag = new EditShiftFragment();

        if(shift != null) {
            Bundle args = new Bundle();
            args.putParcelable(KEY_SHIFT, shift);
            frag.setArguments(args);
        }

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInitialJulianDay = -1;
        if(getArguments() != null) {
            mInitialShift = getArguments().getParcelable(KEY_SHIFT);
            mInitialJulianDay = getArguments().getInt(KEY_JULIAN_DAY, mInitialJulianDay);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_shift, container, false);

        mName = (StatefulEditText) v.findViewById(R.id.name);
        mNotes = (StatefulEditText) v.findViewById(R.id.notes);
        mPayRate = (StatefulEditText) v.findViewById(R.id.pay_rate);
        mUnpaidBreak = (StatefulEditText) v.findViewById(R.id.unpaid_break);
        mDate = (TextView) v.findViewById(R.id.date);
        mStartTime = (TextView) v.findViewById(R.id.start_time);
        mEndTime = (TextView) v.findViewById(R.id.end_time);

        mDate.setOnClickListener(this);
        mStartTime.setOnClickListener(this);
        mEndTime.setOnClickListener(this);

        mName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mName.setError(null);
            }
        });

        mUnpaidBreak.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mUnpaidBreak.setError(null);
            }
        });

        prepopulate();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mInitialShift != null && mInitialShift.id != -1)
            getLoaderManager().initLoader(LOADER_ID_SHIFT, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return new CursorLoader(getActivity(),
                                Provider.SHIFTS_URI,
                                null,
                                DbField.ID + "=?",
                                new String[]{String.valueOf(mInitialShift == null ? -1 : mInitialShift.id)},
                                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case LOADER_ID_SHIFT:
                if(mHasLoadedShift)
                    return;
                else
                    mHasLoadedShift = true;

                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void prepopulate() {
        if(mInitialShift != null) {
            mName.setText(mInitialShift.name);
            mNotes.setText(mInitialShift.note);
            if(mInitialShift.julianDay >= 0)
                onDateSelected(mInitialShift.julianDay);

            if(mInitialShift.startTime >= 0)
                setStartTime(mInitialShift.startTime);

            if(mInitialShift.endTime >= 0)
                setEndTime(mInitialShift.endTime);

            if(mInitialShift.breakDuration >= 0)
                mUnpaidBreak.setText(String.valueOf(mInitialShift.breakDuration));

            if(mInitialShift.payRate >= 0)
                mPayRate.setText(String.valueOf(mInitialShift.payRate));
        } else {
            //No initial shift, just set up our date/time values
            onDateSelected(mInitialJulianDay < 0 ? TimeUtils.getCurrentJulianDay() : mInitialJulianDay);

            //Default 9 - 5 shift
            Time t = new Time();
            t.setToNow();
            t.hour = 9;
            t.minute = 0;
            t.second = 0;
            t.normalize(true);


            final Prefs p = Prefs.getInstance(getActivity());
            setStartTime(p.get(getString(R.string.settings_key_default_start_time), t.toMillis(true)));

            t.hour = 17;
            t.normalize(true);

            setEndTime(p.get(getString(R.string.settings_key_default_end_time), t.toMillis(true)));

            mUnpaidBreak.setText(p.get(getString(R.string.settings_key_default_break_duration), null));
            mPayRate.setText(p.get(getString(R.string.settings_key_default_pay_rate), null));
        }
    }

    private void setStartTime(long time) {
        mLastTimeSelected = LastTimeSelected.START;
        onTimeSelected(time);
    }

    private void setEndTime(long time) {
        mLastTimeSelected = LastTimeSelected.END;
        onTimeSelected(time);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.date :  {
                if(mDateDialog != null && mDateDialog.isResumed())
                    return; //We're showing already!

                final int centerJd = TimeUtils.getCurrentJulianDay();
                final int count = 104 * 7; // 2 years

                final Time time = new Time();
                time.setJulianDay(centerJd - (count / 2));
                final long min = time.toMillis(true);

                time.setJulianDay(centerJd + (count / 2));
                final long max = time.toMillis(true);

                final Integer date = (Integer) mDate.getTag();

                mDateDialog = DatePickerFragment.newInstance("Date of shift", "Set date", min, max, date == null ? -1 : date);
                mDateDialog.setOnDateSelectedListener(this);
                mDateDialog.show(getSherlockActivity().getSupportFragmentManager(), null);

                break;
            }
            case R.id.start_time: {
                mLastTimeSelected = LastTimeSelected.START;
                if(mTimeDialog != null && mTimeDialog.isResumed())
                    return; //We're showing already!

                long time = mStartTime.getTag() == null ? -1 : (Long) mStartTime.getTag();
                mTimeDialog = TimePickerFragment.newInstance(time);
                mTimeDialog.setOnTimeSelectedListener(this);
                mTimeDialog.show(getSherlockActivity().getSupportFragmentManager(), null);

                break;
            }
            case R.id.end_time: {
                mLastTimeSelected = LastTimeSelected.END;
                if(mTimeDialog != null && mTimeDialog.isResumed())
                    return; //We're showing already!

                long time = mEndTime.getTag() == null ? -1 : (Long) mEndTime.getTag();
                mTimeDialog = TimePickerFragment.newInstance(time);
                mTimeDialog.setOnTimeSelectedListener(this);
                mTimeDialog.show(getSherlockActivity().getSupportFragmentManager(), null);

                break;
            }
        }
    }

    @Override
    public void onDateSelected(int julianDay) {
        final long millis = TimeUtils.getStartMillisForJulianDay(julianDay);
        mDate.setTag(julianDay);
        mDate.setText(DateUtils.formatDateRange(getActivity(), millis, millis,
                DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    @Override
    public void onTimeSelected(long millis) {
        TextView tv = null;
        if(mLastTimeSelected == LastTimeSelected.START)
            tv = mStartTime;
        else if(mLastTimeSelected == LastTimeSelected.END)
            tv = mEndTime;
        else
            return; //O no! This should never happen!

        tv.setTag(millis);

        int flags = DateUtils.FORMAT_SHOW_TIME;
        if(DateFormat.is24HourFormat(getActivity()))
            flags |= DateUtils.FORMAT_24HOUR;
        else
            flags |= DateUtils.FORMAT_12HOUR;

        tv.setError(null);
        tv.setText(DateUtils.formatDateRange(getActivity(), millis, millis, flags));
    }

    public Shift getShift() {
        Shift shift = new Shift();

        shift.id = mInitialShift == null ? -1 : mInitialShift.id;
        shift.name = mName.getText() == null ? null : mName.getText().toString();
        shift.note = mNotes.getText() == null ? null : mNotes.getText().toString();
        shift.julianDay = mDate.getTag() == null ? -1 : (Integer) mDate.getTag();
        shift.startTime = mDate.getTag() == null ? -1 : (Long) mStartTime.getTag();
        shift.endTime = mDate.getTag() == null ? -1 : (Long) mEndTime.getTag();

        try {
            CharSequence breakDuration = mUnpaidBreak.getText();
            if(!TextUtils.isEmpty(breakDuration))
                shift.breakDuration = Integer.valueOf(breakDuration.toString());
        } catch(NumberFormatException e) {
            shift.breakDuration = -1;
        }

        try {
            CharSequence payRate = mPayRate.getText();
            if(!TextUtils.isEmpty(payRate))
                shift.payRate = Float.valueOf(payRate.toString());
        } catch(NumberFormatException e) {
            shift.breakDuration = -1;
        }

        return shift;
    }

    /**
     * @return Non-null with an error message to show the user
     */
    public String validate() {
        String error = null;

        if(TextUtils.isEmpty(mName.getText())) {
            error = "Please enter a name";
            mName.setError(error);
            return error;
        }

        error = validateTime();
        if(!TextUtils.isEmpty(error))
            return error;

        error = validateBreakDuration();
        if(!TextUtils.isEmpty(error))
            return error;

        return null;
    }

    private String validateBreakDuration() {
        Long startMillis = (Long) mStartTime.getTag();
        Long endMillis = (Long) mEndTime.getTag();

        final CharSequence breakDurationAsStr = mUnpaidBreak.getText();
        if(startMillis == null || endMillis == null || TextUtils.isEmpty(breakDurationAsStr))
            return null;

        if(!TextUtils.isDigitsOnly(breakDurationAsStr)) {
            String error = "Invalid break time";
            mUnpaidBreak.setError(error);
            return error;
        }

        long duration = (endMillis - startMillis) / TimeUtils.InMillis.MINUTE;
        if(duration < Long.valueOf(breakDurationAsStr.toString())) {
            String error = "Break duration longer than shift";
            mUnpaidBreak.setError(error);
            return error;
        }

        return null;
    }

    private String validateTime() {
        //Validate that starttime is after endtime
        Long startMillis = (Long) mStartTime.getTag();
        Long endMillis = (Long) mEndTime.getTag();

        if(startMillis == null) {
            String error = "Please select a start time";
            mStartTime.setError(error);
            return error;
        }

        if(endMillis == null) {
            String error = "Please select an end time";
            mEndTime.setError(error);
            return error;
        }

        if(startMillis >= endMillis) {
            String error = "End time is before start time";
            mEndTime.setError(error);
            return error;
        }

        return null;
    }

    public boolean isEditing() {
        return getEditingId() >= 0;
    }

    public long getEditingId() {
        return mInitialShift != null ? mInitialShift.id : -1;
    }
}
