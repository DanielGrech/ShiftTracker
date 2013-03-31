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

package com.dgsd.android.ShiftTracker.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.dgsd.android.ShiftTracker.R;
import org.holoeverywhere.widget.TimePicker;

/**
 * A preference type that allows a user to choose a time

 */
public class TimePickerPreferenceCompat extends DialogPreference implements
        TimePicker.OnTimeChangedListener {

    private long mCurrentValue;

    private Time mTime;

    private TimePicker mTimePicker;

    /**
     * @param context
     * @param attrs
     */
    public TimePickerPreferenceCompat(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.Holo_Theme_Dialog_Alert_Light), attrs);
        initialize();
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TimePickerPreferenceCompat(Context context, AttributeSet attrs,
                                      int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Initialize this preference
     */
    private void initialize() {
        setPersistent(true);
        mTime = new Time();
        mTime.setToNow();

        setPositiveButtonText(R.string.done);
        setNegativeButtonText(R.string.cancel);
    }

    /*
    * (non-Javadoc)
    *
    * @see android.preference.DialogPreference#onCreateDialogView()
    */
    @Override
    protected View onCreateDialogView() {

        mTimePicker = new TimePicker(getContext());
        mTimePicker.setOnTimeChangedListener(this);

        int h = getHour();
        int m = getMinute();
        if (h >= 0 && m >= 0) {
            mTimePicker.setCurrentHour(h);
            mTimePicker.setCurrentMinute(m);

            mTime.hour = h;
            mTime.minute = m;
            mTime.normalize(true);
            mCurrentValue = mTime.toMillis(true);
        }

        return mTimePicker;
    }

    /*
    * (non-Javadoc)
    *
    * @see
    * android.widget.TimePicker.OnTimeChangedListener#onTimeChanged(android
    * .widget.TimePicker, int, int)
    */

    public void onTimeChanged(TimePicker view, int hour, int minute) {
        mTime.hour = hour;
        mTime.minute = minute;
        mTime.second = 0;

        final long millis = mTime.toMillis(true);
        persistLong(millis);
        callChangeListener(millis);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if(restorePersistedValue) {
            mCurrentValue = getPersistedLong(mCurrentValue);
            return;
        }

        if(defaultValue == null)
            return;

        String defValAsString = defaultValue.toString();

        try {
            mCurrentValue = Long.valueOf(defValAsString);
            //The default value is in GMT, convert it to timezone
            mTime.timezone = "GMT";
            mTime.set(mCurrentValue);
            int hour = mTime.hour;
            int min = mTime.minute;

            mTime = new Time();
            mTime.hour = hour;
            mTime.minute = min;
            mTime.second = 0;

            mCurrentValue = mTime.toMillis(true);
        } catch (Exception e) {
            //O well..

            mTime.hour = 9;
            mTime.minute = 0;
            mTime.second = 0;

            mCurrentValue = mTime.toMillis(true);
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTime.set(mCurrentValue);
        mTime.normalize(true);
        mTimePicker.setCurrentHour(mTime.hour);
        mTimePicker.setCurrentMinute(mTime.minute);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /**
     * Get the hour value (in 24 hour time)
     *
     * @return The hour value, will be 0 to 23 (inclusive)
     */
    private int getHour() {
        long millis = getPersistedLong(mCurrentValue);
        mTime.set(millis);
        mTime.normalize(true);

        return mTime.hour;
    }

    /**
     * Get the minute value
     *
     * @return the minute value, will be 0 to 59 (inclusive)
     */
    private int getMinute() {
        long millis = getPersistedLong(mCurrentValue);
        mTime.set(millis);
        mTime.normalize(true);

        return mTime.minute;
    }
}
