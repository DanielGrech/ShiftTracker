package com.dgsd.android.shifttracker.mvp.view;

import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.ShiftWeekMapping;

import java.util.Date;

public interface WeekMvpView extends MvpView {

    void showShifts(ShiftWeekMapping weekMapping);

    void showShift(Shift shift);

    void addShiftAt(Date date);

    void cloneShift(Shift shift);

    void exportToCalendar(Shift shift);

    void showError(String message);
}
