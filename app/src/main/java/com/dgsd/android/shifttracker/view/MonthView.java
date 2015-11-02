package com.dgsd.android.shifttracker.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dgsd.android.shifttracker.R;
import com.dgsd.android.shifttracker.util.TimeUtils;
import com.dgsd.android.shifttracker.util.ViewUtils;

import java.util.Arrays;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressWarnings("deprecation")
public class MonthView extends LinearLayout implements View.OnClickListener {

    private static final int NOT_SET = -1;

    @Bind(R.id.days)
    MonthViewRow daysRow;

    @Bind(R.id.row1)
    MonthViewRow row1;

    @Bind(R.id.row2)
    MonthViewRow row2;

    @Bind(R.id.row3)
    MonthViewRow row3;

    @Bind(R.id.row4)
    MonthViewRow row4;

    @Bind(R.id.row5)
    MonthViewRow row5;

    @Bind(R.id.row6)
    MonthViewRow row6;

    MonthViewCell[] dateCells;

    private int month = NOT_SET;

    private int year = NOT_SET;

    private int offest;

    private int lastMonthDay;

    private MonthViewCell selectedCell;

    private OnDateSelectedListener onDateSelectedListener;

    private int firstDayOfWeek;

    public static MonthView inflate(Context context) {
        return (MonthView) LayoutInflater.from(context).inflate(R.layout.view_month, null);
    }

    public MonthView(final Context context) {
        super(context);
    }

    public MonthView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        populateDaysRow();

        dateCells = new MonthViewCell[6 * 7]; // 6 rows * 7 cells

        ButterKnife.apply(Arrays.asList(row1, row2, row3, row4, row5, row6),
                new ButterKnife.Action<MonthViewRow>() {
                    @Override
                    public void apply(MonthViewRow row, int index) {
                        for (int i = 0, childCount = row.getChildCount(); i < childCount; i++) {
                            MonthViewCell cell = (MonthViewCell) row.getChildAt(i);
                            cell.setOnClickListener(MonthView.this);
                            dateCells[(index * childCount) + i] = cell;
                        }
                    }
                }
        );
    }

    private void populateDaysRow() {
        final String[] weekDayNames =
                getResources().getStringArray(R.array.month_header_titles);
        for (int i = 0, len = daysRow.getChildCount(); i < len; i++) {
            MonthViewCell cell = (MonthViewCell) daysRow.getChildAt(i);
            cell.setText(weekDayNames[mapRealWeekdayToStartOfWorkWeek(i, firstDayOfWeek)]);
        }
    }

    private void populateCells() {
        final Time now = new Time();
        now.setToNow();

        for (int i = 0; i < offest; i++) {
            dateCells[i].clear();
        }

        final int lastCell = lastMonthDay + offest;
        for (int i = offest, date = 1; i < lastCell; i++, date++) {
            dateCells[i].populate(date, month, year);
            dateCells[i].isToday(now.monthDay == date && now.month == month && now.year == year);
        }

        for (int i = lastCell, len = dateCells.length; i < len; i++) {
            dateCells[i].clear();
        }


        // Hide last row if it's unused
        ViewUtils.visibleWhen(lastCell > dateCells.length - 7, row6);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        onDateSelectedListener = listener;
    }

    public void setFirstDayOfWeek(int firstDay) {
        firstDayOfWeek = firstDay;
        populateDaysRow();

        final Date selectedDate = getSelectedDate();
        set(month, year);

        if (selectedDate != null) {
            selectDay(TimeUtils.toTime(selectedDate).monthDay);
        }
    }

    public void set(int month, int year) {
        this.month = month;
        this.year = year;

        final Time time = new Time();
        time.set(1, month, year);
        time.normalize(true);

        // Time class defaults to 0 == Sunday .. need to adjust
        offest = time.weekDay - 1 - firstDayOfWeek;
        if (offest < 0) {
            offest = 6 - Math.abs(offest + 1);
        }

        time.month += 1;
        time.normalize(true);
        time.monthDay -= 1;
        time.normalize(true);

        lastMonthDay = time.monthDay;

        populateCells();
    }

    public void selectDay(int day) {
        ensureHasSetMonth();
        dispatchClickOnCell(dateCells[dayOfMonthToCellIndex(day)]);
    }

    public void setDayMarked(int day, boolean marked) {
        setDayMarked(day, marked, Color.BLACK);
    }

    public void setDayMarked(int day, boolean marked, @ColorInt int color) {
        ensureHasSetMonth();
        MonthViewCell cell = dateCells[dayOfMonthToCellIndex(day)];
        if (cell.hasDate()) {
            cell.isMarked(marked);
            cell.setTextColor(color);
        }
    }

    public void resetMarkedCells() {
        for (MonthViewCell dateCell : dateCells) {
            if (dateCell.hasDate()) {
                dateCell.isMarked(false);
            }
        }
    }

    public Date getSelectedDate() {
        return selectedCell == null || !selectedCell.hasDate() ?
                null : selectedCell.getDate();
    }

    @Override
    public void onClick(final View v) {
        dispatchClickOnCell((MonthViewCell) v);
    }

    private int dayOfMonthToCellIndex(int day) {
        return day - 1 + offest;
    }

    private void dispatchClickOnCell(MonthViewCell cell) {
        if (!cell.hasDate()) {
            return;
        }

        if (selectedCell != null && selectedCell != cell) {
            selectedCell.setSelected(false);
        }

        selectedCell = cell;
        selectedCell.setSelected(true);

        dispatchOnDateClick(getSelectedDate());
    }

    private void dispatchOnDateClick(Date date) {
        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(date);
        }
    }

    private void ensureHasSetMonth() {
        if (month == NOT_SET || year == NOT_SET) {
            throw new IllegalStateException("Need to set month and year");
        }
    }

    private int mapRealWeekdayToStartOfWorkWeek(int realWorkday, int startDay) {
        int retval = realWorkday + startDay;
        return retval > 6 ? (retval - 7) : retval;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Date date);
    }
}