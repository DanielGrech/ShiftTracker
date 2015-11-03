package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dgsd.android.shifttracker.R;

import java.util.Date;
import java.util.GregorianCalendar;

public class MonthViewCell extends TextView {

    private int day = -1;
    private int month = -1;
    private int year = -1;

    private boolean marked;

    private boolean isToday;

    public MonthViewCell(final Context context) {
        super(context);
    }

    public MonthViewCell(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthViewCell(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    public void populate(int day, int month, int year) {
        setEnabled(true);
        this.day = day;
        this.month = month;
        this.year = year;

        setText(String.valueOf(this.day));
    }

    public void clear() {
        day = -1;
        month = -1;
        year = -1;
        setText("");
        setEnabled(false);
    }

    @Override
    public void setTextColor(int color) {
        //noinspection deprecation
        final int accentColor = getResources().getColor(R.color.accent);
        super.setTextColor(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_selected},
                        new int[]{-android.R.attr.state_selected, android.R.attr.state_activated},
                        new int[]{},
                },
                new int[]{
                        Color.WHITE, accentColor, color
                }
        ));
    }

    public void isMarked(boolean isMarked) {
        marked = isMarked;
        setTypeface(isMarked || isToday ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        if (!marked) {
            //noinspection deprecation
            setTextColor(getResources().getColor(R.color.month_row_cell));
        }
    }

    public void isToday(boolean isToday) {
        this.isToday = isToday;
        setActivated(isToday);
        isMarked(this.marked || isToday);
    }

    public boolean hasDate() {
        return day != -1;
    }

    public Date getDate() {
        return new GregorianCalendar(year, month, day).getTime();
    }
}