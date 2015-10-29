package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class TimeTextView extends TextView {

    private Time time;

    public TimeTextView(Context context) {
        super(context);
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTime(long millis) {
        final Time time = new Time();
        time.set(millis);
        setTime(time);
    }

    public void setTime(Time time) {
        this.time = time;
        setText(DateUtils.formatDateTime(getContext(),
                time.toMillis(false),
                DateUtils.FORMAT_SHOW_TIME));
    }

    public Time getTime() {
        return time;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (getTime() == null) {
            return superState;
        } else {
            return new SavedState(superState, getTime().toMillis(false));
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (! (state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            SavedState ss = (SavedState)state;
            super.onRestoreInstanceState(ss.getSuperState());

            final Time time = new Time();
            time.set(ss.millis);
            setTime(time);
        }
    }

    public static class SavedState extends BaseSavedState {

        final long millis;

        public SavedState(Parcelable source, long millis) {
            super(source);
            this.millis = millis;
        }

        public SavedState(Parcel in) {
            super(in);
            millis = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(millis);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
