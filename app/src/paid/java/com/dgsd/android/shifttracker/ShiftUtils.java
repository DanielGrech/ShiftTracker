package com.dgsd.android.shifttracker;

import android.content.Context;

import com.dgsd.shifttracker.data.DataProvider;
import com.dgsd.shifttracker.model.Shift;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

public class ShiftUtils {

    private ShiftUtils() {
        // No instances..
    }

    public static Observable<Shift> getNextShift(Context context) {
        try {
            final DataProvider dataProvider = ((STApp) context.getApplicationContext())
                    .getAppServicesComponent()
                    .dataProvider();

            return dataProvider.getShiftsBetween(0, Long.MAX_VALUE)
                    .flatMap(new Func1<List<Shift>, Observable<Shift>>() {
                        @Override
                        public Observable<Shift> call(List<Shift> shifts) {
                            return Observable.from(shifts);
                        }
                    })
                    .takeFirst(new Func1<Shift, Boolean>() {
                        @Override
                        public Boolean call(Shift shift) {
                            return shift.timePeriod().startMillis() > System.currentTimeMillis();
                        }
                    });
        } catch (Exception ex) {
            Timber.e(ex, "Error getting next shift");
        }

        return null;
    }
}
