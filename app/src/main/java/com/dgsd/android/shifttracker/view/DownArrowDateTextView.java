package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.util.AttributeSet;

import com.dgsd.android.shifttracker.util.TimeUtils;

import java.util.Date;

public class DownArrowDateTextView extends DateTextView {
    public DownArrowDateTextView(Context context) {
        super(context);
    }

    public DownArrowDateTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownArrowDateTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getFormattedDate(Date date) {
        return TimeUtils.formatAsAbbreviatedDate(date);
    }

    @Override
    protected final void onSetDateString(String dateStr) {
        setText(dateStr + " ▼");
    }
}
