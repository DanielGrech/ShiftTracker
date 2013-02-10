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

package com.dgsd.android.ShiftTracker.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import com.actionbarsherlock.app.SherlockDialogFragment;
import org.holoeverywhere.app.TimePickerDialog;
import org.holoeverywhere.widget.TimePicker;

public class TimePickerFragment extends SherlockDialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String KEY_TIME = "_time";

    private long mTime = -1;

    private OnTimeSelectedListener mOnTimeSelectedListener;

    public static TimePickerFragment newInstance(long time) {
        TimePickerFragment frag = new TimePickerFragment();

        Bundle args = new Bundle();
        args.putLong(KEY_TIME, time);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        if(args != null) {
            mTime = args.getLong(KEY_TIME, mTime);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Time time = new Time();

        if(mTime == -1)
            time.setToNow();
        else
            time.set(mTime);

        return new TimePickerDialog(getActivity(), this, time.hour, time.minute, DateFormat.is24HourFormat(getActivity()));
    }

    public void setOnTimeSelectedListener(OnTimeSelectedListener listener) {
        this.mOnTimeSelectedListener = listener;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if(mOnTimeSelectedListener != null) {
            Time time = new Time();
            time.hour = hour;
            time.minute = minute;
            time.second = 0;
            time.normalize(true);

            mOnTimeSelectedListener.onTimeSelected(time.toMillis(true));
        }
    }

    public static interface OnTimeSelectedListener {
        public void onTimeSelected(long time);
    }

}