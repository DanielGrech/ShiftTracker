package com.dgsd.android.shifttracker.mvp.view;

import com.dgsd.shifttracker.model.Shift;

import java.util.List;

public interface MonthMvpView extends MvpView {

    void setStartDayOfWeek(int startDay);

    void setDaysMarked(int[] monthDaysToMark);

    void setDayTitle(String title);

    void selectDay(int dayOfMonth);

    void showShifts(List<Shift> shifts);

    void showShift(Shift shift);

    void showError(String message);

    void exportToCalendar(Shift shift);

    void cloneShift(Shift shift);
}
