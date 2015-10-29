package com.dgsd.shifttracker.data;

import com.dgsd.shifttracker.model.Shift;

import java.util.List;

import rx.Observable;

public interface DataProvider {

    String UPDATE_ACTION = "data_provider_update";

    Observable<List<Shift>> getShiftsBetween(long startMillis, long endMillis);

    Observable<List<Shift>> getTemplateShifts();

    Observable<Shift> getShift(long shiftId);

    Observable<Shift> addShift(Shift shift);

    Observable<Void> removeShift(long shiftId);
}
