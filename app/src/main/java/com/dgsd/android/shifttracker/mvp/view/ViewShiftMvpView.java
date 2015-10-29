package com.dgsd.android.shifttracker.mvp.view;

import com.dgsd.shifttracker.model.Shift;

public interface ViewShiftMvpView extends MvpView {

    void showShift(Shift shift);

    void editShift(long shiftId);

    void cloneShift(long shiftId);

    void showError(String message);

    void showShiftList();

    void exportToCalendar(Shift shift);

    void showAd();
}
