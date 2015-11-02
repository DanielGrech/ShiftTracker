package com.dgsd.android.shifttracker.mvp.view;

import com.dgsd.shifttracker.model.Shift;

import java.util.List;
import java.util.Map;

public interface MonthMvpView extends MvpView {

    void setStartDayOfWeek(int startDay);

    void setDaysMarked(Map<Integer, Integer> monthDayToColorMap);

    void setDayTitle(String title);

    void selectDay(int dayOfMonth);

    void showShifts(List<Shift> shifts);

    void showShift(Shift shift);

    void showError(String message);

    void exportToCalendar(Shift shift);

    void cloneShift(Shift shift);
}
