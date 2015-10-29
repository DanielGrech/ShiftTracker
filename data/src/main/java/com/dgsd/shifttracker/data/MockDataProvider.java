package com.dgsd.shifttracker.data;

import com.dgsd.shifttracker.model.Shift;
import com.dgsd.shifttracker.model.TimePeriod;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

public class MockDataProvider implements DataProvider {

    private static final long DEFAULT_DELAY = 0; // seconds

    final List<Shift> shifts = new LinkedList<>();

    public MockDataProvider() {
        for (int i = 0; i < 5; i++) {
            shifts.add(createShiftForOffset(shifts.size(), i));
            for (int j = 0; j < i; j++) {
                shifts.add(createShiftForOffset(shifts.size(), i));
            }
        }
    }

    @Override
    public Observable<List<Shift>> getShiftsBetween(long startMillis, long endMillis) {
        return Observable.just(shifts).delay(DEFAULT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Observable<List<Shift>> getTemplateShifts() {
        return getShiftsBetween(0, Long.MAX_VALUE).flatMap(new Func1<List<Shift>, Observable<Shift>>() {
            @Override
            public Observable<Shift> call(List<Shift> shifts) {
                return Observable.from(shifts);
            }
        }).filter(new Func1<Shift, Boolean>() {
            @Override
            public Boolean call(Shift shift) {
                return shift.isTemplate();
            }
        }).toList();
    }

    @Override
    public Observable<Shift> getShift(final long shiftId) {
        return getShiftsBetween(0, Long.MAX_VALUE)
                .flatMap(new Func1<List<Shift>, Observable<Shift>>() {
                    @Override
                    public Observable<Shift> call(List<Shift> shifts) {
                        return Observable.from(shifts);
                    }
                }).takeFirst(new Func1<Shift, Boolean>() {
                    @Override
                    public Boolean call(Shift shift) {
                        return shift.id() == shiftId;
                    }
                });
    }

    @Override
    public Observable<Shift> addShift(Shift shift) {
        if (shift.id() < 0) {
            shift = shift.withId(shifts.size());
        } else {
            Iterator<Shift> iter = shifts.iterator();
            while (iter.hasNext()) {
                final Shift s = iter.next();
                if (s.id() == shift.id()) {
                    iter.remove();
                    break;
                }
            }
        }

        shifts.add(shift);
        return Observable.just(shift).delay(DEFAULT_DELAY, TimeUnit.SECONDS);
    }

    @Override
    public Observable<Void> removeShift(final long shiftId) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                Iterator<Shift> iter = shifts.iterator();
                while (iter.hasNext()) {
                    final Shift s = iter.next();
                    if (s.id() == shiftId) {
                        iter.remove();
                        break;
                    }
                }
                return null;
            }
        }).delay(DEFAULT_DELAY, TimeUnit.SECONDS);
    }

    private Shift createShiftForOffset(int id, int offset) {
        return Shift.builder()
                .id(id)
                .title("Shift #" + id)
                .notes("This is a note for shift #" + id)
                .payRate(10 * offset)
                .unpaidBreakDuration(TimeUnit.MINUTES.toMillis(45 + offset))
                .color(0xFFF44336) // Red
                .timePeriod(TimePeriod.builder()
                        .startMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(offset))
                        .endMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(offset) + TimeUnit.HOURS.toMillis(8))
                        .create())
                .overtimePayRate(offset % 2 == 0 ? -1f : 10 * offset * 2)
                .overtime(offset % 2 == 0 ? null : TimePeriod.builder()
                        .startMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(offset) + TimeUnit.HOURS.toMillis(8))
                        .endMillis(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(offset) + TimeUnit.HOURS.toMillis(9))
                        .create())
                .reminderBeforeShift(offset % 2 == 0 ? -1 : TimeUnit.HOURS.toMillis(1))
                .isTemplate(offset % 2 == 0)
                .create();
    }
}
