package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dgsd.android.shifttracker.util.TimeUtils;

import java.util.Date;

public class DateTextView extends TextView {

    private Date date;

    public DateTextView(Context context) {
        super(context);
    }

    public DateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDate(long millis) {
        setDate(new Date(millis));
    }

    public void setDate(Date date) {
        this.date = date;
        onSetDateString(getFormattedDate(date));
    }

    public Date getDate() {
        return date;
    }

    protected void onSetDateString(String dateStr) {
        setText(dateStr);
    }

    protected String getFormattedDate(Date date) {
        return TimeUtils.formatAsDate(date);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (date == null) {
            return superState;
        } else {
            return new SavedState(superState, date.getTime());
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());

            final Date date = new Date();
            date.setTime(ss.millis);
            setDate(date);
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
